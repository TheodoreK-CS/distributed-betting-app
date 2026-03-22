package RNG_Server;

public class Tuple {

    private final int winningNumber;
    private final String securityHash;

    public Tuple(int winningNumber, String securityHash) {
        this.winningNumber = winningNumber;
        this.securityHash = securityHash;
    }

    public int getWinningNumber(){
        return winningNumber;
    }

    public String getSecurityHash(){
        return securityHash;
    }

}
