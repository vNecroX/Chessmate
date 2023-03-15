package com.jorjaiz.chessmateapplicationv1;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.Communication;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class View_Peer extends AppCompatActivity implements Constants, CommunicationInterface.OnCommunicationListener,
        FireQuery.OnResponseFireQuery
{
    TextView tvUserName0, tvUserStatus0, tvUserName1, tvUserStatus1;

    ParamsGame pG;

    DatabaseReference reference;

    private Timer inactiveTime;
    int counterInactiveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__peer);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_PEER ON CREATE ");

            Firebase.query(this).updateConnPlayer(CP.get().getIdPlayer(), 3);

            Bundle parameters = getIntent().getExtras();

            pG = new ParamsGame();
            pG.setIdPlayer(Integer.parseInt(parameters.getString("idPlayer")));
            pG.setIdOponent(Integer.parseInt(parameters.getString("idOponent")));
            pG.setPlayerName(parameters.getString("playerName"));
            pG.setOponentName(parameters.getString("oponentName"));
            pG.setNewSaved(parameters.getString("NewSaved"));
            pG.setMode(parameters.getInt("gameMode"));
            pG.setKindPlayer(parameters.getInt("kindPlayer"));
            pG.setKeyComunnic(parameters.getString("keyNotif"));
            pG.setDifficulty(NULL);

            /*if(pG.getMode() == PVPONLINE)
            {
                if(CP.get().isBoard())
                    pG.setKindOfLocal(IS_REMOTE_T);
                else
                    pG.setKindOfLocal(IS_NOLOCAL);
            }
            else
                pG.setKindOfLocal(IS_NOLOCAL);*/

            tvUserName0 = findViewById(R.id.tvUserName0);
            tvUserStatus0 = findViewById(R.id.tvUserStatus0);
            tvUserName1 = findViewById(R.id.tvUserName1);
            tvUserStatus1 = findViewById(R.id.tvUserStatus1);

            tvUserName0.setText(pG.getPlayerName());
            tvUserName1.setText(pG.getOponentName());

            tvUserStatus0.setText("---");
            tvUserStatus1.setText("---");

            Log.w(TAG, " ");
            Log.w(TAG, "--> KEY COMMUNICATION: " + pG.getKeyComunnic());
            Log.w(TAG, " ");

            reference = FirebaseDatabase.getInstance().getReference("Communications").child(pG.getKeyComunnic());

            if(pG.getKindPlayer() == SLAVE)
            {
                CommunicationInterface.getInstance().start(this, reference,
                        String.valueOf(pG.getIdOponent()), String.valueOf(pG.getIdPlayer()));
                CommunicationInterface.getInstance().respondAsSlave("");
            }
            else
            {
                CommunicationInterface.getInstance().setListener(this);
                CommunicationInterface.getInstance().respondAsMaster("");
            }

            inactiveTime = new Timer();

            checkInactiveTime();

            findViewById(R.id.btnBluetooth).setBackgroundResource(R.drawable.animation_wifi);
            AnimationDrawable animWifi = (AnimationDrawable) findViewById(R.id.btnBluetooth).getBackground();
            animWifi.start();
        }
        catch (Exception e)
        {
            Log.e(TAG, "VIEW_PEER onCreate exception " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {

    }

    public void checkInactiveTime()
    {
        counterInactiveTime = 0;

        final Handler handler = new Handler();
        inactiveTime.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Log.i(TAG, "INACTIVE TIME -  <" + counterInactiveTime + ">");
                        counterInactiveTime++;

                        if(counterInactiveTime >= 70)
                        {
                            if(counterInactiveTime == 90)
                            {
                                Toast.makeText(getBaseContext(), "Tiempo límite de espera alcanzado, intente de nuevo", Toast.LENGTH_LONG).show();

                                inactiveTime.cancel();

                                Intent i = new Intent(getBaseContext(), View_MainInterface.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("idPlayer", CP.get().getIdPlayer());
                                i.putExtra("namePlayer", CP.get().getNamePlayer());
                                getBaseContext().startActivity(i);
                            }
                            else if((50-counterInactiveTime)%2 != 0)
                            {
                                Toast.makeText(getBaseContext(), 90-counterInactiveTime+"", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        },0,1000);
    }

    public void gotoNextActivity()
    {
        try
        {
            NotificationManager notificationManager = ((NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE));
            notificationManager.cancelAll();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Tried to delete no Notifications " + e.toString());
        }

        inactiveTime.cancel();

        Intent i = new Intent(this, View_PiecesColor.class);
        i.putExtra("pG", pG);
        this.startActivity(i);
    }

    // COMMUNICATIONS IMPLEMENTATIONS

    @Override
    public void masterReplays(String data)
    {
        tvUserStatus0.setText("LISTO");

        if(pG.getKindPlayer() == SLAVE)
        {
            tvUserStatus1.setText("LISTO");

            CommunicationInterface.getInstance().respondAsSlave("");

            Handler handler = new Handler();
            handler.postDelayed(
                    () ->
                    {
                        gotoNextActivity();

                    }, 2000);
        }
    }

    @Override
    public void slaveReplays(String data)
    {
        tvUserStatus0.setText("LISTO");

        if(pG.getKindPlayer() == MASTER)
        {
            tvUserStatus1.setText("LISTO");

            Handler handler = new Handler();
            handler.postDelayed(
                    () ->
                    {
                        gotoNextActivity();

                    }, 2000);
        }
    }

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {

    }

}
