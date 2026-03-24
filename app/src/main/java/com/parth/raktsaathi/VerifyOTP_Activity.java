package com.parth.raktsaathi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class VerifyOTP_Activity extends AppCompatActivity {

    EditText otp;
    Button verifyBtn;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

        otp = findViewById(R.id.otp);
        verifyBtn = findViewById(R.id.verifyBtn);

        // 🔥 Get email from previous screen
        email = getIntent().getStringExtra("email");

        verifyBtn.setOnClickListener(v -> {

            String userOtp = otp.getText().toString().trim();

            if (TextUtils.isEmpty(userOtp)) {
                otp.setError("Enter OTP");
                return;
            }

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("email", email);
            params.put("otp", userOtp);

            client.post(Urls.VerifyOTPWebServiceAddress, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            String response = new String(responseBody).trim();

                            if (response.equalsIgnoreCase("success")) {

                                Toast.makeText(VerifyOTP_Activity.this,
                                        "OTP Verified",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(VerifyOTP_Activity.this, Reset_PasswordActivity.class);
                                intent.putExtra("email", email);
                                startActivity(intent);

                            } else {
                                Toast.makeText(VerifyOTP_Activity.this,
                                        "Invalid OTP",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(VerifyOTP_Activity.this,
                                    "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}