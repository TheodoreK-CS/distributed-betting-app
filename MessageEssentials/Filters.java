package MessageEssentials;

import java.io.Serializable;

public class Filters implements Serializable{
    //custom object id so transfer doesn't crash because of small changes in the Filters class
    private static final long serialVersionUID = 1L;

    //used as player id for filter search and gain/loss search based on player or used as provider name for gain/loss search based on manager
    private String id;

    //the three types of filters

    //1-5 stars
    private int stars;

    //min bet is in the form of $, $$, $$$
    private String minBet;

    //risk level is low, medium or high
    private String riskLevel;

    private String category;

    Filters(String id, int stars, String minBet, String riskLevel, String category){
        this.id = id;
        this.stars = stars;
        this.minBet = minBet;
        this.riskLevel = riskLevel;

    }

    //for gain/loss manager search based on playerID or provider name
    Filters(String id){
        this.id = id;
    }

    //getters
    public String getID(){
        return id;
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