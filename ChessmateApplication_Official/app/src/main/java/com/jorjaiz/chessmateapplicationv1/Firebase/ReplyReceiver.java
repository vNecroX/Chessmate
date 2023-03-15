package com.jorjaiz.chessmateapplicationv1.Firebase;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.MainActivity;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.R;
import com.jorjaiz.chessmateapplicationv1.View_MainInterface;

import java.util.HashMap;

public class ReplyReceiver extends BroadcastReceiver implements Constants, FireQuery.OnResponseFireQuery
{
    Context ctx;
    String playerName;
    String idOponent;

    public ReplyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.w(TAG, "''''' BROADCAST RECEIVER onReceiver");

        int number_notif = intent.getExtras().getInt("number_notif", 0);
        idOponent = intent.getExtras().getString("idOponent");
        playerName = intent.getExtras().getString("playerName");

        ctx = context;

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(number_notif);

        Firebase.query(this).getTokenOfReceiverListener(idOponent);
    }

    @Override
    public void getResponseFireQuery(HashMap<String, Object> data, String purpose)
    {
        Log.w(TAG, "PURPOSE: " + purpose);

        DataSnapshot dataSnapshot;

        switch (purpose)
        {
            case "getTokenOfReceiverListener":
                dataSnapshot = (DataSnapshot)data.get("dataSnapshot");

                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Token token = snapshot.getValue(Token.class);

                    Log.w(TAG, "''''' TOKEN OF OPPONENT: " + token.getToken());

                    Data dataToPass = new Data(idOponent, R.mipmap.ic_launcher,
                            playerName + " no aceptó tu solcitud de juego a una partida Online :c ",
                            "Respuesta de invitado", "",
                            0, 0, "",
                            "", "ReplyInvitated");

                    Sender sender = new Sender(dataToPass, token.getToken());

                    Firebase.query(this).deliverNotification(sender);
                }
                break;
        }
    }

}
