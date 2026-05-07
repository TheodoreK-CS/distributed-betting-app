package com.example.tzogos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class mainActivity extends AppCompatActivity {
    private Player player = new Player();
    EditText tPlayerId, tBalance;
    Button enterButton, addBalanceButton;
    TextView balanceText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);

        tPlayerId = findViewById(R.id.text_player_id);
        tBalance = findViewById(R.id.text_balance_amount);
        enterButton = findViewById(R.id.enter_button);
        addBalanceButton = findViewById(R.id.add_balance_button);
        balanceText = findViewById(R.id.balance_text);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("player")) {
            Player temp = (Player) intent.getSerializableExtra("player");
            player = new Player(temp);
        }

        if (player.getPlayerId() != null) {
            tPlayerId.setText(player.getPlayerId());
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
            player.addBalance(amount);
            tBalance.setText("");
            updateBalanceText();
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount.", Toast.LENGTH_SHORT).show();
        }
    }

    public void click_enter(View v) {
        String id = tPlayerId.getText().toString().trim();
        if (id.isEmpty()) {
            Toast.makeText(this, "Please input your Player ID.", Toast.LENGTH_SHORT).show();
            return;
        }
        player.setPlayerId(id);

        Intent temp = new Intent(this, browse_games_activity.class);
        temp.putExtra("player", player);
        startActivity(temp);
    }
}
