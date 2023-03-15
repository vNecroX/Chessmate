package com.grauben98.esteb.appchessmate;

import android.bluetooth.BluetoothSocket;
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
import com.grauben98.esteb.appchessmate.Data.Data_MainConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.TAG4;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.URL_DATABASE;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.cSetAllConfiguration;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.idUser;

public class View_MainConfiguration extends AppCompatActivity {

    //Objects
        //Button
        Button btnAccept;
        //Switch
        Switch swAutoSaved, swUseBoard, swChat, swNotif;
        //BluetoothSocket
        BluetoothSocket bluetoothSocket;

    public int [] oldSettings = new int[5];
    public int [] newSettings = new int[5];
    public int index = 0;

    public static int autosave = 0, board = 0, chat = 0, notif = 0;

    public boolean fOld = true, fDifferent = false;
    public static boolean isMainConfigurationOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_mainconfiguration);

        swAutoSaved = findViewById(R.id.swAutoSaved);
        swUseBoard = findViewById(R.id.swUseBoard);
        swChat = findViewById(R.id.swChat);
        swNotif = findViewById(R.id.swNotif);

        btnAccept = findViewById(R.id.btnAccept);

        if(View_ConnectBoard.isConnected){
            bluetoothSocket = View_ConnectBoard.getBluetoothSocket();
        }

        if(View_BluetoothDevice.isConnected){
            bluetoothSocket = View_BluetoothDevice.getBluetoothSocket();
        }

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readConfiguration();
                setConfiguration();
            }
        });

        getConfiguration();
    }

    public void getConfiguration(){
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                URL_DATABASE,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject jsonObject;

                        for (int i = 0; i < response.length(); i++) {
                            try {
                                jsonObject = response.getJSONObject(i);

                                autosave = jsonObject.getInt("autos");
                                board = jsonObject.getInt("board");
                                chat = jsonObject.getInt("chat");
                                notif = jsonObject.getInt("notif");
                                displaySettings(autosave, board, chat, notif);
                            } catch (JSONException e) { /*e.printStackTrace();*/ }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { /*showToast("" + error.getMessage());*/ }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(View_MainConfiguration.this);
        requestQueue.add(jsonArrayRequest);
    }

    public void displaySettings(int autosave, int board, int chat, int notif){
        if(autosave == 1){
            swAutoSaved.setChecked(true);
            comparator(autosave);
        }
        else{
            swAutoSaved.setChecked(false);
            comparator(autosave);
        }

        if(board == 1){
            swUseBoard.setChecked(true);
            comparator(board);
        }
        else{
            swUseBoard.setChecked(false);
            comparator(board);
        }

        if(chat == 1){
            swChat.setChecked(true);
            comparator(chat);
        }
        else{
            swChat.setChecked(false);
            comparator(chat);
        }

        if(notif == 1){
            swNotif.setChecked(true);
            comparator(notif);
        }
        else{
            swNotif.setChecked(false);
            comparator(notif);
        }
        fOld = false;
        index = 0;
    }

    public void readConfiguration(){
        if(swAutoSaved.isChecked()){
            comparator(1);
            autosave = 1;
        }
        else{
            comparator(0);
            autosave = 0;
        }

        if(swUseBoard.isChecked()){
            comparator(1);
            board = 1;
        }
        else{
            comparator(0);
            board = 0;
        }

        if(swChat.isChecked()){
            comparator(1);
            chat = 1;
        }
        else{
            comparator(0);
            chat = 0;
        }

        if(swNotif.isChecked()){
            comparator(1);
            notif = 1;
        }
        else{
            comparator(0);
            notif = 0;
        }

        for(int i=0; i<oldSettings.length; i++){
            if(newSettings[i] != oldSettings[i]){
                fDifferent = true;
                break;
            }
        }
    }

    public void comparator(int value){
        if(fOld){
            oldSettings[index] = value;
            index++;
        }
        else{
            newSettings[index] = value;
            index++;
        }
    }

    public void setConfiguration() {
        if (fDifferent) {
            Response.Listener<String> response = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) { }
            };

            Data_MainConfiguration data_mainConfiguration = new Data_MainConfiguration(cSetAllConfiguration, autosave, board, chat, notif, idUser, response);
            RequestQueue requestQueue = Volley.newRequestQueue(View_MainConfiguration.this);
            requestQueue.add(data_mainConfiguration);

            if (board == 0 && View_ConnectBoard.isConnected ||
                    board == 0 && View_BluetoothDevice.isConnected){
                try {
                    View_ConnectBoard.isConnected = false;
                    View_ConnectBoard.fConnected = false;
                    View_BluetoothDevice.isConnected = false;
                    View_BluetoothDevice.fConnected = false;
                    bluetoothSocket.close();
                } catch (IOException e) { /*e.printStackTrace();*/ }
            }

            if(board == 1 && View_ConnectBoard.fConnected ||
                    board == 1 && View_BluetoothDevice.fConnected){
                if(View_BluetoothDevice.fConnected){
                    View_BluetoothDevice.isConnected = true;
                    View_BluetoothDevice.fConnected = true;
                }

                if(View_ConnectBoard.fConnected){
                    View_ConnectBoard.isConnected = true;
                    View_ConnectBoard.fConnected = true;
                }
            }

            Toast.makeText(this, "Configuración guardada.", Toast.LENGTH_SHORT).show();
            Log.w(TAG4, "vBoard: " + board + "");
            finish();
        } else {
            Toast.makeText(this, "Cambie por lo menos una configuración.", Toast.LENGTH_SHORT).show();
            index = 0;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(board == 1){
            board = 0;
        }
    }
}