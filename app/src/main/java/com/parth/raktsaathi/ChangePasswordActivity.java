package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class ChangePasswordActivity extends AppCompatActivity {

    TextInputEditText oldPass, newPass, confirmPass;
    MaterialButton btnChange;
    TextView tvStrength;

    ProgressDialog progressDialog;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        userEmail = sp.getString("email", "");

        oldPass = findViewById(R.id.etOldPass);
        newPass = findViewById(R.id.etNewPass);
        confirmPass = findViewById(R.id.etConfirmPass);
        btnChange = findViewById(R.id.btnChangePass);
        tvStrength = findViewById(R.id.tvStrength);
        
        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating Password...");

        btnChange.setOnClickListener(v -> changePassword());
    }

    private void changePassword() {

        String oldP = oldPass.getText().toString().trim();
        String newP = newPass.getText().toString().trim();
        String confirmP = confirmPass.getText().toString().trim();

        if (TextUtils.isEmpty(oldP) || TextUtils.isEmpty(newP) || TextUtils.isEmpty(confirmP)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newP.equals(confirmP)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newP.length() < 6) {
            tvStrength.setText("Weak Password ❌");
            return;
        } else if (newP.length() < 10) {
            tvStrength.setText("Medium Password ⚠️");
        } else {
            tvStrength.setText("Strong Password ✅");
        }

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("email", userEmail);
        params.put("old_password", oldP);
        params.put("new_password", newP);

        client.post(Urls.CHANGE_PASSWORD, params, new com.loopj.android.http.AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                progressDialog.dismiss();

                String res = new String(responseBody).trim();

                if (res.equalsIgnoreCase("success")) {
                    Toast.makeText(ChangePasswordActivity.this, "Password Updated ✅", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (res.equalsIgnoreCase("wrong_old_password")) {
                    Toast.makeText(ChangePasswordActivity.this, "Wrong Old Password ❌", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Failed: " + res, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                progressDialog.dismiss();
                Toast.makeText(ChangePasswordActivity.this, "Server Error ⚠️", Toast.LENGTH_SHORT).show();
            }
        });
    }
}