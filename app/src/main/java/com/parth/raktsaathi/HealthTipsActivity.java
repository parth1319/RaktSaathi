package com.parth.raktsaathi;

import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HealthTipsActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4, tv5, tv6;
    TextView btn1, btn2, btn3, btn4, btn5, btn6;

    Animation expandAnim, collapseAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        // 🔥 FIND IDS
        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);

        // 🔥 LOAD ANIMATIONS
        expandAnim = AnimationUtils.loadAnimation(this, R.anim.expand);
        collapseAnim = AnimationUtils.loadAnimation(this, R.anim.collapse);

        // 🔥 APPLY TO ALL
        setupToggle(tv1, btn1);
        setupToggle(tv2, btn2);
        setupToggle(tv3, btn3);
        setupToggle(tv4, btn4);
        setupToggle(tv5, btn5);
        setupToggle(tv6, btn6);
    }

    // 🔥 MAIN TOGGLE FUNCTION
    private void setupToggle(TextView text, TextView button) {

        button.setOnClickListener(v -> {

            if (text.getMaxLines() == 3) {

                // EXPAND
                text.setMaxLines(Integer.MAX_VALUE);
                text.startAnimation(expandAnim);
                button.setText("See Less");

            } else {

                // COLLAPSE
                text.startAnimation(collapseAnim);

                new Handler().postDelayed(() -> {
                    text.setMaxLines(3);
                }, 200);

                button.setText("See More");
            }
        });
    }
}