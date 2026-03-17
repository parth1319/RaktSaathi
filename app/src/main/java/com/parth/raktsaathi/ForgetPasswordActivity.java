package com.parth.raktsaathi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class ForgetPasswordActivity extends AppCompatActivity {

    EditText etForgetPasswordUserName, etForgetPasswordNewPassword, etForgetPasswordConfirmPassword;
    Button btnForgetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        etForgetPasswordUserName = findViewById(R.id.etForgetPasswordUserName);
        etForgetPasswordNewPassword = findViewById(R.id.etForgetPasswordNewPassword);
        etForgetPasswordConfirmPassword = findViewById(R.id.etForgetPasswordConfirmPassword);
        btnForgetPassword = findViewById(R.id.btnForgetPassword);

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etForgetPasswordUserName.getText().toString().isEmpty()) {
                    etForgetPasswordUserName.setError("Please Enter Your Username");
                } else if (etForgetPasswordNewPassword.getText().toString().length() < 8) {
                    etForgetPasswordNewPassword.setError("Username must be greater than 8 characters");
                } else if (etForgetPasswordNewPassword.getText().toString().isEmpty()) {
                    etForgetPasswordNewPassword.setError("Please Enter Your Password");
                } else if (etForgetPasswordConfirmPassword.getText().toString().isEmpty()) {
                    etForgetPasswordConfirmPassword.setError("Please Enter Your Confirm Password");
                } else if (etForgetPasswordConfirmPassword.getText().toString().length() < 8) {
                    etForgetPasswordConfirmPassword.setError("Confirm Password must be greater than 8 characters");
                } else if (!etForgetPasswordNewPassword.getText().toString().contains(etForgetPasswordConfirmPassword.getText().toString())) {
                    etForgetPasswordConfirmPassword.setError("New Password and Confirm Password must be same");
                } else {
                    forgetPassword();
                }
            }
        });
    }

    private void forgetPassword() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("username", etForgetPasswordUserName.getText().toString());
        params.put("password", etForgetPasswordNewPassword.getText().toString());

        client.post(Urls.ForgetPasswordWebService, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String status = response.getString("success");
                    if (status.equals("1")) {
                        Toast.makeText(ForgetPasswordActivity.this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(ForgetPasswordActivity.this, "Password not change", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(ForgetPasswordActivity.this,
                        "Server not found",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
