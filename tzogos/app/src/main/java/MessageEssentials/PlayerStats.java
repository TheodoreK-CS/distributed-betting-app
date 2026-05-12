package MessageEssentials;

import java.io.Serializable;

public class PlayerStats implements Serializable {
    private static final long serialVersionUID = 1L;

    private String playerId;
    private double netAmount;

    public PlayerStats(String playerId) {
        this.playerId = playerId;
        this.netAmount = 0.0;
    }

    public String getPlayerId() {
        return playerId;
    }

    public double getNetAmount() {
        return netAmount;
    }

    public void updateNet(double betAmount, double payout) {
        netAmount += (payout - betAmount);
    }

    @Override
    public String toString() {
        String sign = netAmount >= 0 ? "+" : "";
        return "player: " + playerId + " , stats: " + sign + netAmount;
    }
}