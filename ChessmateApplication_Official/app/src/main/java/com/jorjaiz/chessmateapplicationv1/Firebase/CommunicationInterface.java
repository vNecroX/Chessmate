package com.jorjaiz.chessmateapplicationv1.Firebase;

import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;

import java.util.HashMap;

public class CommunicationInterface implements Constants
{
    public interface OnCommunicationListener
    {
        void masterReplays(String data);
        void slaveReplays(String data);
    }

    private static CommunicationInterface mInstance;
    private OnCommunicationListener mListener;
    private DatabaseReference reference;
    private ValueEventListener valueEvent;

    private String idPlayerM;
    private String idPlayerS;

    private CommunicationInterface()
    {
    }

    public DatabaseReference getReference() {
        return reference;
    }

    public static CommunicationInterface getInstance()
    {
        if(mInstance == null)
            mInstance = new CommunicationInterface();

        return mInstance;
    }

    public void start(OnCommunicationListener listener, DatabaseReference r, String idPlayerM, String idPlayerS)
    {
        setListener(listener);
        initialize(r, idPlayerM, idPlayerS);
        createCommunication();
        listenReplies();

        Log.i(TAG, " ");
        Log.i(TAG, ". . . <% COMMUNICATION READY %>");
        Log.i(TAG, " ");
    }

    public void setListener(OnCommunicationListener listener)
    {
        mListener = listener;
    }

    private void initialize(DatabaseReference r, String idM, String idS)
    {
        this.reference = r;
        this.idPlayerM = idM;
        this.idPlayerS = idS;
    }

    private void createCommunication()
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("idPlayerM", idPlayerM);
        hashMap.put("idPlayerS", idPlayerS);
        hashMap.put("isPlayerM", false);
        hashMap.put("isPlayerS", false);
        hashMap.put("data", "");
        reference.updateChildren(hashMap);
    }

    public void respondAsSlave(String data)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isPlayerM", false);
        hashMap.put("isPlayerS", true);
        hashMap.put("data", data);
        reference.updateChildren(hashMap);
        Log.i(TAG, "--------> SLAVE SAYS: " + data);
    }

    public void respondAsMaster(String data)
    {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isPlayerM", true);
        hashMap.put("isPlayerS", false);
        hashMap.put("data", data);
        reference.updateChildren(hashMap);
        Log.i(TAG, "--------> MASTER SAYS: " + data);
    }

    private void listenReplies()
    {
        try
        {
            valueEvent = new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Log.i(TAG, " ");

                    Communication n = dataSnapshot.getValue(Communication.class);

                    boolean isPlayerM = n.isPlayerM;
                    boolean isPlayerS = n.isPlayerS;

                    if (!isPlayerM && isPlayerS)
                    {
                        Log.i(TAG, "<-------- SLAVE REPLAYS: " + n.getData());
                        mListener.slaveReplays(n.getData());
                    }
                    else if (isPlayerM && !isPlayerS)
                    {
                        Log.i(TAG, "<-------- MASTER REPLAYS: " + n.getData());
                        mListener.masterReplays(n.getData());
                    }
                    else
                    {
                        Log.i(TAG, "- - - - - NO ONE replays");
                    }

                    Log.i(TAG, "DataSnapshot: " + dataSnapshot.toString());
                    Log.i(TAG, " ");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            };

            reference.addValueEventListener(valueEvent);
        }
        catch (Exception e)
        {
            Log.e(TAG, "FATAL ERROR IN COMMUNICATION INTERFACE listenReplies " + e.toString());
        }
    }

    public void removeListenerReplies()
    {
        reference.removeEventListener(valueEvent);
        reference = null;
    }


}

