package com.example.tzogos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class balance_activity extends AppCompatActivity {

    private Player player;
    TextView balanceText;
    EditText tBalance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.balance_page);

        balanceText = findViewById(R.id.balance_text);
        tBalance = findViewById(R.id.text_balance_amount);

        // Catch the player object from the previous screen
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("player")) {
            Player temp = (Player) intent.getSerializableExtra("player");
            player = new Player(temp);
        } else {
            player = new Player();
        }

        updateBalanceText();
    }

    private void updateBalanceText() {
        balanceText.setText("Balance: " + String.format("%.2f", player.getBalance()));
    }

    public void click_addBalance(View v) {
        String amountText = tBalance.getText().toString().trim();
        if (amountText.isEmpty()) {
            Toast.makeText(this, "Give token amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                Toast.makeText(this, "Amount must be positive.", Toast.LENGTH_SHORT).show();
                return;
            }
            // Add the balance to the player object
            player.addBalance(amount);
            tBalance.setText(""); // Clear the input field
            updateBalanceText(); // Update the text on screen
            Toast.makeText(this, "Tokens added!", Toast.LENGTH_SHORT).show();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount.", Toast.LENGTH_SHORT).show();
        }
    }

    public void click_back(View v) {
        // Send the updated player (with new balance) back to the Browse screen
        Intent temp = new Intent(this, browse_games_activity.class);
        temp.putExtra("player", player);
        temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(temp);
    }
}