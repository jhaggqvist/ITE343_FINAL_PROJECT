package com.example.malthael.weatherprediction;


import java.lang.String;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegAndLogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "";
    private EditText getEmail;
    private EditText getPassword;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_and_login);


        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {




            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();






                if (user != null) {
                    // User is signed in go to show weather, make intent here.

                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        getEmail = (EditText)findViewById(R.id.editTextUsername);
        getPassword = (EditText)findViewById(R.id.editTextUsername);

    }// end of onCreate


    public void createAccount(View view){                       // creates a new account
        String email = getEmail.getText().toString();
        String password = getPassword.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password);
        // check if account exists?
    }

    public void logIn(View view){                               // login with created account
        final String email = getEmail.getText().toString();
        String password = getPassword.getText().toString();
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override

            public void onComplete(@NonNull Task<AuthResult> task) {                // if login is successfull
           Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                Toast.makeText(RegAndLogin.this, "Welcome" + email, Toast.LENGTH_SHORT).show();



                if(!task.isSuccessful()){                                           // if login is not successfull
                    Toast.makeText(RegAndLogin.this, "failed to login", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
