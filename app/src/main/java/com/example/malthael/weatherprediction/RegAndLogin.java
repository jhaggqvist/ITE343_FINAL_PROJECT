package com.example.malthael.weatherprediction;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegAndLogin extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String TAG = "";
    private EditText getEmail;
    private EditText getPassword;
    private GoogleApiClient mGoogleApiClient;
    private static int RC_SIGN_IN = 9001;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_and_login);
//--------GOOGLE--------
        //findViewById(R.id.gPlus).setOnClickListener((View.OnClickListener) this);
        findViewById(R.id.gPlus).setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



//--------GOOGLE--------


        //--------FIREBASE--------
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


    //-----GOOGLE

    /* @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.gPlus:
                signIn();
                break;
        }
    }
*/
private void signIn(){
    Log.e("++++++++", "hey");
    Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
    startActivityForResult(signInIntent, RC_SIGN_IN);
}

public void onActivityResult(int requestCode, int resultCode, Intent data){
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RC_SIGN_IN){
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        handleSignInResult(result);
    }

}
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
           // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
            String name = acct.getDisplayName();

            // pop a welcome toast and send them to weather app
            Toast.makeText(RegAndLogin.this, "Welcome " + name, Toast.LENGTH_SHORT).show();

            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }
    //-----/GOOGLE

//------FIREBASE
    public void createAccount(View view){                       // creates a new account
        String email = getEmail.getText().toString();
        String password = getPassword.getText().toString();

        if (email.equals("") || password.equals("")){
            Toast.makeText(RegAndLogin.this, "Please enter Email and Password.", Toast.LENGTH_SHORT).show();
        }
else {
            mAuth.createUserWithEmailAndPassword(email,password);
            Toast.makeText(RegAndLogin.this, "Successfully created a new account, welcome " + email, Toast.LENGTH_SHORT).show();
        }




    }

    public void logIn(final View view){                               // login with created account
        final String email = getEmail.getText().toString();
        final String password = getPassword.getText().toString();

        if (email.equals("") || password.equals("")){                  // if password and email is not set
            Toast.makeText(RegAndLogin.this, "please enter Email and Password", Toast.LENGTH_SHORT).show();
        }

        if (!email.equals("") || !password.equals("")){                 // if password and email is set, login
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override

                public void onComplete(@NonNull Task<AuthResult> task) {                // if login is successfull
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                    if (task.isSuccessful()){
                        Toast.makeText(RegAndLogin.this, "Welcome " + email, Toast.LENGTH_SHORT).show();
                        loginSuccess(view); // new intent for MainActivity/start of app
                    }

                    if(!task.isSuccessful()){                                           // if login is not successfull
                        Toast.makeText(RegAndLogin.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //error
                    }
                }
            });
        }








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
    public void loginSuccess(View view){
        Intent myIntent = new Intent(this, MainActivity.class);
        startActivity(myIntent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    //-----/FIREBASE


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if  (i == R.id.gPlus){
            signIn();
        }
    }
}
