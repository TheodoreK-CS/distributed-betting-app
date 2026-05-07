package com.example.tzogos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import MessageEssentials.Game;
import MessageEssentials.Message;

public class browse_games_activity extends AppCompatActivity {

    private Player player = new Player();
    private ArrayList<Game> games = new ArrayList<>();
    private ArrayList<game_model> gameModels = new ArrayList<>();

    private RecyclerView recGames;
    private ImageButton filterButton, refreshButton;
    private Button balanceButton;
    private TextView welcome;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitymain);

        recGames = findViewById(R.id.game_recycle);
        filterButton = findViewById(R.id.filter_button);
        refreshButton = findViewById(R.id.refresh_button);
        balanceButton = findViewById(R.id.balance_button);
        welcome = findViewById(R.id.welcome_text);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("player")) {
            Player temp = (Player) intent.getSerializableExtra("player");
            player = new Player(temp);
        }

        welcome.setText("Welcome " + player.getPlayerId());
        balanceButton.setText("Balance\n" + String.format("%.2f", player.getBalance()));

        fetchGames();
    }

    private void fetchGames() {
        final Context ctx = browse_games_activity.this;
        final Activity thisActivity = this;

        new Thread(() -> {
            try {
                Message req = new Message(Message.action.search_games, player.getPlayerId());
                req.add_payload(
                        player.getProviders(),
                        player.getStarsLow(),
                        player.getStarsHigh(),
                        player.getRisk(),
                        player.getBetCategory()
                );

                Message response = NetworkClient.send(req);
                games.clear();
                if (response.getSearchedGames() != null) {
                    for (Game g : response.getSearchedGames()) {
                        if (g.ifActive()) games.add(g);
                    }
                }
            } catch (Exception e) {
                Log.e("BrowseGames", "TCP fetch failed", e);
                thisActivity.runOnUiThread(() -> Toast.makeText(ctx, "Could not connect to server.", Toast.LENGTH_SHORT).show());
            }

            thisActivity.runOnUiThread(() -> {
                gameModels.clear();
                setGameModels();
                game_adapter adapter = new game_adapter(ctx, gameModels, player);
                recGames.setAdapter(adapter);
                recGames.setLayoutManager(new LinearLayoutManager(ctx));
            });
        }).start();
    }

    private void setGameModels() {
        for (Game g : games) {
            gameModels.add(new game_model(g));
        }
    }

    public void click_balance(View v) {
        Intent temp = new Intent(this, mainActivity.class);
        temp.putExtra("player", player);
        startActivity(temp);
    }

    public void click_filters(View v) {
        Intent temp = new Intent(this, filters_activity.class);
        temp.putExtra("player", player);
        startActivity(temp);
    }

    public void click_refresh(View v) {
        fetchGames();
    }
}
