package MessageEssentials;

import java.io.Serializable;

public class Message implements Serializable{

    //custom object id so transfer doesn't crash because of small changes in the Message class
    private static final long serialVersionUID = 1L;
    private int id_pairing;


    //what is the message carrying?
    public enum action {
        //these are also all final variables

        //will have Game object payload(see Game class)
        add_game,        // Manager -> Master

        //will have Game object payload(see Game class)
        remove_game,     // Manager -> Master

        //will have Filters object payload(see Filters class)
        search_games,    // Player  -> Master

        //will have ArrayList<Game> object payload
        search_results,  // Reducer -> Master / Master -> Player

        //will have Bet object payload(see Bet class)
        play_bet,        // Player  -> Master -> Worker

        //doesn't need payload only the action
        request_random,  // Worker  -> RNG

        //will have RandomResult object payload(see RandomResult class)
        random_result,

        send_map_data,   // Worker  -> Reducer

        change_game,
        
        //will have Filters object payload(see Filters class) with constructor that only has provider name as the id variable
        game_profit,

        //will have Filters object payload(see Filters class) with constructor that only has player id as the id variable
        player_profit,

        
        review_game,
    }

    //these values must be from the list of senders and actions

    private action message_action;

    //the payload can take the form of anything we want to send
    private Object payload;

    //message with payload
    public Message(action message_action, Object payload){

        this.message_action = message_action;
        this.payload = payload;

    }

    //message without payload for simple commands that don't need payload(example: request_random, )
    public Message(action message_action){

        this.message_action = message_action;

    }

    public action getMessageAction() {
        return message_action;
    }

    public Object getPayload(){
        return payload;
    }

    public int get_id(){
        return id_pairing;
    }

    public void set_id(int id){
        this.id_pairing = id;
    }
}
