package RNG_Server;

import java.util.LinkedList;

public class Buffer {

    //buffer list
    private final LinkedList<Tuple> buffer = new LinkedList<>();
    
    //buffer capacity
    private final int MAX_CAPACITY = 10; 

    //used by producer for new numbers
    public synchronized void put(Tuple pair) {
        try {

            while (buffer.size() == MAX_CAPACITY) {
                //if buffer full dont produce
                wait(); 
            }
            
            //add new number and hashed number at the end of list
            buffer.addLast(pair);
            
            //wake up consumer that might be waiting
            notifyAll(); 
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Producer was interrupted.");
        }
    }

    //used by consumer to get number
    public synchronized Tuple get() {
        try {
            //if no numbers available ait for producer
            while (buffer.isEmpty()) {
                wait();
            }
            
            //remove oldest number
            Tuple pair = buffer.removeFirst();
            
            //notify producer that it can produce
            notifyAll(); 
            
            //return number and hashed number
            return pair;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Consumer was interrupted.");
            return null;
        }
    }
}
