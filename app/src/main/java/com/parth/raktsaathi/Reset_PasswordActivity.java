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

public class Reset_PasswordActivity extends AppCompatActivity {

    EditText newPass, confirmPass;
    Button resetBtn;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPass = findViewById(R.id.newPass);
        confirmPass = findViewById(R.id.confirmPass);
        resetBtn = findViewById(R.id.resetBtn);

        email = getIntent().getStringExtra("email");

        resetBtn.setOnClickListener(v -> {

            String pass = newPass.getText().toString().trim();
            String confirm = confirmPass.getText().toString().trim();

            if (TextUtils.isEmpty(pass)) {
                newPass.setError("Enter Password");
                return;
            }

            if (pass.length() < 6) {
                newPass.setError("Minimum 6 characters required");
                return;
            }

            if (!pass.equals(confirm)) {
                confirmPass.setError("Password does not match");
                return;
            }

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("email", email);
            params.put("password", pass);

            client.post(Urls.ResetPasswordWebServiceAddress, params,
                    new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                            String response = new String(responseBody).trim();

                            if (response.equalsIgnoreCase("done")) {

                                Toast.makeText(Reset_PasswordActivity.this,
                                        "Password Changed Successfully",
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Reset_PasswordActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            } else {
                                Toast.makeText(Reset_PasswordActivity.this,
                                        "Failed to change password",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(Reset_PasswordActivity.this,
                                    "Error: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}