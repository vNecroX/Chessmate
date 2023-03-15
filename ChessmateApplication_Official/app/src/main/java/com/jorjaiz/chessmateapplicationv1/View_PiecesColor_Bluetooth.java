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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class View_PiecesColor_Bluetooth extends AppCompatActivity implements Constants,
        BTCommunication.OnBTCommunicationListener, Fragment_StartGame.OnFragmentInteractionListener,
        Fragment_InviteAgain.OnFragmentInteractionListener
{
    Button btnSend;

    private Timer listenerData;

    private Timer inactiveTime;
    int counterInactiveTime;

    ParamsGame pG;

    boolean respondOponent = false;

    Button btnW, btnB;

    boolean touched = false;

    boolean colorTriggered;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__pieces_color__bluetooth);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_PIECES COLOR BLUETOOTH ON CREATE");

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

            listenerData = new Timer();
            inactiveTime = new Timer();

            BTCommunication.getInstance().setListener(this);
            timerData();
            checkInactiveTime();

            Log.i(TAG, "");
            Log.i(TAG, "CP.get().getConn() " + CP.get().getConn());
            Log.i(TAG, "pG.getMode() " + pG.getMode());
            Log.i(TAG, "pG.getNewSaved().equals(NEW) " + pG.getNewSaved());
            Log.i(TAG, "");
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_PIECESCOLORBLUETOOTH onCreate: " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
    }

    public void timerData()
    {
        final Handler handler = new Handler();
        listenerData.schedule(new TimerTask()
        {
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        Log.i(TAG, "Run Color");
                        BTCommunication.getInstance().listenForData();
                    }
                });
            }
        },1000,1000);
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

                        if(counterInactiveTime >= 20)
                        {
                            if(counterInactiveTime == 30)
                            {
                                Toast.makeText(getBaseContext(), "Tiempo límite de inactividad alcanzado", Toast.LENGTH_LONG).show();

                                inactiveTime.cancel();
                                listenerData.cancel();

                                Intent i = new Intent(getBaseContext(), View_MainInterface.class);
                                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra("idPlayer", CP.get().getIdPlayer());
                                i.putExtra("namePlayer", (CP.get().getNamePlayer().equals("Anónimo")?"":CP.get().getNamePlayer()));
                                getBaseContext().startActivity(i);
                            }
                            else if((50-counterInactiveTime)%2 != 0)
                            {
                                Toast.makeText(getBaseContext(), 50-counterInactiveTime+"", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        },0,1000);
    }

    //

    public void gotoPlay(int colorPlayer)
    {
        try
        {
            gotoPrepareGame(colorPlayer, false);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_PIECESCOLOR BLUETOOTH gotoPlay: " + e.toString());
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
            Log.e(TAG, "Exception VIEW_PIECESCOLOR BLUETOOTH gotoWait: " + e.toString());
        }
    }

    private void gotoPrepareGame(int colorPlayer, boolean wait)
    {
        pG.setColorPlayer(colorPlayer);
        pG.setResume(1);

        Bundle b = new Bundle();
        b.putParcelable("pG", pG);
        b.putBoolean("waitingToPlay", wait);

        listenerData.cancel();
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

            if(!respondOponent)
            {
                BTCommunication.getInstance().sendData("W");
            }

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

            colorTriggered = true;
        }
    }

    public void clickBlack(View view)
    {
        if(!touched)
        {
            touched = true;

            if(!respondOponent)
            {
                BTCommunication.getInstance().sendData("B");
            }

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

            colorTriggered = false;
        }
    }

    private void gotoNext()
    {
        if(colorTriggered)
        {
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
        else
        {
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
    public void onCancelInvitation()
    {
        Intent i = new Intent(this, View_MainInterface.class);
        i.putExtra("idPlayer", CP.get().getIdPlayer());
        i.putExtra("namePlayer", (CP.get().getNamePlayer().equals("Anónimo")?"":CP.get().getNamePlayer()));
        this.startActivity(i);
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

    @Override
    public void onFragRemoveBLayer()
    {
        findViewById(R.id.bLayer).setVisibility(View.INVISIBLE);
    }


    @Override
    public void onPlayAgain()
    {

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

    // BLUETOOTH COMMUNICATION IMPLEMENTATIONS

    @Override
    public void bluetoothReply(String data)
    {
        if(!data.equals(""))
        {
            if(pG.getKindPlayer() == MASTER)
            {
                if(data.equals("W"))
                {
                    Log.w(TAG, "SLAVE CHOOSE A WHITE COLOR!");

                    respondOponent = true;
                    BTCommunication.getInstance().sendData("OK");

                    findViewById(R.id.btnWhiteColor).performClick();

                    gotoNext();
                }
                else if(data.equals("B"))
                {
                    Log.w(TAG, "SLAVE CHOOSE A BLACK COLOR!");

                    respondOponent = true;
                    BTCommunication.getInstance().sendData("OK");

                    findViewById(R.id.btnBlackColor).performClick();

                    gotoNext();
                }
                else if(data.equals("OK"))
                {
                    BTCommunication.getInstance().sendData("COLOR");

                    gotoNext();
                }
            }
            else
            {
                if(data.equals("W"))
                {
                    Log.w(TAG, "MASTER CHOOSE A WHITE COLOR!");

                    respondOponent = true;
                    BTCommunication.getInstance().sendData("OK");

                    findViewById(R.id.btnWhiteColor).performClick();

                    gotoNext();
                }
                else if(data.equals("B"))
                {
                    Log.w(TAG, "MASTER CHOOSE A BLACK COLOR!");

                    respondOponent = true;
                    BTCommunication.getInstance().sendData("OK");

                    findViewById(R.id.btnBlackColor).performClick();

                    gotoNext();
                }
                else if(data.equals("OK"))
                {
                    BTCommunication.getInstance().sendData("COLOR");

                    gotoNext();
                }
            }
        }

    }

}
