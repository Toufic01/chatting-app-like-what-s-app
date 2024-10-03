package com.example.messaging;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.messaging.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;

    EditText username, useremail, userpassword;
    Button signup;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        // getting id's
        username = findViewById(R.id.etusername);
        useremail = findViewById(R.id.etuseremail);
        userpassword = findViewById(R.id.etuserpassword);
        signup = findViewById(R.id.signup);

        // creating progressdialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Creating account");
        dialog.setMessage("We're creating your account");


        // getting firebase auth instance
        auth = FirebaseAuth.getInstance();

        // signup manually to database
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                final String uname = username.getText().toString();
                final String uemail = useremail.getText().toString();
                final String upassword = userpassword.getText().toString();

                if (uname.isEmpty() || uemail.isEmpty() || upassword.isEmpty()) {
                    dialog.dismiss();
                    Toast.makeText(SignUpActivity.this, "Fill up all the information bar", Toast.LENGTH_SHORT).show();
                } else {

                    auth.createUserWithEmailAndPassword(uemail, upassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Users user = new Users(uname, uemail, upassword);
                                        String id = task.getResult().getUser().getUid();
                                        database = FirebaseDatabase.getInstance();
                                        database.getReference("Users").child(id).setValue(user)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            username.setText("");
                                                            useremail.setText("");
                                                            userpassword.setText("");
                                                            Toast.makeText(SignUpActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();
                                                            dialog.dismiss();
                                                            Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                                                            startActivity(intent);
                                                        } else {
                                                            dialog.dismiss();
                                                            Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        dialog.dismiss();
                                        Toast.makeText(SignUpActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });


        // redirecting to signin by already have account
        TextView textView = findViewById(R.id.already_have_account);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
                startActivity(intent);
            }
        });

    }
}