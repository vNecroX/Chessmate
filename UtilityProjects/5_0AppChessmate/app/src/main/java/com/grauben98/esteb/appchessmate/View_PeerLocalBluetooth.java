package com.grauben98.esteb.appchessmate;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.grauben98.esteb.appchessmate.Fragment.Fragment_BoardConn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.TAG5;

public class View_PeerLocalBluetooth extends AppCompatActivity {

    //Objects
    //BTConnection_FragmentBoardConn
    //BTConnection_FragmentBoardConn btConnection_fragmentBoardConn = new BTConnection_FragmentBoardConn(this);
    //Timer
    public static Timer requestSlave = new Timer();
    Timer responseMaster = new Timer();
    public static Timer requestMaster = new Timer();
    Timer responseSlave = new Timer();
    //InputStream
    public static InputStream inputStream;
    //OutputStream
    public static OutputStream outputStream;
    //ProgressDialog
    ProgressDialog progressDialog;

    //Variables
    //int
    public int byteCount = 0;
    //String
    public String responseUser = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peerlocalbluetooth);

        /*if(View_ConnectBoard.isConnected){
        }

        if(View_MainInterface.isConnected){
        }*/

        waitingAnotherPlayer();

        if(View_BluetoothDevice.fromPvPLocal){
            Log.w(TAG5, "IOStream!");

            inputStream = View_BluetoothDevice.getInputStream();
            outputStream = View_BluetoothDevice.getOutputStream();

            View_BluetoothDevice.fromPvPLocal = false;
            requestSlaveReady();
            responseSlaveListening();
        }

        if(View_ConnectBoard.fromFragment){
            inputStream = View_ConnectBoard.getInputStream();
            outputStream = View_ConnectBoard.getOutputStream();

            View_ConnectBoard.fromFragment = false;
            responseMasterListening();
        }
    }

    public void waitingAnotherPlayer(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Esperando jugador...");
        progressDialog.setMessage("Disfrute la partida.");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void requestSlaveReady(){
        final Handler handler = new Handler();
        requestSlave.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        slaveReady();
                    }
                });
            }
        },0,500);
    }

    public void slaveReady(){
        try{
            String ready = "S";
            byte[] buffer = ready.getBytes();
            try {
                outputStream.write(buffer);
            }catch(IOException e){
                e.printStackTrace();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void responseMasterListening(){
        final Handler handler = new Handler();
        responseMaster.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            masterListening();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        },0,10);
    }

    public void masterListening() throws IOException{
        if(View_ConnectBoard.isConnected){
            byteCount = inputStream.available(); //This.
        }
        else if(View_BluetoothDevice.isConnected){
            byteCount = View_BluetoothDevice.inputStream.available();
        }

        Log.w(TAG5, "byteCount: " + byteCount+"");

        if(byteCount > 0){
            byte[] rawBytes = new byte[byteCount];
            inputStream.read(rawBytes);

            responseUser = "";

            for(byte b : rawBytes){
                responseUser += (char) b;
            }

            Log.e(TAG5, "responseUser: " + responseUser);

            if(responseUser.equals("S")){
                responseMaster.cancel();
                progressDialog.cancel();
                Log.w(TAG5, "slaveCaptioned!");
                requestMasterReady();
                openDummy();
            }
        }
    }

    public void requestMasterReady(){
        final Handler handler = new Handler();
        requestMaster.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        masterReady();
                    }
                });
            }
        },0,500);
    }

    public void masterReady(){
        try{
            String ready = "M";
            byte[] buffer = ready.getBytes();
            try {
                outputStream.write(buffer);
            }catch(IOException e){
                e.printStackTrace();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void responseSlaveListening(){
        final Handler handler = new Handler();
        responseSlave.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            slaveListening();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        },0,10);
    }

    public void slaveListening() throws IOException{
        if(View_ConnectBoard.isConnected){
            byteCount = inputStream.available();
        }
        else if(View_BluetoothDevice.isConnected){
            byteCount = View_BluetoothDevice.inputStream.available();
        }

        Log.w(TAG5, "byteCount: " + byteCount+"");

        if(byteCount > 0){
            byte[] rawBytes = new byte[byteCount];
            inputStream.read(rawBytes);

            responseUser = "";

            for(byte b : rawBytes){
                responseUser += (char) b;
            }

            Log.e(TAG5, "responseUser: " + responseUser);

            if(responseUser.equals("M")){
                responseSlave.cancel();
                progressDialog.cancel();
                Log.w(TAG5, "masterCaptioned!");
                openDummy();
            }
        }
    }

    public void openDummy(){
        Intent intentBluetoothDummy = new Intent(this, View_BluetoothDummy.class);
        startActivity(intentBluetoothDummy);
    }
}
