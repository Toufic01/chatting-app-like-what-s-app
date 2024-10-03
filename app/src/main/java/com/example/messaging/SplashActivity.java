package com.example.messaging;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class SplashActivity extends AppCompatActivity {

    CircleImageView logo;
    TextView appname, developer_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();


        logo = findViewById(R.id.logo);
        appname = findViewById(R.id.app_name);
        developer_name = findViewById(R.id.developer_name);


        YoYo.with(Techniques.FadeInUp)
                .duration(9000)
                .playOn(logo);

        YoYo.with(Techniques.FadeInUp)
                .duration(9000)
                .playOn(appname);

        YoYo.with(Techniques.FadeInUp)
                .duration(9000)
                .playOn(developer_name);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        },7000);

    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}