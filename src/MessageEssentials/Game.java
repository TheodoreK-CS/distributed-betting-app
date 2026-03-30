package MessageEssentials;

import java.io.Serializable;

public class Game implements Serializable{
    //custom object id so transfer doesn't crash because of small changes in the Game class
    private static final long serialVersionUID = 1L;

    private String gameName;
    private String providerId;
    private double Stars;
    private int NoOfVotes;
    private String GameLogo;
    private double MinBet;
    private double MaxBet;
    private String RiskLevel;
    private double profit;
    private String betCategory;
    private int jackpot;
    private Boolean is_Active = true;
    //for rng safety
    private String HashKey;
    private String safetyNumber;


    public Game(String gameName, String providerName, double stars, int noOfVotes, String gameLogoPath, double minBet, double maxBet, String riskLevel, String safetyNumber){
        this.gameName = gameName;
        this.providerId = providerName;
        this.Stars = stars;
        this.NoOfVotes = noOfVotes;
        this.GameLogo = gameLogoPath;
        this.MinBet = minBet;
        this.MaxBet = maxBet;
        this.RiskLevel = riskLevel;
        this.HashKey = safetyNumber;

        this.safetyNumber=HashKey;

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

    public void game_review(double star){

        NoOfVotes++;
        Stars = (Stars*(NoOfVotes-1)+star)/NoOfVotes;
    }

    //getters
    public String getGameName(){
        return gameName;
    }
    public String getProviderName(){
        return providerId;
    }
    public double getStars(){
        return Stars;
    }
    public int getNoOfVotes(){
        return NoOfVotes;
    }
    public String getGameLogoPath(){
        return GameLogo;
    }
    public double getMinBet(){
        return MinBet;
    }
    public double getMaxBet(){
        return MaxBet;
    }
    public String getRiskLevel(){
        return RiskLevel;
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
    public boolean ifActive(){
        return is_Active;
    }


    // setters

    public void setGameName(String gameName){
        this.gameName = gameName;
    }

    public void setProviderName(String providerName){
        this.providerId = providerName;
    }

    public void setStars(double stars){
        this.Stars = stars;
    }

    public void setNoOfVotes(int noOfVotes){
        this.NoOfVotes = noOfVotes;
    }

    public void setGameLogoPath(String gameLogoPath){
        this.GameLogo = gameLogoPath;
    }

    public void setMinBet(double minBet){
        this.MinBet = minBet;
        this.betCategory = calculateBetCategory(this.MinBet);
    }

    public void setMaxBet(double maxBet){
        this.MaxBet = maxBet;
    }

    public void setRiskLevel(String riskLevel){
        this.RiskLevel = riskLevel;
        this.jackpot = calculateJackpot(this.RiskLevel);
    }

    public void setSafetyNumber(String safetyNumber){
        this.safetyNumber = safetyNumber;
    }
    public void setActive(boolean is_Active){
        this.is_Active = is_Active;
    }

    public String toString(){
        String temp=gameName+"  "+Stars;
        return temp;
    }

    public void change_profit(double x){
        profit=profit+x;
    }
    public double getProfit(){
        return profit;
    }
}