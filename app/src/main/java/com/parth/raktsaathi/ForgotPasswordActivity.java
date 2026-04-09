package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText forgotEmail;
    Button sendOtpBtn;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        forgotEmail = findViewById(R.id.forgotEmail);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending OTP...");
        progressDialog.setCancelable(false);

        sendOtpBtn.setOnClickListener(v -> sendOtp());
    }

    private void sendOtp() {

        String email = forgotEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            forgotEmail.setError("Enter Email");
            return;
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);

        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.SEND_OTP, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                String response = new String(responseBody).trim();

                if (response.equalsIgnoreCase("success")) {

                    Toast.makeText(ForgotPasswordActivity.this,
                            "OTP Sent Successfully",
                            Toast.LENGTH_SHORT).show();

                    String otp = new String(responseBody).trim();

                    Intent i = new Intent(ForgotPasswordActivity.this, VerifyOTP_Activity.class);
                    i.putExtra("email", email);
                    i.putExtra("otp", otp);
                    startActivity(i);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this,
                            response,
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();

                Toast.makeText(ForgotPasswordActivity.this,
                        "Server Error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}