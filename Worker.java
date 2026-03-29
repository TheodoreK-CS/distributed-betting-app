import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
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
                Message obj = (Message) in.readObject();
                Message.action command = obj.getMessageAction();
                if (command == Message.action.add_game){
                    game_list.add(obj.get_game());
                    responce = new Message(command);
                    responce.addContext("new game added ");
                    communicateMaster(responce);
                }
                else if(command == Message.action.remove_game){
                    for(Game g:game_list){
                        if(obj.get_name().equals(g.getGameName())){
                            g.setActive(false);
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

                }
                else if(command == Message.action.change_game){

                }
                else if(command == Message.action.game_profit){

                }
                else if(command == Message.action.player_profit){

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
        
    }



























}
