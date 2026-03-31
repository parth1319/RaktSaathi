package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

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

        loginbtnlogin.setOnClickListener(v -> loginUser());

        signupText.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistrationActivity.class))
        );

        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class))
        );
    }

    private void loginUser(){

        String userInput = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if(TextUtils.isEmpty(userInput)){
            email.setError("Enter Email or Mobile");
            return;
        }

        if(TextUtils.isEmpty(userPassword)){
            password.setError("Enter Password");
            return;
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000); // 🔥 avoid timeout crash

        RequestParams params = new RequestParams();
        params.put("email", userInput);
        params.put("password", userPassword);

        Log.d("LOGIN_URL", Urls.LoginUserWebServiceAddress);

        client.post(Urls.LoginUserWebServiceAddress, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                Log.d("LOGIN", "Request Started");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                String res = new String(responseBody).trim();
                Log.d("LOGIN_RESPONSE", res);

                if(res.equalsIgnoreCase("success")){

                    // 🔥 SAVE SESSION
                    SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putBoolean("isLoggedIn", true);
                    editor.putString("user_input", userInput);

                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                } else if(res.equalsIgnoreCase("invalid_password")){

                    Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();

                } else if(res.equalsIgnoreCase("not_found")){

                    Toast.makeText(LoginActivity.this, "User Not Found", Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(LoginActivity.this, "Server Response: " + res, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();

                Log.e("LOGIN_ERROR", error.toString());

                Toast.makeText(LoginActivity.this,
                        "Server Error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}