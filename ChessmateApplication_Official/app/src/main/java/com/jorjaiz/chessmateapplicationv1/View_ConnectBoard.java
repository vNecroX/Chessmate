package com.jorjaiz.chessmateapplicationv1;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.jorjaiz.chessmateapplicationv1.Bluetooth.BTCommunication;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.Parameters.ParamsGame;
import com.jorjaiz.chessmateapplicationv1.Settings.Data_MainConfiguration;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class View_ConnectBoard extends AppCompatActivity implements Constants, BTCommunication.OnBTCommunicationListener
{
    Button btnConnectBoard;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;

    public static BluetoothSocket bluetoothSocket;

    ProgressDialog progressDialog;

    ArrayList<String> deviceList;

    public static boolean isConnected, fConnected;
    public static boolean fromFragment;
    public static boolean isMaster;
    public boolean availableDevice;
    String deviceMAC = "98:D3:31:FC:1B:CA";

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__connect_board);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_CONNECTBOARD START ON CREATE");

            btnConnectBoard = findViewById(R.id.btnConnectBoard);

            bluetoothAdapter = View_MainInterface.getBluetoothAdapter();

            isMaster = false;

            btnConnectBoard.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if(bluetoothAdapter.isEnabled())
                    {
                        CP.get().setBoard(false);

                        if(View_ConnectBoard.fConnected || View_BluetoothDevice.fConnected)
                        {
                            gotoPeerLocalBluetooth();
                        }
                        else
                        {
                            connectingToDevice();
                            connectToMaster();
                        }
                    }
                    else
                    {
                        showToast("Active el Bluetooth.");
                    }
                }
            });

            deviceList = new ArrayList<>();

            isConnected = false;
            fConnected = false;
            fromFragment = false;
            availableDevice = false;

            IntentFilter intentFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, intentFound);

            IntentFilter intentDiscoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(broadcastReceiver, intentDiscoveryFinished);
        }
        catch(Exception e)
        {
            Log.e(TAG, "Exception VIEW_CONNECTBOARD onCreate: " + e.toString());
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
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    // FUNCTIONS

    public void connectingToDevice()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Conectando...");
        progressDialog.setMessage("Espere un momento.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void connectToMaster()
    {
        if(bluetoothAdapter.isEnabled())
        {
            if(bluetoothAdapter.isDiscovering())
                bluetoothAdapter.cancelDiscovery();

            ActivityCompat.requestPermissions(View_ConnectBoard.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            bluetoothAdapter.startDiscovery();
        }
    }

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver()
    {
        public void onReceive(Context context, Intent intent)
        {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action))
            {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                deviceList.add(device.getAddress());
            }

            if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                if(deviceList.size() != 0)
                {
                    for(int i = 0; i < deviceList.size(); i++)
                    {
                        if(deviceList.get(i).equals(deviceMAC))
                        {
                            availableDevice = true;
                            break;
                        }
                    }

                    if(availableDevice)
                    {
                        availableDevice = false;
                        connectToDevice(deviceMAC);
                    }
                    else
                    {
                        if(progressDialog != null)
                            progressDialog.cancel();

                        isMaster = false;
                        showToast( "Dispositivo no se encuentra disponible.");
                        gotoMainInterface();
                    }
                }
                else
                {
                    if(progressDialog != null)
                        progressDialog.cancel();

                    isMaster = false;
                    showToast("No se encontraron dispositivos Bluetooth.");
                }
            }
        }
    };

    public void connectToDevice(String device){
        try
        {
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(device);

            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();

            BTCommunication.getInstance().start(this, bluetoothSocket,
                    bluetoothSocket.getInputStream(), bluetoothSocket.getOutputStream());

            if(bluetoothSocket.isConnected())
            {
                Response.Listener<String> response = new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) { }
                };

                Data_MainConfiguration data_mainConfiguration =
                        new Data_MainConfiguration("setBoardConfiguration", 1, CP.get().getIdPlayer()+"", response);
                RequestQueue requestQueue = Volley.newRequestQueue(View_ConnectBoard.this);
                requestQueue.add(data_mainConfiguration);

                View_BluetoothDevice.fromPvPLocal = false;
                fromFragment = true;
                isConnected = true;
                fConnected = true;
                isMaster = true;
                CP.get().setBoard(true);

                showToast("Conectado al tablero.");
                gotoMainInterface();
            }
            else
            {
                isMaster = false;
                showToast("Conexión fallida.");
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, "VIEW_CONNECTBOARD connectToDevice exception");
            if(progressDialog != null)
                progressDialog.cancel();

            isMaster = false;
        }
    }

    public void gotoMainInterface()
    {
        Intent i = new Intent(this, View_MainInterface.class);
        i.putExtra("idPlayer", CP.get().getIdPlayer());
        i.putExtra("namePlayer", CP.get().getNamePlayer());
        this.startActivity(i);
    }

    public void gotoPeerLocalBluetooth()
    {
        Intent intentPeerLocalBluetooth = new Intent(this, View_PeerLocalBluetooth.class);
        startActivity(intentPeerLocalBluetooth);
    }

    @Override
    public void bluetoothReply(String data)
    {

    }

    public void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
