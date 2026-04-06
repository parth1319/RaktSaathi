package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class ChangePasswordActivity extends AppCompatActivity {

    EditText etOldPass, etNewPass, etConfirmPass;
    Button btnChangePass;

    ProgressDialog progressDialog;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etOldPass = findViewById(R.id.etOldPass);
        etNewPass = findViewById(R.id.etNewPass);
        etConfirmPass = findViewById(R.id.etConfirmPass);
        btnChangePass = findViewById(R.id.btnChangePass);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Password...");
        progressDialog.setCancelable(false);

        // 🔥 SharedPreferences madhun email ghe
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        email = sp.getString("email", "");

        btnChangePass.setOnClickListener(v -> changePassword());
    }

    private void changePassword(){

        String oldPass = etOldPass.getText().toString().trim();
        String newPass = etNewPass.getText().toString().trim();
        String confirmPass = etConfirmPass.getText().toString().trim();

        // 🔴 validation
        if(TextUtils.isEmpty(oldPass)){
            etOldPass.setError("Enter Old Password");
            return;
        }

        if(TextUtils.isEmpty(newPass)){
            etNewPass.setError("Enter New Password");
            return;
        }

        if(TextUtils.isEmpty(confirmPass)){
            etConfirmPass.setError("Confirm Password");
            return;
        }

        if(!newPass.equals(confirmPass)){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("old_password", oldPass);
        params.put("new_password", newPass);

        client.post(Urls.CHANGE_PASSWORD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                String res = new String(responseBody).trim();

                if(res.equalsIgnoreCase("success")){

                    Toast.makeText(ChangePasswordActivity.this,
                            "Password Changed Successfully",
                            Toast.LENGTH_SHORT).show();

                    finish();

                } else if(res.equalsIgnoreCase("wrong_old_password")){

                    Toast.makeText(ChangePasswordActivity.this,
                            "Old Password Incorrect",
                            Toast.LENGTH_SHORT).show();

                } else {

                    Toast.makeText(ChangePasswordActivity.this,
                            "Update Failed",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();

                Toast.makeText(ChangePasswordActivity.this,
                        "Server Error",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}