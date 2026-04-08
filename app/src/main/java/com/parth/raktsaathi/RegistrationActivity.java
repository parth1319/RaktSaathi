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

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, phone, email, location, password, confirmPassword, lastdonationdate;
    Spinner blood;
    Button register;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            getWindow().setDecorFitsSystemWindows(true);
        }

        setContentView(R.layout.activity_registration);

        // 🔥 XML MATCHED IDS
        name = findViewById(R.id.et_name);
        phone = findViewById(R.id.et_phone);
        email = findViewById(R.id.et_email);
        location = findViewById(R.id.et_location);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        lastdonationdate = findViewById(R.id.et_lastdonationdate);
        blood = findViewById(R.id.sp_blood);
        register = findViewById(R.id.btn_register);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Account...");
        progressDialog.setCancelable(false);

        // 🔥 SPINNER
        String[] bloodGroups = {"Select Blood Group","A+","A-","B+","B-","O+","O-","AB+","AB-"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, bloodGroups);

        blood.setAdapter(adapter);

        register.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String sName = name.getText().toString().trim();
        String sPhone = phone.getText().toString().trim();
        String sEmail = email.getText().toString().trim();
        String sLocation = location.getText().toString().trim();
        String sPassword = password.getText().toString().trim();
        String sConfirm = confirmPassword.getText().toString().trim();
        String sBlood = blood.getSelectedItem().toString();
        String sDate = lastdonationdate.getText().toString().trim();

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

        if (sBlood.equals("Select Blood Group")) {
            Toast.makeText(this, "Select Blood Group", Toast.LENGTH_SHORT).show();
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
        params.put("last_donation_date", sDate);
        params.put("location", sLocation);

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