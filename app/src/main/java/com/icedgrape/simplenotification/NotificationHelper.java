package com.icedgrape.simplenotification;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static com.icedgrape.simplenotification.R.raw.bells;
import static com.parse.Parse.getApplicationContext;

public class NotificationHelper {

    public static void displayNotification(Context context, String title, String body){


        Intent intent = new Intent(context, Billboard.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);



        try {
            Uri uri = RingtoneManager.getDefaultUri(bells);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
            r.play();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + getApplicationContext().getPackageName() +"/"+ bells))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{1000, 1000, 1000});

            NotificationManagerCompat mNotificationMgr = NotificationManagerCompat.from(context);
            mNotificationMgr.notify(1, mBuilder.build());
        }
        catch (Error e){

            e.printStackTrace();
        }
    }
}
