package com.parth.raktsaathi;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class EditProfileActivity extends AppCompatActivity {

    EditText etName, etMobile, etCity;
    Button btnSave;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_profile);

        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etCity = findViewById(R.id.etCity);
        btnSave = findViewById(R.id.btnSave);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating...");
        progressDialog.setCancelable(false);

        // 🔥 Load old data
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);

        etName.setText(sp.getString("name", ""));
        etMobile.setText(sp.getString("mobile", ""));
        etCity.setText(sp.getString("city", ""));

        // 🔥 Save button
        btnSave.setOnClickListener(v -> updateProfile());
    }

    private void updateProfile() {

        String name = etName.getText().toString();
        String mobile = etMobile.getText().toString();
        String city = etCity.getText().toString();

        progressDialog.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("name", name);
        params.put("phone", mobile);
        params.put("city", city);

        client.post(Urls.UPDATE_PROFILE, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        progressDialog.dismiss();

                        String res = new String(responseBody).trim();

                        if(res.equals("success")){

                            // 🔥 update local session
                            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();

                            editor.putString("name", name);
                            editor.putString("mobile", mobile);
                            editor.putString("city", city);

                            editor.apply();

                            Toast.makeText(EditProfileActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                            finish();

                        } else {
                            Toast.makeText(EditProfileActivity.this, "Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}