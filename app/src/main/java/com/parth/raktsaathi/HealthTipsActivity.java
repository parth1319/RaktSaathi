package com.parth.raktsaathi;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class HealthTipsActivity extends AppCompatActivity {

    TextView tv1, tv2, tv3, tv4 , tv5 ,tv6 ,tv7 ,tv8;

    Button btn1, btn2, btn3, btn4 ,btn5 , btn6 , btn7 ,btn8 ;

    boolean isExpanded1 = false;
    boolean isExpanded2 = false;
    boolean isExpanded3 = false;
    boolean isExpanded4 = false;
    boolean isExpanded5 = false;
    boolean isExpanded6 = false;
    boolean isExpanded7 = false;
    boolean isExpanded8 = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_tips);

        tv1 = findViewById(R.id.tv1);
        tv2 = findViewById(R.id.tv2);
        tv3 = findViewById(R.id.tv3);
        tv4 = findViewById(R.id.tv4);
        tv5 = findViewById(R.id.tv5);
        tv6 = findViewById(R.id.tv6);
        tv7 = findViewById(R.id.tv7);
        tv8 = findViewById(R.id.tv8);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);
        btn7 = findViewById(R.id.btn7);
        btn8 = findViewById(R.id.btn8);

        btn1.setOnClickListener(v -> toggleText(tv1, btn1, 1));
        btn2.setOnClickListener(v -> toggleText(tv2, btn2, 2));
        btn3.setOnClickListener(v -> toggleText(tv3, btn3, 3));
        btn4.setOnClickListener(v -> toggleText(tv4, btn4, 4));
        btn5.setOnClickListener(v -> toggleText(tv5, btn5, 5));
        btn6.setOnClickListener(v -> toggleText(tv6, btn6, 6));
        btn7.setOnClickListener(v -> toggleText(tv7, btn7, 7));
        btn8.setOnClickListener(v -> toggleText(tv8, btn8, 8));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void toggleText(TextView tv, Button btn, int index) {
        boolean expanded = false;
        switch (index) {
            case 1: isExpanded1 = !isExpanded1; expanded = isExpanded1; break;
            case 2: isExpanded2 = !isExpanded2; expanded = isExpanded2; break;
            case 3: isExpanded3 = !isExpanded3; expanded = isExpanded3; break;
            case 4: isExpanded4 = !isExpanded4; expanded = isExpanded4; break;
            case 5: isExpanded5 = !isExpanded5; expanded = isExpanded5; break;
            case 6: isExpanded6 = !isExpanded6; expanded = isExpanded6; break;
            case 7: isExpanded7 = !isExpanded7; expanded = isExpanded7; break;
            case 8: isExpanded8 = !isExpanded8; expanded = isExpanded8; break;
        }

        if (expanded) {
            tv.setMaxLines(Integer.MAX_VALUE);
            btn.setText("See Less");
        } else {
            tv.setMaxLines(3);
            btn.setText("Read More");
        }
    }
}