package com.parth.raktsaathi;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, phone, email, location, password, confirmPassword;
    Spinner blood;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // 🔗 Bind views
        name = findViewById(R.id.et_name);
        phone = findViewById(R.id.et_phone);
        email = findViewById(R.id.et_email);
        location = findViewById(R.id.et_location);
        password = findViewById(R.id.et_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        blood = findViewById(R.id.sp_blood);
        register = findViewById(R.id.btn_register);

        // 🩸 Spinner setup
        String[] bloodGroups = {"Select Blood Group","A+","A-","B+","B-","O+","O-","AB+","AB-"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, bloodGroups);

        blood.setAdapter(adapter);

        // 🔥 Button click
        register.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String sName = name.getText().toString();
        String sPhone = phone.getText().toString();
        String sEmail = email.getText().toString();
        String sLocation = location.getText().toString();
        String sPassword = password.getText().toString();
        String sConfirm = confirmPassword.getText().toString();
        String sBlood = blood.getSelectedItem().toString();

        if (!sPassword.equals(sConfirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("name", sName);
        params.put("phone", sPhone);
        params.put("email", sEmail);
        params.put("password", sPassword);
        params.put("blood_group", sBlood);
        params.put("location", sLocation);

        client.post(Urls.UserRegistrationWebServiceAddress, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(RegistrationActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(RegistrationActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}