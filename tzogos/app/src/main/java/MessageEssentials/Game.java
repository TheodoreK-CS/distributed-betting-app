package MessageEssentials;

import java.io.Serializable;

public class Game implements Serializable {
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
    private String HashKey;
    private String safetyNumber;

    public Game(String gameName, String providerName, double stars, int noOfVotes, String gameLogoPath,
                double minBet, double maxBet, String riskLevel, String safetyNumber) {
        this.gameName = gameName;
        this.providerId = providerName;
        this.Stars = stars;
        this.NoOfVotes = noOfVotes;
        this.GameLogo = gameLogoPath;
        this.MinBet = minBet;
        this.MaxBet = maxBet;
        this.RiskLevel = riskLevel;
        this.HashKey = safetyNumber;
        this.safetyNumber = HashKey;
        this.betCategory = calculateBetCategory(minBet);
        this.jackpot = calculateJackpot(riskLevel);
    }

    private String calculateBetCategory(double minBet) {
        if (minBet < 1) return "$";
        else if (minBet < 5) return "$$";
        else return "$$$";
    }

    private int calculateJackpot(String riskLevel) {
        if (riskLevel.equals("low")) return 10;
        if (riskLevel.equals("medium")) return 20;
        if (riskLevel.equals("high")) return 40;
        return 0;
    }

    public void game_review(double star) {
        NoOfVotes++;
        Stars = (Stars * (NoOfVotes - 1) + star) / NoOfVotes;
    }

    public String getGameName() { return gameName; }
    public String getProviderName() { return providerId; }
    public double getStars() { return Math.round(Stars * 100.0) / 100.0; }
    public int getNoOfVotes() { return NoOfVotes; }
    public String getGameLogoPath() { return GameLogo; }
    public double getMinBet() { return MinBet; }
    public double getMaxBet() { return MaxBet; }
    public String getRiskLevel() { return RiskLevel; }
    public String getSafetyNumber() { return safetyNumber; }
    public String getBetCategory() { return betCategory; }
    public int getJackpot() { return jackpot; }
    public boolean ifActive() { return is_Active; }

    public void setActive(boolean active) { is_Active = active; }
    public void change_profit(double x) { profit = profit + x; }
    public double getProfit() { return profit; }

    public String toString() {
        return gameName + "  " + getStars() + "\nminBet: " + MinBet + "  maxBet: " + MaxBet + "\nProvider: " + providerId + "\n";
    }
}
