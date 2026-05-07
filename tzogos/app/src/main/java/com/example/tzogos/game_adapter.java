package com.example.tzogos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class game_adapter extends RecyclerView.Adapter<game_adapter.myViewHolder> {

    Context context;
    Player player;
    ArrayList<game_model> games;

    public game_adapter(Context context, ArrayList<game_model> games, Player player) {
        this.context = context;
        this.games = games;
        this.player = player;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.game_row, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        game_model model = games.get(position);

        holder.gameName.setText(model.getName());
        holder.gameRating.setText(model.getRating() + "★");
        holder.gameProvider.setText("Provider: " + model.getProvider());
        holder.gameRisk.setText("Risk: " + model.getRisk());
        holder.gameBets.setText("Bet: " + model.getMinBet() + " - " + model.getMaxBet() + "  " + model.getBetCategory());

        holder.itemView.setOnClickListener(v -> {
            Intent temp = new Intent(context, game_activity.class);
            temp.putExtra("game", model.getGame());
            temp.putExtra("player", player);
            context.startActivity(temp);
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {
        TextView gameName, gameRating, gameProvider, gameRisk, gameBets;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            gameName = itemView.findViewById(R.id.game_name);
            gameRating = itemView.findViewById(R.id.game_rating);
            gameProvider = itemView.findViewById(R.id.game_provider);
            gameRisk = itemView.findViewById(R.id.game_risk);
            gameBets = itemView.findViewById(R.id.game_bets);
        }
    }
}
