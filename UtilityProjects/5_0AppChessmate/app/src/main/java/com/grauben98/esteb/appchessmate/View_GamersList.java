package com.grauben98.esteb.appchessmate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;

import com.grauben98.esteb.appchessmate.Adapter.Adapter_GamersList;
import com.grauben98.esteb.appchessmate.POJO.POJO_GamersList;

import java.util.ArrayList;
import java.util.List;

public class View_GamersList extends AppCompatActivity {

    Button btnInviteGamer;
    EditText etGamersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gamerslist);

        RecyclerView rvGamersList;
        List<POJO_GamersList> list;
        Adapter_GamersList adapter_gamersList;

        list = new ArrayList<>();

        btnInviteGamer = findViewById(R.id.btnInvitePlayer);
        etGamersList = findViewById(R.id.etGamersList);
        rvGamersList = findViewById(R.id.rvGamersList);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvGamersList.setLayoutManager(linearLayoutManager);

        for(int i = 0; i < 15; i++){
            POJO_GamersList pojo_gamersList = new POJO_GamersList();
            pojo_gamersList.setPlayerName("Jorgais26");
            pojo_gamersList.setNationName("Alemania");
            list.add(pojo_gamersList);
        }

        adapter_gamersList = new Adapter_GamersList(list);
        rvGamersList.setAdapter(adapter_gamersList);
    }
}
