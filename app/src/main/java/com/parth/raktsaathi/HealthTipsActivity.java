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

        // 🔥 FIND VIEWS
        tvLong1 = findViewById(R.id.tvLong1);
        tvLong2 = findViewById(R.id.tvLong2);
        tvLong3 = findViewById(R.id.tvLong3);
        tvLong4 = findViewById(R.id.tvLong4);

        btnMore1 = findViewById(R.id.btnMore1);
        btnMore2 = findViewById(R.id.btnMore2);
        btnMore3 = findViewById(R.id.btnMore3);
        btnMore4 = findViewById(R.id.btnMore4);

        // 🔥 CLICK EVENTS
        btnMore1.setOnClickListener(v -> toggle(tvLong1, btnMore1));
        btnMore2.setOnClickListener(v -> toggle(tvLong2, btnMore2));
        btnMore3.setOnClickListener(v -> toggle(tvLong3, btnMore3));
        btnMore4.setOnClickListener(v -> toggle(tvLong4, btnMore4));
    }

    // 🔥 COMMON FUNCTION (BEST PRACTICE)
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