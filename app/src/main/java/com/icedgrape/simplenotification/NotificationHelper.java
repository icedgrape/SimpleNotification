package com.icedgrape.simplenotification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {

    public static void displayNotification(Context context, String title, String body){

        Intent intent = new Intent(context, Billboard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[0]);

        NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(context);
        mNotificationMgr.notify(1, mBuilder.build());
    }
}
