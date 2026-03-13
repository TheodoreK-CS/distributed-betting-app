package MessageEssentials;

import java.io.Serializable;

public class Game implements Serializable{
    //custom object id so transfer doesn't crash because of small changes in the Game class
    private static final long serialVersionUID = 1L;

    private String gameName;
    private String providerName;
    private int stars;
    private int noOfVotes;
    private String gameLogoPath;
    private double minBet;
    private double maxBet;
    private String riskLevel;

    private String betCategory;
    private int jackpot;

    //for rng safety
    private String safetyNumber;

    Game(String gameName, String providerName, int stars, int noOfVotes, String gameLogoPath, double minBet, double maxBet, String riskLevel, String safetyNumber){
        this.gameName = gameName;
        this.providerName = providerName;
        this.stars = stars;
        this.noOfVotes = noOfVotes;
        this.gameLogoPath = gameLogoPath;
        this.minBet = minBet;
        this.maxBet = maxBet;
        this.riskLevel = riskLevel;
        this.safetyNumber = safetyNumber;

        //gets the bet category from calculateBetCategory method
        this.betCategory = calculateBetCategory(minBet);

        //sets the jackpot of the game
        this.jackpot = calculateJackpot(riskLevel);


    }

    //calculates the bet Category and returns it as "$", "$$", "$$$"
    private String calculateBetCategory(double minBet){

        //if minimum bet is 0.1 then $
        if(minBet<1){
            return "$";
        }
        //if minimum bet is 1 then $$
        else if(minBet<5){
            return "$$";
        }
        //if minimum bet is 5 then $$$
        else{
            return "$$$";
        }

    }

    //calculates jackpot based on risk level and returns jackpot value as an int
    private int calculateJackpot(String riskLevel){

        if(riskLevel.equals("low")){
            return 10;
        }else if(riskLevel.equals("medium")){
            return 20;
        }else if(riskLevel.equals("high")){
            return 40;
        }

        //will only return zero if riskLevel isn't properly named
        return 0;
    }

    //getters
    public String getGameName(){
        return gameName;
    }
    public String getProviderName(){
        return providerName;
    }
    public int getStars(){
        return stars;
    }
    public int getNoOfVotes(){
        return noOfVotes;
    }
    public String getGameLogoPath(){
        return gameLogoPath;
    }
    public double getMinBet(){
        return minBet;
    }
    public double getMaxBet(){
        return maxBet;
    }
    public String getRiskLevel(){
        return riskLevel;
    }
    public String getSafetyNumber(){
        return safetyNumber;
    }
    public String getBetCategory(){
        return betCategory;
    }
    public int getJackpot(){
        return jackpot;
    }

    // setters

    public void setGameName(String gameName){
        this.gameName = gameName;
    }

    public void setProviderName(String providerName){
        this.providerName = providerName;
    }

    public void setStars(int stars){
        this.stars = stars;
    }

    public void setNoOfVotes(int noOfVotes){
        this.noOfVotes = noOfVotes;
    }

    public void setGameLogoPath(String gameLogoPath){
        this.gameLogoPath = gameLogoPath;
    }

    public void setMinBet(double minBet){
        this.minBet = minBet;
        this.betCategory = calculateBetCategory(this.minBet);
    }

    public void setMaxBet(double maxBet){
        this.maxBet = maxBet;
    }

    public void setRiskLevel(String riskLevel){
        this.riskLevel = riskLevel;
        this.jackpot = calculateJackpot(this.riskLevel);
    }

    public void setSafetyNumber(String safetyNumber){
        this.safetyNumber = safetyNumber;
    }

}