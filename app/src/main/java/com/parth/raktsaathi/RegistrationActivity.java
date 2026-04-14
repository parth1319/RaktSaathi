package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {

    TextInputEditText name, phone, email, address, password, confirmPassword;
    AutoCompleteTextView blood, city;
    Button register;
    TextView loginText;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before super.onCreate
        SharedPreferences themeSp = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDark = themeSp.getBoolean("isDark", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(true);
        }

        setContentView(R.layout.activity_registration);

        // 🔥 Initialize Views
        name = findViewById(R.id.et_name);
        phone = findViewById(R.id.et_phone);
        email = findViewById(R.id.et_email);
        address = findViewById(R.id.et_address);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        blood = findViewById(R.id.sp_blood);
        city = findViewById(R.id.sp_city);
        register = findViewById(R.id.btn_register);
        loginText = findViewById(R.id.loginText);

        loginText.setOnClickListener(v -> finish());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(false);

        // 🔥 BLOOD GROUP SPINNER (AUTOCOMPLETE)
        String[] bloodGroups = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, bloodGroups);
        blood.setAdapter(bloodAdapter);

        // 🔥 TALUKA & DISTRICT LEVEL CITIES (Akola & Amravati)
        String[] cities = {
            "Akola", "Akot", "Telhara", "Balapur", "Patur", "Murtizapur", "Barshitakli",
            "Amravati", "Achalpur (Paratwada)", "Anjangaon Surji", "Daryapur", "Warud",
            "Morshi", "Chandur Bazar", "Chandur Railway", "Dhamangaon Railway",
            "Nandgaon Khandeshwar", "Dharni", "Chikhaldara", "Teosa", "Bhatkuli", "Other"
        };
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, cities);
        city.setAdapter(cityAdapter);

        register.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String sName = name.getText().toString().trim();
        String sPhone = phone.getText().toString().trim();
        String sEmail = email.getText().toString().trim();
        String sAddress = address.getText().toString().trim();
        String sPassword = password.getText().toString().trim();
        String sConfirm = confirmPassword.getText().toString().trim();
        String sBlood = blood.getText().toString().trim();
        String sCity = city.getText().toString().trim();

        // 🔥 VALIDATION
        if (TextUtils.isEmpty(sName)) {
            name.setError("Enter Name");
            return;
        }

        if (TextUtils.isEmpty(sPhone)) {
            phone.setError("Enter Phone");
            return;
        }

        if (TextUtils.isEmpty(sEmail) || !Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
            email.setError("Enter valid Email");
            return;
        }

        if (TextUtils.isEmpty(sBlood)) {
            Toast.makeText(this, "Select Blood Group", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(sCity)) {
            Toast.makeText(this, "Select City", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(sAddress)) {
            address.setError("Enter Full Address");
            return;
        }

        if (TextUtils.isEmpty(sPassword)) {
            password.setError("Enter Password");
            return;
        }

        if (!sPassword.equals(sConfirm)) {
            confirmPassword.setError("Passwords do not match");
            return;
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);

        RequestParams params = new RequestParams();
        params.put("name", sName);
        params.put("phone", sPhone);
        params.put("email", sEmail);
        params.put("password", sPassword);
        params.put("blood_group", sBlood);
        params.put("city", sCity);
        params.put("address", sAddress); // changed from 'location' to 'address' to match PHP backend

        client.post(Urls.REGISTER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                String res = new String(responseBody).trim();

                if (res.equalsIgnoreCase("success")) {

                    // 🔥 SAVE LOGIN
                    SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("email", sEmail);
                    editor.apply();

                    Toast.makeText(RegistrationActivity.this,
                            "Registration Successful",
                            Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(RegistrationActivity.this, HomeActivity.class));
                    finish();

                } else {
                    Toast.makeText(RegistrationActivity.this, res, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();

                Toast.makeText(RegistrationActivity.this,
                        "Server Error",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}