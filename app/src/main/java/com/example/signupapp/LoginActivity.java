package com.example.signupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    EditText edEmailCheck, edPasswordCheck;
    TextView tvForgotPassword;
    private FirebaseAuth mAuth;
    Button btnLogin, bSignUp;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        edEmailCheck = findViewById(R.id.edEmailCheck);
        edPasswordCheck = findViewById(R.id.edPasswordCheck);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        bSignUp = findViewById(R.id.bSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmailCheck.getText().toString();
                String password = edPasswordCheck.getText().toString();
                if (email.isEmpty()) {

                    //Toast message here if the Email is empty
                    edEmailCheck.setError("Email Required.");
                    edEmailCheck.requestFocus();
                } else if (password.isEmpty()) {

                    //Toast message here if the Password is empty
                    edPasswordCheck.setError("Password Required.");
                    edPasswordCheck.requestFocus();
                } else {

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){

                                FirebaseUser user = mAuth.getCurrentUser();

                                //if user is signing in first time then get and show user info from google account
                                if (task.getResult().getAdditionalUserInfo().isNewUser()){

                                    //Get user email and uid from auth
                                    String email = user.getEmail();
                                    String uid = user.getUid();
                                    //When user is registered store user info in firebase realtime database too using HashMap
                                    HashMap<Object, String> hashMap = new HashMap<>();
                                    //putting the info in hashmap
                                    hashMap.put("email", email);
                                    hashMap.put("uid", uid);
                                    //will be added later during editting the profile and during the registeration
                                    hashMap.put("name", "");
                                    hashMap.put("phone", "");
                                    hashMap.put("image", "");
                                    hashMap.put("cover", "");

                                    //firebase database instance
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    //path to store the user data named "Users"
                                    DatabaseReference reference = database.getReference("Users");
                                    //put data within the hashmap in database
                                    reference.child(uid).setValue(hashMap);

                                }

                                Toast.makeText(LoginActivity.this, "Logging In Unsuccessful! Please try again later...", Toast.LENGTH_LONG).show();
                            }else{
                                /*FirebaseUser mUser = mAuth.getCurrentUser();*/
                                Toast.makeText(LoginActivity.this, "Welcome Back to Vision!!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });

        bSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });

    }
}
