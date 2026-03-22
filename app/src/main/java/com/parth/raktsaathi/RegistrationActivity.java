package com.parth.raktsaathi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegistrationActivity extends AppCompatActivity {

    private EditText regEmail, regPassword, regConfirmPassword;
    private Button registerBtn;
    private LinearLayout googleSignInBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // 🔗 Bind Views
        regEmail = findViewById(R.id.regEmail);
        regPassword = findViewById(R.id.regPassword);
        regConfirmPassword = findViewById(R.id.regConfirmPassword);
        registerBtn = findViewById(R.id.registerBtn);
        googleSignInBtn = findViewById(R.id.googleSignInBtn);

        // 🔥 Register Button Click
        registerBtn.setOnClickListener(v -> {

            String email = regEmail.getText().toString().trim();
            String password = regPassword.getText().toString().trim();
            String confirmPassword = regConfirmPassword.getText().toString().trim();

            // ✅ Validation
            if (TextUtils.isEmpty(email)) {
                regEmail.setError("Enter email or phone");
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

            // ✅ Success (Dummy for now)
            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();

            // 👉 Go to HomeActivity (Eligible = TRUE)
            Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
            intent.putExtra("isEligible", true);
            startActivity(intent);
            finish();
        });

        // 🔹 Google Sign-In Click (optional for now)
        googleSignInBtn.setOnClickListener(v -> {
            Toast.makeText(RegistrationActivity.this, "Google Sign-In Coming Soon", Toast.LENGTH_SHORT).show();
        });
    }
}