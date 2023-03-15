package com.jorjaiz.chessmateapplicationv1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jorjaiz.chessmateapplicationv1.Adapters.Adapter_GamersList;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.Firebase.Communication;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Firebase.Data;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Firebase.Notification;
import com.jorjaiz.chessmateapplicationv1.Firebase.PlayerFire;
import com.jorjaiz.chessmateapplicationv1.Firebase.Sender;
import com.jorjaiz.chessmateapplicationv1.Firebase.Token;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class View_GamersList extends AppCompatActivity implements Constants, CommunicationInterface.OnCommunicationListener,
        FireQuery.OnResponseFireQuery, Query.OnResponseDatabase, Adapter_GamersList.MyAdapterGamersListListener
{
    public static int indexItemView;

    RecyclerView rVGamersList;
    ArrayList<User> listGamers;

    ParamsGame pG;
    Data dataToPass;
    boolean notify = false;

    Handler handler;
    Runnable runnable;

    Adapter_GamersList adapter;

    boolean firstUserAccess;

    int serverErrors;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__gamers_list);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_GAMERSLIST ON CREATE");

            Bundle parameters = getIntent().getExtras();
            pG = parameters.getParcelable("pG");

            indexItemView = -1;

            listGamers = new ArrayList<>();

            rVGamersList = findViewById(R.id.rvGamersList);

            firstUserAccess = false;

            findViewById(R.id.btnInvitePlayer).setOnClickListener(
                    view ->
                    {
                        pG.setDifficulty(NULL);
                        beforePlay();
                    });

            serverErrors = 0;

            checkPlayersConnected();
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMERSLIST onCreate: " + e.toString());
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

    @Override
    protected void onStop()
    {
        super.onStop();
        cancelAllRunnables();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        checkPlayersConnected();
    }

    public void beforePlay()
    {
        try
        {
            if(indexItemView != -1)
            {
                notify = true;

                Firebase.query(this)
                        .prepareNotification(FirebaseInstanceId.getInstance().getToken(), String.valueOf(pG.getIdPlayer()));
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMERSLIST beforePlay: " + e.toString());
        }
    }

    public void gotoPlay()
    {
        try
        {
            Intent i = new Intent(this, View_Peer.class);
            i.putExtra("idPlayer", dataToPass.getUser());
            i.putExtra("idOponent", dataToPass.getSented());
            i.putExtra("playerName", dataToPass.getOponentName());
            i.putExtra("oponentName", dataToPass.getPlayerName());
            i.putExtra("NewSaved", NEW);
            i.putExtra("gameMode", dataToPass.getGameMode());
            i.putExtra("kindPlayer", MASTER);
            i.putExtra("keyNotif", dataToPass.getKeyNotif());

            startActivity(i);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_GAMERSLIST gotoPlay: " + e.toString());
        }
    }

    public void checkPlayersConnected()
    {
        if(handler == null)
        {
            handler = new Handler();
            runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    MySQL.query(View_GamersList.this).getPlayersList(pG.getIdPlayer());
                    handler.postDelayed(this, 10000);
                }
            };
            handler.post(runnable);
        }
    }

    public void cancelAllRunnables()
    {
        if(handler != null)
        {
            handler.removeCallbacks(runnable);
            handler = null;
        }
    }

    @Override
    public void onItemViewGamersListClicked(boolean enableButton)
    {
        if(enableButton)
            findViewById(R.id.btnInvitePlayer).setEnabled(true);
        else
            findViewById(R.id.btnInvitePlayer).setEnabled(false);
    }

    // DATABASE IMPLEMENTATIONS

    // MYSQL

    @Override
    public void getResponseDB(HashMap<String, JSONArray> data, String purpose)
    {
        Log.w(TAG, "PURPOSE: " + purpose);

        if(data==null)
        {
            Log.e(TAG, "FATAL ERROR (DATA NULL) VIEW_GAMERSLIST getResponseDB");
        }
        else
        {
            if(data.containsKey("DATA"))
            {
                try
                {
                    JSONArray jA = null;

                    if(data.get("DATA")!=null)
                        jA = data.get("DATA");

                    switch(purpose)
                    {
                        case "getPlayersList":

                            listGamers.clear();

                            for(int i = 0 ; i < jA.length() ; i++)
                            {
                                JSONObject object = jA.getJSONObject(i);

                                User user = new User();
                                user.setIdPlayer(object.getString("idOponent"));
                                user.setPlayerName(object.getString("userName"));
                                user.setNation(object.getString("nation"));
                                user.setNotif(object.getInt("notif"));
                                user.setConnection(OFFLINE);

                                if(user.getNotif() == 1)
                                    listGamers.add(user);
                            }

                            Firebase.query(this).getPlayers();
                            break;
                    }

                }
                catch (JSONException e)
                {
                    Log.e(TAG, "Exception VIEW_GAMERSLIST getResponseDB: " + e.toString());
                }
            }
            else if(data.containsKey("ERROR"))
            {
                if(serverErrors >= 5)
                {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Fragment_ErrorWithServer frag = new Fragment_ErrorWithServer();
                    transaction.replace(R.id.fragmentPlace, frag);
                    transaction.commit();

                    CP.get().setConn(OFFLINE);

                    Firebase.query(this).updateConnPlayer(CP.get().getIdPlayer(), CP.get().getConn());
                }
                else
                {
                    serverErrors++;

                    switch(purpose)
                    {
                        case "getPlayersList":
                            MySQL.query(View_GamersList.this).getPlayersList(pG.getIdPlayer());
                            break;
                    }
                }
            }
        }
    }

    // FIREBASE

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {
        Log.w(TAG, ">>>>> PURPOSE: " + purpose);

        DataSnapshot dataSnapshot;

        switch(purpose)
        {
            case "getPlayers":

                dataSnapshot = (DataSnapshot)data.get("dataSnapshot");

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    PlayerFire playerFire = snapshot.getValue(PlayerFire.class);

                    if(playerFire.getConnection() == ONLINE)
                    {
                        for(int i=0; i<listGamers.size(); i++)
                        {
                            if(playerFire.getNamePlayer().equals(listGamers.get(i).getPlayerName()))
                            {
                                listGamers.get(i).setConnection(ONLINE);
                                break;
                            }
                        }
                    }
                    else if(playerFire.getConnection() == BUSSY)
                    {
                        for(int i=0; i<listGamers.size(); i++)
                        {
                            if(playerFire.getNamePlayer().equals(listGamers.get(i).getPlayerName()))
                            {
                                listGamers.get(i).setConnection(BUSSY);
                                break;
                            }
                        }
                    }
                }

                if(!firstUserAccess)
                {
                    adapter = new Adapter_GamersList(listGamers, getApplicationContext(), this);
                    LinearLayoutManager l = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                    rVGamersList.setLayoutManager(l);
                    rVGamersList.setAdapter(adapter);

                    firstUserAccess = true;
                }
                else
                {
                    if(indexItemView != -1)
                    {
                        boolean selectedPlayerOnline = false;

                        for(int i=0; i<listGamers.size(); i++)
                        {
                            if(Adapter_GamersList.lastUser.getPlayerName().equals(listGamers.get(i).getPlayerName()))
                            {
                                if(listGamers.get(i).getConnection() == ONLINE)
                                {
                                    selectedPlayerOnline = true;
                                }
                                break;
                            }
                        }

                        if(selectedPlayerOnline)
                            onItemViewGamersListClicked(true);
                        else
                            onItemViewGamersListClicked(false);

                    }

                    adapter.notifyDataSetChanged();
                }
                break;

            case "prepareNotification":
                Firebase.query(this).sendNotification(String.valueOf(pG.getIdPlayer()), listGamers.get(indexItemView).getIdPlayer());
                break;

            case "sendNotification":
                Firebase.query(this).getNotifications();
                break;

            case "getNotifications":
                dataSnapshot = (DataSnapshot)data.get("dataSnapshot");
                String keyNotif = "";

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Notification notif = snapshot.getValue(Notification.class);

                    if(String.valueOf(pG.getIdPlayer()).equals(notif.getSender())
                            && listGamers.get(indexItemView).getIdPlayer().equals(notif.getReceiver())
                            && !notif.isReplay() && !notif.isAccepted())
                    {
                        keyNotif = snapshot.getKey();
                        Log.d(TAG, "snapshot.getKey()" + snapshot.getKey());
                    }
                }

                Log.i(TAG, "LINK KEY OF COMMUNICATION: " + keyNotif);

                pG.setKeyComunnic(keyNotif);

                Firebase.query(this).getPlayerListener(String.valueOf(pG.getIdPlayer()));
                break;

            case "getPlayerListener":
                if(notify)
                {
                    Firebase.query(this).getTokenOfReceiverListener(listGamers.get(indexItemView).getIdPlayer());

                    Log.i(TAG, " ");
                    Log.i(TAG, "LISTENING KEY: " + pG.getKeyComunnic());
                    Log.i(TAG, " ");

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Communications").child(pG.getKeyComunnic());
                    CommunicationInterface.getInstance().start(this, ref, String.valueOf(pG.getIdPlayer()), String.valueOf(listGamers.get(indexItemView)));

                }
                notify = false;
                break;

            case "getTokenOfReceiverListener":
                dataSnapshot = (DataSnapshot)data.get("dataSnapshot");

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Token token = snapshot.getValue(Token.class);

                    Log.w(TAG, "TOKEN OF OPPONENT: " + token.getToken());

                    Log.i(TAG, " ");
                    Log.i(TAG, "LISTENING KEY AGAIN...: " + pG.getKeyComunnic());
                    Log.i(TAG, " ");


                    dataToPass = new Data(String.valueOf(pG.getIdPlayer()), R.mipmap.ic_launcher,
                            pG.getPlayerName() + " te ha invitado a una partida en Chessmate!!! \n\n" +
                                    "¿Aceptas el reto? ", "Invitación Chessmate", listGamers.get(indexItemView).getIdPlayer(),
                            pG.getMode(), CP.get().getConn(), listGamers.get(indexItemView).getPlayerName(),
                            pG.getPlayerName(), pG.getKeyComunnic());

                    Sender sender = new Sender(dataToPass, token.getToken());

                    Firebase.query(this).deliverNotification(sender);
                }
                break;

            case "Error":

                break;
        }
    }

    // COMMUNICATION IMPLEMENTATIONS

    @Override
    public void masterReplays(String data)
    {
        Log.w(TAG, "STARTING THE PEER ... >>> MASTER REPLAYS");
        gotoPlay();
    }

    @Override
    public void slaveReplays(String data)
    {
        Log.w(TAG, "STARTING THE PEER ... >>> SLAVE REPLAYS");
        gotoPlay();
    }



}
