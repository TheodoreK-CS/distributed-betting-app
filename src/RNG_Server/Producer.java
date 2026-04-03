package RNG_Server;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Producer extends Thread{

    private final Buffer buffer;
    private final String secretS;
    private final SecureRandom secureRandom;

    public Producer(Buffer buffer, String secretS){

        this.buffer = buffer;
        this.secretS = secretS;

        this.secureRandom = new SecureRandom(); 
    }

    @Override
    public void run(){

        while (!Thread.interrupted()){
            try {
                //generate random number
                int winningNumber = secureRandom.nextInt(100);

                //combine number and secret to get them ready for hashing
                String rawData = winningNumber + secretS;
                    
                //hash
                String hash = calculateSHA256(rawData);
                    
                //put number and hash in tuple
                Tuple pair = new Tuple(winningNumber, hash);
                    
                //put tuple in buffer in last place
                buffer.put(pair);
            }catch (Exception e) {
                break;
            }
        }
    }


    private String calculateSHA256(String input) {
        try {
            //get sha256 algorithm
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            //use algorithm on the bytes and put them in a byte array
            byte[] hashBytes = digest.digest(input.getBytes());
            
            //make hashed bytes into a string
            StringBuilder hexString = new StringBuilder(2 * hashBytes.length);
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm missing!", e);
        }
    }

}
