package com.example.signupapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogoutActivity extends AppCompatActivity {

    Button bBack, bLogout;
    private FirebaseAuth mFirebaseAuth;
    TextView tvGetEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        //initializing firebase authentication object
        mFirebaseAuth = FirebaseAuth.getInstance();

        //if the user is not logged in
        //that means current user will return null
        if(mFirebaseAuth.getCurrentUser() == null){
            //closing this activity
            finish();
            //starting login activity
            startActivity(new Intent(this, LoginActivity.class));
        }

        //getting current user
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        bBack = findViewById(R.id.bBack);
        bLogout = findViewById(R.id.bLogout);
        tvGetEmail = findViewById(R.id.tvGetEmail);

        //displaying logged in user name
        tvGetEmail.setText(user.getEmail());


        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LogoutActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                //closing activity
                finish();
                //starting login activity
                startActivity(new Intent(LogoutActivity.this, LoginActivity.class));
            }
        });
    }
}
