package com.cyberwith.notificationdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class ProfileActivity extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        //generate token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(Task<InstanceIdResult> task) {
                        if(task.isSuccessful()){
                            String token = task.getResult().getToken();
                            saveToken(token);
                        }
                        else {

                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(auth.getCurrentUser() == null){
            Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

    }

    public void saveToken(String token){
        String email = auth.getCurrentUser().getEmail();
        User user = new User(email, token);

        databaseReference.child(auth.getCurrentUser().getUid()).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(ProfileActivity.this," Token saved ",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}
