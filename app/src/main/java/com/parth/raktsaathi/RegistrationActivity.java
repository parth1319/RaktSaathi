package com.parth.raktsaathi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {

    private EditText regEmail, regPassword, regConfirmPassword;
    private Button registerBtn;
    private LinearLayout googleSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regConfirmPassword = findViewById(R.id.regConfirmPassword);
        registerBtn = findViewById(R.id.registerBtn);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);

        registerBtn.setOnClickListener(v -> {

            String input = regEmail.getText().toString().trim();
            String password = regPassword.getText().toString().trim();
            String confirmPassword = regConfirmPassword.getText().toString().trim();

            String email = "";
            String phone = "";

            // ✅ Validation
            if (TextUtils.isEmpty(input)) {
                regEmail.setError("Enter email or phone");
                return;
            }

            // Detect email or phone
            if (Patterns.EMAIL_ADDRESS.matcher(input).matches()) {
                email = input;
            } else if (input.length() == 10) {
                phone = input;
            } else {
                regEmail.setError("Invalid email or phone");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                regPassword.setError("Enter password");
                return;
            }

            if (password.length() < 6) {
                regPassword.setError("Password must be at least 6 characters");
                return;
            }

            if (!password.equals(confirmPassword)) {
                regConfirmPassword.setError("Passwords do not match");
                return;
            }

            // 🚀 API CALL
            AsyncHttpClient client = new AsyncHttpClient();

            RequestParams params = new RequestParams();
            params.put("email", email);
            params.put("phone", phone);
            params.put("password", password);

            client.post(Urls.UserRegistrationWebServiceAddress, params, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    Toast.makeText(RegistrationActivity.this, "Registering...", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String response = new String(responseBody);

                    try {
                        JSONObject obj = new JSONObject(response);
                        String status = obj.optString("status");
                        String message = obj.optString("message");

                        Toast.makeText(RegistrationActivity.this, message, Toast.LENGTH_SHORT).show();

                        if (status.equalsIgnoreCase("success")) {
                            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
                            intent.putExtra("isEligible", true);
                            startActivity(intent);
                            finish();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(RegistrationActivity.this, "Response Error", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    String errorMsg = "Server Error";

                    if (responseBody != null) {
                        errorMsg = new String(responseBody);
                    }

                    Toast.makeText(RegistrationActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });

        });

        googleSignInBtn.setOnClickListener(v -> {
            Toast.makeText(RegistrationActivity.this, "Google Sign-In Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }
}