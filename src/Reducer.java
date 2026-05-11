
import MessageEssentials.Game;
import MessageEssentials.Message;
import MessageEssentials.PlayerStats;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


class Reducer{

    private ServerSocket ReducerSocket;
    private int num_workers,master_port;
    private Map<Integer, ArrayList<ArrayList<Game>>> request_ofoworckers = new HashMap<>(); 
    private Map<Integer, ArrayList<ArrayList<PlayerStats>>> player_requests = new HashMap<>();
    private String master_ip;

    
    public Reducer(int port,String master_ip, int master_port, int num_workers) throws IOException{
        ReducerSocket = new ServerSocket(port);
        this.num_workers = num_workers;
        this.master_port = master_port;
        this.master_ip=master_ip;
    }

    public static void main(String args[]){
        try{
            int port = Integer.parseInt(args[0]);
            String master_ip = args[1];
            int master_port = Integer.parseInt(args[2]);
            int num_workers = Integer.parseInt(args[3]);
            Reducer reducer = new Reducer(port, master_ip, master_port,num_workers);
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
                Socket master = new Socket(master_ip, master_port);
                ObjectOutputStream out2 = new ObjectOutputStream(master.getOutputStream());
                ObjectInputStream in2 = new ObjectInputStream(master.getInputStream());
                out2.writeObject(responce);
                out2.flush();

                in2.close();
                out2.close();
                master.close();
            }
            catch (IOException e) {

            }
        }


       public void run(){
            try {
                Message obj = (Message) in.readObject();
                int reqId = obj.get_id();

                if (obj.getMessageAction() == Message.action.player_profit) {
                    ArrayList<PlayerStats> temp = new ArrayList<>(obj.getPlayerStatsList());

                    synchronized (player_requests) {
                        if (player_requests.containsKey(reqId)) {
                            player_requests.get(reqId).add(temp);
                        } else {
                            ArrayList<ArrayList<PlayerStats>> list = new ArrayList<>();
                            list.add(temp);
                            player_requests.put(reqId, list);
                        }

                        if (num_workers == player_requests.get(reqId).size()) {
                            Message response = new Message(Message.action.reducer_resp);
                            response.set_id(reqId);

                            ArrayList<PlayerStats> merged = new ArrayList<>();
                            for (int i = 0; i < num_workers; i++) {
                                merged.addAll(player_requests.get(reqId).get(i));
                            }

                            response.setPlayerStatsList(merged);
                            player_requests.remove(reqId);
                            communicateMaster(response);
                        }
                    }
                } else {
                    ArrayList<Game> temp2 = new ArrayList<>();
                    temp2.addAll(obj.getSearchedGames());

                    synchronized (request_ofoworckers) {
                        if(request_ofoworckers.containsKey(reqId)){
                            request_ofoworckers.get(reqId).add(temp2);
                        }
                        else {
                            ArrayList<ArrayList<Game>> list = new ArrayList<>();
                            list.add(temp2);
                            request_ofoworckers.put(reqId, list);
                        }

                        if(num_workers == request_ofoworckers.get(reqId).size()){
                            Message responce = new Message(Message.action.reducer_resp);
                            responce.set_id(reqId);
                            ArrayList<Game> temp3 = new ArrayList<>();

                            for(int i = 0; i < num_workers; i++){
                                temp3.addAll(request_ofoworckers.get(reqId).get(i));
                            }

                            responce.setSearchedGames(temp3);
                            request_ofoworckers.remove(reqId);
                            communicateMaster(responce);
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