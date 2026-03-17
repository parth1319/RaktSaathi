package com.parth.raktsaathi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    LinearLayout llMain;
    ImageView ivMainlogo;
    TextView  tvMainTitle;
    TextView  tvMainSlogan;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        llMain = findViewById(R.id.llmain);
        ivMainlogo = findViewById(R.id.ivmainlogo);
        tvMainTitle = findViewById(R.id.tvmaintitle);
        tvMainSlogan = findViewById(R.id.tvmainslogan);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean isLogin = preferences.getBoolean("isLogin", false);
                Intent intent;
                if (isLogin) {
                    intent = new Intent(SplashActivity.this, HomeActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },3000);

    }
}
