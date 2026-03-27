import java.io.*;

public class ServerListener implements Runnable {
    private BufferedReader bufferedReader;

    public ServerListener(BufferedReader bufferedReader){
        this.bufferedReader = bufferedReader;
    }

    @Override
    public void run() {
        try{
            String msgFromServer;

            while((msgFromServer = bufferedReader.readLine()) != null){
                System.out.println(msgFromServer);
                
            }
            
        }catch (IOException e){
            System.err.println("Thank you for participating in our game");
        }
             
                
            
    }

}
