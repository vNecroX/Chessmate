package com.jorjaiz.chessmateapplicationv1;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Database.MySQL;
import com.jorjaiz.chessmateapplicationv1.Database.Query;
import com.jorjaiz.chessmateapplicationv1.Firebase.FireQuery;
import com.jorjaiz.chessmateapplicationv1.Firebase.Firebase;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Settings.Data_MainConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

public class View_MainConfiguration extends AppCompatActivity implements Constants, Query.OnResponseDatabase,
        FireQuery.OnResponseFireQuery
{
    Switch swAutoSaved, swUseBoard, swChat, swNotif;
    Button btnSaveSettings;

    int [] oldSettings = new int[5];
    int [] newSettings = new int[5];
    int index = 0;
    boolean fOld = true, fDifferent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__main_configuration);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Log.w(TAG, "----------------------------------------------------------------------");
        Log.w(TAG, "VIEW_MAIN CONFIGURATION ON CREATE");

        swAutoSaved = findViewById(R.id.swAutoSaved);
        swUseBoard = findViewById(R.id.swUseBoard);
        swChat = findViewById(R.id.swChat);
        swNotif = findViewById(R.id.swNotif);
        btnSaveSettings = findViewById(R.id.btnSaveSettings);

        fDifferent = false;

        btnSaveSettings.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                readConfiguration();
                setConfiguration();
            }
        });

        MySQL.query(this).getConfiguration(CP.get().getIdPlayer()+"");
    }

    @Override
    public void onBackPressed()
    {
        Intent i = new Intent(this, View_MainInterface.class);
        i.putExtra("idPlayer", CP.get().getIdPlayer());
        i.putExtra("namePlayer", CP.get().getNamePlayer());
        this.startActivity(i);
    }

    public void displaySettings()
    {
        swAutoSaved.setChecked(CP.get().isAutosave());
        comparator(CP.get().isAutosave()?1:0);

        swUseBoard.setChecked(CP.get().isBoard());
        comparator(CP.get().isBoard()?1:0);

        swChat.setChecked(CP.get().isChat());
        comparator(CP.get().isBoard()?1:0);

        swNotif.setChecked(CP.get().isNotif());
        comparator(CP.get().isNotif()?1:0);

        fOld = false;
        index = 0;
    }

    public void readConfiguration()
    {
        if(swAutoSaved.isChecked()){
            comparator(1);
            CP.get().setAutosave(true);
        }
        else{
            comparator(0);
            CP.get().setAutosave(false);
        }

        if(swUseBoard.isChecked()){
            comparator(1);
            CP.get().setBoard(true);
        }
        else{
            comparator(0);
            CP.get().setBoard(false);
        }

        if(swChat.isChecked()){
            comparator(1);
            CP.get().setChat(true);
        }
        else{
            comparator(0);
            CP.get().setChat(false);
        }

        if(swNotif.isChecked()){
            comparator(1);
            CP.get().setNotif(true);
        }
        else{
            comparator(0);
            CP.get().setNotif(false);
        }

        for(int i=0; i<oldSettings.length; i++)
        {
            if(newSettings[i] != oldSettings[i])
            {
                fDifferent = true;
                break;
            }
        }
    }

    public void comparator(int value)
    {
        if(fOld)
        {
            oldSettings[index] = value;
            index++;
        }
        else
        {
            newSettings[index] = value;
            index++;
        }
    }

    public void setConfiguration()
    {
        if (fDifferent)
        {
            Response.Listener<String> response = new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response) { }
            };

            Data_MainConfiguration data_mainConfiguration =
                    new Data_MainConfiguration("setAllConfiguration",
                            CP.get().isAutosave()?1:0, CP.get().isBoard()?1:0,
                            CP.get().isChat()?1:0, CP.get().isNotif()?1:0, CP.get().getIdPlayer()+"", response);

            RequestQueue requestQueue = Volley.newRequestQueue(View_MainConfiguration.this);
            requestQueue.add(data_mainConfiguration);

            if (!CP.get().isBoard() && View_ConnectBoard.isConnected ||
                    !CP.get().isBoard() && View_BluetoothDevice.isConnected)
            {
                try
                {
                    View_ConnectBoard.isConnected = false;
                    View_ConnectBoard.fConnected = false;
                    View_BluetoothDevice.isConnected = false;
                    View_BluetoothDevice.fConnected = false;

                    if(BTCommunication.getInstance().getBluetoothSocket() != null)
                    {
                        BTCommunication.getInstance().getBluetoothSocket().close();
                        BTCommunication.getInstance().setBluetoothSocket(null);
                    }
                }
                catch (IOException e)
                {
                    Log.e(TAG, "VIEW_MAINCONFIGURATION setConfiguration exception " + e.toString());
                }
            }

            if (CP.get().isBoard() && View_ConnectBoard.isConnected ||
                    CP.get().isBoard() && View_BluetoothDevice.isConnected)
            {
                if(View_BluetoothDevice.fConnected)
                    View_BluetoothDevice.isConnected = true;

                if(View_ConnectBoard.fConnected)
                    View_ConnectBoard.isConnected = true;
            }

            Toast.makeText(this, "Configuración guardada.", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            Toast.makeText(this, "Cambie por lo menos una configuración.", Toast.LENGTH_SHORT).show();
            index = 0;
        }
    }

    // DATABASE IMPLEMENTATIONS

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {

    }

    @Override
    public void getResponseDB(HashMap<String, JSONArray> data, String purpose)
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
                                    CP.get().setChat(jsonObject.getInt("chat")==1);
                                    CP.get().setNotif(jsonObject.getInt("notif")==1);

                                    displaySettings();
                                }
                                catch (JSONException e)
                                {
                                    Log.e(TAG, "VIEW_MAINCONFIGURATION getConfiguration exception " + e.toString());
                                }
                            }
                            break;
                    }
                }
                else if(data.containsKey("ERROR"))
                {
                    Log.e(TAG, "VIEW_MAIN CONFIGURATION VolleyError getConfiguration");

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    Fragment_ErrorWithServer frag = new Fragment_ErrorWithServer();
                    transaction.replace(R.id.fragmentPlace, frag);
                    transaction.commit();
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "");
        }
    }
}
