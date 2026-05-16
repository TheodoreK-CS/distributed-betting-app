package com.example.tzogos;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class filters_activity extends AppCompatActivity {

    private Player player;
    private ArrayList<String> providers = new ArrayList<>();

    Button applyButton;
    ImageButton backButton, providerB, rateLp, rateLm, rateHp, rateHm, riskB, betCatB;
    TextView provider, rating, risk, riskValue, betCat, betCatValue, ratelow, ratehigh;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filters);

        applyButton = findViewById(R.id.apply_button);
        backButton = findViewById(R.id.back_button);
        providerB = findViewById(R.id.menu_provider_button);
        rateLp = findViewById(R.id.plus_button1);
        rateLm = findViewById(R.id.minus_button1);
        rateHp = findViewById(R.id.plus_button2);
        rateHm = findViewById(R.id.minus_button2);
        riskB = findViewById(R.id.menu_risk_button);
        betCatB = findViewById(R.id.menu_bet_category_button);

        provider = findViewById(R.id.provider);
        rating = findViewById(R.id.rating);
        risk = findViewById(R.id.risk);
        riskValue = findViewById(R.id.risk_value);
        betCat = findViewById(R.id.bet_category);
        betCatValue = findViewById(R.id.bet_category_value);
        ratelow = findViewById(R.id.low_stars);
        ratehigh = findViewById(R.id.high_stars);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("player")) {
            Player temp = (Player) intent.getSerializableExtra("player");
            player = new Player(temp);
        } else {
            player = new Player();
        }

        providers = player.getProviders();
        ratelow.setText(String.valueOf((float) player.getStarsLow()));
        ratehigh.setText(String.valueOf((float) player.getStarsHigh()));
        riskValue.setText(player.getRisk());
        betCatValue.setText(player.getBetCategory());

        providerB.setOnClickListener(this::showProviderMenu);
        riskB.setOnClickListener(this::showRiskMenu);
        betCatB.setOnClickListener(this::showBetCategoryMenu);

        rateLp.setOnClickListener(v -> {
            double x = Double.parseDouble(ratelow.getText().toString());
            if (x < 5) x += 0.5;
            ratelow.setText(String.valueOf((float) x));
        });

        rateLm.setOnClickListener(v -> {
            double x = Double.parseDouble(ratelow.getText().toString());
            if (x > 0) x -= 0.5;
            ratelow.setText(String.valueOf((float) x));
        });

        rateHp.setOnClickListener(v -> {
            double x = Double.parseDouble(ratehigh.getText().toString());
            if (x < 5) x += 0.5;
            ratehigh.setText(String.valueOf((float) x));
        });

        rateHm.setOnClickListener(v -> {
            double x = Double.parseDouble(ratehigh.getText().toString());
            if (x > 0) x -= 0.5;
            ratehigh.setText(String.valueOf((float) x));
        });
    }

    private void showProviderMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(filters_activity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.provider_menu, popupMenu.getMenu());
        int green = ContextCompat.getColor(this, R.color.green);
        Menu menu = popupMenu.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            String key = mi.getTitle().toString();
            if (providers.contains(key)) {
                SpannableString s = new SpannableString(mi.getTitle());
                s.setSpan(new ForegroundColorSpan(green), 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                mi.setTitle(s);
            }
        }

        popupMenu.setOnMenuItemClickListener(item -> {
            String key = item.getTitle().toString();
            if (key.equalsIgnoreCase("all")) {
                providers.clear();
                providers.add("all");
            } else {
                providers.remove("all");
                if (!providers.remove(key)) providers.add(key);
                if (providers.isEmpty()) providers.add("all");
            }
            return false;
        });
        popupMenu.show();
    }

    private void showRiskMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(filters_activity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.riskmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.risk_low) riskValue.setText("low");
            if (item.getItemId() == R.id.risk_medium) riskValue.setText("medium");
            if (item.getItemId() == R.id.risk_high) riskValue.setText("high");
            if (item.getItemId() == R.id.risk_all) riskValue.setText("all");
            return false;
        });
        popupMenu.show();
    }

    private void showBetCategoryMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(filters_activity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.betcategorymenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.bet_one) betCatValue.setText("$");
            if (item.getItemId() == R.id.bet_two) betCatValue.setText("$$");
            if (item.getItemId() == R.id.bet_three) betCatValue.setText("$$$");
            if (item.getItemId() == R.id.bet_all) betCatValue.setText("all");
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

    public void click_apply(View v) {
        double low = Double.parseDouble(ratelow.getText().toString());
        double high = Double.parseDouble(ratehigh.getText().toString());
        if (low > high) {
            double temp = low;
            low = high;
            high = temp;
        }

        player.setStarsRange(low, high);
        player.setRisk(riskValue.getText().toString());
        player.setBetCategory(betCatValue.getText().toString());
        player.setProviders(providers);

        Intent temp = new Intent(this, browse_games_activity.class);
        temp.putExtra("player", player);
        temp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(temp);
    }
}
