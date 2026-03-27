import java.net.Socket;
import java.io.*;
import java.util.Scanner;

public class DummyClient{

    public static void main(String args[]) throws IOException {
        


            Socket socket = null;  //Creates socket object as null

            InputStreamReader inputStreamReader = null;  //Reader to read chars from the server
            OutputStreamWriter outputStreamWriter = null;  //Writer to write chars to the

            BufferedReader bufferedReader = null;  //Buffered reader to read the input stream
            BufferedWriter bufferedWriter = null;  //Buffered writer to write the output stream

        
            try{

                socket = new Socket("localhost", 1234); //Initialization of socket object for the server

                
                //New Reader and Writer objects
                inputStreamReader = new InputStreamReader(socket.getInputStream());
                outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

                bufferedReader = new BufferedReader(inputStreamReader);
                bufferedWriter = new BufferedWriter(outputStreamWriter);

                //Start of first thread, the thread responsible for writing to the server
                ServerListener listener = new ServerListener(bufferedReader);
                Thread thread1 = new Thread(listener);
                thread1.start();

                //Creation of scanner
                Scanner scanner = new Scanner(System.in);

                System.out.println("Welcome player, please input your ID");
                int PlayerID = scanner.nextInt();
                scanner.nextLine();

                    //Start of a while loop that continues till the player exits
                    while(true){
                        //Player Option Choice
                        System.out.println("Chose an option:\n1) Search Games || 2) Play Game || 3) Add balance || 4) Exit");
                        String choice = scanner.nextLine();

                        //Switch section to choose an option
                        if (choice.equals("1")){
                           search(PlayerID,bufferedWriter, scanner); 
                        }
                        else if(choice.equals("2")){
                            play(PlayerID,bufferedWriter, scanner);
                        }
                        else if(choice.equals("3")){
                            addBalance(PlayerID,bufferedWriter, scanner);
                        }else if(choice.equals("4")){  //Exits the game sending a BYE msg to the server and one to the player
                            synchronized(bufferedWriter){
                                bufferedWriter.write("BYE");
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            break;
                        }else{
                            System.out.println("Error: Unknown option");
                        }
            
                    } 
                }catch (IOException e){
                    e.printStackTrace();
                }
                finally{

                    try{
                        if(socket != null){
                            socket.close();
                        }
                        if(inputStreamReader != null){
                            inputStreamReader.close();
                        }
                        if(outputStreamWriter != null){
                            outputStreamWriter.close();
                        }
                        if(bufferedReader != null){
                            bufferedReader.close();
                        }
                        if(bufferedWriter != null){
                            bufferedWriter.close();
                        }
                    }catch(IOException e){
                        e.printStackTrace();
                    }

                
                }

    }
    //Sends out search filters to the Master
    public static void search(int PlayerID,BufferedWriter bufferedWriter, Scanner scanner){
        try{//PlayerID, ΣΗΜΕΙΩΣΗ: Το έχω βάει σε comments επειδή παίρνει είδη το ID στην αρχή του προγράμματος, δεν είμαι σίγουρος αν η εκφώνηση λέει να το ξαναζητήσει ή όχι στο search function so το άφησα comment
            //System.out.println("Please input your ID: ");
            //int ID = scanner.nextInt();
            //scanner.nextLine();
            //String strID = Integer.toString(ID);

            //Category -----
            String category = "";
            while(!category.equals("Slots") && !category.equals("Poker") && !category.equals("Roulette")){
                System.out.println("Please input one of the 3 game categories: Slots || Poker || Roulette ");
                category = scanner.nextLine();

                if(!category.equals("Slots") && !category.equals("Poker") && !category.equals("Roulette")){
                    System.out.println("Unknown game type");
                }
            }

            //Stars -----
            int stars = 0;
            while(stars<1 || stars>5){
                System.out.println("Please input your desired number of stars from 1 to 5 ");
                stars = scanner.nextInt();
                scanner.nextLine();
                
                if(stars<1 || stars>5){
                    System.out.println("False star number");
                }
            }
            String strStars = Integer.toString(stars);

            //Risk -----
            String risk = "";
            while(!risk.equals("low") && !risk.equals("medium") && !risk.equals("high")){
                System.out.println("Please choose your risk (low,medium,high): ");
                risk = scanner.nextLine();

                if(!risk.equals("low") && !risk.equals("medium") && !risk.equals("high")){
                    System.out.println("False risk choice");
                }
            }
            

            //Bet Category -----
            String betCategory = "";
            while(!betCategory.equals("$") && !betCategory.equals("$$") && !betCategory.equals("$$$")){
                System.out.println("Please choose a bet category ($, $$, $$$): ");
                betCategory = scanner.nextLine();

                if(!betCategory.equals("$") && !betCategory.equals("$$") && !betCategory.equals("$$$")){
                    System.out.println("Wrong bet category choice");
                }
            }
            System.out.println("Search request sent!\n");
            //String Result αν το search διαβάζει και ID
            //String Result = "SEARCH;" + strID + ";" + category + ";" + strStars + ";" + risk + ";" + betCategory;

            //String Result αν χρησιμοποιεί το ID από την main
            String Result = "SEARCH;" + PlayerID + ";" + category + ";" + strStars + ";" + risk + ";" + betCategory;

            
            synchronized(bufferedWriter){ //Synchronized keyword to avoid deadlocks per project requirements 
                                bufferedWriter.write(Result);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            
        }catch (IOException e){
            System.out.println("Error during search: " + e.getMessage());
        }
        

    }


    //Sends the player's game and bet amount choices to the master
    public static void play(int PlayerID,BufferedWriter bufferedWriter, Scanner scanner){
        try{

            //Game Name -----
            System.out.println("Please input game name");
            String GameName = scanner.nextLine();
            
            //Bet Amount -----
            float BetAmount = -1;
            while(BetAmount < 0){
                System.out.println("Please input bet amount");
                BetAmount = scanner.nextFloat();
                scanner.nextLine();

                if(BetAmount < 0){
                    System.out.println("Error: Negative number");
                }
            }
            System.out.println("Play request sent!\n");

            String Result = "PLAY;" + PlayerID + ";" + GameName + ";" + BetAmount;

            synchronized(bufferedWriter){ //Synchronized keyword to avoid deadlocks for the project needs
                                bufferedWriter.write(Result);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                            
        }catch (IOException e){
            System.out.println("Error during search: " + e.getMessage());            
        }
        
    }


    //Sends player token choice to the Master
    public static void addBalance(int PlayerID,BufferedWriter bufferedWriter, Scanner scanner){
        try{
            //Balance-----
            float Balance = -1;
            while(Balance < 0){
                System.out.println("Please input the amount of tokens you would like to add");
                Balance = scanner.nextInt();

                if(Balance < 0){
                    System.out.println("Error: Negative token count");
                }
            }
            System.out.println("Token request sent!\n");
            String strBalance = Float.toString(Balance);

            scanner.nextLine(); //scanner command so it keeps working properly after .nextInt()

            String Result = "BALANCE;" + PlayerID + ";" + strBalance;

            synchronized(bufferedWriter){ //Synchronized keyword to avoid deadlocks per project requirements 
                                bufferedWriter.write(Result);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                            }
                         
        }catch (IOException e){
            System.out.println("Error during search: " + e.getMessage());
        }
        

    }
}
            

