import java.net.Socket;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import MessageEssentials.Message;


public class DummyClient{
    private Scanner scanner;
    private Socket master;
    private ObjectOutputStream out ;
	private ObjectInputStream in ;
    private int master_port;
    private int PlayerID;
    private double balance=0;



    public static void main(String args[])  {
        int master_port=Integer.parseInt(args[0]);
        DummyClient client=new DummyClient(master_port);
        client.begin();
    }

    public DummyClient(int master_port){
        scanner = new Scanner(System.in);
        this.master_port=master_port;
        try{
            master=new Socket("localhost",master_port);
            out = new ObjectOutputStream(master.getOutputStream());
            in = new ObjectInputStream(master.getInputStream());
            if (out != null) out.close();
            if (in != null) in.close();
            if (master != null) master.close();
        }
        catch(IOException e){
            System.out.println("Could not initialize app, Restart.");
            System.exit(0);
        }
    }
        

    public void begin(){
        
            

        System.out.println("Welcome player, please input your ID");
        PlayerID = scanner.nextInt();
        scanner.nextLine();
        //Start of a while loop that continues till the player exits
        while(true){
            //Player Option Choice
            System.out.println("Balance: "+ balance+"\n");
            System.out.println("1. Search Games.");
            System.out.println("2.  Play Game.");
            System.out.println("3. Add balance.");
            System.out.println("4. Review.");
            System.out.println("5. Refresh.");
            System.out.println("6. Exit.");
            System.out.print("Your choice...: ");
            String choice = scanner.nextLine();

            //Switch section to choose an option
            if (choice.equals("1")){
                search(); 
            }
            else if(choice.equals("2")){
                play();
            }
            else if(choice.equals("3")){
                addBalance();
            }
            else if(choice.equals("4")){  
                review();
            }
            else if(choice.equals("5")){  
                printGames();
            }
            else if(choice.equals("6")){
                System.out.println("Goodbye!");
                break;
            }
            else{
                System.out.println("Error: Unknown option");
            }

        }
    scanner.close();
    try {
        if (out != null) out.close();
        if (in != null) in.close();
        if (master != null) master.close();
        
        
    } 
    catch (IOException e) {
        e.printStackTrace();
    } 
            
    }

    public void review(){

        System.out.println("Give store name.");
        String GameName = scanner.nextLine();
        System.out.println("Give rating.");
        String rating = scanner.nextLine();

        
        Message req = new Message(Message.Action.review_game,PlayerID);

        req.add_payload(GameName,rating);

        Message resp = communicate(req);

    }


    //Sends out search filters to the Master
    public void search(){

        //Category -----
        String category = "all";
        System.out.println("Please input game provider");
        category = scanner.nextLine();

        //Stars -----
        int lowstars = -1;
        int highstars = 6;
        while(lowstars<0 || highstars>5 || lowstars > highstars){
            System.out.println("Please input your desired numbers of stars from 0 to 5 ");
            lowstars = scanner.nextInt();
            scanner.nextLine();
            highstars = scanner.nextInt();
            scanner.nextLine();
        }

        //Risk -----
        String risk = "all";
        while(!risk.equals("low") && !risk.equals("medium") && !risk.equals("high") && !risk.equals("all")){
            System.out.println("Please choose your risk (low,medium,high,all): ");
            risk = scanner.nextLine();

            if(!risk.equals("low") && !risk.equals("medium") && !risk.equals("high")&& !risk.equals("all")){
                System.out.println("False risk choice");
            }
        }
        

        //Bet Category -----
        String betCategory = "all";
        while(!betCategory.equals("$") && !betCategory.equals("$$") && !betCategory.equals("$$$")&& !betCategory.equals("all")){
            System.out.println("Please choose a bet category ($, $$, $$$, all): ");
            betCategory = scanner.nextLine();

            if(!betCategory.equals("$") && !betCategory.equals("$$") && !betCategory.equals("$$$")&& !betCategory.equals("all")){
                System.out.println("Wrong bet category choice");
            }
        }

        
        Message req = new Message(Message.Action.search,PlayerID);

        req.add_payload(category,lowstars,highstars,risk,betCategory);

        Message resp = communicate(req);

           
           

            

    }


    //Sends the player's game and bet amount choices to the master
    public void play(){

        //Game Name -----
        System.out.println("Please input game name");
        String GameName = scanner.nextLine();
        
        //Bet Amount -----
        double BetAmount = -1;
        while(BetAmount <= 0 || BetAmount > balance){
            System.out.println("Please input bet amount");
            BetAmount = scanner.nextDouble();
            scanner.nextLine();
        }

        Message req = new Message(Message.Action.play_bet,PlayerID);

        req.add_payload(GameName, BetAmount);

        Message resp = communicate(req);

    
        
    }


    //Sends player token choice to the Master
    public void addBalance(){
        double x=-1;
        while(x < 0){
            System.out.println("Please input the amount of tokens you would like to add");
            x = scanner.nextDouble();

            if(x < 0){
                System.out.println("Error: Negative token count");
            }
        }

        scanner.nextLine(); //scanner command so it keeps working properly after .nextInt()

        balance=balance+x;       
                                 
    }


    public void printGames(){

        System.out.println("\n");

        Message req = new Message(Message.Action.search_games,PlayerID);

        req.add_payload("all",0,5,"all","all");

        Message resp = communicate(req);


    }

    public Message communicate(Message obj){

        Message res=new Message();

        try{
            master=new Socket("localhost",master_port);
            out = new ObjectOutputStream(master.getOutputStream());
            in = new ObjectInputStream(master.getInputStream());

        }
        catch(IOException e){
            System.out.println("Could not initialize app, Restart.");
            System.exit(0);
        }
        try{
            out.writeObject(obj);
            out.flush();
            try{

                res = (Message) in.readObject();

                return res;
            }
            catch(ClassNotFoundException e){
                System.out.println("Request sent but response not found.");
            }

        }
        catch(IOException e){
            System.out.println("Unable to communicate with server.");
        }
        finally{
            try{
                if (out != null) out.close();
                if (in != null) in.close();
                if (master != null) master.close();
            }
            catch(IOException e){

            }
        }

        


        return res;
        
    }
}

    
    

