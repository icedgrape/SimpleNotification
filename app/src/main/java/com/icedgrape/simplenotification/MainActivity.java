package com.icedgrape.simplenotification;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import es.dmoral.toasty.Toasty;

import static com.icedgrape.simplenotification.R.raw.bells;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "com.icedgrape.simplenotification.updates";
    private static final String CHANNEL_NAME = "Updates";
    private static final String CHANNEL_DESCRIPTION = "New information will be shared through this channel";
    private EditText edtUsername, edtPassword;
    private String username, password;
    private ProgressBar progressBar;
    private ParseUser parseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Uri uri = RingtoneManager.getDefaultUri(bells);
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword =findViewById(R.id.edtPassword);
        progressBar = findViewById(R.id.progressBar);

        parseUser = ParseUser.getCurrentUser();
        ParseInstallation.getCurrentInstallation().saveInBackground();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Log.i("Channel", "Channel " + CHANNEL_ID + " deleted");
            notificationManager.deleteNotificationChannel(CHANNEL_ID);
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){

            AudioAttributes att = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+ "://" + getApplicationContext().getPackageName() +"/"+ bells), att);
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            channel.enableVibration(true);
            channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setBypassDnd(true);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(1);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }





    }

    public void btnPressed(View view) {

        username = edtUsername.getText().toString();
        password = edtPassword.getText().toString();



        progressBar.setVisibility(View.VISIBLE);

        if (username.equals("")) {

            Toasty.error(this, "Enter the username", Toasty.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);

        } else {
            if (password.equals("")) {

                Toasty.error(this, "Enter the password", Toasty.LENGTH_LONG).show();
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                parseUser = new ParseUser();
                // Set the user's username and password, which can be obtained by a forms
                parseUser.setUsername(username);
                parseUser.setPassword(password);
                parseUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            progressBar.setVisibility(View.INVISIBLE);
                            alertDisplayer("Successful Sign Up!", "Welcome" + parseUser.getUsername() + "!");
                        } else {
                            String errorMessage = e.getMessage();
                            Log.e("login",errorMessage);

                            if(errorMessage.equals("Account already exists for this username.") || errorMessage.equals("Cannot sign up a user that has already signed up.")){
                                ParseUser.logOut();
                                loginProcess(username, password);

                            }else {
                                ParseUser.logOut();
                                progressBar.setVisibility(View.INVISIBLE);
                                Toasty.error(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.e("login",e.getMessage());
                            }
                        }
                    }
                });
            }
        }
    }

    private void alertDisplayer(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();


                        // don't forget to change the line below with the names of your Activities
                        Intent intent = new Intent(MainActivity.this, Billboard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        AlertDialog ok = builder.create();
        ok.show();
    }

    private void loginProcess(String username, String password){

        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    alertDisplayer("Successful Login","Welcome back" + edtUsername.getText().toString() + "!");
                } else {
                    ParseUser.logOut();
                    Toasty.error(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    protected void onStart() {
        super.onStart();
        if(ParseUser.getCurrentUser() != null){

            Intent intent = new Intent(this, Billboard.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
