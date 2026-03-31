
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import MessageEssentials.Game;
import MessageEssentials.Message;
import java.lang.Runnable;


class Reducer{

    private ServerSocket ReducerSocket;
    private int num_workers,master_port;
    private Map<Integer, ArrayList<ArrayList<Game>>> request_ofoworckers = new HashMap<>(); 
    
    public Reducer(int port, int master_port, int num_workers) throws IOException{
        ReducerSocket = new ServerSocket(port);
        this.num_workers = num_workers;
        this.master_port = master_port;
    }

    public static void main(String args[]){
        try{
            int master_port = Integer.parseInt(args[1]);
            int port = Integer.parseInt(args[0]);
            int num_workers = Integer.parseInt(args[2]);
            Reducer reducer = new Reducer(port, master_port, num_workers);
            reducer.begin();
        }
        catch(IOException e){

            return ;
        }
    }

    public void begin(){

        while (true){
            try{
                Socket socket1 = ReducerSocket.accept();
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

        public requestHandler(Socket socket1){
            try{
                this.socket = socket1;
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            }
            catch(IOException e){

            }
        }

        public void communicateMaster(Message responce){
            try{
                Socket master = new Socket("localhost", master_port);
                out = new ObjectOutputStream(master.getOutputStream());
                in = new ObjectInputStream(master.getInputStream());
                out.writeObject(responce);
                out.flush();

                in.close();
                out.close();
                master.close();
            }
            catch (IOException e) {

            }
        }


        public void run(){
                try {
                    Message obj = (Message) in.readObject();
                    int temp1 = obj.get_id();
                    ArrayList<Game> temp2 = new ArrayList<>();
                    temp2.addAll(obj.getSearchedGames());
                    if(request_ofoworckers.containsKey(temp1)){
                        request_ofoworckers.get(temp1).add(temp2);
                    }
                    else {
                        ArrayList<ArrayList<Game>> list = new ArrayList<>();
                        list.add(temp2);
                        request_ofoworckers.put(temp1, list);
                    }
                    if(num_workers == request_ofoworckers.get(temp1).size()){
                        Message responce = new Message(Message.action.reducer_resp);
                        responce.set_id(temp1);
                        ArrayList<Game> temp3 = new ArrayList<>();
                        for(int i = 0; i < num_workers; i++){
                            temp3.addAll(request_ofoworckers.get(temp1).get(i));
                        }
                        responce.setSearchedGames(temp3);
                        communicateMaster(responce);
                    }

                }
                catch (IOException e) {

                }
                catch (ClassNotFoundException e) {

                }
        }
    }




}