import MessageEssentials.Game;
import MessageEssentials.Message;
import MessageEssentials.PlayerStats;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;




class Worker{
   
    private ServerSocket WorkerSocket;
    private ArrayList<Game> game_list = new ArrayList<>();
    private int reducerPort ;
    private final Object gameListLock = new Object();
    private Map<String, PlayerStats> playerStatsMap = new HashMap<>();
    private final Object playerStatsLock = new Object();
    private String reducer_ip;
    private String rng_ip;
    private int rng_port;


    public Worker(int port, String reducer_ip ,int reducer_port,String rng_ip,int rng_port) throws IOException{  
        WorkerSocket = new ServerSocket(port);
        this.reducerPort = reducer_port;
        this.reducer_ip=reducer_ip;
        this.rng_ip=rng_ip;
        this.rng_port=rng_port;
    }

    public static void main(String args[]){
        try{
            
            int port = Integer.parseInt(args[0]);
            String reducer_ip = args[1];
            int reducer_port = Integer.parseInt(args[2]);
            String rng_ip = args[3];
            int rng_port = Integer.parseInt(args[4]);

            Worker worker = new Worker(port, reducer_ip, reducer_port, rng_ip, rng_port);
            worker.begin();

        }
        catch(IOException e){
            return ;
        }

    }

    public void begin(){

        while (true){
            try{
                Socket socket1 = WorkerSocket.accept();
                requestHandler thread = new requestHandler(socket1);
                Thread t = new Thread(thread);
                t.start();

            }
            catch(IOException e){

            }
            
        }

    }

    private class requestHandler implements Runnable{

        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private Message obj;

        public requestHandler(Socket socket1){
            try{
                this.socket = socket1;
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            }
            catch(IOException e){

            }
        }

