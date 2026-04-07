package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class VerifyOTP_Activity extends AppCompatActivity {

    EditText etOtp;
    Button btnVerifyOtp;
    TextView tvResend;

    ProgressDialog progressDialog;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        etOtp = findViewById(R.id.etOtp);
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp);
        tvResend = findViewById(R.id.tvResend);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Verifying OTP...");
        progressDialog.setCancelable(false);

        // 🔥 email ghe previous screen madhun
        email = getIntent().getStringExtra("email");

        btnVerifyOtp.setOnClickListener(v -> verifyOtp());
        tvResend.setOnClickListener(v -> resendOtp());
    }

    // 🔥 VERIFY OTP FROM DATABASE
    private void verifyOtp() {

        String otp = etOtp.getText().toString().trim();

        if (TextUtils.isEmpty(otp)) {
            etOtp.setError("Enter OTP");
            return;
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("email", email);
        params.put("otp", otp);

        client.post(Urls.VERIFY_OTP, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                String res = new String(responseBody).trim();

                if (res.equalsIgnoreCase("success")) {
                    Toast.makeText(VerifyOTP_Activity.this,
                            "OTP Verified Successfully",
                            Toast.LENGTH_SHORT).show();

                    // 👉 next screen (Reset Password)
                    Intent i = new Intent(VerifyOTP_Activity.this, Reset_PasswordActivity.class);
                    i.putExtra("email", email);
                    startActivity(i);
                    finish();

                } else {
                    Toast.makeText(VerifyOTP_Activity.this,
                            "Invalid OTP",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();

                Toast.makeText(VerifyOTP_Activity.this,
                        "Server Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔥 RESEND OTP
    private void resendOtp() {

        progressDialog.setMessage("Resending OTP...");
        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("email", email);

        client.post(Urls.SEND_OTP, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                Toast.makeText(VerifyOTP_Activity.this,
                        "OTP Resent Successfully",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();

                Toast.makeText(VerifyOTP_Activity.this,
                        "Failed to resend OTP",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}