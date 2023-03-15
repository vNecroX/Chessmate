package com.jorjaiz.chessmateapplicationv1;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class View_MainInterface extends AppCompatActivity implements Constants, Query.OnResponseDatabase, FireQuery.OnResponseFireQuery,
        Fragment_BoardConn.OnFragmentInteractionListener, BTCommunication.OnBTCommunicationListener
{
    // Things of connection
    Handler handlerConn;
    Runnable runnableConn;
    int serverErrors;
    boolean serverErrorFrag;

    // Bluetooth
    public static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    public static boolean firstUserAccess = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__main_interface);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            if(CommunicationInterface.getInstance().getReference() != null)
            {
                CommunicationInterface.getInstance().removeListenerReplies();
                CommunicationInterface.getInstance().setListener(null);
            }

            if(BTCommunication.getInstance().getmListener() != null)
            {
                //BTCommunication.getInstance().setListener(null);
            }

            if(MainActivity.timerMainActivity != null)
            {
                MainActivity.timerMainActivity.cancel();
            }

            if(CP.get().getLastKindOfPlayer() != Constants.NO_ONE)
            {
                if(CP.get().getLastKindOfPlayer() == SLAVE)
                {
                    if(BTCommunication.getInstance().getBluetoothSocket() != null)
                    {
                        View_ConnectBoard.isConnected = false;
                        View_BluetoothDevice.isConnected = false;
                        View_ConnectBoard.fConnected = false;
                        View_BluetoothDevice.fConnected = false;
                        BTCommunication.getInstance().getBluetoothSocket().close();
                        BTCommunication.getInstance().setBluetoothSocket(null);

                        MySQL.query(this).setBoardConfiguration(0+"", CP.get().getIdPlayer()+"");
                    }
                }
            }

            Bundle parameters = getIntent().getExtras();

            CP.get().setIdPlayer(parameters.getInt("idPlayer", 0));
            CP.get().setNamePlayer(parameters.getString("namePlayer"));

            if(CP.get().getIdPlayer() != 0 && !CP.get().getNamePlayer().equals("Anónimo"))
                CP.get().setKindOfUser(REGISTERED);
            else
                CP.get().setKindOfUser(ANONYMOUS);

            findViewById(R.id.btnPvIA).setOnClickListener(view -> gotoPlay(PVIA));
            findViewById(R.id.btnPvPLocal).setOnClickListener(view -> gotoPlay(PVPLOCAL));

            if(CP.get().getKindOfUser() == ANONYMOUS)
            {
                TextView tVUserName = findViewById(R.id.tvUserName);
                tVUserName.setText("Anónimo");
                CP.get().setConn(OFFLINE);

                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    findViewById(R.id.btnProfile).setBackgroundTintList(this.getResources().getColorStateList(R.color.state_color_anonymous));

                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                firstAccess();

                if(CP.get().isBoard())
                    findViewById(R.id.bLayerMainInterface).setVisibility(View.INVISIBLE);
            }
            else
            {
                TextView tVUserName = findViewById(R.id.tvUserName);
                tVUserName.setText(CP.get().getNamePlayer());

                findViewById(R.id.btnPvPOnline).setOnClickListener(view -> gotoPlay(PVPONLINE));
                findViewById(R.id.btnSavedGames).setOnClickListener(view -> gotoResume());

                findViewById(R.id.btnProfile).setOnClickListener(view -> gotoProfile());
                findViewById(R.id.tvUserName).setOnClickListener(view -> gotoProfile());

                findViewById(R.id.btnConfig).setOnClickListener(view -> gotoConfig());

                Firebase.query(this).updateConnPlayer(CP.get().getIdPlayer(), 1);

                serverErrors = 0;
                serverErrorFrag = false;

                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                firstAccess();

                checkConnection();
                getConfiguration();
            }

            runMainTitleAnimation();

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_MAIN INTERFACE ON CREATE");
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_MAININTERFACE onCreate: " + e.toString());
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        if(CP.get().getKindOfUser() == REGISTERED)
            checkConnection();

        if(!firstUserAccess)
        {
            if(CP.get().isBoard())
            {
                if(!View_ConnectBoard.isConnected && !View_BluetoothDevice.isConnected)
                {
                    showFragmentBoardConn();
                }
            }
        }

        findViewById(R.id.bLayerMainInterface).setVisibility(View.INVISIBLE);
        findViewById(R.id.bLayerMainInterface).setVisibility(View.GONE);

        if(MainActivity.timerMainActivity != null)
        {
            MainActivity.timerMainActivity.cancel();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        if(CP.get().getKindOfUser() == REGISTERED)
            cancelAllRunnables();
    }

    @Override
    public void onBackPressed()
    {
        if(CP.get().getKindOfUser() == REGISTERED)
            cancelAllRunnables();

        Firebase.query(this).updateConnPlayer(CP.get().getIdPlayer(), 2);
        MySQL.query(this).setBoardConfiguration(0+"", CP.get().getIdPlayer()+"");

        try
        {
            if(BTCommunication.getInstance().getBluetoothSocket() != null)
            {
                View_ConnectBoard.isConnected = false;
                View_BluetoothDevice.isConnected = false;
                View_ConnectBoard.fConnected = false;
                View_BluetoothDevice.fConnected = false;
                BTCommunication.getInstance().getBluetoothSocket().close();
                BTCommunication.getInstance().setBluetoothSocket(null);
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "MAIN_ACTIVITY onBackPressed " + e.toString());
        }

        this.startActivity(new Intent(this, View_ApplicationStart.class));
    }

    // INTERFACES

    @Override
    public void onFragRemoveBLayerBoardConn()
    {
        findViewById(R.id.bLayerMainInterface).setVisibility(View.INVISIBLE);
        findViewById(R.id.bLayerMainInterface).setVisibility(View.GONE);
    }

    // GENERAL FUNCTIONS

    public void gotoPlay(int gameMode)
    {
        try
        {
            ParamsGame paramsGame = new ParamsGame();
            paramsGame.setIdPlayer(CP.get().getIdPlayer());
            paramsGame.setPlayerName(CP.get().getNamePlayer());
            paramsGame.setNewSaved(NEW);
            paramsGame.setMode(gameMode);

            Log.e(TAG, " ********** CP.get().isBoard() " + CP.get().isBoard());
            Log.e(TAG, "");

            Intent i;

            if(gameMode == PVIA)
            {
                if(CP.get().isBoard())
                    paramsGame.setKindOfLocal(IS_LOCALIA_T);
                else
                    paramsGame.setKindOfLocal(IS_NOLOCAL);

                i = new Intent(this, View_Difficulty.class);

                Firebase.query(this).updateConnPlayer(CP.get().getIdPlayer(), 3);
            }
            else if(gameMode == PVPLOCAL)
            {
                paramsGame.setKindOfLocal(IS_LOCALPVP_T);

                if(View_ConnectBoard.isConnected || View_BluetoothDevice.isConnected)
                {
                    if(View_BluetoothDevice.isMaster || View_ConnectBoard.isMaster)
                    {
                        paramsGame.setKindPlayer(MASTER);
                    }
                    else
                    {
                        paramsGame.setKindPlayer(SLAVE);
                    }

                    i = new Intent(View_MainInterface.this, View_PeerLocalBluetooth.class);
                }
                else
                {
                    i = new Intent(View_MainInterface.this, View_BluetoothDevice.class);
                }

                Firebase.query(this).updateConnPlayer(CP.get().getIdPlayer(), 3);
            }
            else
            {
                if(View_ConnectBoard.isConnected || View_BluetoothDevice.isConnected)
                {
                    paramsGame.setKindOfLocal(IS_REMOTE_T);
                }
                else
                {
                    paramsGame.setKindOfLocal(IS_NOLOCAL);
                }

                i = new Intent(this, View_GamersList.class);
            }

            i.putExtra("pG", paramsGame);

            if(CP.get().getKindOfUser() == REGISTERED)
                cancelAllRunnables();

            this.startActivity(i);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_MAININTERFACE gotoPlay: " + e.toString());
        }
    }

    public void gotoResume()
    {
        try
        {
            ParamsGame paramsGame = new ParamsGame();
            paramsGame.setIdPlayer(CP.get().getIdPlayer());
            paramsGame.setPlayerName(CP.get().getNamePlayer());
            paramsGame.setNewSaved(SAVED);
            paramsGame.setKindPlayer(MASTER);

            cancelAllRunnables();

            Intent i = new Intent(this, View_GamesSaved.class);
            i.putExtra("pG", paramsGame);
            this.startActivity(i);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_MAININTERFACE gotoResume: " + e.toString());
        }
    }

    public void gotoProfile()
    {
        try
        {
            cancelAllRunnables();

            Intent i = new Intent(this, View_EditProfile.class);
            i.putExtra("case", "editProfile");
            i.putExtra("idPlayer", CP.get().getIdPlayer());
            this.startActivity(i);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_MAININTERFACE gotoProfile: " + e.toString());
        }
    }

    public void gotoConfig()
    {
        try
        {
            Intent i = new Intent(this, View_MainConfiguration.class);
            cancelAllRunnables();
            this.startActivity(i);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_MAININTERFACE gotoConfig: " + e.toString());
        }
    }

    public void gotoNothing()
    {

    }

    public void configCheckWifi(String wifi)
    {
        findViewById(R.id.btnPvIA).setOnClickListener(view -> gotoPlay(PVIA));

        TextView tVUserName = findViewById(R.id.tvUserName);

        if(wifi.equals(" ON"))
        {
            findViewById(R.id.btnPvPOnline).setOnClickListener(view -> gotoPlay(PVPONLINE));
            findViewById(R.id.btnSavedGames).setOnClickListener(view -> gotoResume());

            findViewById(R.id.btnProfile).setOnClickListener(view -> gotoProfile());
            findViewById(R.id.tvUserName).setOnClickListener(view -> gotoProfile());

            findViewById(R.id.btnConfig).setOnClickListener(view -> gotoConfig());

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                findViewById(R.id.btnProfile).setBackgroundTintList(this.getResources().getColorStateList(R.color.state_color_connect));
        }
        else
        {
            findViewById(R.id.btnPvPOnline).setOnClickListener(view -> gotoNothing());
            findViewById(R.id.btnSavedGames).setOnClickListener(view -> gotoNothing());

            findViewById(R.id.btnProfile).setOnClickListener(view -> gotoNothing());
            findViewById(R.id.tvUserName).setOnClickListener(view -> gotoNothing());

            findViewById(R.id.btnConfig).setOnClickListener(view -> gotoNothing());

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                findViewById(R.id.btnProfile).setBackgroundTintList(this.getResources().getColorStateList(R.color.state_color_disconnect));
        }

        //tVUserName.setText(CP.get().getNamePlayer() + wifi);
    }

    public void checkConnection()
    {
        if(handlerConn == null)
        {
            handlerConn = new Handler();
            runnableConn = new Runnable()
            {
                @Override
                public void run()
                {
                    MySQL.query(View_MainInterface.this).checkWifi();
                    handlerConn.postDelayed(this, 8000);
                }
            };
            handlerConn.post(runnableConn);
        }
    }

    private void getConfiguration()
    {
        MySQL.query(this).getConfiguration(CP.get().getIdPlayer()+"");
    }

    public void cancelAllRunnables()
    {
        if(handlerConn != null)
        {
            handlerConn.removeCallbacks(runnableConn);
            handlerConn = null;
        }
    }

    private void runMainTitleAnimation()
    {
        TextView tVMainTitle = findViewById(R.id.tvChessmate);

        ObjectAnimator animX = ObjectAnimator.ofFloat(tVMainTitle, "scaleX", 1.2f);
        animX.setDuration(4000);
        animX.setRepeatMode(ValueAnimator.REVERSE);
        animX.setRepeatCount(Animation.INFINITE);
        animX.setInterpolator(new LinearInterpolator());

        ObjectAnimator animY = ObjectAnimator.ofFloat(tVMainTitle, "scaleY", 1.2f);
        animY.setDuration(4000);
        animY.setRepeatMode(ValueAnimator.REVERSE);
        animY.setRepeatCount(Animation.INFINITE);
        animY.setInterpolator(new LinearInterpolator());

        ObjectAnimator animColor = ObjectAnimator.ofInt(tVMainTitle, "textColor",
                getResources().getColor(R.color.colorTurquoise0), getResources().getColor(R.color.colorLima));
        animColor.setDuration(1500);
        animColor.setEvaluator(new ArgbEvaluator());
        animColor.setRepeatMode(ValueAnimator.REVERSE);
        animColor.setRepeatCount(Animation.INFINITE);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animX, animY, animColor);
        animatorSet.start();
    }

    // BLUETOOTH IMPLEMENTATIONS

    public void firstAccess()
    {
        if (firstUserAccess)
        {
            firstUserAccess = false;
            showFragmentBoardConn();
        }
    }

    public void showFragmentBoardConn()
    {
        findViewById(R.id.bLayerMainInterface).setVisibility(View.VISIBLE);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_BoardConn fragment_boardConn = new Fragment_BoardConn();
        fragmentTransaction.replace(R.id.fragmentBoardConn, fragment_boardConn);
        fragmentTransaction.commit();
    }

    public static BluetoothAdapter getBluetoothAdapter(){
        return bluetoothAdapter;
    }

    // DATABASE IMPLEMENTATIONS

    // MYSQL

    @Override
    public synchronized void getResponseDB(HashMap<String, JSONArray> data, String purpose)
    {
        try
        {
            Log.w(TAG, ">>>>> PURPOSE: " + purpose);

            if(data==null)
            {
                Log.e(TAG, "FATAL ERROR (DATA NULL) VIEW_MAININTERFACE getResponseDB");
            }
            else
            {
                if(data.containsKey("DATA"))
                {
                    switch(purpose)
                    {
                        case "checkWifi":
                            serverErrors = 0;
                            serverErrorFrag = false;
                            CP.get().setConn(ONLINE);
                            configCheckWifi(" ON");
                            break;

                        case "getConfiguration":

                            JSONArray jA = null;

                            if(data.get("DATA")!=null)
                                jA = data.get("DATA");

                            for (int i = 0; i < jA.length(); i++)
                            {
                                JSONObject jsonObject;

                                try
                                {
                                    jsonObject = jA.getJSONObject(i);

                                    CP.get().setAutosave(jsonObject.getInt("autos")==1);
                                    CP.get().setBoard(jsonObject.getInt("board")==1);
                                    CP.get().setNotif(jsonObject.getInt("notif")==1);
                                    CP.get().setChat(jsonObject.getInt("chat")==1);

                                    Log.i(TAG, " ");
                                    Log.i(TAG, " ");
                                    Log.i(TAG, "<--- DATA OF PLAYER --->");
                                    Log.i(TAG, " * BASIC DATA");
                                    Log.i(TAG, "    - NAME OF PLAYER: " + CP.get().getNamePlayer());
                                    Log.i(TAG, "    - ID OF PLAYER: " + CP.get().getIdPlayer());
                                    Log.i(TAG, "    - KIND OF USER: " + CP.get().getKindOfUser());

                                    Log.i(TAG, " * CONFIG DATA");
                                    Log.i(TAG, "    - AUTOSAVE: " + CP.get().isAutosave());
                                    Log.i(TAG, "    - BOARD: " + CP.get().isBoard());
                                    Log.i(TAG, "    - NOTIFICATION: " + CP.get().isNotif());
                                    Log.i(TAG, "    - CHAT: " + CP.get().isChat());
                                    Log.i(TAG, " ");
                                    Log.i(TAG, " ");
                                }
                                catch (JSONException e)
                                {
                                    Log.e(TAG, "VIEW_MAININTERFACE getConfiguration exception " + e.toString());
                                }
                            }

                            if(CP.get().isBoard())
                                findViewById(R.id.bLayerMainInterface).setVisibility(View.INVISIBLE);

                            break;
                    }
                }
                else if(data.containsKey("ERROR"))
                {
                    if(CP.get().getKindOfUser() == REGISTERED)
                    {
                        if(serverErrors >= 2)
                        {
                            serverErrors = 0;

                            if(!serverErrorFrag)
                            {
                                serverErrorFrag = true;

                                CP.get().setConn(OFFLINE);
                                configCheckWifi(" OFF");

                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                Fragment_ErrorWithServer frag = new Fragment_ErrorWithServer();
                                transaction.replace(R.id.fragmentPlace, frag);
                                transaction.commit();
                            }
                        }
                        else
                        {
                            serverErrors++;
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "");
        }
    }

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {

    }

    @Override
    public void bluetoothReply(String data)
    {

    }


}
