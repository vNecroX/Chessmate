package com.grauben98.esteb.chatfirebase.Notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.grauben98.esteb.chatfirebase.MessageActivity;
import com.grauben98.esteb.chatfirebase.R;

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);

        Log.e("TAG", " ");
        Log.e("TAG", "ME HA LLEGADO UNA NUEVA NOTIFICACION");

        String sented = remoteMessage.getData().get("sented");
        String user = remoteMessage.getData().get("user");

        Log.e("TAG", "SENTED: " + sented);
        Log.e("TAG", "USER: " + user);

        SharedPreferences preferences = getSharedPreferences("PREFS", MODE_PRIVATE);
        String currentUser = preferences.getString("currentuser", "none");

        Log.e("TAG", "CURRENT USER PREFERENCES: " + currentUser);

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        Log.e("TAG", "FIREBASEUSER IS NULL?: " + (firebaseUser==null?"YES":"NO"));

        if(firebaseUser != null && sented.equals(firebaseUser.getUid())){
            if(!currentUser.equals(user)){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                    Log.e("TAG", "MAKE NOTIF PIE");
                    sendPieNotification(remoteMessage);
                }
                else{
                    Log.e("TAG", "MAKE NOTIF NORMAL");
                    sendNotification(remoteMessage);
                }
            }
        }
    }

    private void sendPieNotification(RemoteMessage remoteMessage){
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Log.e("TAG", "USER PIE: " + user);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        PieNotification pieNotification = new PieNotification(this);
        Notification.Builder builder = pieNotification.getPieNotification(title, body, pendingIntent,
                defaultSound, icon);

        int i = 0;
        if(j > 0){
            i = j;
        }

        pieNotification.getManager().notify(i, builder.build());


        Log.e("TAG", "OREO NOTIFICATION");
    }

    private void sendNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String icon = remoteMessage.getData().get("icon");
        String title = remoteMessage.getData().get("title");
        String body = remoteMessage.getData().get("body");

        Log.e("TAG", "USER NORMAL: " + user);

        RemoteMessage.Notification notification = remoteMessage.getNotification();
        int j = Integer.parseInt(user.replaceAll("[\\D]", ""));
        Intent intent = new Intent(this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("userid", user);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, j, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(Integer.parseInt(icon))
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSound)
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimary));
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        int i = 0;
        if(j > 0){
            i = j;
        }

        notificationManager.notify(i, builder.build());


        Log.e("TAG", "NORMAL NOTIFICATION");
    }
}