        public void communicateReducer(Message reducerMessage){

            try{

                Socket reducer = new Socket(reducer_ip,reducerPort);
                ObjectOutputStream reducerOut = new ObjectOutputStream(reducer.getOutputStream());
                ObjectInputStream reducerIn = new ObjectInputStream(reducer.getInputStream());

                reducerOut.writeObject(reducerMessage);
                reducerOut.flush();

                reducerIn.close();
                reducerOut.close();
                reducer.close();
            }
            catch (IOException e) {

            }
        }       
        public void communicateMaster(Message responce){

            try{
                out.writeObject(responce);
                out.flush();    
            }
            catch (IOException e) {

            }
        }
        public void run(){
            Message responce;
            try {
                obj = (Message) in.readObject();
                Message.action command = obj.getMessageAction();
                if (command == Message.action.add_game){
                    Game newGame = obj.get_game();
                    boolean added = false;

                    synchronized (gameListLock) {
                        boolean exists = false;
                        for (Game g : game_list) {
                            if (g.getGameName().equals(newGame.getGameName())) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            game_list.add(newGame);
                            added = true;
                        }
                    }

                    responce = new Message(command);
                    if (added) {
                        contactRNG(newGame, Message.action.init_random);
                        responce.addContext("new game added");
                    } else {
                        responce.addContext("game already exists");
                    }
                    communicateMaster(responce);
                }
                else if(command == Message.action.remove_game){
                    String removedGameName = null;

                    synchronized (gameListLock) {
                        for (Game g : game_list) {
                            if (obj.get_name().equals(g.getGameName())) {
                                g.setActive(false);
                                removedGameName = g.getGameName();
                                break;
                            }
                        }
                    }

                    if (removedGameName != null) {
                        contactRNG(removedGameName, Message.action.delete_random);

                        responce = new Message(command);
                        responce.addContext("removed game ");
                        communicateMaster(responce);
                    }
                    else{
                        responce = new Message(command);
                        responce.addContext("game does not exist");
                        communicateMaster(responce);
                    }
                }
                else if(command == Message.action.search_games){
                    ArrayList<Game> snapshot;

                    synchronized (gameListLock) {
                        snapshot = new ArrayList<>(game_list);
                    }
                    ArrayList<Game> searchedGames = new ArrayList<>();
                    Message reducerMessage = new Message(command);
                    for(Game g:snapshot){
                        if(obj.getProviders().contains(g.getProviderName()) || obj.getProviders().contains("all")){
                            if(obj.getLowStars()<=g.getStars() && obj.getHighStars()>=g.getStars()){
                                if (obj.getRisk().equals(g.getRiskLevel())|| obj.getRisk().equals("all")) {
                                    if(obj.getCategory().equals(g.getBetCategory()) || obj.getCategory().equals("all")){
                                        searchedGames.add(g);
                                    }
                                }
                            }
                        }                   
                    }
                    reducerMessage.setSearchedGames(searchedGames);
                    reducerMessage.set_id(obj.get_id());
                    communicateReducer(reducerMessage);
                }
                else if(command == Message.action.play_bet){

                    String gName = obj.get_name();
                    double betAmount = obj.get_betAmount();

                    Game targetGame = null;

                    synchronized (gameListLock) {
                        for (Game g : game_list) {
                            if (g.getGameName().equals(gName) && g.ifActive()) {
                                targetGame = g;
                                break;
                            }
                        }
                    }

                    responce = new Message(command, obj.getPlayerId());

                    if (targetGame == null) {
                        responce.addContext("Game does not exist.");
                        responce.setPayout(betAmount);
                        communicateMaster(responce);
                        return;
                    }

                    if (betAmount < targetGame.getMinBet() || betAmount > targetGame.getMaxBet()) {
                        responce.addContext("Bet amount is outside allowed limits.");
                        responce.setPayout(betAmount);
                        communicateMaster(responce);
                        return;
                    }

                    RNG_Server.Tuple rngData = requestNumberFromRNG(gName);

                    if (rngData == null) {
                        responce.addContext("RNG unavailable.");
                        responce.setPayout(betAmount);
                        communicateMaster(responce);
                        return;
                    }

                    String dataToVerify = rngData.getWinningNumber() + targetGame.getSafetyNumber();
                    String localHash = calculateSHA256(dataToVerify);

                    if (!localHash.equals(rngData.getSecurityHash())) {
                        responce.addContext("Security error: invalid RNG hash.");
                        responce.setPayout(betAmount);
                        communicateMaster(responce);
                        return;
                    }

                    double[] low =    {0.0, 0.0, 0.0, 0.1, 0.5, 1.0, 1.1, 1.3, 2.0, 2.5};
                    double[] medium = {0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 1.0, 1.5, 2.5, 3.5};
                    double[] high =   {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 2.0, 6.5};

                    int winningNumber = rngData.getWinningNumber();
                    double payout = 0.0;

                    if (winningNumber % 100 == 0) {
                        payout = betAmount * targetGame.getJackpot();
                    } 
                    else {
                        int idx = winningNumber % 10;
                        double multiplier = 0.0;

                        switch (targetGame.getRiskLevel()) {
                            case "low":
                                multiplier = low[idx];
                                break;
                            case "medium":
                                multiplier = medium[idx];
                                break;
                            case "high":
                                multiplier = high[idx];
                                break;
                            default:
                                responce.addContext("Invalid game risk level.");
                                responce.setPayout(betAmount);
                                communicateMaster(responce);
                                return;
                        }

                        payout = betAmount * multiplier;
                    }

                    synchronized (gameListLock) {
                        targetGame.change_profit(betAmount - payout);
                    }
                    synchronized (playerStatsLock) {
                        if (playerStatsMap.containsKey(obj.getPlayerId())) {
                            playerStatsMap.get(obj.getPlayerId()).updateNet(betAmount, payout);
                        } else {
                            PlayerStats newPlayer = new PlayerStats(obj.getPlayerId());
                            newPlayer.updateNet(betAmount, payout);
                            playerStatsMap.put(obj.getPlayerId(), newPlayer);
                        }
                    }
                    responce.setPayout(payout);
                    responce.addContext(
                        "Bet completed. Winning number: " + winningNumber +
                        ", payout: " + payout
                    );
                    communicateMaster(responce);
                }

                
                else if(command == Message.action.change_game){
                    boolean changed = false;

                    synchronized (gameListLock) {
                        for (Game g : game_list) {
                            if (obj.get_name().equals(g.getGameName())) {
                                g.setMaxBet(obj.get_maxBet());
                                g.setMinBet(obj.get_minBet());
                                g.setRiskLevel(obj.get_risk());
                                changed = true;
                                break;
                            }
                        }
                    }

                    if (changed) {
                        responce = new Message(command);
                        responce.addContext("Game Changed");
                        communicateMaster(responce);
                    }
                    else{
                        responce = new Message(command);
                        responce.addContext("game does not exist");
                        communicateMaster(responce);
                    }
                }
                else if(command == Message.action.game_profit){
                    ArrayList<Game> snapshot;

                    synchronized (gameListLock) {
                        snapshot = new ArrayList<>(game_list);
                    }

                    Message reducerMessage = new Message(command);
                    reducerMessage.setSearchedGames(snapshot);
                    reducerMessage.set_id(obj.get_id());
                    communicateReducer(reducerMessage);
                }
                else if(command == Message.action.player_profit){
                    ArrayList<PlayerStats> snapshot = new ArrayList<>();

                    synchronized (playerStatsLock) {
                        snapshot.addAll(playerStatsMap.values());
                    }

                    Message reducerMessage = new Message(command);
                    reducerMessage.setPlayerStatsList(snapshot);
                    reducerMessage.set_id(obj.get_id());
                    communicateReducer(reducerMessage);
                }
                else if(command == Message.action.review_game){
                    boolean reviewed = false;

                    synchronized (gameListLock) {
                        for (Game g : game_list) {
                            if (obj.get_name().equals(g.getGameName())) {
                                g.game_review(obj.getStar());
                                reviewed = true;
                                break;
                            }
                        }
                    }

                    if (reviewed) {
                        responce = new Message(command);
                        responce.addContext("new review added");
                        communicateMaster(responce);
                    }
                    else{
                        responce = new Message(command);
                        responce.addContext("game does not exist");
                        communicateMaster(responce);
                    }
                }
            }
            catch (IOException e) {
            }
            catch (ClassNotFoundException e) {
            } 
        }

