package MessageEssentials;

import java.io.Serializable;

public class Filters implements Serializable{
    //custom object id so transfer doesn't crash because of small changes in the Filters class
    private static final long serialVersionUID = 1L;

    private String playerID;

    //the three types of filters

    //1-5 stars
    private int stars;

    //min bet is in the form of $, $$, $$$
    private String minBet;

    //risk level is low, medium or high
    private String riskLevel;

    private String category;

    Filters(String playerID, int stars, String minBet, String riskLevel, String category){
        this.playerID = playerID;
        this.stars = stars;
        this.minBet = minBet;
        this.riskLevel = riskLevel;

    }

    //getters
    public String getPlayerID(){
        return playerID;
    }

    public int getStars(){
        return stars;
    }

    public String getMinBet(){
        return minBet;
    }

    public String getRiskLevel(){
        return riskLevel;
    }

    public String getCategory(){
        return category;
    }
}