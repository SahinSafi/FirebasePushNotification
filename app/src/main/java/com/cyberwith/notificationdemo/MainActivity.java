package com.cyberwith.notificationdemo;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;




public class MainActivity extends AppCompatActivity {
    //1. NOTIFICATION CHANNEL
    //2. NOTIFICATION BUILDER
    //3. NOTIFICATION MANAGER

    public static final String CHANNEL_ID = "Safiz DesignTech";
    private static final String CHANNEL_NAME = "Safiz DesignTech";
    private static final String CHANNEL_DESC = "Hello this is a demo notification ";

    private EditText emailEditText,passwordEditText;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEditText = findViewById(R.id.emailID);
        passwordEditText = findViewById(R.id.passwordID);
        auth = FirebaseAuth.getInstance();

        //this is extra code for sdk version above of 26 rio
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        findViewById(R.id.loginButtonID).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() != null){
            profileActivity();
        }

    }

    private void profileActivity(){
        Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void createUser(){
        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if(email.isEmpty()){
            emailEditText.setError("Enter email ");
            emailEditText.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordEditText.setError("Enter password");
            passwordEditText.requestFocus();
            return;
        }
        if(password.length()<6){
            passwordEditText.setError("Enter password more than 6 digit");
            passwordEditText.requestFocus();
            return;
        }
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    displayNotification();
                    profileActivity();
                }
                else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException){
                        userLogin(email, password);
                    }
                    else {
                        Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void userLogin(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            profileActivity();
                        }
                        else {
                            Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //for click notify
    public void displayNotification(){
        NotificationCompat.Builder nBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_account_circle_black_24dp)
                .setContentTitle("Profile")
                .setContentText("Register is successful ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1,nBuilder.build());
    }

}
