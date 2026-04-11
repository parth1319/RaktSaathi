package com.parth.raktsaathi;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class AddCampActivity extends AppCompatActivity {

    EditText etName, etLocation, etDate, etEmail;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camp);

        etName = findViewById(R.id.etCampName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etEmail = findViewById(R.id.etEmail);
        btnSubmit = findViewById(R.id.btnSubmit);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        // 🔥 DATE PICKER (PRO TOUCH)
        etDate.setOnClickListener(v -> openDatePicker());

        btnSubmit.setOnClickListener(v -> addCamp());
    }

    private void openDatePicker(){

        Calendar calendar = Calendar.getInstance();

        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, day) -> {

            month = month + 1;
            String date = year + "-" + month + "-" + day;
            etDate.setText(date);

        }, y, m, d);

        dp.show();
    }

    private void addCamp(){

        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if(name.isEmpty() || location.isEmpty() || date.isEmpty()){
            Toast.makeText(this,"Fill all required fields",Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("camp_name", name);
        params.put("location", location);
        params.put("camp_date", date);
        params.put("email", etEmail.getText().toString());

        client.post(Urls.ADD_CAMP, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                if(res.equalsIgnoreCase("success")){
                    Toast.makeText(AddCampActivity.this,
                            "✅ Camp Registered Successfully",
                            Toast.LENGTH_LONG).show();

                    finish(); // 🔥 back to home
                }else{
                    Toast.makeText(AddCampActivity.this,
                            "Failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AddCampActivity.this,
                        "Server Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}