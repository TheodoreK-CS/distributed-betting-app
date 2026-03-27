import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class Manager {
    public static void main(String[] args) throws IOException{
        Socket socket = null; //Creates socket object as null

        OutputStream outputStream = null;
        InputStreamReader inputStreamReader = null;  //Reader to read chars from the server
        OutputStreamWriter outputStreamWriter = null;  //Writer to write chars to the

        BufferedReader bufferedReader = null;  //Buffered reader to read the input stream
        BufferedWriter bufferedWriter = null;  //Buffered writer to write the output stream

        try{
            socket = new Socket("localhost", 1234);

            //New Reader and Writer objects
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            //Start of first thread, the thread responsible for writing to the server
            ServerListener listener = new ServerListener(bufferedReader);
            Thread thread1 = new Thread(listener);
            thread1.start();

            Scanner scanner = new Scanner(System.in);


            while(true){
                System.out.println("Select an option: 1. Insert Game || 2. Remove Game || 3. Update Risk || 4. Statistics || 5. Exit ");
                String choice = scanner.nextLine();
                
                if (choice.equals("1")){
                    insertGame(bufferedWriter, scanner);
                }
                if (choice.equals("2")){
                    removeGame(bufferedWriter, scanner);
                }
                else if(choice.equals("3")){
                    updateRisk(bufferedWriter, scanner);
                }
                else if(choice.equals("4")){
                    getStats(bufferedWriter, scanner);
                }
                else if(choice.equals("5")){
                    synchronized(bufferedWriter){
                                bufferedWriter.write("BYE");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            break;
                    }
                }
                
        



        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //Manager menu methods
    //Insert game
    public static void insertGame(BufferedWriter bufferedWriter, Scanner scanner){
        try{
            System.out.println("Please input .json file path and image path");
            String jsonPath = scanner.nextLine();
            
            String jsonContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(jsonPath)));

            jsonContent = jsonContent.replace("\n", "").replace("\r","");

            String message = "INSERT;" + jsonContent;

            synchronized(bufferedWriter){
                                bufferedWriter.write(message);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            System.out.println("Game sent succesfully");
                        


        }catch(IOException e){
            e.printStackTrace();
        }
    }
    
    //Remove game
    public static void removeGame(BufferedWriter bufferedWriter, Scanner scanner){
        try{
            System.out.println("Enter the name of the game to remove:");
            String gameName = scanner.nextLine();

            String message = "REMOVE;" + gameName;
            synchronized(bufferedWriter){
                                bufferedWriter.write(message);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            System.out.println("Remove request sent");

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    //update Risk
    public static void updateRisk(BufferedWriter bufferedWriter, Scanner scanner){
        try{
            
            System.out.println("Please enter the name of the game you would like to change:");
            String GameName = scanner.nextLine();

            System.out.println("Please enter the new Risk adjustment:");
            String Risk = scanner.nextLine();

            String Result = "UPDATE" + ";" + GameName + ";" + Risk;

            synchronized(bufferedWriter){ //Synchronized keyword to avoid deadlocks per project requirements 
                                bufferedWriter.write(Result);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }



        }catch(IOException e){
            e.printStackTrace();
        }


    }

    
    //View Stats
    public static void getStats(BufferedWriter bufferedWriter, Scanner scanner){
        try{
            System.out.println("View stats for: 1. Specific Game | 2. Specific Player");
            String choice = scanner.nextLine();
            String message = "";

            if(choice.equals("1")){
                System.out.println("Enter game name:");
                String name = scanner.nextLine();

                message = "STATS;GAME;" + name;
            }else if (choice.equals("2")){
                System.out.println("Enter player ID:");
                String id = scanner.nextLine();

                message = "STATS;PLAYER;" + id;
            }else if (choice.equals("3")){
                System.out.println("Enter provider name:");
                String provider = scanner.nextLine();

                message = "STATS;PROVIDER;" + provider;
            }

            if(!message.isEmpty()){
                synchronized(bufferedWriter){ //Synchronized keyword to avoid deadlocks per project requirements 
                                bufferedWriter.write(message);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
