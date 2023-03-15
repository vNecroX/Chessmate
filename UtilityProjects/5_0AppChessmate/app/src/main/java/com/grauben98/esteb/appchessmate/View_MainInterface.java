package com.grauben98.esteb.appchessmate;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
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
import com.grauben98.esteb.appchessmate.Fragment.Fragment_BoardConn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.TAG3;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.URL_DATABASE;

public class View_MainInterface extends AppCompatActivity {

    //Objects
        //Button
        Button btnConfig, btnPvPLocal;
        //BluetoothAdapter
        public static BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    //Variables
        //boolean
        public boolean firstUserAccess = true;
        public static boolean avoidFragmentBoardConn = false;

    //Constants
        //int
        public static int board = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_maininterface);

        btnConfig = findViewById(R.id.btnConfig);
        btnPvPLocal = findViewById(R.id.btnPvPLocal);

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMainConfiguration = new Intent(View_MainInterface.this, View_MainConfiguration.class);
                startActivity(intentMainConfiguration);
            }
        });

        btnPvPLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(View_ConnectBoard.isConnected || View_BluetoothDevice.isConnected){
                    Intent intentPeerLocalBluetooth = new Intent(View_MainInterface.this, View_PeerLocalBluetooth.class);
                    startActivity(intentPeerLocalBluetooth);
                }
                else{
                    Intent intentBluetoothDevice = new Intent(View_MainInterface.this, View_BluetoothDevice.class);
                    startActivity(intentBluetoothDevice);
                }
            }
        });

        Log.w(TAG3, "vBoardAfter, onCreate: " + View_MainConfiguration.board+"");
        getConfiguration();
        firstAccess();
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

                                board = jsonObject.getInt("board");
                            } catch (JSONException e) { e.printStackTrace(); }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { showToast("" + error.getMessage()); }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(View_MainInterface.this);
        requestQueue.add(jsonArrayRequest);
    }

    public void firstAccess() {
        if (firstUserAccess) {
            if (!View_ConnectBoard.isConnected) {
                firstUserAccess = false;
                showFragment_BoardConn();
            }
        }
    }

    public static BluetoothAdapter getBluetoothAdapter(){
        return bluetoothAdapter;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w(TAG3, "vBoardAfter, onResume: " + View_MainConfiguration.board+"");

        if(View_MainConfiguration.board == 1){
            if(!View_ConnectBoard.isConnected && !View_BluetoothDevice.isConnected){
                showFragment_BoardConn();
            }
        }
    }

    public void showFragment_BoardConn(){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment_BoardConn fragment_boardConn = new Fragment_BoardConn();
        fragmentTransaction.replace(R.id.fragmentBoardConn, fragment_boardConn);
        fragmentTransaction.commit();
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
