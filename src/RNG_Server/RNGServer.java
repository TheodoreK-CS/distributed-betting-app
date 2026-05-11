package RNG_Server;


import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.HashMap;

import MessageEssentials.Game;
import MessageEssentials.Message;

public class RNGServer{

    //list of buffers using game name as a key for each buffer
    private static final HashMap<String, RNG_Server.Buffer> gameBuffers = new HashMap<>();

    //list of producer threads
    private static final HashMap<String, Thread> activeThreads = new HashMap<>();

    public static void main(String[] args) {
        int port = Integer.parseInt(args[0]);
        System.out.println("RNG Server booted. Awaiting Workers...");

        //open socket
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            
            //start continuous loop
            while (true) {

                //accept any client
                Socket clientSocket = serverSocket.accept();

                //open input stream
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());

                //read message
                Message incomingMessage = (Message) in.readObject();

                //check what worker want to do
                Message.action action = incomingMessage.getMessageAction();

                //if new random generator for game
                if (action == Message.action.init_random) {

                    //use setupNewGame method to make a buffer for the game if it doesn't already have one
                    Game game = (Game) incomingMessage.get_game();
                    setupNewGame(game);
                    //close the connection
                    clientSocket.close(); 
                    
                }
                //if worker is asking for a random number
                else if (action == Message.action.request_random) {
                    //use provideNumber method to give 
                    provideNumber(incomingMessage, clientSocket);

                }
                //if worker wants to delete the buffer
                else if (action == Message.action.delete_random) {
                    //get name of game that needs it's buffer deleted
                    String gameNameToRemove = incomingMessage.get_name();

                    //check producer list to find producer of this buffer
                    Thread producerToStop = activeThreads.remove(gameNameToRemove);

                    //if the producer exists
                    if (producerToStop != null){
                        //interrupt the producer
                        producerToStop.interrupt();
                    }

                    //stop other threads from accesing buffer list
                    synchronized (gameBuffers) {
                        //if buffer for game exists
                        if (gameBuffers.containsKey(gameNameToRemove)) {
                            //remove buffer from the list of buffers
                            gameBuffers.remove(gameNameToRemove);
                            System.out.println("CLEANUP: Buffer and Producer removed for " + gameNameToRemove);
                        } else {
                            System.out.println("CLEANUP: No active buffer found for " + gameNameToRemove);
                        }
                    }
                    //close the connection
                    clientSocket.close();
                }
            }
            
        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void provideNumber(Message incomingMessage, Socket clientSocket) {
        try {
            //get the game name from payload
            String gameName = incomingMessage.get_name();
            
            RNG_Server.Buffer targetBuffer = null;

            //stop other threads from accessing buffer list
            synchronized (gameBuffers) {
                //check the buffer list to find if the game's buffer
                targetBuffer = gameBuffers.get(gameName);
            }

            //if a buffer for the game exists
            if (targetBuffer != null) {
                System.out.println("Buffer found for '" + gameName + "'. Dispatching Consumer...");
                
                //make a consumer and give it the connection and the buffer to hand random number to worker
                RNG_Server.Consumer consumer = new RNG_Server.Consumer(targetBuffer, clientSocket);
                consumer.start();
                
            }
            //if buffer for the game doesn't exist 
            else {
                System.err.println("CRITICAL: Worker requested a number for uninitialized game: " + gameName);
                
                //close the connection
                clientSocket.close(); 
            }
        } catch (Exception e){
            System.err.println("Error while trying to provide a number: " + e.getMessage());
            //close connection if something goes wrong
            try { clientSocket.close(); } catch (Exception ex) {}
        }
    }

    private static void setupNewGame(Game game) {
        String gameName = game.getGameName();
        String gameSecret = game.getSafetyNumber();

        //stop other threads from accesing buffer list
        synchronized (gameBuffers) {
            
            //check if buffer for this game already exists
            if (gameBuffers.containsKey(gameName)) {
                System.out.println("Game '" + gameName + "' is already running. No new buffer needed.");
            } else {
                System.out.println("Initializing new assembly line for: " + gameName);
                
                //make buffer for the game
                RNG_Server.Buffer newBuffer = new RNG_Server.Buffer();
                
                //put the buffer in the list with the game name as a key
                gameBuffers.put(gameName, newBuffer);
                
                //make a producer for the buffer, give it the secret number for the sha256
                Producer producer = new Producer(newBuffer, gameSecret);

                //start the producer thread
                producer.start();

                //add new producer thread to producer list
                activeThreads.put(gameName, producer);
                
                System.out.println("Successfully started Producer for '" + gameName + "'.");
            }
        }
    }
}
