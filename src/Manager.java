import java.net.Socket;
import java.io.*;
import java.util.Scanner;
import java.util.ArrayList;
import MessageEssentials.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Manager {
    private Scanner scanner;
    private Socket master;
    private ObjectOutputStream out ;
	private ObjectInputStream in ;
    private int master_port;


    public static void main(String args[])  {
        int master_port=Integer.parseInt(args[0]);
        Manager manager=new Manager(master_port);
        manager.begin();
    }

    public Manager(int master_port){
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


    public void begin() {

        while(true){

            System.out.println("1. Insert Game.");
            System.out.println("2. Remove Game.");
            System.out.println("3. Update Risk.");
            System.out.println("4. Statistics.");
            System.out.println("5. Exit.");
            System.out.print("Your choice...: ");
            String choice = scanner.nextLine();
            
            if (choice.equals("1")){
                insertGame();
            }
            else if (choice.equals("2")){
                removeGame();
            }
            else if(choice.equals("3")){
                updateGame();
            }
            else if(choice.equals("4")){
                getStats();
            }
            else if(choice.equals("5")){
                System.out.println("Goodbye!");
                break;               
            }
            else {
                System.out.println("Invalid choice.");
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


    

    //Manager menu methods

    //Insert game

    public void insertGame(){
        System.out.print("Please input .json file path: ");
        String jsonPath = scanner.nextLine();
        
        try{
            FileReader reader = new FileReader(jsonPath);

            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();

            reader.close();

            String gameName = obj.get("GameName").getAsString();
            String provider = obj.get("ProviderName").getAsString();
            double stars = obj.get("Stars").getAsDouble();
            int votes = obj.get("NoOfVotes").getAsInt();
            String logo = obj.get("GameLogo").getAsString();
            double minBet = obj.get("MinBet").getAsDouble();
            double maxBet = obj.get("MaxBet").getAsDouble();
            String risk = obj.get("RiskLevel").getAsString();
            String hash = obj.get("HashKey").getAsString();

            Game game=new Game(gameName,provider,stars,votes,logo,minBet,maxBet,risk,hash);

            Message req = new Message(Message.action.add_game);
            req.add_Game(game);
            Message resp = communicate(req);
            System.out.println(resp.getContext());

        }
        catch (Exception e) {
            System.out.println("Invalid json path");
        }

    }
    
    //Remove game
    public void removeGame(){
        
        System.out.println("Enter the name of the game to remove:");
        String gameName = scanner.nextLine();

        
        Message req = new Message(Message.action.remove_game);
        req.addGameName(gameName);
        Message resp = communicate(req);
        System.out.println(resp.getContext());
    }

    //update Risk
    public void updateGame(){
      
            
        System.out.println("Please enter the name of the game you would like to change:");
        String gameName = scanner.nextLine();

        System.out.println("Set new MinBet");
        double minBet = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Set new MaxBet");
        double maxBet = scanner.nextDouble();
        scanner.nextLine();
        System.out.println("Set new Risk Level (low,medium,high)");
        String risk = scanner.nextLine();




        Message req = new Message(Message.action.change_game);
        req.addGameName(gameName);
        req.add_payload(minBet,maxBet,risk);
        Message resp = communicate(req);
        System.out.println(resp.getContext());




    }

    
    //View Stats
    public void getStats(){
        
        System.out.println("View stats for: 1.Games | 2.Player");
        String choice = scanner.nextLine();
        ArrayList<Game> games=new ArrayList<>();
        if(choice.equals("1")){
            Message req = new Message(Message.action.game_profit);
            Message resp = communicate(req);
            games=resp.getSearchedGames();
            for (Game g:games){
                System.out.println("Game: "+g.getGameName()+", plus/minus: "+g.getProfit());
            }
        }
        else if (choice.equals("2")){
            Message req = new Message(Message.action.player_profit);
            Message resp = communicate(req);

            ArrayList<PlayerStats> players = resp.getPlayerStatsList();

            if (players.isEmpty()) {
                System.out.println("No player stats available.");
            } else {
                for (PlayerStats p : players) {
                    System.out.println(p.toString());
                }
                System.out.println();
            }
        }
        else {
            System.out.println("Invalid choice.");
        }

        
    }


    public Message communicate(Message obj){

        Message res=new Message(Message.action.empty);

        try{
            master=new Socket("localhost",master_port);
            out = new ObjectOutputStream(master.getOutputStream());
            in = new ObjectInputStream(master.getInputStream());

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