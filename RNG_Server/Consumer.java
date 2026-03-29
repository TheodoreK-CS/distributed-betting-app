package RNG_Server;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class Consumer extends Thread {
    
    private final Buffer buffer;
    private final Socket clientSocket;

    //make consumer thread with buffer access and client socket
    public Consumer(Buffer buffer, Socket clientSocket) {
        this.buffer = buffer;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            //set up tube
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            
            //get number and hashed number from buffer
            Tuple tuple = buffer.get();
            
            //send it
            out.writeObject(tuple);
            out.flush();
            
            //close connection
            out.close();
            clientSocket.close();
            
            //debbuging message
            System.out.println("Consumer successfully delivered number " + tuple.getWinningNumber() + " to a Worker.");
            
        } catch (Exception e) {
            System.err.println("Network error while talking to Worker: " + e.getMessage());
        }
    }
}
