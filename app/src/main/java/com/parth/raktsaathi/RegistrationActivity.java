package com.parth.raktsaathi;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, phone, email, address, password, confirmPassword, lastdonationdate;
    Spinner blood, city;
    Button register;
    ProgressDialog progressDialog;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // INIT
        name = findViewById(R.id.et_name);
        phone = findViewById(R.id.et_phone);
        email = findViewById(R.id.et_email);
        address = findViewById(R.id.et_address);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        lastdonationdate = findViewById(R.id.et_lastdonationdate);

        blood = findViewById(R.id.sp_blood);
        city = findViewById(R.id.sp_city);
        register = findViewById(R.id.btn_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        progressDialog.setCancelable(false);

        calendar = Calendar.getInstance();

        // 🔥 BLOOD SPINNER
        String[] bloodGroups = {"Select Blood Group","A+","A-","B+","B-","O+","O-","AB+","AB-"};
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, bloodGroups);
        blood.setAdapter(bloodAdapter);

        // 🔥 CITY SPINNER (ALL MAHARASHTRA DISTRICTS)
        String[] cities = {
                "Select City",
                "Ahmednagar","Akola","Amravati","Aurangabad","Beed","Bhandara","Buldhana",
                "Chandrapur","Dhule","Gadchiroli","Gondia","Hingoli","Jalgaon","Jalna",
                "Kolhapur","Latur","Mumbai City","Mumbai Suburban","Nagpur","Nanded",
                "Nandurbar","Nashik","Osmanabad","Palghar","Parbhani","Pune","Raigad",
                "Ratnagiri","Sangli","Satara","Sindhudurg","Solapur","Thane","Wardha",
                "Washim","Yavatmal"
        };

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, cities);
        city.setAdapter(cityAdapter);

        // 🔥 DATE PICKER
        lastdonationdate.setOnClickListener(v -> openCalendar());

        // 🔥 BUTTON
        register.setOnClickListener(v -> registerUser());
    }

    // 📅 CALENDAR
    private void openCalendar() {
        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, year, month, day) -> {
                    calendar.set(year, month, day);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    lastdonationdate.setText(sdf.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        dp.show();
    }

    // 🔥 REGISTER FUNCTION
    private void registerUser() {

        String sName = name.getText().toString().trim();
        String sPhone = phone.getText().toString().trim();
        String sEmail = email.getText().toString().trim();
        String sAddress = address.getText().toString().trim();
        String sPassword = password.getText().toString().trim();
        String sConfirm = confirmPassword.getText().toString().trim();
        String sBlood = blood.getSelectedItem().toString();
        String sCity = city.getSelectedItem().toString();
        String sDate = lastdonationdate.getText().toString().trim();

        // VALIDATION
        if (TextUtils.isEmpty(sName)) {
            name.setError("Enter Name");
            return;
        }

        if (TextUtils.isEmpty(sPhone)) {
            phone.setError("Enter Phone");
            return;
        }

        if (TextUtils.isEmpty(sEmail)) {
            email.setError("Enter Email");
            return;
        }

        if (sBlood.equals("Select Blood Group")) {
            Toast.makeText(this, "Select Blood Group", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sCity.equals("Select City")) {
            Toast.makeText(this, "Select City", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(sDate)) {
            lastdonationdate.setError("Select Date");
            return;
        }

        if (!sPassword.equals(sConfirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("name", sName);
        params.put("phone", sPhone);
        params.put("email", sEmail);
        params.put("password", sPassword);
        params.put("blood_group", sBlood);
        params.put("last_donation_date", sDate);
        params.put("address", sAddress);
        params.put("city", sCity);

        client.post(Urls.REGISTER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                Toast.makeText(RegistrationActivity.this,
                        "Registration Successful", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();

                Toast.makeText(RegistrationActivity.this,
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}