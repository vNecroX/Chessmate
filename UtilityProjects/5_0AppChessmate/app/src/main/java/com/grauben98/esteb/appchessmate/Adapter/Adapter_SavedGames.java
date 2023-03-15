package com.grauben98.esteb.appchessmate.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grauben98.esteb.appchessmate.POJO.POJO_SavedGames;
import com.grauben98.esteb.appchessmate.R;

import java.util.List;

public class Adapter_SavedGames extends RecyclerView.Adapter<Adapter_SavedGames.SavedGamesHolder>{

    private List<POJO_SavedGames> list;

    public Adapter_SavedGames(List<POJO_SavedGames> list){
        this.list = list;
    }

    @NonNull
    @Override
    public SavedGamesHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_savedgames, viewGroup, false);
        return new SavedGamesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedGamesHolder savedGamesHolder, int i) {
        savedGamesHolder.tvGameID.setText(list.get(i).getGameID());
        savedGamesHolder.tvStatus.setText(list.get(i).getStatus());
        savedGamesHolder.tvName.setText(list.get(i).getName());
        savedGamesHolder.tvPlayerOneName.setText(list.get(i).getPlayerOneName());
        savedGamesHolder.tvPlayerOnePieces.setText(list.get(i).getPlayerOnePieces());
        savedGamesHolder.tvPlayerOneStatus.setText(list.get(i).getPlayerOneStatus());
        savedGamesHolder.tvPlayerTwoName.setText(list.get(i).getPlayerTwoName());
        savedGamesHolder.tvPlayerTwoPieces.setText(list.get(i).getPlayerTwoPieces());
        savedGamesHolder.tvPlayerTwoStatus.setText(list.get(i).getPlayerTwoStatus());
        savedGamesHolder.tvMode.setText(list.get(i).getMode());
        savedGamesHolder.tvDifficulty.setText(list.get(i).getDifficulty());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class SavedGamesHolder extends RecyclerView.ViewHolder{

        TextView tvGameID;
        TextView tvStatus;
        TextView tvName;
        TextView tvPlayerOneName;
        TextView tvPlayerOnePieces;
        TextView tvPlayerOneStatus;
        TextView tvPlayerTwoName;
        TextView tvPlayerTwoPieces;
        TextView tvPlayerTwoStatus;
        TextView tvMode;
        TextView tvDifficulty;

        SavedGamesHolder(View itemView){
            super(itemView);
            tvGameID = itemView.findViewById(R.id.tvGameID);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvName = itemView.findViewById(R.id.tvName);
            tvPlayerOneName = itemView.findViewById(R.id.tvPlayerOneName);
            tvPlayerOnePieces = itemView.findViewById(R.id.tvPlayerOnePieces);
            tvPlayerOneStatus = itemView.findViewById(R.id.tvPlayerOneStatus);
            tvPlayerTwoName = itemView.findViewById(R.id.tvPlayerTwoName);
            tvPlayerTwoPieces = itemView.findViewById(R.id.tvPlayerTwoPieces);
            tvPlayerTwoStatus = itemView.findViewById(R.id.tvPlayerTwoStatus);
            tvMode = itemView.findViewById(R.id.tvMode);
            tvDifficulty = itemView.findViewById(R.id.tvDifficulty);
        }
    }
}
