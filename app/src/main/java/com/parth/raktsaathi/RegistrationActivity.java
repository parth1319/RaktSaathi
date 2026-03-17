package com.parth.raktsaathi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {

    private static final String TAG = "RegistrationActivity";
    EditText etUsername, etMobileNo, etEmailid, etPassword, etBloodGroup, etAddress, etCity;
    Button btnRegister;
    TextView tvAlreadyHaveAccount;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        preferences = PreferenceManager.getDefaultSharedPreferences(RegistrationActivity.this);
        editor = preferences.edit();


        etUsername = findViewById(R.id.etRegisterUserName);
        etMobileNo = findViewById(R.id.etRegisterMobileNo);
        etEmailid = findViewById(R.id.etRegisterEmailid);
        etPassword = findViewById(R.id.etRegisterPassword);
        etBloodGroup = findViewById(R.id.etRegisterBloodGroup);
        etAddress = findViewById(R.id.etRegisterAddress);
        etCity = findViewById(R.id.etRegisterCity);
        btnRegister = findViewById(R.id.btnRegisterRegister);
        tvAlreadyHaveAccount = findViewById(R.id.tvRegisterAlreadyHaveAccount);

        btnRegister.setOnClickListener((View v) -> {
            Log.d(TAG, "Register button clicked");


            String username = etUsername.getText().toString().trim();
            String mobileNo = etMobileNo.getText().toString().trim();
            String email = etEmailid.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String bloodGroup = etBloodGroup.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String city = etCity.getText().toString().trim();

            if (username.isEmpty()) {
                etUsername.setError("Enter Username");
            } else if (mobileNo.isEmpty()) {
                etMobileNo.setError("Enter Mobile Number");
            } else if (mobileNo.length() != 10) {
                etMobileNo.setError("Enter Valid Mobile Number");
            } else if (email.isEmpty()) {
                etEmailid.setError("Enter Email");
            } else if (password.isEmpty()) {
                etPassword.setError("Enter Password");
            } else if (password.length() < 8) {
                etPassword.setError("Password must be at least 8 characters");
            } else if (bloodGroup.isEmpty()) {
                etBloodGroup.setError("Enter Blood Group");
            } else if (address.isEmpty()) {
                etAddress.setError("Enter Address");
            } else if (city.isEmpty()) {
                etCity.setError("Enter City");
            } else {
                registerUser();
            }
        });
    }

    private void registerUser() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("username", etUsername.getText().toString());
        params.put("mobileno", etMobileNo.getText().toString());
        params.put("emailid", etEmailid.getText().toString());
        params.put("password", etPassword.getText().toString());
        params.put("blood_group", etBloodGroup.getText().toString());
        params.put("address", etAddress.getText().toString());
        params.put("city", etCity.getText().toString());

        client.post(Urls.RegisterUserWebServiceAddress, params, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            String status = response.getString("success");
                            if (status.equals("1")) {
                                Toast.makeText(RegistrationActivity.this, "Registration Successfully Done", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(RegistrationActivity.this, LoginActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Toast.makeText(RegistrationActivity.this, "Username or Password Exists", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                        Toast.makeText(RegistrationActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                }
        );

    }
}