package MessageEssentials;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_pairing;
    private String playerId;
    private String gameName, Risk, Category;
    private Game game;
    private double stars, LowStars, HighStars, betAmount, minBet, maxBet, payout;
    private String context;
    private ArrayList<Game> games = new ArrayList<>();
    private ArrayList<String> Providers = new ArrayList<>();

    public enum action {
        add_game,
        remove_game,
        search_games,
        search_results,
        play_bet,
        init_random,
        delete_random,
        request_random,
        random_result,
        send_map_data,
        change_game,
        game_profit,
        player_profit,
        review_game,
        empty,
        reducer_resp,
    }

    private action message_action;

    public Message(action message_action, String playerId) {
        this.message_action = message_action;
        this.playerId = playerId;
    }

    public Message(action message_action) {
        this.message_action = message_action;
    }

    public action getMessageAction() { return message_action; }
    public int get_id() { return id_pairing; }
    public void set_id(int id) { this.id_pairing = id; }
    public String getPlayerId() { return playerId; }
    public void setPlayerId(String playerId) { this.playerId = playerId; }
    public void addContext(String context) { this.context = context; }
    public String getContext() { return context; }
    public String get_name() { return gameName; }
    public void addGameName(String gameName) { this.gameName = gameName; }
    public Game get_game() { return game; }
    public void add_Game(Game game) { this.game = game; this.gameName = game.getGameName(); }
    public double getStar() { return stars; }
    public void setStars(double stars) { this.stars = stars; }
    public void setSearchedGames(ArrayList<Game> games) { this.games = games; }
    public ArrayList<Game> getSearchedGames() { return games; }
    public ArrayList<String> getProviders() { return Providers; }
    public Double getLowStars() { return LowStars; }
    public Double getHighStars() { return HighStars; }
    public String getRisk() { return Risk; }
    public String getCategory() { return Category; }

    public void add_payload(ArrayList<String> Providers, double lowstars, double highstars, String risk, String Category) {
        this.Providers = Providers;
        this.LowStars = lowstars;
        this.HighStars = highstars;
        this.Risk = risk;
        this.Category = Category;
    }

    public void add_payload(String gameName, double betAmount) {
        this.gameName = gameName;
        this.betAmount = betAmount;
    }

    public void add_payload(double minBet, double maxBet, String Risk) {
        this.minBet = minBet;
        this.maxBet = maxBet;
        this.Risk = Risk;
    }

    public Double get_minBet() { return minBet; }
    public Double get_maxBet() { return maxBet; }
    public String get_risk() { return Risk; }
    public Double get_betAmount() { return betAmount; }
    public void setPayout(double payout) { this.payout = payout; }
    public double getPayout() { return payout; }
}
