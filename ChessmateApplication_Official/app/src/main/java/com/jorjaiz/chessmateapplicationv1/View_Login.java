package com.jorjaiz.chessmateapplicationv1;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jorjaiz.chessmateapplicationv1.Adapters.Adapter_GamersList;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.Firebase.Data;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Firebase.Notification;
import com.jorjaiz.chessmateapplicationv1.Firebase.PlayerFire;
import com.jorjaiz.chessmateapplicationv1.Firebase.Sender;
import com.jorjaiz.chessmateapplicationv1.Firebase.Token;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class View_Login extends AppCompatActivity implements Constants, Query.OnResponseDatabase,
        FireQuery.OnResponseFireQuery
{
    EditText eTUserName, eTUserPassword;

    String anUserName;
    int anUserId;

    boolean touched = false;

    static boolean doingQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__login);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_LOGIN ON CREATE");

            eTUserName = findViewById(R.id.etUserName);
            eTUserPassword = findViewById(R.id.etUserPsw);

            doingQuery = false;

            findViewById(R.id.btnNext).setOnClickListener(v -> verifyLogin());

            RelativeLayout rL = findViewById(R.id.rLLogin);

            AnimationDrawable animDraw = (AnimationDrawable) this.getResources().getDrawable(R.drawable.gradient_blackb_list);
            rL.setBackground(animDraw);
            animDraw.setEnterFadeDuration(2000);
            animDraw.setExitFadeDuration(2000);
            animDraw.start();
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_LOGIN onCreate: " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        if(!doingQuery)
        {
            Intent i = new Intent(this, View_ApplicationStart.class);
            this.startActivity(i);
        }
    }

    public void verifyLogin()
    {
        if(!touched)
        {
            touched = true;

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            Fragment_Loading frag = new Fragment_Loading();
            transaction.replace(R.id.fragmentPlace2, frag);
            transaction.commit();

            doingQuery = true;
            MySQL.query(this).verifyLogin(eTUserName.getText().toString(), eTUserPassword.getText().toString());
        }
    }

    public void login(boolean login)
    {
        try
        {
            if(login)
            {
                Firebase.query(this).getPlayers();
            }
            else
            {
                Toast.makeText(this, "Nombre de usuario o contraseña incorrecta", Toast.LENGTH_SHORT).show();
                getSupportFragmentManager().beginTransaction().
                        remove(getSupportFragmentManager().findFragmentById(R.id.fragmentPlace2)).commit();
                touched = false;

                doingQuery = false;
            }

        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_LOGIN login: " + e.toString());
        }
    }

    // DATABASE IMPLEMENTATIONS

    // MYSQL

    @Override
    public void getResponseDB(HashMap<String, JSONArray> data, String purpose)
    {
        Log.w(TAG, ">>>>> PURPOSE: " + purpose);

        if(data==null)
        {
            Log.e(TAG, "FATAL ERROR (DATA NULL) VIEW_LOGIN getResponseDB");
        }
        else
        {
            if(data.containsKey("DATA"))
            {
                try
                {
                    switch(purpose)
                    {
                        case "verifyLogin":
                            JSONArray jA = data.get("DATA");

                            boolean l;
                            String userName = "";
                            int idUser = 0;

                            if((jA!=null?jA.length():0) == 0)
                            {
                                l = false;
                            }
                            else
                            {
                                JSONObject object = jA.getJSONObject(0);
                                userName = object.getString("userName");
                                idUser = Integer.parseInt(object.getString("idUser"));
                                l = true;
                            }

                            anUserId = idUser;
                            anUserName = userName;

                            login(l);
                            break;
                    }
                }
                catch (JSONException e)
                {
                    Log.e(TAG, "Exception VIEW_LOGIN getResponseDB: " + e.toString());
                }
            }
            else if(data.containsKey("ERROR"))
            {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                Fragment_ErrorWithServer frag = new Fragment_ErrorWithServer();
                transaction.replace(R.id.fragmentPlace, frag);
                transaction.commit();

                touched = false;

                doingQuery = false;

                try
                {
                    getSupportFragmentManager().beginTransaction().
                            remove(getSupportFragmentManager().findFragmentById(R.id.fragmentPlace2)).commit();
                }
                catch (Exception e)
                {
                    Log.e(TAG, "Error detected, theres no problem, do not worry");
                }
            }
        }
    }

    // FIREBASE

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {
        Log.w(TAG, ">>>>> PURPOSE: " + purpose);

        Intent i;

        switch(purpose)
        {
            case "registerPlayer":

                Firebase.query(this).updateToken(FirebaseInstanceId.getInstance().getToken(), anUserId);

                View_MainInterface.firstUserAccess = true;

                i = new Intent(View_Login.this, View_MainInterface.class);
                i.putExtra("idPlayer", (int)data.get("idPlayer"));
                i.putExtra("namePlayer", String.valueOf(data.get("namePlayer")));
                startActivity(i);

                break;

            case "getPlayers":
                boolean registered = false;

                DataSnapshot dataSnapshot = (DataSnapshot)data.get("dataSnapshot");

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    try
                    {
                        if(!snapshot.getKey().equals(""+0))
                        {
                            PlayerFire playerFire = snapshot.getValue(PlayerFire.class);

                            if(playerFire.getNamePlayer().equals(eTUserName.getText().toString()) && playerFire.getConnection() == OFFLINE)
                            {
                                Firebase.query(this).updateToken(FirebaseInstanceId.getInstance().getToken(), anUserId);

                                View_MainInterface.firstUserAccess = true;

                                i = new Intent(View_Login.this, View_MainInterface.class);
                                i.putExtra("idPlayer", anUserId);
                                i.putExtra("namePlayer", anUserName);
                                startActivity(i);
                                registered = true;
                                break;
                            }
                            else if (playerFire.getNamePlayer().equals(eTUserName.getText().toString()) &&
                                    (playerFire.getConnection() == ONLINE || playerFire.getConnection() == BUSSY))
                            {
                                Toast.makeText(this, "Lo sentimos, cuenta esta activa en este momento", Toast.LENGTH_SHORT).show();
                                registered = true;
                                touched = false;

                                try
                                {
                                    getSupportFragmentManager().beginTransaction().
                                            remove(getSupportFragmentManager().findFragmentById(R.id.fragmentPlace2)).commit();
                                }
                                catch (Exception e)
                                {
                                    Log.e(TAG, "");
                                }

                                break;
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        Log.w(TAG, "Strange 0 User in Firebase");
                        doingQuery = false;
                    }
                }

                if(!registered)
                    Firebase.query(this).registerPlayer(anUserId, anUserName);

                break;
        }

        doingQuery = false;
    }

}