package com.grauben98.esteb.appchessmate;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.grauben98.esteb.appchessmate.Data.Data_MainConfiguration;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.cSetBoardConfiguration;
import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.idUser;

public class View_BluetoothDevice extends AppCompatActivity {

    //Objects
        //Button
        Button btnMaster, btnSlave;
        //BluetoothAdapter
        BluetoothAdapter bluetoothAdapter;
        //BluetoothDevice
        BluetoothDevice bluetoothDevice;
        //BluetoothSocket
        public static BluetoothSocket bluetoothSocket;
        //InputStream
        public static InputStream inputStream;
        //OutputStream
        public static OutputStream outputStream;
        //ProgressDialog
        ProgressDialog progressDialog;

    //Lists
        //ArrayList
        ArrayList<String> deviceList = new ArrayList<>();

    //Variables
        //boolean
        public static boolean isConnected = false, fConnected = false;
        public boolean isMaster = false;
        public boolean availableDevice = false;
        public static boolean fromPvPLocal = false;
        //String
        public String deviceMAC;

    //Constants
        //int
        private static final int REQUEST_ENABLE_BT = 0;
        private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_bluetoothdevice);

        btnMaster = findViewById(R.id.btnMaster);
        btnSlave = findViewById(R.id.btnSlave);

        bluetoothAdapter = View_MainInterface.getBluetoothAdapter();

        btnMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMaster = true;
                resumeConnection();
            }
        });

        btnSlave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMaster = false;
                resumeConnection();
            }
        });

        IntentFilter intentFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(broadcastReceiver, intentFound);

        IntentFilter intentDiscoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentDiscoveryFinished);
    }

    public void resumeConnection(){
        if(bluetoothAdapter.isEnabled()){
            connectingToDevice();
            connectToMasterSlave();
        }
        else{
            turningOnBluetooth();
        }
    }

    public void turningOnBluetooth(){
        Intent intentEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intentEnable, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK){
                    if(isMaster){
                        btnMaster.performClick();
                    }
                    else{
                        btnSlave.performClick();
                    }
                }
                else{
                    showToast("Active el Bluetooth para continuar...");
                }
                break;
        }
    }

    public void connectingToDevice(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Conectando...");
        progressDialog.setMessage("Espere un momento.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void connectToMasterSlave(){
        if(isMaster){
            deviceMAC = "00:21:13:00:F1:55";
        }
        else{
            deviceMAC = "98:D3:31:FC:1B:CA";
        }

        if(bluetoothAdapter.isEnabled()){
            if(bluetoothAdapter.isDiscovering()){
                bluetoothAdapter.cancelDiscovery();
            }
            ActivityCompat.requestPermissions(View_BluetoothDevice.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            bluetoothAdapter.startDiscovery();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver(){
        public void onReceive(Context context, Intent intent){
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device.getAddress());
            }

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                if (deviceList.size() != 0) {
                    for (int i = 0; i < deviceList.size(); i++) {
                        if (deviceList.get(i).equals(deviceMAC)) {
                            availableDevice = true;
                            break;
                        }
                    }

                    if(availableDevice){
                        availableDevice = false;
                        connectToDevice(deviceMAC);
                    }
                    else{
                        progressDialog.cancel();
                        showToast("Este dispositivo ya esta en uso.");
                    }
                } else {
                    progressDialog.cancel();
                    showToast("No se encontraron dispositivos Bluetooth.");
                }
            }
        }
    };

    public void connectToDevice(String device){
        try{
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(device);
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            inputStream = bluetoothSocket.getInputStream();

            if(bluetoothSocket.isConnected()){

                if(View_MainInterface.board == 0 || View_MainConfiguration.board == 0){
                    Response.Listener<String> response = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) { }
                    };

                    Data_MainConfiguration data_mainConfiguration = new Data_MainConfiguration(cSetBoardConfiguration, 1, idUser, response);
                    RequestQueue requestQueue = Volley.newRequestQueue(View_BluetoothDevice.this);
                    requestQueue.add(data_mainConfiguration);
                }

                progressDialog.cancel();
                fromPvPLocal = true;
                isConnected = true;
                fConnected = true;
                View_MainInterface.board = 0;
                View_MainConfiguration.board = 0;
                gotoPeerLocalBluetooth();
            }
            else{
                showToast("Conexión fallida.");
            }
        }catch(Exception e){ progressDialog.cancel(); /*showToast("Conexión fallida...");*/ }
    }

    public void gotoPeerLocalBluetooth(){
        Intent intentPeerLocalBluetooth = new Intent(this, View_PeerLocalBluetooth.class);
        startActivity(intentPeerLocalBluetooth);
        finish();
    }

    public static InputStream getInputStream(){
        return inputStream;
    }

    public static OutputStream getOutputStream(){
        return outputStream;
    }

    public static BluetoothSocket getBluetoothSocket(){
        return bluetoothSocket;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
