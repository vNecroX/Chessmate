package com.grauben98.esteb.appchessmate;

import android.content.Context;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_AnotherGame;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_BoardConn;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_Chat;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_CreateAccount;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_Difficulty;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_ErrorWithServer;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_GI_PvIa;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_GI_Pvp;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_GameFinished;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_IllegalAndIncorrect;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_NoBluetoothConn;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_NoWifiConn;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_OpponentDisconn;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_OtherInterruptions;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_PieceMovAndFalseCon;
import com.grauben98.esteb.appchessmate.Fragment.Fragment_PreparingGame;
import com.grauben98.esteb.appchessmate.Interface.Interface_BackPressed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.URL_DATA;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.cas1;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.TAG;

public class View_GI_PvPOnline extends AppCompatActivity {

    //Objects.
        //Button.
        public Button btnPause, btnChat, btnBadgeCounter;
        //Timer.
        public Timer tNewMessages;
        //Interface_BackPressed.
        private Interface_BackPressed interface_backPressed;

    //Variables.
        //int.
        public int type;
        public static int auxId = 0, idMssg;
        public static int aResLength = 0, rLength, hmNewMssgs;
        //boolean.
        public boolean fNewTimer = true;
        public static boolean fChatOpened = true;

    public void setInterface_backPressed(Interface_BackPressed interface_backPressed){
        this.interface_backPressed = interface_backPressed;
    }

