package com.jorjaiz.chessmateapplicationv1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import java.util.HashMap;

public class View_Difficulty extends AppCompatActivity implements Constants, FireQuery.OnResponseFireQuery
{
    ParamsGame pG;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__difficulty);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_DIFFICULTY ON CREATE");

            Bundle parameters = getIntent().getExtras();
            pG = parameters.getParcelable("pG");

            findViewById(R.id.tvEasy).setOnClickListener(view -> gotoPlay(EASY));
            findViewById(R.id.tvMedium).setOnClickListener(view -> gotoPlay(INTERMEDIATE));
            findViewById(R.id.tvHard).setOnClickListener(view -> gotoPlay(DIFFICULT));

            findViewById(R.id.btnPawn).setOnClickListener(view -> gotoPlay(EASY));
            findViewById(R.id.btnBishop).setOnClickListener(view -> gotoPlay(INTERMEDIATE));
            findViewById(R.id.btnKing).setOnClickListener(view -> gotoPlay(DIFFICULT));

            findViewById(R.id.btnEasyInfo).setOnClickListener(view -> gotoDifficultyInfo(EASY));
            findViewById(R.id.btnMediumInfo).setOnClickListener(view -> gotoDifficultyInfo(INTERMEDIATE));
            findViewById(R.id.btnHardInfo).setOnClickListener(view -> gotoDifficultyInfo(DIFFICULT));

            pG.setOponentName("ia");
            pG.setIdOponent(1);
            pG.setKindPlayer(MASTER);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_DIFFICULTY onCreate: " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, View_MainInterface.class);
        i.putExtra("idPlayer", CP.get().getIdPlayer());
        i.putExtra("namePlayer", CP.get().getNamePlayer());
        this.startActivity(i);
    }

    public void gotoPlay(int difficulty)
    {
        try
        {
            pG.setDifficulty(difficulty);

            Intent i = new Intent(this, View_PiecesColor.class);
            i.putExtra("pG", pG);
            this.startActivity(i);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_DIFFICULTY gotoPlay: " + e.toString());
        }
    }

    public void gotoDifficultyInfo(int difficulty)
    {
        Bundle b = new Bundle();
        b.putInt("difficulty", difficulty);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment_InfoDifficulty frag = new Fragment_InfoDifficulty();
        frag.setArguments(b);
        transaction.replace(R.id.fragmentPlace, frag);
        transaction.commit();
    }

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {

    }
}
