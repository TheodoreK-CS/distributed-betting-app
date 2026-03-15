package MessageEssentials;

import java.io.Serializable;

public class Message implements Serializable{

    //custom object id so transfer doesn't crash because of small changes in the Message class
    private static final long serialVersionUID = 1L;

    //who is sending the message
    public enum sender {
        //these are all final variables
        manager, player, master, worker, reducer, rng
    }

    //what is the message carrying?
    public enum action {
        //these are also all final variables

        //will have Game object payload
        add_game,        // Manager -> Master

        //will have Game object payload
        remove_game,     // Manager -> Master

        //will have Filters object payload
        search_games,    // Player  -> Master

        //will have ArrayList<Game> object payload
        search_results,  // Reducer -> Master / Master -> Player

        //will have Bet object payload
        play_bet,        // Player  -> Master -> Worker

        //will have
        request_random,  // Worker  -> RNG

        send_map_data,   // Worker  -> Reducer

        change_game, 
        
        game_profit,

        player_profit,

        review_game,
    }

    //these values must be from the list of senders and actions
    private sender message_sender;
    private action message_action;

    //the payload can take the form of anything we want to send
    private Object payload;

    public Message(sender message_sender, action message_action, Object payload){

        this.message_sender = message_sender;
        this.message_action = message_action;
        this.payload = payload;

    }

    public action getMessageAction() {
        return message_action;
    }

    public Object getPayload(){
        return payload;
    }
}
