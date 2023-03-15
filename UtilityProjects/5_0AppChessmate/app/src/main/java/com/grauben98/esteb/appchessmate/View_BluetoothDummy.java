package com.grauben98.esteb.appchessmate;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;

import static com.grauben98.esteb.appchessmate.Interface.Interface_Constants.TAG2;

public class View_BluetoothDummy extends AppCompatActivity {

    //Objects
        //Button
        Button btnSend;
        //EditText
        EditText etYou, etAnother;
        //Timer
        Timer listenerData = new Timer();
        //InputStream
        InputStream inputStream;
        //OutputStream
        OutputStream outputStream;

     //Variables
        //int
        int byteCount = 0;
        int contPackages = 0;
        //String
        String result = "", segmentedResult = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_bluetoothdummy);

        btnSend = findViewById(R.id.btnSend);
        etYou = findViewById(R.id.etYou);
        etAnother = findViewById(R.id.etAnother);

        View_PeerLocalBluetooth.requestMaster.cancel();
        View_PeerLocalBluetooth.requestSlave.cancel();

        if(View_ConnectBoard.isConnected){
            inputStream = View_ConnectBoard.getInputStream();
            outputStream = View_ConnectBoard.getOutputStream();
        }

        if(View_BluetoothDevice.isConnected){
            inputStream = View_BluetoothDevice.getInputStream();
            outputStream = View_BluetoothDevice.getOutputStream();
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();
            }
        });

        timerData();
    }

    public void sendData(){
        try{
            String msg = etYou.getText().toString();
            byte[] buffer = msg.getBytes();
            try {
                outputStream.write(buffer);
            }catch(IOException e){
                e.printStackTrace();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void timerData(){
        final Handler handler = new Handler();
        listenerData.schedule(new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            beginListenForData();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        },1000,1000);
    }

    public void beginListenForData() throws IOException{
        if(View_ConnectBoard.isConnected){
            byteCount = View_ConnectBoard.inputStream.available();
        }
        else if(View_BluetoothDevice.isConnected){
            byteCount = View_BluetoothDevice.inputStream.available();
        }

        Log.i(TAG2, "byteCount: " + byteCount+"");
        if(byteCount > 0){
            contPackages++;
            byte[] rawBytes = new byte[byteCount];
            Log.w(TAG2, "byte[]: " + rawBytes.length+"");
            inputStream.read(rawBytes);

            for(byte b : rawBytes){
                if(contPackages == 1){
                    result += (char) b;
                }
                else if(contPackages > 1){
                    segmentedResult += (char) b;
                }
            }

            if(contPackages == 1){
                Log.w(TAG2, "contPackaged: " + contPackages+"");
                Log.e(TAG2, "text: " + result);
            }
            else if(contPackages > 1){
                result += segmentedResult;
                segmentedResult = "";
                Log.w(TAG2, "contPackaged: " + contPackages+"");
                Log.e(TAG2, "text: " + result);
            }

            byteCount = 0;
        }
        else{
            etAnother.setText(result);  //Here Arduino Package is full.
            segmentedResult = "";
            result = "";
            contPackages = 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        listenerData.cancel();
    }
}
