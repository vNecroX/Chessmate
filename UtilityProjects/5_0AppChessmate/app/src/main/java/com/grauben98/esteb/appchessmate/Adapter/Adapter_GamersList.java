package com.grauben98.esteb.appchessmate.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grauben98.esteb.appchessmate.POJO.POJO_GamersList;
import com.grauben98.esteb.appchessmate.R;

import java.util.List;

public class Adapter_GamersList extends RecyclerView.Adapter<Adapter_GamersList.GamersHolder> {

    private List<POJO_GamersList> list;

    public Adapter_GamersList(List<POJO_GamersList> list){
        this.list = list;
    }

    @NonNull
    @Override
    public GamersHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_gamerslist, viewGroup, false);
        return new GamersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GamersHolder gamersHolder, int i) {
        gamersHolder.tvGamerName.setText(list.get(i).getPlayerName());
        gamersHolder.tvNationName.setText(list.get(i).getNationName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class GamersHolder extends RecyclerView.ViewHolder{

        TextView tvGamer;
        TextView tvNation;
        TextView tvGamerName;
        TextView tvNationName;

        GamersHolder(View itemView){
            super(itemView);
            tvGamer = itemView.findViewById(R.id.tvPlayer);
            tvNation = itemView.findViewById(R.id.tvNation);
            tvGamerName = itemView.findViewById(R.id.tvPlayerName);
            tvNationName = itemView.findViewById(R.id.tvNationName);
        }
    }
}
