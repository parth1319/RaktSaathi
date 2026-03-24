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

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText email;
    Button sendOtpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // 🔗 Bind Views
        email = findViewById(R.id.forgotEmail);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);

        sendOtpBtn.setOnClickListener(v -> {

            String userEmail = email.getText().toString().trim();

            // ✅ Validation
            if (TextUtils.isEmpty(userEmail)) {
                email.setError("Enter Email");
                return;
            }

            // 🔥 API CALL
            AsyncHttpClient client = new AsyncHttpClient();

            RequestParams params = new RequestParams();
            params.put("email", userEmail);

            client.post(Urls.ForgotPasswordWebServiceAddress, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            String otp = new String(responseBody);

                            Toast.makeText(ForgotPasswordActivity.this,
                                    "OTP Sent Successfully",
                                    Toast.LENGTH_SHORT).show();

                            // 👉 Next screen
                            Intent intent = new Intent(ForgotPasswordActivity.this, VerifyOTP_Activity.class);
                            intent.putExtra("email", userEmail);
                            intent.putExtra("otp", otp);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}