    @Override
    public void onBackPressed() {
        if(interface_backPressed != null){
            interface_backPressed.onBackPressed();
            timerChat();
            hideBadgeCounter();
            btnChat.setEnabled(true);
            fChatOpened = false;
        }
        else{
            super.onBackPressed();
            tNewMessages.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_gipvponline);

        btnPause = findViewById(R.id.btnPause);
        btnChat = findViewById(R.id.btnChat);
        btnBadgeCounter = findViewById(R.id.btnBadgeCounter);

        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFragment_Chat();
            }
        });

        timerChat();

        //showFragment_AnotherGame();
        //showFragment_BoardConn();
        //showFragment_Chat();
        //showFragment_CreateAccount();
        //showFragment_Difficulty();
        //showFragment_ErrorWithServer();
        //showFragment_GameFinished();
        //showFragment_GI_PvIa();
        //showFragment_GI_Pvp();
        //showFragment_IllegalAndIncorrect();
        //showFragment_NoBluetoothConn();
        //showFragment_NoWifiConn();
        //showFragment_OpponentDisconn();
        //showFragment_OtherInterruptions();
        //showFragment_PieceMovAndFalseCon();
        //showFragment_PreparingGame();
    }

    public void showFragment_AnotherGame(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_AnotherGame fragment_anotherGame = new Fragment_AnotherGame();
        fragmentTransaction.replace(R.id.fragmentAnotherGame, fragment_anotherGame);
        fragmentTransaction.commit();
    }

    public void showFragment_BoardConn(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_BoardConn fragment_boardConn = new Fragment_BoardConn();
        fragmentTransaction.replace(R.id.fragmentBoardConn, fragment_boardConn);
        fragmentTransaction.commit();
    }

    public void showFragment_Chat(){
        hideBadgeCounter();
        fChatOpened = true;
        tNewMessages.cancel();
        btnChat.setEnabled(false);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_Chat fragment_chat = new Fragment_Chat();
        fragmentTransaction.replace(R.id.fragmentChat, fragment_chat);
        fragmentTransaction.commit();
    }

    public void hideBadgeCounter(){
        hmNewMssgs = 0;
        btnBadgeCounter.setVisibility(View.INVISIBLE);
    }

    public void timerChat(){
        newTimer();
        timerNewMessages();
    }

    public void newTimer(){
        tNewMessages = new Timer();
    }

    public void timerNewMessages(){
        final Handler handler = new Handler();
        tNewMessages.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        unseenMessages();
                    }
                });
            }
        },0, 2500);
    }

    public void unseenMessages(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_DATA + "&case=" + cas1,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jsonObject;

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                rLength = response.length();

                                jsonObject = response.getJSONObject(i);
                                idMssg = jsonObject.getInt("message_id");
                                Log.w(TAG, "idMssg: " + idMssg);
                                type = jsonObject.getInt("message_type");

                                if(Fragment_Chat.msggOp){
                                    Log.w(TAG, "mssgOp: " + "true");
                                    if(type == 2){
                                        if(idMssg - Fragment_Chat.contOp > auxId){
                                            hmNewMssgs++;
                                            if(hmNewMssgs > 0){
                                                btnBadgeCounter.setText(hmNewMssgs + "");
                                                btnBadgeCounter.setVisibility(View.VISIBLE);
                                            }
                                            auxId = idMssg;
                                            Fragment_Chat.msggOp = false;
                                        }
                                    }
                                }
                                else {
                                    Log.w(TAG, "mssgOp: " + "false");
                                    if (type == 2) {
                                        if (idMssg > auxId) {
                                            hmNewMssgs++;
                                            Log.e(TAG, "hm: " + hmNewMssgs);

                                            if (hmNewMssgs > 0) {
                                                if (Fragment_Chat.msggOp) {
                                                    hmNewMssgs--;
                                                    Fragment_Chat.msggOp = false;
                                                }
                                                btnBadgeCounter.setText(hmNewMssgs + "");
                                                btnBadgeCounter.setVisibility(View.VISIBLE);
                                            }
                                            auxId = idMssg;
                                        }
                                    }
                                }
                            } catch (JSONException e) { /*e.printStackTrace();*/ }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { /*showToast("" + error.getMessage());*/ }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(View_GI_PvPOnline.this);
        requestQueue.add(jsonArrayRequest);
    }

    public void showFragment_CreateAccount(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_CreateAccount fragment_createAccount = new Fragment_CreateAccount();
        fragmentTransaction.replace(R.id.fragmentCreateAcc, fragment_createAccount);
        fragmentTransaction.commit();
    }

    public void showFragment_Difficulty(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_Difficulty fragment_difficulty = new Fragment_Difficulty();
        fragmentTransaction.replace(R.id.fragmentDifficulty, fragment_difficulty);
        fragmentTransaction.commit();
    }

    public void showFragment_ErrorWithServer(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_ErrorWithServer fragment_errorWithServer = new Fragment_ErrorWithServer();
        fragmentTransaction.replace(R.id.fragmentErrorWithServer, fragment_errorWithServer);
        fragmentTransaction.commit();
    }

    public void showFragment_GameFinished(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_GameFinished fragment_gameFinished = new Fragment_GameFinished();
        fragmentTransaction.replace(R.id.fragmentGameFinished, fragment_gameFinished);
        fragmentTransaction.commit();
    }

    public void showFragment_GI_PvIa(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_GI_PvIa fragment_gi_pvIa = new Fragment_GI_PvIa();
        fragmentTransaction.replace(R.id.fragmentGiPvIa, fragment_gi_pvIa);
        fragmentTransaction.commit();
    }

    public void showFragment_GI_Pvp(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_GI_Pvp fragment_gi_pvp = new Fragment_GI_Pvp();
        fragmentTransaction.replace(R.id.fragmentGiPvp, fragment_gi_pvp);
        fragmentTransaction.commit();
    }

    public void showFragment_IllegalAndIncorrect(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_IllegalAndIncorrect fragment_illegalAndIncorrect = new Fragment_IllegalAndIncorrect();
        fragmentTransaction.replace(R.id.fragmentIllAndInc, fragment_illegalAndIncorrect);
        fragmentTransaction.commit();
    }

    public void showFragment_NoBluetoothConn(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_NoBluetoothConn fragment_noBluetoothConn = new Fragment_NoBluetoothConn();
        fragmentTransaction.replace(R.id.fragmentNoBluetoothConn, fragment_noBluetoothConn);
        fragmentTransaction.commit();
    }

    public void showFragment_NoWifiConn(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_NoWifiConn fragment_noWifiConn = new Fragment_NoWifiConn();
        fragmentTransaction.replace(R.id.fragmentNoWifiConn, fragment_noWifiConn);
        fragmentTransaction.commit();
    }

    public void showFragment_OpponentDisconn(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_OpponentDisconn fragment_opponentDisconn = new Fragment_OpponentDisconn();
        fragmentTransaction.replace(R.id.fragmentOppDisc, fragment_opponentDisconn);
        fragmentTransaction.commit();
    }

    public void showFragment_OtherInterruptions(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_OtherInterruptions fragment_otherInterruptions = new Fragment_OtherInterruptions();
        fragmentTransaction.replace(R.id.fragmentOtherInterruptions, fragment_otherInterruptions);
        fragmentTransaction.commit();
    }

    public void showFragment_PieceMovAndFalseCon(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_PieceMovAndFalseCon fragment_pieceMovAndFalseCon = new Fragment_PieceMovAndFalseCon();
        fragmentTransaction.replace(R.id.fragmentPieceMovAndFalseCon, fragment_pieceMovAndFalseCon);
        fragmentTransaction.commit();
    }

    public void showFragment_PreparingGame(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_PreparingGame fragment_preparingGame= new Fragment_PreparingGame();
        fragmentTransaction.replace(R.id.fragmentPreparingGame, fragment_preparingGame);
        fragmentTransaction.commit();
    }
}