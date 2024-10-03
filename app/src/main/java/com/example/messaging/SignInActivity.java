package com.example.messaging;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messaging.Models.Users;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import static android.content.ContentValues.TAG;

public class SignInActivity extends AppCompatActivity {

    EditText useremail, userpassword;
    Button signin;

    FirebaseAuth auth;
    FirebaseDatabase database;

    ProgressDialog progressDialog;

    GoogleSignInClient mGoogleSigninClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        getSupportActionBar().hide();

        // calling id's
        useremail = findViewById(R.id.etuseremail_signIn);
        userpassword = findViewById(R.id.etuserpassword_signIn);
        signin = findViewById(R.id.signin);


        // instance of firebase auth
        auth = FirebaseAuth.getInstance();

        // instance of firebase database
        database = FirebaseDatabase.getInstance();


        // creating progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Login");
        progressDialog.setMessage("Login to your account");

// Initialize sign in options the client-id is copied form google-services.json file
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Initialize sign in client
        mGoogleSigninClient = GoogleSignIn.getClient(SignInActivity.this, gso);

        // google signin calling singIn function to login
        Button google = findViewById(R.id.google_signin);

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        // manully registration login
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();

                String signIn_useremail = useremail.getText().toString();
                String signIn_userpassword = userpassword.getText().toString();

                if (signIn_useremail.isEmpty()){
                    useremail.setError("Enter your email");
                    return;
                }
                if (signIn_userpassword.isEmpty()){
                    userpassword.setError("Enter your password");
                    return;
                }

                auth.signInWithEmailAndPassword(signIn_useremail, signIn_userpassword)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                progressDialog.dismiss();

                                if (task.isSuccessful()) {

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(SignInActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });


        // going to singup activity by create account textview
        TextView textView = findViewById(R.id.create_account);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                startActivity(intent);
            }
        });


        // checking if any login to the account
        if (auth.getCurrentUser() != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

    }


    // work with google signin
    int RC_SIGN_IN = 63;

    private void signIn() {
        Intent signInIntent = mGoogleSigninClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());

        } catch (ApiException e) {

            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser user = auth.getCurrentUser();

                            // uploading the google login data to firebase database
                            Users users = new Users();
                            users.setUserId(user.getUid());
                            users.setUserName(user.getDisplayName());
                            users.setProfilepic(user.getPhotoUrl().toString());
                            database.getReference().child("Users").child(user.getUid()).setValue(users);


                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);


                            Toast.makeText(SignInActivity.this, "signInWithCredential success", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(SignInActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}