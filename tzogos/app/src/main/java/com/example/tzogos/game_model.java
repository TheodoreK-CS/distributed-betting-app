package com.example.tzogos;

import java.io.Serializable;

import MessageEssentials.Game;

public class game_model implements Serializable {
    private static final long serialVersionUID = 1L;
    private Game game;

    public game_model(Game game) {
        this.game = game;
    }

    public Game getGame() { return game; }
    public String getName() { return game.getGameName(); }
    public String getProvider() { return game.getProviderName(); }
    public String getRating() { return String.valueOf(game.getStars()); }
    public String getRisk() { return game.getRiskLevel(); }
    public String getBetCategory() { return game.getBetCategory(); }
    public String getMinBet() { return String.valueOf(game.getMinBet()); }
    public String getMaxBet() { return String.valueOf(game.getMaxBet()); }
}
