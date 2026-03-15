package MessageEssentials;

import java.io.Serializable;

public class Bet implements Serializable{
    
    private String playerID;
    private double betAmount;

    Bet(String playerID, double betAmount){

        this.playerID = playerID;
        this.betAmount = betAmount;
        
    }

    public String getPlayerID(){
        return playerID;
    }

    public double getBetAmount(){
        return betAmount;
    }

}