        private void contactRNG(Object payload, Message.action rngAction) {
            try (Socket rngSocket = new Socket(rng_ip, rng_port);
                ObjectOutputStream outRNG = new ObjectOutputStream(rngSocket.getOutputStream())) {
                
                Message msg = new Message(rngAction);
                
                if (payload instanceof Game) {
                    msg.add_Game((Game) payload);
                } else if (payload instanceof String) {
                    msg.addGameName((String) payload);
                }
                
                outRNG.writeObject(msg);
                outRNG.flush();
            } catch (IOException e) {
                System.err.println("Worker: Could not contact RNG Server for " + rngAction);
            }
        }

        private RNG_Server.Tuple requestNumberFromRNG(String gName) {
            try (Socket rngSocket = new Socket("localhost", 9090)) {

                ObjectOutputStream outRNG = new ObjectOutputStream(rngSocket.getOutputStream());

                Message msg = new Message(Message.action.request_random);
                msg.addGameName(gName);

                outRNG.writeObject(msg);
                outRNG.flush();

                ObjectInputStream inRNG = new ObjectInputStream(rngSocket.getInputStream());

                RNG_Server.Tuple result = (RNG_Server.Tuple) inRNG.readObject();

                inRNG.close();
                outRNG.close();

                return result;
            } 
            catch (Exception e) {
                System.err.println("Worker: RNG Request failed - " + e.getMessage());
                return null;
            }
        }

        private String calculateSHA256(String input) {
            try {
                //get sha-256 algorithm
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                //use algorithm on the bytes and put them in a byte array
                byte[] hashBytes = digest.digest(input.getBytes());
                
                //make hashed bytes into a string
                StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
                for (byte b : hashBytes) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }
                return hexString.toString();
                
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 algorithm missing!", e);
            }
        }
    }
}
