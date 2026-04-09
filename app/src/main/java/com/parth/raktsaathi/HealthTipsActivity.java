package com.parth.raktsaathi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HealthTipsActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4;
    Button btn1, btn2, btn3, btn4;

    boolean isExpanded1 = false;
    boolean isExpanded2 = false;
    boolean isExpanded3 = false;
    boolean isExpanded4 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        btn1.setOnClickListener(v -> toggleText(tv1, btn1, 1));

        btn2.setOnClickListener(v -> toggleText(tv2, btn2, 2));

        btn3.setOnClickListener(v -> toggleText(tv3, btn3, 3));

        btn4.setOnClickListener(v -> toggleText(tv4, btn4, 4));
    }

    private void toggleText(TextView tv, Button btn, int index) {

        switch (index) {
            case 1:
                isExpanded1 = !isExpanded1;
                if (isExpanded1) {
                    tv.setMaxLines(Integer.MAX_VALUE);
                    btn.setText("See Less");
                } else {
                    tv.setMaxLines(2);
                    btn.setText("See More");
                }
                break;

            case 2:
                isExpanded2 = !isExpanded2;
                if (isExpanded2) {
                    tv.setMaxLines(Integer.MAX_VALUE);
                    btn.setText("See Less");
                } else {
                    tv.setMaxLines(2);
                    btn.setText("See More");
                }
                break;

            case 3:
                isExpanded3 = !isExpanded3;
                if (isExpanded3) {
                    tv.setMaxLines(Integer.MAX_VALUE);
                    btn.setText("See Less");
                } else {
                    tv.setMaxLines(2);
                    btn.setText("See More");
                }
                break;

            case 4:
                isExpanded4 = !isExpanded4;
                if (isExpanded4) {
                    tv.setMaxLines(Integer.MAX_VALUE);
                    btn.setText("See Less");
                } else {
                    tv.setMaxLines(2);
                    btn.setText("See More");
                }
                break;
        }
    }
}