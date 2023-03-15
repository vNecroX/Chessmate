package com.jorjaiz.chessmateapplicationv1.Firebase;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.Parameters.CP;
import com.jorjaiz.chessmateapplicationv1.R;
import com.jorjaiz.chessmateapplicationv1.View_MainInterface;
import com.jorjaiz.chessmateapplicationv1.View_Peer;

public class MyFirebaseMessagging extends FirebaseMessagingService implements Constants
{
    String idOponent, idPlayer, icon, title, body;
    int gameMode;
    String playerName, oponentName;
    String keyNotif;

    int j;

    static int number_notif;
    static String bundle_notif_id;

    PendingIntent pendingIntent;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        number_notif = 0;

        String sentedId = remoteMessage.getData().get("sented");
        String otherUserId = remoteMessage.getData().get("user");

        if(sentedId.equals(String.valueOf(CP.get().getIdPlayer())))
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                Log.w(TAG, "+++++ MAKE NOTIF PIE");
                sendPieNotification(remoteMessage);
            }
            else
            {
                Log.w(TAG, "+++++ MAKE NOTIF NORMAL");
                sendNotification(remoteMessage);
            }
        }
        else
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                Log.w(TAG, "+++++ MAKE NOTIF PIE REPLY");
                replyPieNotification(remoteMessage);
            }
            else
            {
                Log.w(TAG, "+++++ MAKE NOTIF NORMAL REPLY");
                replyNotification(remoteMessage);
            }
        }
    }

    private void getAndBundleDataFromNotification(RemoteMessage remoteMessage)
    {
        idOponent = remoteMessage.getData().get("user");
        icon = remoteMessage.getData().get("icon");
        title = remoteMessage.getData().get("title");
        body = remoteMessage.getData().get("body");

        idPlayer = remoteMessage.getData().get("sented");

        gameMode = Integer.valueOf(remoteMessage.getData().get("gameMode"));

        playerName = remoteMessage.getData().get("playerName");
        oponentName = remoteMessage.getData().get("oponentName");

        keyNotif = remoteMessage.getData().get("keyNotif");


        Log.w(TAG, ">>> LISTENING KEY RECEIVED: " + keyNotif);

        j = Integer.parseInt(idOponent.replaceAll("[\\D]", ""));

        Intent intent = new Intent(this, View_Peer.class);
        Bundle bundle = new Bundle();
        bundle.putString("idPlayer", idPlayer);
        bundle.putString("idOponent", idOponent);
        bundle.putString("playerName", playerName);
        bundle.putString("oponentName", oponentName);
        bundle.putString("NewSaved", NEW);
        bundle.putInt("gameMode", gameMode);
        bundle.putInt("kindPlayer", SLAVE);
        bundle.putString("keyNotif", keyNotif);
        intent.putExtras(bundle);

        number_notif = Integer.parseInt(idOponent);
        bundle_notif_id = "bundle_notification_" + number_notif;
        intent.putExtra("notification_id", number_notif);

        /*intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.);*/

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        pendingIntent = PendingIntent.getActivity(this, number_notif, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private void sendPieNotification(RemoteMessage remoteMessage)
    {
        getAndBundleDataFromNotification(remoteMessage);

        icon = String.valueOf(R.drawable.ic_notification_icon);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PieNotification pieNotification = new PieNotification(this);
        Notification.Builder builder = pieNotification.getPieNotification(title, body, pendingIntent,
                defaultSound, icon, bundle_notif_id, number_notif,this, idOponent, playerName);

        pieNotification.getManager().notify(number_notif, builder.build());

        Log.w(TAG, "+++++ AN OREO NOTIFICATION");

    }

    private void sendNotification(RemoteMessage remoteMessage)
    {
        getAndBundleDataFromNotification(remoteMessage);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent intent = new Intent(this, ReplyReceiver.class);
        intent.putExtra("number_notif", number_notif);
        intent.putExtra("idOponent", idOponent);
        intent.putExtra("playerName", playerName);

        PendingIntent replyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= 24)
        {
            replyPendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "bundle_channel_id")
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setGroup(bundle_notif_id)
                .setGroupSummary(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .addAction(R.drawable.ic_duel, "En otra ocasión", replyPendingIntent)
                .addAction(R.drawable.ic_duel, "Acepto", pendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimary));
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(number_notif, builder.build());

        Log.e(TAG, "+++++ A NORMAL NOTIFICATION");
    }

    // WHEN INVITED REPLY NOT WANNA PLAY

    private void getAndBundleDataFromNotificationNoPending(RemoteMessage remoteMessage)
    {
        idOponent = remoteMessage.getData().get("user");
        icon = remoteMessage.getData().get("icon");
        title = remoteMessage.getData().get("title");
        body = remoteMessage.getData().get("body");

        idPlayer = remoteMessage.getData().get("sented");

        gameMode = Integer.valueOf(remoteMessage.getData().get("gameMode"));

        playerName = remoteMessage.getData().get("playerName");
        oponentName = remoteMessage.getData().get("oponentName");

        keyNotif = remoteMessage.getData().get("keyNotif");

        Log.w(TAG, "+++++ LISTENING KEY OF NO PENDING " + keyNotif);

        number_notif = Integer.parseInt(idOponent);
        bundle_notif_id = "bundle_notification_" + number_notif;
    }

    private void replyPieNotification(RemoteMessage remoteMessage)
    {
        getAndBundleDataFromNotificationNoPending(remoteMessage);

        icon = String.valueOf(R.drawable.ic_notification_icon);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PieNotification pieNotification = new PieNotification(this);
        Notification.Builder builder = pieNotification.getPieNotificationNoPending(title, body,
                defaultSound, icon, bundle_notif_id);

        pieNotification.getManager().notify(number_notif, builder.build());

        Log.e(TAG, "+++++ AN OREO NOTIFICATION REPLY");

    }

    private void replyNotification(RemoteMessage remoteMessage)
    {
        getAndBundleDataFromNotificationNoPending(remoteMessage);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "bundle_channel_id")
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setGroup(bundle_notif_id)
                .setGroupSummary(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setColor(getResources().getColor(R.color.colorPrimary));

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(number_notif, builder.build());

        Log.e(TAG, "+++++ A NORMAL NOTIFICATION REPLY");
    }
}
