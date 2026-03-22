package com.parth.raktsaathi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginbtnlogin;
    TextView signupText, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // your XML file name

        // 🔗 Bind Views
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginbtnlogin = findViewById(R.id.loginbtnlogin); // ⚠️ make sure ID exists
        signupText = findViewById(R.id.signupText);

        // 🔥 LOGIN BUTTON
        loginbtnlogin.setOnClickListener(v -> {

            String userEmail = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            // ✅ Validation
            if (TextUtils.isEmpty(userEmail)) {
                email.setError("Enter email or phone");
                return;
            }

            if (TextUtils.isEmpty(userPassword)) {
                password.setError("Enter password");
                return;
            }

            if (userPassword.length() < 6) {
                password.setError("Password must be at least 6 characters");
                return;
            }

            // 🔥 SUCCESS LOGIN (for now dummy)
            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

            // 👉 Go to HomeActivity (Eligible = true)
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("isEligible", true);
            startActivity(intent);
            finish();
        });

        // 🔹 SIGN UP CLICK
        signupText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        // 🔹 FORGOT PASSWORD (optional)
        // If you add id in XML, then uncomment
        /*
        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> {
            Toast.makeText(this, "Forgot Password Clicked", Toast.LENGTH_SHORT).show();
        });
        */
    }
}