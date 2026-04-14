package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.loopj.android.http.*;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    EditText email, password;
    Button loginbtnlogin;
    TextView signupText, forgotPassword;
    View googleBtn;

    ProgressDialog progressDialog;

    GoogleSignInClient googleSignInClient;
    int RC_SIGN_IN = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences themeSp = getSharedPreferences("theme", MODE_PRIVATE);
        boolean isDark = themeSp.getBoolean("isDark", false);
        if (isDark) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        if (sp.getBoolean("isLoggedIn", false)) {
            checkProfileAndRedirect(sp.getString("email",""));
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
                .requestIdToken("790875880674-fsn1vpgi69f86f7mgnq39bdncvloal0q.apps.googleusercontent.com")
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        googleBtn.setOnClickListener(v -> signInWithGoogle());
    }

    private void signInWithGoogle() {
        // Force sign-out before starting intent to show the Account Picker dialog
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn
                        .getSignedInAccountFromIntent(data)
                        .getResult(ApiException.class);

                String name = account.getDisplayName();
                String emailStr = account.getEmail();
                String googleId = account.getId();

                sendGoogleDataToServer(name, emailStr, googleId);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Google Failed", Toast.LENGTH_LONG).show();
            }
        }
    }


    private void sendGoogleDataToServer(String name, String emailStr, String googleId) {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("name", name);
        params.put("email", emailStr);
        params.put("google_id", googleId);

        client.post(Urls.GoogleSignIn, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                saveLogin(emailStr);

                Toast.makeText(LoginActivity.this, "Google Login Success", Toast.LENGTH_SHORT).show();

                checkProfileAndRedirect(emailStr);
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
            email.setError("Enter Email or Phone");
            return;
        }

        if (TextUtils.isEmpty(userPassword)) {
            password.setError("Enter Password");
            return;
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();


        params.put("input", userInput);
        params.put("password", userPassword);

        client.post(Urls.LOGIN, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                String res = new String(responseBody).trim();
                Log.d("LOGIN_RESPONSE", res);

                try {
                    JSONObject obj = new JSONObject(res);

                    String email = obj.getString("email");

                    saveLogin(email);

                    Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();

                    checkProfileAndRedirect(email);

                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, "Invalid Login", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();
                Toast.makeText(LoginActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void checkProfileAndRedirect(String email){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try{
                    JSONObject obj = new JSONObject(new String(responseBody));

                    String phone = obj.optString("phone","");
                    String blood = obj.optString("blood_group","");
                    String city = obj.optString("location","");

                    if(phone.equals("null")) phone="";
                    if(blood.equals("null")) blood="";
                    if(city.equals("null")) city="";

                    if(phone.isEmpty() || blood.isEmpty() || city.isEmpty()){

                        Toast.makeText(LoginActivity.this, "Complete your profile from Profile tab ⚠️", Toast.LENGTH_LONG).show();

                    }
                    
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();

                }catch(Exception e){
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                finish();
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