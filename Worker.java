


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


    public Worker(int port) throws IOException{  
        WorkerSocketSocket = new ServerSocket(port);

    }

    public void main(String args[]){
        try{
            
            int port = Integer.parseInt(args[0]);
            Worker worker = new Worker(port);
            worker.begin();

        }
        catch(IOException e){
            return;
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

        public requestHandler(Socket socket1){
            this.socket = socket1;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            
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
