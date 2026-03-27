


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
        WorkerSocketSocket = new ServerSocket(port);
        this.reducerPort = reducer_ports;
    }

    public void main(String args[]){
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
                Thread(thread).start();

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

    public void communicate(){

        //Message responce = new Message();
        try{
            Socket reducer = new Socket("localhost",reducerPort);
            ObjectOutputStream reducerOut = new ObjectOutputStream(reducer.getOutputStream());
            ObjectInputStream reducerIn = new ObjectInputStream(reducer.getInputStream());

            reducerOut.writeObject(obj);
            reducerOut.flush();

            reducerIn.close();
            reducerOut.close();
            reducer.close();
        }
        catch (IOException e) {

        }


    }

        public void run(){
            try {
                Message obj = (Message) in.readObject();
                Message.action command = obj.getMessageAction();
                if (command == Message.action.add_game){
                    game_list.add(obj.get_game());
                }
                else if(command == Message.action.remove_game){
                    for(Game g:game_list){
                        if(obj.get_name().equals(g.getGameName())){
                            g.setActive(false);
                            break;
                        }
                        
                    }
                }
                else if(command == Message.action.search_games){

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
