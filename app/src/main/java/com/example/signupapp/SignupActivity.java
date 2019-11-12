package com.example.signupapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity {

    EditText edName, edEmail, edPassword, edConfirmPassword;
    Button bSignUpConfirm;
    FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mFirebaseAuth = FirebaseAuth.getInstance();
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);
        edConfirmPassword = findViewById(R.id.edConfirmPassword);
        bSignUpConfirm = findViewById(R.id.bSignUpConfirm);

        bSignUpConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edName.getText().toString().trim();
                String email = edEmail.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                String cpassword = edConfirmPassword.getText().toString().trim();

                if (username.isEmpty()) {

                    //Toast message here if the Email is empty
                    edName.setError("Username Required.");
                    edName.requestFocus();
                } else if (email.isEmpty()) {

                    //Toast message here if the Email is empty
                    edEmail.setError("Email Required.");
                    edEmail.requestFocus();
                } else if (password.length()<=6) {

                    //Toast message here if the Password is empty
                    edPassword.setError("Password ust have at least 6 characters.");
                    edPassword.requestFocus();
                } else if (password.isEmpty()) {

                    //Toast message here if the Password is empty
                    edPassword.setError("Password Required.");
                    edPassword.requestFocus();
                } else if (cpassword.isEmpty()) {

                    //Toast message ere if the Password is empty
                    edConfirmPassword.setError("Confirm Password is Required.");
                    edConfirmPassword.requestFocus();
                } else if (!password.equals(cpassword)) {

                    //Toast message here if the Password is empty
                    Toast.makeText(SignupActivity.this, "Passwords do not match! Try again.", Toast.LENGTH_LONG).show();
                } else {

                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(SignupActivity.this, "Welcome to Vision!!", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(SignupActivity.this, "SignUp Unsuccessful!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
