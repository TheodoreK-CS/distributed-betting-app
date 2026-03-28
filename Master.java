import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import MessageEssentials.Message;
import java.lang.Runnable;

class Master{

    private ServerSocket MasterSocket;
    private ArrayList<Integer> workerPorts = new ArrayList<>();
    private Map<Integer, Socket> id = new HashMap<>();
    private int count = 0;


    public Master(int port,ArrayList<Integer> worker_ports) throws IOException{  
        MasterSocket = new ServerSocket(port);
        this.workerPorts = worker_ports;
    }

    public static void main(String args[]){
        try{
            ArrayList<Integer> worker_ports = new ArrayList<Integer>(); 
            for (int i = 1; i < args.length; i++){
                worker_ports.add(Integer.parseInt(args[i]));
            }
            int port = Integer.parseInt(args[0]);
            Master master = new Master(port,worker_ports);
            master.begin();


        }
        catch(IOException e){
            return;
        }

    }

    public void begin(){

        while (true){
            try{
                Socket socket1 = MasterSocket.accept();
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

        

        public Message communicate(int temp){

            Message response = new Message(Message.action.empty);
            try{
                Socket worker = new Socket("localhost",workerPorts.get(temp));

                ObjectOutputStream workerOut = new ObjectOutputStream(worker.getOutputStream());
                ObjectInputStream workerIn = new ObjectInputStream(worker.getInputStream());

                workerOut.writeObject(obj);
                workerOut.flush();

                response = (Message) workerIn.readObject();

                workerIn.close();
                workerOut.close();
                worker.close();
            
            } 
            catch (IOException e) {

            }
            catch (ClassNotFoundException e) {

            }
            return response;
        }

        public void communicate_all(){


            try{
                for(Integer i:workerPorts){
                    //syncronized
                    obj.set_id(count);
                    id.put(count, socket);
                    count ++;
                    Socket worker = new Socket("localhost",i);
                    ObjectOutputStream workerOut = new ObjectOutputStream(worker.getOutputStream());
                    ObjectInputStream workerIn = new ObjectInputStream(worker.getInputStream());

                    workerOut.writeObject(obj);
                    workerOut.flush();

                    workerIn.close();
                    workerOut.close();
                    worker.close();
                }
            }
            catch (IOException e) {

            }

           
        } 

        public void run(){
            Message response;
            try {
                obj = (Message) in.readObject();
                Message.action command = obj.getMessageAction();
                if (command == Message.action.add_game){
                    int hash = obj.get_name().hashCode();
                    int temp = Math.abs(hash % workerPorts.size());
                    response = communicate(temp);

                }
                else if(command == Message.action.remove_game){
                    int hash = obj.get_name().hashCode();
                    int temp = Math.abs(hash % workerPorts.size());
                    response = communicate(temp);

                }
                else if(command == Message.action.search_games){
                    communicate_all();
                }
                else if(command == Message.action.play_bet){
                    int hash = obj.get_name().hashCode();
                    int temp = Math.abs(hash % workerPorts.size());
                    response = communicate(temp);

                }
                else if(command == Message.action.change_game){
                    int hash = obj.get_name().hashCode();
                    int temp = Math.abs(hash % workerPorts.size());
                    response = communicate(temp);

                }
                else if(command == Message.action.game_profit){
                    communicate_all();
                }
                else if(command == Message.action.player_profit){
                    communicate_all();
                }
                else if(command == Message.action.review_game){
                    int hash = obj.get_name().hashCode();
                    int temp = Math.abs(hash % workerPorts.size());
                    response = communicate(temp);
                }
                
            }
            catch (IOException e) {

            }
            catch (ClassNotFoundException e) {

            }

            
        }

    }

}
