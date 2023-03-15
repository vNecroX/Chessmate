package com.jorjaiz.chessmateapplicationv1.Firebase;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.jorjaiz.chessmateapplicationv1.Classes.Constants;
import com.jorjaiz.chessmateapplicationv1.R;

public class PieNotification extends ContextWrapper implements Constants
{
    private static final String CHANNEL_ID = "com.jorjaiz.chessmateapplicationv1";
    private static final String CHANNEL_NAME = "chessmateapplicationv1";

    private NotificationManager notificationManager;

    public PieNotification(Context base)
    {
        super(base);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel()
    {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);

        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(true);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);

    }

    public NotificationManager getManager()
    {
        if(notificationManager == null)
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        return  notificationManager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getPieNotification(String title, String body, PendingIntent pendingIntent, Uri soundUri, String icon,
                                                   String bundle_notif_id, int number_notif, Context ctx, String idOponent, String playerName)
    {
        Intent intent = new Intent(ctx, ReplyReceiver.class);
        intent.putExtra("number_notif", number_notif);
        intent.putExtra("idOponent", idOponent);
        intent.putExtra("playerName", playerName);

        PendingIntent replyPendingIntent = null;

        if (Build.VERSION.SDK_INT >= 24)
        {
            replyPendingIntent = PendingIntent.getBroadcast(
                    ctx,
                    0,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
        }

        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(soundUri)
                .setAutoCancel(true)
                .setGroup(bundle_notif_id)
                .setGroupSummary(true)
                .setStyle(new Notification.BigTextStyle()
                    .bigText(body))
                .addAction(R.drawable.ic_duel, "En otra ocasion", replyPendingIntent)
                .addAction(R.drawable.ic_duel, "Acepto", pendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimary));

    }


    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getPieNotificationNoPending(String title, String body,
                                                   Uri soundUri, String icon, String bundle_notif_id)
    {
        return new Notification.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(Integer.parseInt(icon))
                .setSound(soundUri)
                .setAutoCancel(true)
                .setGroup(bundle_notif_id)
                .setGroupSummary(true)
                .setStyle(new Notification.BigTextStyle()
                        .bigText(body))
                .setColor(getResources().getColor(R.color.colorPrimary));

    }
}
