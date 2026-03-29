package RNG_Server;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import MessageEssentials.Game;
import MessageEssentials.Message;

public class RNGServer {

    // 1. The Shared Dictionary (Outside of main, perfectly legal!)
    private static final HashMap<String, Buffer> gameBuffers = new HashMap<>();

    public static void main(String[] args) {
        
        System.out.println("RNG Server booted. Awaiting Workers...");

        // 2. Open the main network gate on port 9090
        try (ServerSocket serverSocket = new ServerSocket(9090)) {
            
            // 3. The infinite traffic cop loop
            while (true) {
                Socket clientSocket = serverSocket.accept();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                // 1. Read the incoming Message
                Message incomingMessage = (Message) in.readObject();

                // 2. Figure out WHAT the Worker wants to do
                Message.action action = incomingMessage.getMessageAction(); // (Swap this with your actual method)

                if (action == Message.action.init_random) {
                    // --- PHASE 1: INITIALIZATION ---
                    Game game = (Game) incomingMessage.getPayload();
                    setupNewGame(game);
                    
                    // Close the socket because we don't need to send a number back right now
                    clientSocket.close(); 
                    
                } else if (action == Message.action.request_random) {
                    // --- PHASE 2: HAND OUT A NUMBER ---
                    provideNumber(incomingMessage, clientSocket);
                }
            }
            
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void provideNumber(Message incomingMessage, Socket clientSocket) {
        try {
            // 1. Open the envelope to find out WHICH game needs a number
            //get the game name from payload
            String gameName = (String) incomingMessage.getPayload();
            
            Buffer targetBuffer = null;

            // 2. Check our dictionary to find the assembly line for this game
            synchronized (gameBuffers) {
                targetBuffer = gameBuffers.get(gameName);
            }

            // 3. Did we find it?
            if (targetBuffer != null) {
                System.out.println("Buffer found for '" + gameName + "'. Dispatching Consumer...");
                
                // Hand the specific Buffer and the Worker's open phone line to the Consumer
                Consumer consumer = new Consumer(targetBuffer, clientSocket);
                consumer.start();
                
            } else {
                // ERROR: The Master server forgot to send an INIT message for this game first!
                System.err.println("CRITICAL: Worker requested a number for uninitialized game: " + gameName);
                
                // Hang up the phone immediately so the Worker doesn't wait forever
                clientSocket.close(); 
            }
        } catch (Exception e){
            System.err.println("Error while trying to provide a number: " + e.getMessage());
            // Always cleanly close the socket if something crashes
            try { clientSocket.close(); } catch (Exception ex) {}
        }
    }

    private static void setupNewGame(Game game) {
        String gameName = game.getGameName();
        String gameSecret = game.getSafetyNumber();

        // 2. Lock the dictionary so multiple threads don't trip over each other
        synchronized (gameBuffers) {
            
            // 3. Check if we already built an assembly line for this game
            if (gameBuffers.containsKey(gameName)) {
                System.out.println("Game '" + gameName + "' is already running. No new buffer needed.");
            } else {
                System.out.println("Initializing new assembly line for: " + gameName);
                
                // 4. Build the new storage box (capacity of 10)
                Buffer newBuffer = new Buffer();
                
                // 5. Put the new buffer in the dictionary so Workers can find it later
                gameBuffers.put(gameName, newBuffer);
                
                // 6. Hire the Producer, hand it the secret key, and turn it on!
                Producer producer = new Producer(newBuffer, gameSecret);
                producer.start();
                
                System.out.println("Successfully started Producer for '" + gameName + "'.");
            }
        }
    }
}
