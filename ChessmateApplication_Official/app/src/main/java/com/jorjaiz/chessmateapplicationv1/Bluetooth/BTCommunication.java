package com.jorjaiz.chessmateapplicationv1.Bluetooth;

import android.bluetooth.BluetoothSocket;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Firebase.Communication;
import com.jorjaiz.chessmateapplicationv1.Firebase.CommunicationInterface;
import com.jorjaiz.chessmateapplicationv1.View_PiecesColor_Bluetooth;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class BTCommunication implements Constants
{
    public interface OnBTCommunicationListener
    {
        void bluetoothReply(String data);
    }

    private static BTCommunication mInstance;

    private BTCommunication.OnBTCommunicationListener mListener;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private String result = "";

    private BTCommunication()
    {
    }

    public OnBTCommunicationListener getmListener() {
        return mListener;
    }

    public static BTCommunication getInstance()
    {
        if(mInstance == null)
            mInstance = new BTCommunication();

        return mInstance;
    }

    public void start(BTCommunication.OnBTCommunicationListener listener, BluetoothSocket bTSocket,
                      InputStream inputStream, OutputStream outputStream)
    {
        setListener(listener);

        this.bluetoothSocket = bTSocket;
        this.inputStream = inputStream;
        this.outputStream = outputStream;

        Log.w(TAG, "<------  BLUETOOTH COMMUNICATION READY  ------>");
    }

    public void setListener(BTCommunication.OnBTCommunicationListener listener)
    {
        mListener = listener;
    }

    public void listenForData()
    {
        try
        {
            int byteCount = inputStream.available();

            if(byteCount > 0)
            {
                byte[] rawBytes = new byte[byteCount];

                inputStream.read(rawBytes);

                for(byte b : rawBytes)
                {
                    result += (char) b;
                }

                mListener.bluetoothReply("");
            }
            else
            {
                if(!result.equals(""))
                    Log.i(TAG, "<-------- DATA LISTENED: " + result);

                mListener.bluetoothReply(result);

                result = "";
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "BTCommunication listenForData exception " + e.toString());
            mListener.bluetoothReply("ERROR");
        }
    }

    public void sendData(String data)
    {
        try
        {
            byte[] buffer = data.getBytes();
            outputStream.write(buffer);

            Log.i(TAG, "--------> DATA SENT: " + data);
        }
        catch (Exception ex)
        {
            Log.e(TAG, "BTCommunication sendData exception " + ex.toString());
            mListener.bluetoothReply("ERROR");
        }
    }

    public BluetoothSocket getBluetoothSocket() {
        return bluetoothSocket;
    }
    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.bluetoothSocket = bluetoothSocket;
    }
}
