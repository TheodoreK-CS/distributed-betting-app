package com.example.tzogos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import MessageEssentials.Game;
import MessageEssentials.Message;

public class game_activity extends AppCompatActivity {

    private Player player;
    private Game game;
    private Message response;

    TextView gName, gRating, gProvider, gRisk, gBetCategory, gMinBet, gMaxBet, gBalance, gResult;
    EditText betAmount;
    Button playButton;
    ImageButton backButton;
    final Activity thisActivity = this;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_page);

        gName = findViewById(R.id.gname);
        gRating = findViewById(R.id.grating);
        gProvider = findViewById(R.id.gprovider);
        gRisk = findViewById(R.id.grisk);
        gBetCategory = findViewById(R.id.gbet_category);
        gMinBet = findViewById(R.id.gmin_bet);
        gMaxBet = findViewById(R.id.gmax_bet);
        gBalance = findViewById(R.id.gbalance);
        gResult = findViewById(R.id.gresult);
        betAmount = findViewById(R.id.bet_amount);
        playButton = findViewById(R.id.play_button);
        backButton = findViewById(R.id.back_button_game);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("player") && intent.hasExtra("game")) {
            Player temp = (Player) intent.getSerializableExtra("player");
            player = new Player(temp);
            game = (Game) intent.getSerializableExtra("game");
        } else {
            player = new Player();
        }

        if (game != null) {
            gName.setText(game.getGameName());
            gRating.setText(game.getStars() + "★");
            gProvider.setText("Provider: " + game.getProviderName());
            gRisk.setText("Risk: " + game.getRiskLevel());
            gBetCategory.setText("Bet Category: " + game.getBetCategory());
            gMinBet.setText("Min Bet: " + game.getMinBet());
            gMaxBet.setText("Max Bet: " + game.getMaxBet());
        }
        updateBalanceText();
    }

    private void updateBalanceText() {
        gBalance.setText("Balance: " + String.format("%.2f", player.getBalance()));
    }

    public void click_playBet(View v) {
        if (game == null) return;

        String amountText = betAmount.getText().toString().trim();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Give bet amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid bet amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Bet must be positive.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (amount > player.getBalance()) {
            Toast.makeText(this, "Not enough balance.", Toast.LENGTH_SHORT).show();
            return;
        }

        Message req = new Message(Message.action.play_bet, player.getPlayerId());
        req.add_payload(game.getGameName(), amount);

        new Thread(() -> {
            try {
                response = NetworkClient.send(req);
                double payout = response.getPayout();
                player.applyBetResult(amount, payout);

                thisActivity.runOnUiThread(() -> {
                    updateBalanceText();
                    gResult.setText(response.getContext() + "\nNew balance: " + String.format("%.2f", player.getBalance()));
                    Toast.makeText(thisActivity, response.getContext(), Toast.LENGTH_SHORT).show();

                    int color = ContextCompat.getColor(thisActivity, R.color.color_1);
                    gRating.setBackgroundColor(color);
                    gRating.setClickable(true);
                    gRating.setFocusable(true);
                    gRating.setOnClickListener(this::showRatingMenu);
                });
            } catch (Exception e) {
                Log.e("GameActivity", "Play bet failed", e);
                thisActivity.runOnUiThread(() -> Toast.makeText(thisActivity, "Could not connect to server.", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private void showRatingMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(game_activity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.ratingmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            String rate = "0.0";
            if (item.getItemId() == R.id.rate_one) rate = "1.0";
            if (item.getItemId() == R.id.rate_two) rate = "2.0";
            if (item.getItemId() == R.id.rate_three) rate = "3.0";
            if (item.getItemId() == R.id.rate_four) rate = "4.0";
            if (item.getItemId() == R.id.rate_five) rate = "5.0";

            Message req = new Message(Message.action.review_game, player.getPlayerId());
            req.addGameName(game.getGameName());
            req.setStars(Double.parseDouble(rate));

            new Thread(() -> {
                try {
                    Message resp = NetworkClient.send(req);
                    thisActivity.runOnUiThread(() -> Toast.makeText(thisActivity, resp.getContext(), Toast.LENGTH_SHORT).show());
                } catch (Exception e) {
                    Log.e("GameActivity", "Review failed", e);
                    thisActivity.runOnUiThread(() -> Toast.makeText(thisActivity, "Could not send review.", Toast.LENGTH_SHORT).show());
                }
            }).start();

            gRating.setClickable(false);
            gRating.setFocusable(false);
            int color = ContextCompat.getColor(thisActivity, R.color.background_primary);
            gRating.setBackgroundColor(color);

            return false;
        });
        popupMenu.show();
    }

    public void click_back(View v) {
        Intent temp = new Intent(this, browse_games_activity.class);
        temp.putExtra("player", player);
        temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(temp);
    }
}
