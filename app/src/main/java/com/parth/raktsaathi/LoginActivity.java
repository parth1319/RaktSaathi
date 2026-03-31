package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginbtnlogin;
    TextView signupText, forgotPassword;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginbtnlogin = findViewById(R.id.loginbtnlogin);
        signupText = findViewById(R.id.signupText);
        forgotPassword = findViewById(R.id.forgotPassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        loginbtnlogin.setOnClickListener(v -> {

            String userInput = email.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (TextUtils.isEmpty(userInput)) {
                email.setError("Enter Email or Mobile");
                return;
            }

            if (TextUtils.isEmpty(userPassword)) {
                password.setError("Enter Password");
                return;
            }

            progressDialog.show();

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("email", userInput);
            params.put("password", userPassword);

            client.post(Urls.UserLoginWebServiceAddress, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    progressDialog.dismiss();

                    String res = new String(responseBody).trim();

                    if (res.equalsIgnoreCase("success")) {

                        // ✅ Save login
                        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("isLoggedIn", true);
                        editor.putString("user_input", userInput);
                        editor.apply();

                        Toast.makeText(LoginActivity.this, "Login Successful Done", Toast.LENGTH_SHORT).show();

                        // ✅ Go to Home
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();

                    } else if (res.equalsIgnoreCase("invalid_password")) {

                        Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();

                    } else {

                        Toast.makeText(LoginActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    progressDialog.dismiss();

                    Toast.makeText(LoginActivity.this,
                            "Server Error: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
        });

        signupText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class))
        );

        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class))
        );
    }
}