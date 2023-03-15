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
import android.os.Handler;
import android.support.annotation.Nullable;
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

public class View_BluetoothDevice extends AppCompatActivity implements Constants, BTCommunication.OnBTCommunicationListener
{
    Button btnMaster, btnSlave;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice bluetoothDevice;

    public static BluetoothSocket bluetoothSocket;

    ProgressDialog progressDialog;
    ArrayList<String> deviceList = new ArrayList<>();

    public static boolean isConnected = false, fConnected = false;
    public static boolean isMaster = false;
    public static boolean fromPvPLocal = false;
    public boolean availableDevice = false;
    String deviceMAC;

    private static final int REQUEST_ENABLE_BT = 0;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;

    private ParamsGame pG;

    boolean needToWait;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view__bluetooth_device);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            Log.w(TAG, "----------------------------------------------------------------------");
            Log.w(TAG, "VIEW_BLUETOOHDEVICE START ON CREATE");

            btnMaster = findViewById(R.id.btnMaster);
            btnSlave = findViewById(R.id.btnSlave);

            bluetoothAdapter = View_MainInterface.getBluetoothAdapter();

            Bundle parameters = getIntent().getExtras();
            pG = parameters.getParcelable("pG");

            needToWait = false;

            btnMaster.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    isMaster = true;
                    resumeConnection();
                }
            });

            btnSlave.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    isMaster = false;
                    resumeConnection();
                }
            });

            IntentFilter intentFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, intentFound);

            IntentFilter intentDiscoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(broadcastReceiver, intentDiscoveryFinished);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception VIEW_BLUETOOTHDEVICE onCreate: " + e.toString());
        }
    }

    @Override
    public void onBackPressed()
    {
        if(!needToWait)
        {
            Intent i = new Intent(this, View_MainInterface.class);
            i.putExtra("idPlayer", CP.get().getIdPlayer());
            i.putExtra("namePlayer", CP.get().getNamePlayer());
            this.startActivity(i);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    public void resumeConnection()
    {
        needToWait = true;

        if(bluetoothAdapter.isEnabled())
        {
            connectingToDevice();
            connectToMasterSlave();
        }
        else
        {
            turningOnBluetooth();
        }
    }

    public void turningOnBluetooth()
    {
        Intent intentEnable = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intentEnable, REQUEST_ENABLE_BT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode)
        {
            case REQUEST_ENABLE_BT:

                if(resultCode == RESULT_OK)
                {
                    if(isMaster)
                        btnMaster.performClick();
                    else
                        btnSlave.performClick();
                }
                else
                {
                    showToast("Active el Bluetooth para continuar...");
                    needToWait = false;
                    isMaster = false;
                }

                break;
        }
    }

    public void connectingToDevice()
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Conectando...");
        progressDialog.setMessage("Espere un momento.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void connectToMasterSlave()
    {
        if(isMaster)
            deviceMAC = "98:D3:31:FC:1B:CA";
        else
            deviceMAC = "00:21:13:00:F1:55";

        discoveringBluetooth();
    }

    private void discoveringBluetooth()
    {
        if(bluetoothAdapter.isEnabled())
        {
            if(bluetoothAdapter.isDiscovering())
                bluetoothAdapter.cancelDiscovery();

            ActivityCompat.requestPermissions(View_BluetoothDevice.this,
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
                if (deviceList.size() != 0)
                {
                    for (int i = 0; i < deviceList.size(); i++)
                    {
                        if (deviceList.get(i).equals(deviceMAC))
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

                        needToWait = false;
                        isMaster = false;
                        showToast( "Dispositivo no se encuentra disponible.");
                    }
                }
                else
                {
                    if(progressDialog != null)
                        progressDialog.cancel();

                    needToWait = false;
                    isMaster = false;
                    showToast("No se encontró tablero Chessmate.");
                }
            }
        }
    };

    public void connectToDevice(String device)
    {
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
                if(!CP.get().isBoard())
                {
                    Response.Listener<String> response = new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) { }
                    };

                    Data_MainConfiguration data_mainConfiguration =
                            new Data_MainConfiguration("setBoardConfiguration", 1, CP.get().getIdPlayer()+"", response);
                    RequestQueue requestQueue = Volley.newRequestQueue(View_BluetoothDevice.this);
                    requestQueue.add(data_mainConfiguration);
                }

                if(progressDialog != null)
                    progressDialog.cancel();

                View_ConnectBoard.fromFragment = false;
                fromPvPLocal = true;
                isConnected = true;
                fConnected = true;
                CP.get().setBoard(true);

                gotoPeerLocalBluetooth();
            }
            else
            {
                isMaster = false;
                showToast("Conexión fallida con tablero Chessmate.");
            }

            needToWait = false;
        }
        catch(Exception e)
        {
            Log.e(TAG, "VIEW_BLUETOOTHDEVICE connectToDevice exception");
            if(progressDialog != null)
                progressDialog.cancel();

            isMaster = false;

            needToWait = false;
        }
    }

    public void gotoPeerLocalBluetooth()
    {
        if(isMaster)
        {
            Log.i(TAG, "MASTER GOIN' TO PEER LOCAL");
            CP.get().setLastKindOfPlayer(MASTER);
            pG.setKindPlayer(MASTER);
        }
        else
        {
            Log.i(TAG, "SLAVE GOIN' TO PEER LOCAL");
            CP.get().setLastKindOfPlayer(SLAVE);
            pG.setKindPlayer(SLAVE);
        }

        needToWait = false;

        Intent intentPeerLocalBluetooth = new Intent(this, View_PeerLocalBluetooth.class);
        intentPeerLocalBluetooth.putExtra("pG", pG);
        startActivity(intentPeerLocalBluetooth);
        finish();
    }

    // BLUETOOTH COMMUNICATION IMPLEMENTATIONS

    @Override
    public void bluetoothReply(String data)
    {

    }

    public void showToast(String text)
    {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
