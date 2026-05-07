package com.example.tzogos;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerId;
    private double balance;
    private ArrayList<String> providers = new ArrayList<>();
    private double starsLow = 0.0;
    private double starsHigh = 5.0;
    private String risk = "all";
    private String betCategory = "all";

    public Player() {
        this.playerId = "";
        this.balance = 0.0;
        providers.add("all");
    }

    public Player(String playerId) {
        this();
        this.playerId = playerId;
    }

    public Player(Player other) {
        this.playerId = other.playerId;
        this.balance = other.balance;
        this.providers = new ArrayList<>(other.providers);
        this.starsLow = other.starsLow;
        this.starsHigh = other.starsHigh;
        this.risk = other.risk;
        this.betCategory = other.betCategory;
    }

    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }

    public double getBalance() { return balance; }
    public void addBalance(double amount) {
        if (amount > 0) balance += amount;
    }
    public void applyBetResult(double betAmount, double payout) {
        balance = balance - betAmount + payout;
        if (balance < 0) balance = 0;
    }

    public ArrayList<String> getProviders() { return new ArrayList<>(providers); }
    public void setProviders(ArrayList<String> providers) {
        this.providers.clear();
        if (providers == null || providers.isEmpty()) {
            this.providers.add("all");
        } else {
            this.providers.addAll(providers);
        }
    }

    public double getStarsLow() { return starsLow; }
    public double getStarsHigh() { return starsHigh; }
    public void setStarsRange(double low, double high) {
        this.starsLow = low;
        this.starsHigh = high;
    }

    public String getRisk() { return risk; }
    public void setRisk(String risk) { this.risk = risk; }

    public String getBetCategory() { return betCategory; }
    public void setBetCategory(String betCategory) { this.betCategory = betCategory; }
}
