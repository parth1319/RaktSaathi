package com.parth.raktsaathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {

    EditText etUsername, etMobileNo, etEmailid, etPassword, etBloodGroup, etCity;
    Button btnRegister, btnBackToLogin;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        // Initialize views
        etUsername = findViewById(R.id.etRegisterUserName);
        etMobileNo = findViewById(R.id.etRegisterMobileNo);
        etEmailid = findViewById(R.id.etRegisterEmailid);
        etPassword = findViewById(R.id.etRegisterPassword);
        etBloodGroup = findViewById(R.id.etRegisterBloodGroup);
        etCity = findViewById(R.id.etRegisterCity);

        btnRegister = findViewById(R.id.btnRegisterRegister);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        // Register button
        btnRegister.setOnClickListener(v -> validateAndRegister());

        // Back to login
        btnBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void validateAndRegister() {

        String username = etUsername.getText().toString().trim();
        String mobileNo = etMobileNo.getText().toString().trim();
        String email = etEmailid.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String bloodGroup = etBloodGroup.getText().toString().trim();
        String city = etCity.getText().toString().trim();

        if (username.isEmpty()) {
            etUsername.setError("Enter Username");

        } else if (mobileNo.isEmpty()) {
            etMobileNo.setError("Enter Mobile Number");

        } else if (mobileNo.length() != 10) {
            etMobileNo.setError("Enter Valid Mobile Number");

        } else if (email.isEmpty()) {
            etEmailid.setError("Enter Email");

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmailid.setError("Enter Valid Email");

        } else if (password.isEmpty()) {
            etPassword.setError("Enter Password");

        } else if (password.length() < 8) {
            etPassword.setError("Minimum 8 characters");

        } else if (bloodGroup.isEmpty()) {
            etBloodGroup.setError("Enter Blood Group");

        } else if (city.isEmpty()) {
            etCity.setError("Enter City");

        } else {
            registerUser();
        }
    }

    private void registerUser() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("username", etUsername.getText().toString());
        params.put("mobileno", etMobileNo.getText().toString());
        params.put("emailid", etEmailid.getText().toString());
        params.put("password", etPassword.getText().toString());
        params.put("blood_group", etBloodGroup.getText().toString());
        params.put("city", etCity.getText().toString());

        client.post(Urls.RegisterUserWebServiceAddress, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    String status = response.getString("success");

                    if (status.equals("1")) {
                        Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

                        // Save data
                        editor.putString("userName", etUsername.getText().toString());
                        editor.putString("userMobileNo", etMobileNo.getText().toString());
                        editor.putString("userEmail", etEmailid.getText().toString());
                        editor.putString("userBloodGroup", etBloodGroup.getText().toString());
                        editor.putString("userCity", etCity.getText().toString());
                        editor.apply(); // IMPORTANT

                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                        finish();

                    } else {
                        Toast.makeText(RegistrationActivity.this, "User already exists", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(RegistrationActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(RegistrationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}