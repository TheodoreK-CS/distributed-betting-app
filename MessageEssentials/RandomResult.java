package MessageEssentials;

import java.io.Serializable;

public class RandomResult implements Serializable{
    //custom object id so transfer doesn't crash because of small changes in the Filters class
    private static final long serialVersionUID = 1L;

    //the random number that gets returned
    private int winningNumber;

    //security number
    private String securityHash;


    public RandomResult(int winningNumber, String securityHash) {
        this.winningNumber = winningNumber;
        this.securityHash = securityHash;
    }

    //getters
    public int getWinningNumber() { return winningNumber; }
    public String getSecurityHash() { return securityHash; }

}
