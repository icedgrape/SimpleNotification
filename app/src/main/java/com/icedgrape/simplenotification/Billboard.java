package com.icedgrape.simplenotification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import es.dmoral.toasty.Toasty;


public class Billboard extends AppCompatActivity {

    public static final String NODE  = "users";
    private ParseUser parseUser;
    private String FCM_TOKEN;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billboard);

        parseUser = ParseUser.getCurrentUser();

        FirebaseMessaging.getInstance().subscribeToTopic("updates");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if(task.isSuccessful()){

                            FCM_TOKEN = task.getResult().getToken();
                            Log.i("FCM", "Token is " + FCM_TOKEN);

                            saveToken(FCM_TOKEN);
                        }else{

                            String errorMessage = task.getException().toString();

                            Log.e("FCM", errorMessage);
                            Toasty.error(Billboard.this,errorMessage,Toasty.LENGTH_LONG).show();
                        }
                    }
                });




    }

    protected void onStart() {
        super.onStart();

        if(ParseUser.getCurrentUser() == null){

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
    private void saveToken(String token){

        parseUser.put("FCM_Token", token);
        parseUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null){

                    Toasty.success(Billboard.this, "FCM TOKEN saved to server " +  FCM_TOKEN ,Toasty.LENGTH_LONG).show();

                }else{
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_ui_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) { switch(item.getItemId()) {
        case R.id.btnLogut:
            Toasty.success(this, "Logging out " + ParseUser.getCurrentUser().getUsername(),Toasty.LENGTH_LONG).show();
            ParseUser.logOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return(true);
    }
        return(super.onOptionsItemSelected(item));
    }

}