package com.parth.raktsaathi;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DonateBloodActivity extends AppCompatActivity {

    EditText etdonatebloodName, etdonatebloodMobileNo, etdonatebloodage,
            etdonatebloodaddress, etdonatebloodcity, etdonatebloodweight, etdonateblooddisease;

    Spinner spinnerbloodGroupSpinner;
    RadioGroup rgdonatebloodselectgender;
    RadioButton rbdonatebloodmale, rbdonatebloodfemale, rbdonatebloodother;
    CheckBox cbdonatebloodready;
    Button donatebloodBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_blood);

        // connect IDs
        etdonatebloodName = findViewById(R.id.etdonatebloodName);
        etdonatebloodMobileNo = findViewById(R.id.etdonatebloodMobileNo);
        etdonatebloodage = findViewById(R.id.etdonatebloodage);
        etdonatebloodaddress = findViewById(R.id.etdonatebloodaddress);
        etdonatebloodcity = findViewById(R.id.etdonatebloodcity);
        etdonatebloodweight = findViewById(R.id.etdonatebloodweight);
        etdonateblooddisease = findViewById(R.id.etdonateblooddisease);
        rgdonatebloodselectgender = findViewById(R.id.rgdonatebloodselectgender);
        rbdonatebloodmale = findViewById(R.id.rbdonatebloodmale);
        rbdonatebloodfemale = findViewById(R.id.rbdonatebloodfemale);
        rbdonatebloodother = findViewById(R.id.rbdonatebloodother);
        cbdonatebloodready = findViewById(R.id.cbdonatebloodready);
        donatebloodBtn = findViewById(R.id.donatebloodBtn);
        spinnerbloodGroupSpinner = findViewById(R.id.spinnerbloodGroupSpinner);

        String[] bloodGroups = {"Select Blood Group",
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                bloodGroups);

        spinnerbloodGroupSpinner.setAdapter(bloodAdapter);

        donatebloodBtn.setOnClickListener(v -> donateBlood());
    }

    private void donateBlood() {

        String sName = etdonatebloodName.getText().toString().trim();
        String sPhone = etdonatebloodMobileNo.getText().toString().trim();
        String sAge = etdonatebloodage.getText().toString().trim();
        String sAddress = etdonatebloodaddress.getText().toString().trim();
        String sCity = etdonatebloodcity.getText().toString().trim();
        String sWeight = etdonatebloodweight.getText().toString().trim();
        String sBlood_Group = spinnerbloodGroupSpinner.getSelectedItem().toString();

        // NAME
        if (TextUtils.isEmpty(sName)) {
            etdonatebloodName.setError("Enter full name");
            etdonatebloodName.requestFocus();
            return;
        }

        // PHONE
        if (TextUtils.isEmpty(sPhone)) {
            etdonatebloodMobileNo.setError("Enter mobile number");
            etdonatebloodMobileNo.requestFocus();
            return;
        } else if (sPhone.length() != 10) {
            etdonatebloodMobileNo.setError("Enter valid 10 digit number");
            etdonatebloodMobileNo.requestFocus();
            return;
        }

        // AGE
        if (TextUtils.isEmpty(sAge)) {
            etdonatebloodage.setError("Enter age");
            etdonatebloodage.requestFocus();
            return;
        } else {
            try {
                int ageVal = Integer.parseInt(sAge);
                if (ageVal < 18 || ageVal > 60) {
                    etdonatebloodage.setError("Age must be between 18 to 60");
                    etdonatebloodage.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etdonatebloodage.setError("Enter valid age");
                etdonatebloodage.requestFocus();
                return;
            }
        }

        // WEIGHT
        if (TextUtils.isEmpty(sWeight)) {
            etdonatebloodweight.setError("Enter weight");
            etdonatebloodweight.requestFocus();
            return;
        } else {
            try {
                int weightVal = Integer.parseInt(sWeight);
                if (weightVal < 50) {
                    etdonatebloodweight.setError("Minimum weight should be 50kg");
                    etdonatebloodweight.requestFocus();
                    return;
                }
            } catch (NumberFormatException e) {
                etdonatebloodweight.setError("Enter valid weight");
                etdonatebloodweight.requestFocus();
                return;
            }
        }

        // BLOOD GROUP
        if (sBlood_Group.equals("Select Blood Group")) {
            Toast.makeText(this, "Select blood group", Toast.LENGTH_SHORT).show();
            return;
        }

        // GENDER
        int selectedId = rgdonatebloodselectgender.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(this, "Select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        // ADDRESS
        if (TextUtils.isEmpty(sAddress)) {
            etdonatebloodaddress.setError("Enter address");
            etdonatebloodaddress.requestFocus();
            return;
        }

        // CITY
        if (TextUtils.isEmpty(sCity)) {
            etdonatebloodcity.setError("Enter city");
            etdonatebloodcity.requestFocus();
            return;
        }

        // CHECKBOX
        if (!cbdonatebloodready.isChecked()) {
            Toast.makeText(this, "Please confirm you are ready to donate blood", Toast.LENGTH_SHORT).show();
            return;
        }

        donateBloodUser();
    }

    private void donateBloodUser() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("fullname", etdonatebloodName.getText().toString().trim());
        params.put("mobileno", etdonatebloodMobileNo.getText().toString().trim());
        params.put("age", etdonatebloodage.getText().toString().trim());
        params.put("address", etdonatebloodaddress.getText().toString().trim());
        params.put("city", etdonatebloodcity.getText().toString().trim());
        params.put("weight", etdonatebloodweight.getText().toString().trim());
        params.put("disease", etdonateblooddisease.getText().toString().trim());
        params.put("blood_group", spinnerbloodGroupSpinner.getSelectedItem().toString());

        String gender = "Other";
        if (rbdonatebloodmale.isChecked()) gender = "Male";
        else if (rbdonatebloodfemale.isChecked()) gender = "Female";

        params.put("gender", gender); // ✅ FIX
        params.put("ready", "Yes");
        params.put("status", "Pending");

        client.post(Urls.DonateBloodWebService, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    String status = response.getString("success");

                    if (status.equals("1")) {
                        Toast.makeText(DonateBloodActivity.this,
                                "Blood Donation Successfully Done", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(DonateBloodActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();

                    } else {
                        Toast.makeText(DonateBloodActivity.this,
                                "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Toast.makeText(DonateBloodActivity.this,
                        "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}