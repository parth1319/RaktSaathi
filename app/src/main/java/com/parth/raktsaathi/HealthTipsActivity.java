package com.parth.raktsaathi;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HealthTipsActivity extends AppCompatActivity {

    TextView tvLong1, tvLong2, tvLong3, tvLong4;
    TextView btnMore1, btnMore2, btnMore3, btnMore4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        tvLong1 = findViewById(R.id.tv1);
        tvLong2 = findViewById(R.id.tv2);
        tvLong3 = findViewById(R.id.tv3);
        tvLong4 = findViewById(R.id.tv4);

        btnMore1 = findViewById(R.id.btn1);
        btnMore2 = findViewById(R.id.btn2);
        btnMore3 = findViewById(R.id.btn3);
        btnMore4 = findViewById(R.id.btn4);

        btnMore1.setOnClickListener(v -> toggle(tvLong1, btnMore1));
        btnMore2.setOnClickListener(v -> toggle(tvLong2, btnMore2));
        btnMore3.setOnClickListener(v -> toggle(tvLong3, btnMore3));
        btnMore4.setOnClickListener(v -> toggle(tvLong4, btnMore4));
    }

    private void toggle(TextView longText, TextView button) {

        if (longText.getVisibility() == View.GONE) {
            longText.setVisibility(View.VISIBLE);
            button.setText("See Less");
        } else {
            longText.setVisibility(View.GONE);
            button.setText("See More");
        }
    }
}