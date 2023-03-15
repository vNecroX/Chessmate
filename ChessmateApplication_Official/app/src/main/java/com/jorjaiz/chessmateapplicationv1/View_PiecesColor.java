package com.jorjaiz.chessmateapplicationv1;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;

import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class View_PiecesColor extends AppCompatActivity implements Constants,
        Fragment_StartGame.OnFragmentInteractionListener, CommunicationInterface.OnCommunicationListener,
        Fragment_InviteAgain.OnFragmentInteractionListener, FireQuery.OnResponseFireQuery
{
    ParamsGame pG;

    boolean respondOponent = false;

    Button btnW, btnB;

    boolean touched = false;

    private Timer inactiveTime;
    int counterInactiveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__pieces_color);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_PIECES COLOR ON CREATE");

            findViewById(R.id.bLayer).setVisibility(View.INVISIBLE);

            Bundle parameters = getIntent().getExtras();
            pG = parameters.getParcelable("pG");

            btnW = findViewById(R.id.btnWhiteColor);
            btnB = findViewById(R.id.btnBlackColor);

            btnW.setOnClickListener(
                    view ->
                    {
                        clickWhite(view);
                    });

            btnB.setOnClickListener(
                    view ->
                    {
                        clickBlack(view);
                    });

            findViewById(R.id.btnWhiteRook).setOnClickListener(
                    view ->
                    {
                        clickWhite(view);
                    });

            findViewById(R.id.btnBlackRook).setOnClickListener(
                    view ->
                    {
                        clickBlack(view);
                    });


            TextView tVVersus = findViewById(R.id.tvVersus);

            ObjectAnimator animX = ObjectAnimator.ofFloat(tVVersus, "scaleX", 1.5f);
            animX.setDuration(2000);
            animX.setRepeatMode(ValueAnimator.REVERSE);
            animX.setRepeatCount(Animation.INFINITE);
            animX.setInterpolator(new LinearInterpolator());

            ObjectAnimator animY = ObjectAnimator.ofFloat(tVVersus, "scaleY", 1.5f);
            animY.setDuration(2000);
            animY.setRepeatMode(ValueAnimator.REVERSE);
            animY.setRepeatCount(Animation.INFINITE);
            animY.setInterpolator(new LinearInterpolator());

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(animX, animY);
            animatorSet.start();

            if(pG.getMode() == PVPONLINE && CP.get().getConn() == ONLINE)
                CommunicationInterface.getInstance().setListener(this);

            inactiveTime = new Timer();

            if(pG.getMode() == PVPONLINE)
                checkInactiveTime();

        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_PIECESCOLOR onCreate: " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        if(pG.getMode() == PVIA)
        {
            Intent i = new Intent(this, View_MainInterface.class);
            i.putExtra("idPlayer", CP.get().getIdPlayer());
            i.putExtra("namePlayer", CP.get().getNamePlayer());
            this.startActivity(i);
        }
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

                        if(counterInactiveTime >= 40)
                        {
                            if(counterInactiveTime == 50)
                            {
                                Toast.makeText(getBaseContext(), "Tiempo límite de espera alcanzado, intente de nuevo", Toast.LENGTH_LONG).show();

                                inactiveTime.cancel();

                                Intent i = new Intent(getBaseContext(), View_MainInterface.class);
                                i.putExtra("idPlayer", CP.get().getIdPlayer());
                                i.putExtra("namePlayer", CP.get().getNamePlayer());
                                getBaseContext().startActivity(i);
                            }
                            else if((50-counterInactiveTime)%2 != 0)
                            {
                                //Toast.makeText(getBaseContext(), 50-counterInactiveTime+"", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        },0,1000);
    }

    public void gotoPlay(int colorPlayer)
    {
        try
        {
            gotoPrepareGame(colorPlayer, false);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_PIECESCOLOR gotoPlay: " + e.toString());
        }
    }

    public void gotoWait(int colorPlayer)
    {
        try
        {
            gotoPrepareGame(colorPlayer, true);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_PIECESCOLOR gotoWait: " + e.toString());
        }
    }

    private void gotoPrepareGame(int colorPlayer, boolean wait)
    {
        pG.setColorPlayer(colorPlayer);
        pG.setResume(1);

        Bundle b = new Bundle();
        b.putParcelable("pG", pG);

        b.putBoolean("waitingToPlay", wait);

        inactiveTime.cancel();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment_StartGame frag = new Fragment_StartGame();
        frag.setArguments(b);
        transaction.replace(R.id.fragmentPlace, frag);
        transaction.commit();

        findViewById(R.id.bLayer).setVisibility(View.VISIBLE);
    }

    // CLICK IMPLEMENTATIONS

    public void clickWhite(View view)
    {
        if(!touched)
        {
            touched = true;

            if(pG.getMode() == PVPONLINE && !respondOponent && CP.get().getConn() == ONLINE)
            {
                if(pG.getKindPlayer() == MASTER)
                    CommunicationInterface.getInstance().respondAsMaster(WHITE);
                else
                    CommunicationInterface.getInstance().respondAsSlave(WHITE);
            }

            if(pG.getMode() == PVIA)
            {
                pG.setpOneId(pG.getIdPlayer()); // ID Player White
                pG.setpTwoId(pG.getIdOponent()); // ID Player Black
                pG.setpOneWhite(1);
                pG.setpTwoBlack(0); // 0 = IA, 1 = An User

                Log.w(TAG, "YOU ARE WHITE");

                gotoPlay(1);
            }
            else
            {
                if(respondOponent)
                {
                    pG.setpOneId(pG.getIdOponent()); // ID Player White
                    pG.setpTwoId(pG.getIdPlayer()); // ID Player Black

                    btnW.setText(pG.getOponentName());
                    btnB.setText(pG.getPlayerName());

                    Log.w(TAG, "YOU ARE BLACK");
                }
                else
                {
                    pG.setpOneId(pG.getIdPlayer()); // ID Player White
                    pG.setpTwoId(pG.getIdOponent()); // ID Player Black

                    btnW.setText(pG.getPlayerName());
                    btnB.setText(pG.getOponentName());

                    Log.w(TAG, "YOU ARE WHITE");
                }

                pG.setpOneWhite(1);
                pG.setpTwoBlack(1); // 0 = IA, 1 = An User

                Handler hndlr = new Handler();
                hndlr.postDelayed(
                        () ->
                        {
                            if(respondOponent)
                            {
                                if(pG.getKindPlayer() == MASTER)
                                    gotoPlay(0);
                                else
                                    gotoWait(0);
                            }

                            else
                            {
                                if(pG.getKindPlayer() == MASTER)
                                    gotoPlay(1);
                                else
                                    gotoWait(1);
                            }

                        }, 2000);
            }
        }
    }

    public void clickBlack(View view)
    {
        if(!touched)
        {
            touched = true;

            if(pG.getMode() == PVPONLINE && !respondOponent && CP.get().getConn() == ONLINE)
            {
                if(pG.getKindPlayer() == MASTER)
                    CommunicationInterface.getInstance().respondAsMaster(BLACK);
                else
                    CommunicationInterface.getInstance().respondAsSlave(BLACK);
            }

            if(pG.getMode() == PVIA)
            {
                pG.setpOneId(pG.getIdOponent()); // ID Player White
                pG.setpTwoId(pG.getIdPlayer()); // ID Player Black
                pG.setpOneWhite(0);
                pG.setpTwoBlack(1); // 0 = IA, 1 = An User

                Log.w(TAG, "YOU ARE BLACK");
                gotoPlay(0);
            }
            else
            {
                if(respondOponent)
                {
                    pG.setpOneId(pG.getIdPlayer()); // ID Player White
                    pG.setpTwoId(pG.getIdOponent()); // ID Player Black

                    btnW.setText(pG.getPlayerName());
                    btnB.setText(pG.getOponentName());

                    Log.w(TAG, "YOU ARE WHITE");
                }
                else
                {
                    pG.setpOneId(pG.getIdOponent()); // ID Player White
                    pG.setpTwoId(pG.getIdPlayer()); // ID Player Black

                    btnW.setText(pG.getOponentName());
                    btnB.setText(pG.getPlayerName());

                    Log.w(TAG, "YOU ARE BLACK");
                }

                pG.setpOneWhite(1);
                pG.setpTwoBlack(1); // 0 = IA, 1 = An User

                Handler hndlr = new Handler();
                hndlr.postDelayed(
                        () ->
                        {
                            if(respondOponent)
                            {
                                if(pG.getKindPlayer() == MASTER)
                                    gotoPlay(1);
                                else
                                    gotoWait(1);
                            }
                            else
                            {
                                if(pG.getKindPlayer() == MASTER)
                                    gotoPlay(0);
                                else
                                    gotoWait(0);
                            }

                        }, 2000);
            }
        }
    }

    // INTERFACES IMPLEMENTATIONS

    @Override
    public void onPlayAgain()
    {

    }

    @Override
    public void onCancelInvitation()
    {
        if(Fragment_StartGame.inactiveTimeS != null)
            Fragment_StartGame.inactiveTimeS.cancel();

        if(Fragment_StartGame.listenerDataS != null)
            Fragment_StartGame.listenerDataS.cancel();

        Firebase.query(this).updateConnPlayer(CP.get().getIdPlayer(), 1);

        Intent i = new Intent(this, View_GamersList.class);
        i.putExtra("pG", pG);
        startActivity(i);
    }

    @Override
    public void onFragRemoveBLayer()
    {
        findViewById(R.id.bLayer).setVisibility(View.INVISIBLE);
    }

    @Override
    public void onFragOpponentDisconnected()
    {
        Bundle b = new Bundle();
        b.putParcelable("pG", pG);
        b.putBoolean("oponentDisconnected", true);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment_InviteAgain fragInvitation = new Fragment_InviteAgain();
        fragInvitation.setArguments(b);
        transaction.replace(R.id.fragmentPlace2, fragInvitation);
        transaction.commit();

        touched = false;
    }

    @Override
    public void onFragErrorWithServer()
    {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment_ErrorWithServer frag = new Fragment_ErrorWithServer();
        transaction.replace(R.id.fragmentPlace3, frag);
        transaction.commit();
        touched = false;
    }

    @Override
    public void onFragLoading()
    {
        Bundle b = new Bundle();
        b.putBoolean("waitingToPlay", true);
        b.putParcelable("pG", pG);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment_StartGame frag = new Fragment_StartGame();
        frag.setArguments(b);
        transaction.replace(R.id.fragmentPlace4, frag);
        transaction.commit();

        findViewById(R.id.bLayer).setVisibility(View.VISIBLE);
    }

    @Override
    public void onFragQuitLoading()
    {
        getSupportFragmentManager().beginTransaction().
                remove(getSupportFragmentManager().findFragmentById(R.id.fragmentPlace4)).commit();
    }

    // COMMUNICATION IMPLEMENTATIONS

    @Override
    public void masterReplays(String data)
    {
        if(pG.getKindPlayer() == SLAVE)
        {
            respondOponent = true;

            if(data.equals(WHITE))
                findViewById(R.id.btnWhiteColor).performClick();
            else
                findViewById(R.id.btnBlackColor).performClick();
        }
    }

    @Override
    public void slaveReplays(String data)
    {
        if(pG.getKindPlayer() == MASTER)
        {
            respondOponent = true;

            if(data.equals(WHITE))
                findViewById(R.id.btnWhiteColor).performClick();
            else
                findViewById(R.id.btnBlackColor).performClick();

        }
    }

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {

    }
}
