package com.grauben98.esteb.appchessmate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.grauben98.esteb.appchessmate.Adapter.Adapter_SavedGames;
import com.grauben98.esteb.appchessmate.POJO.POJO_SavedGames;

import java.util.ArrayList;
import java.util.List;

public class View_SavedGames extends AppCompatActivity {

    Button btnEditGameName, btnRestartGame, btnReviewGame, btnDeleteGame;
    EditText etSavedGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_savedgames);

        RecyclerView rvSavedGames;
        List<POJO_SavedGames> list;
        Adapter_SavedGames adapter_savedGames;

        list = new ArrayList<>();

        btnEditGameName = findViewById(R.id.btnEditGameName);
        btnRestartGame = findViewById(R.id.btnRestartGame);
        btnReviewGame = findViewById(R.id.btnReviewGame);
        btnDeleteGame = findViewById(R.id.btnDeleteGame);
        etSavedGames = findViewById(R.id.etSavedGames);

        rvSavedGames = findViewById(R.id.rvSavedGames);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvSavedGames.setLayoutManager(linearLayoutManager);

        for(int i = 0; i < 15; i++){
            POJO_SavedGames pojo_savedGames = new POJO_SavedGames();
            pojo_savedGames.setGameID("ID");
            pojo_savedGames.setStatus("Finalizada");
            pojo_savedGames.setName("Partida 1");
            pojo_savedGames.setPlayerOneName("jorgedavalos26");
            pojo_savedGames.setPlayerOnePieces("Blanco");
            pojo_savedGames.setPlayerOneStatus("Ganador");
            pojo_savedGames.setPlayerTwoName("noobmaster69");
            pojo_savedGames.setPlayerTwoPieces("Negro");
            pojo_savedGames.setPlayerTwoStatus("Perdedor");
            pojo_savedGames.setMode("PvP Online");
            pojo_savedGames.setDifficulty("--");
            list.add(pojo_savedGames);
        }

        adapter_savedGames = new Adapter_SavedGames(list);
        rvSavedGames.setAdapter(adapter_savedGames);
    }
}
