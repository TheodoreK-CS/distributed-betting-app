
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import MessageEssentials.Game;
import MessageEssentials.Message;
import java.lang.Runnable;


class Reducer{

    private ServerSocket ReducerSocket;
    
    
    public Reducer(int port) throws IOException{
        ReducerSocket = new ServerSocket(port);
    }

    public static void main(String args[]){
        try{

            int port = Integer.parseInt(args[0]);
            Reducer reducer = new Reducer(port);
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
                out.writeObject(responce);
                out.flush();
            }
            catch (IOException e) {

            }
        }


        public void run(){
                try {
                    Message obj = (Message) in.readObject();
                    

                }
                catch (IOException e) {

                }
                catch (ClassNotFoundException e) {

                }
        }
    }




}