import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import MessageEssentials.Game;
import MessageEssentials.Message;
import java.lang.Runnable;

class Worker{

    private ServerSocket WorkerSocket;
    private ArrayList<Game> game_list = new ArrayList<>();
    private int reducerPort ;

    public Worker(int port, int reducer_ports) throws IOException{  
        WorkerSocket = new ServerSocket(port);
        this.reducerPort = reducer_ports;
    }

    public static void main(String args[]){
        try{
            int reducer_port = Integer.parseInt(args[1]);
            int port = Integer.parseInt(args[0]);
            Worker worker = new Worker(port,reducer_port);
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

                Socket reducer = new Socket("localhost",reducerPort);
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
                    game_list.add(obj.get_game());

                    contactRNG(obj.get_game(), Message.action.init_random);

                    responce = new Message(command);
                    responce.addContext("new game added ");
                    communicateMaster(responce);
                }
                else if(command == Message.action.remove_game){
                    for(Game g:game_list){
                        if(obj.get_name().equals(g.getGameName())){
                            g.setActive(false);

                            contactRNG(g.getGameName(), Message.action.delete_random);

                            responce = new Message(command);
                            responce.addContext("removed game ");
                            communicateMaster(responce);
                            break;
                        }  
                    }
                }
                else if(command == Message.action.search_games){
                    ArrayList<Game> searchedGames = new ArrayList<>();
                    Message reducerMessage = new Message(command);
                    for(Game g:game_list){
                        if(obj.getProviders().contains(g.getProviderName()) || obj.getProviders().contains("all")){
                            if(obj.getLowStars()<g.getStars() && obj.getHighStars()>g.getStars()){
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
    
                    RNG_Server.Tuple rngData = requestNumberFromRNG(gName);
                    
                    if(rngData != null) {

                        String secretKey = "";
                        for(Game g : game_list) {
                            if(g.getGameName().equals(gName)) {
                                secretKey = g.getSafetyNumber();
                                break;
                            }
                        }


                        String dataToVerify = rngData.getWinningNumber() + secretKey;
                        String localHash = calculateSHA256(dataToVerify);


                        if(localHash.equals(rngData.getSecurityHash())) {
                            System.out.println("SECURITY CHECK PASSED: Number " + rngData.getWinningNumber() + " is authentic.");
                            
                        } else {
                            System.err.println("SECURITY ALERT: TAMPERING DETECTED! Hashes do not match.");
                        }
                    }

                }
                else if(command == Message.action.change_game){
                    for(Game g:game_list){
                        if(obj.get_name().equals(g.getGameName())){
                            g.setMaxBet(obj.get_maxBet());
                            g.setMinBet(obj.get_minBet());
                            g.setRiskLevel(obj.get_risk());
    
                            responce = new Message(command);
                            responce.addContext("Game Changed");
                            communicateMaster(responce);
                            break;
                        }
                    }
                }
                else if(command == Message.action.game_profit){
                    Message reducerMessage = new Message(command);
                    reducerMessage.setSearchedGames(game_list);
                    reducerMessage.set_id(obj.get_id());
                    communicateReducer(reducerMessage);
                }
                else if(command == Message.action.player_profit){
                    /// player id missing ??????????????????????????????????????????
                }
                else if(command == Message.action.review_game){
                    for(Game g:game_list){
                        if(obj.get_name().equals(g.getGameName())){
                            g.game_review(obj.getStar());
                            responce = new Message(command);
                            responce.addContext("new review added");
                            communicateMaster(responce);
                            break;
                        }
                    }
                }
            }
            catch (IOException e) {
            }
            catch (ClassNotFoundException e) {
            } 
        }

        private void contactRNG(Object payload, Message.action rngAction) {
            try (Socket rngSocket = new Socket("localhost", 9090);
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
            try (Socket rngSocket = new Socket("localhost", 9090);
                ObjectOutputStream outRNG = new ObjectOutputStream(rngSocket.getOutputStream());
                ObjectInputStream inRNG = new ObjectInputStream(rngSocket.getInputStream())) {
                
                Message msg = new Message(Message.action.request_random);
                msg.addGameName(gName);
                
                outRNG.writeObject(msg);
                outRNG.flush();
                
                return (RNG_Server.Tuple) inRNG.readObject();
            } catch (Exception e) {
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
