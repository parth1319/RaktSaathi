package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class Reset_PasswordActivity extends AppCompatActivity {

    EditText etNewPassword, etConfirmPassword;
    Button btnResetPassword;

    String email;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // 🔥 INIT
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        // 🔥 email ghe VerifyOTPActivity madhun
        email = getIntent().getStringExtra("email");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Password...");
        progressDialog.setCancelable(false);

        // 🔥 BUTTON CLICK
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {

        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        // 🔴 VALIDATION
        if (TextUtils.isEmpty(newPass)) {
            etNewPassword.setError("Enter New Password");
            return;
        }

        if (TextUtils.isEmpty(confirmPass)) {
            etConfirmPassword.setError("Confirm Password");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", newPass);

        // 🔥 API CALL
        client.post(Urls.RESET_PASSWORD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                String res = new String(responseBody).trim();

                if (res.equalsIgnoreCase("success")) {

                    Toast.makeText(Reset_PasswordActivity.this,
                            "Password Updated Successfully",
                            Toast.LENGTH_SHORT).show();

                    // 👉 login screen la ja
                    Intent intent = new Intent(Reset_PasswordActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();

                } else if (res.equalsIgnoreCase("user_not_found")) {

                    Toast.makeText(Reset_PasswordActivity.this,
                            "User not found",
                            Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(Reset_PasswordActivity.this,
                            "Update Failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();

                Toast.makeText(Reset_PasswordActivity.this,
                        "Server Error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}