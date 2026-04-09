package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parth.raktsaathi.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginbtnlogin;
    TextView signupText, forgotPassword;
    LinearLayout googleBtn;

    ProgressDialog progressDialog;

    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        if (sp.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginbtnlogin = findViewById(R.id.loginbtnlogin);
        signupText = findViewById(R.id.signupText);
        forgotPassword = findViewById(R.id.forgotPassword);
        googleBtn = findViewById(R.id.googleBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        loginbtnlogin.setOnClickListener(v -> loginUser());

        signupText.setOnClickListener(v ->
                startActivity(new Intent(this, RegistrationActivity.class))
        );

        forgotPassword.setOnClickListener(v ->
                startActivity(new Intent(this, ForgotPasswordActivity.class))
        );

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleBtn.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn
                        .getSignedInAccountFromIntent(data)
                        .getResult(ApiException.class);

                sendGoogleDataToServer(account.getDisplayName(), account.getEmail());

            } catch (Exception e) {
                Toast.makeText(this, "Google Sign-In Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void sendGoogleDataToServer(String name, String emailStr) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("name", name);
        params.put("email", emailStr);

        client.post(Urls.GoogleSignIn, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);
                Log.d("GOOGLE_RESPONSE", res);

                saveLogin(emailStr);

                Toast.makeText(LoginActivity.this, "Google Login Success", Toast.LENGTH_SHORT).show();

                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loginUser() {

        String userInput = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (TextUtils.isEmpty(userInput)) {
            email.setError("Enter Email");
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Enter Password");
            return;
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);

        RequestParams params = new RequestParams();
        params.put("email", userInput);
        params.put("password", userPassword);

        client.post(Urls.LOGIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                String res = new String(responseBody).trim();
                Log.d("LOGIN_RESPONSE", res);

                if (res.equalsIgnoreCase("success")) {

                    saveLogin(userInput);

                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                } else {
                    Toast.makeText(LoginActivity.this, res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();

                Toast.makeText(LoginActivity.this,
                        "Server Error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveLogin(String email) {

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean("isLoggedIn", true);
        editor.putString("email", email);

        editor.apply();
    }
}