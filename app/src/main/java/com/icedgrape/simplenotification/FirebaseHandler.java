package com.icedgrape.simplenotification;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import es.dmoral.toasty.Toasty;

public class FirebaseHandler extends FirebaseMessagingService {
    private ParseUser parseUser;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if(remoteMessage.getNotification() != null){

            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            NotificationHelper.displayNotification(getApplicationContext(), title, body);

        }
        if (remoteMessage.getData().size() > 0) {
            Log.d("full data", "Data: " + remoteMessage.getData()); //Whole data
            String title = remoteMessage.getData().get("title"); //Get title key data
            String body = remoteMessage.getData().get("body");

            NotificationHelper.displayNotification(getApplicationContext(), title, body);
        }

    }
    public void onNewToken(String token) {
        Log.d("FCM", "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    private void sendRegistrationToServer(final String token) {
        // TODO: Implement this method to send token to your app server.
        parseUser = ParseUser.getCurrentUser();
        parseUser.put("FCM_TOKEN", token);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){

                    Toasty.success(getApplicationContext(), "New Token " + token + " is saved", Toasty.LENGTH_LONG).show();
                }else {

                    e.printStackTrace();
                }
            }
        });

    }

}

