package com.parth.raktsaathi.DRB_Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parth.raktsaathi.HomeActivity;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DonateBloodFragment extends Fragment {

    EditText etdonatebloodName, etdonatebloodMobileNo, etdonatebloodage, etdonatebloodaddress, etdonatebloodcity, etdonatebloodweight, etdonateblooddisease;
    Spinner spinnerbloodGroupSpinner;
    RadioGroup rgdonatebloodselectgender;
    RadioButton rbdonatebloodmale, rbdonatebloodfemale, rbdonatebloodother;
    CheckBox cbdonatebloodready;
    Button donatebloodBtn;

    public DonateBloodFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donate_blood, container, false);

        // connect IDs
        etdonatebloodName = view.findViewById(R.id.etdonatebloodName);
        etdonatebloodMobileNo = view.findViewById(R.id.etdonatebloodMobileNo);
        etdonatebloodage = view.findViewById(R.id.etdonatebloodage);
        etdonatebloodaddress = view.findViewById(R.id.etdonatebloodaddress);
        etdonatebloodcity = view.findViewById(R.id.etdonatebloodcity);
        etdonatebloodweight = view.findViewById(R.id.etdonatebloodweight);
        etdonateblooddisease = view.findViewById(R.id.etdonateblooddisease);
        rgdonatebloodselectgender = view.findViewById(R.id.rgdonatebloodselectgender);
        rbdonatebloodmale = view.findViewById(R.id.rbdonatebloodmale);
        rbdonatebloodfemale = view.findViewById(R.id.rbdonatebloodfemale);
        rbdonatebloodother = view.findViewById(R.id.rbdonatebloodother);
        cbdonatebloodready = view.findViewById(R.id.cbdonatebloodready);
        donatebloodBtn = view.findViewById(R.id.donatebloodBtn);
        spinnerbloodGroupSpinner = view.findViewById(R.id.spinnerbloodGroupSpinner);

        String[] bloodGroups = {"Select Blood Group",
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        if (getContext() != null) {
            ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    bloodGroups);
            spinnerbloodGroupSpinner.setAdapter(bloodAdapter);
        }

        // button click
        donatebloodBtn.setOnClickListener(v -> donateBlood());
        return view;
    }

    private void donateBlood() {
        String sName = etdonatebloodName.getText().toString().trim();
        String sPhone = etdonatebloodMobileNo.getText().toString().trim();
        String sAge = etdonatebloodage.getText().toString().trim();
        String sAddress = etdonatebloodaddress.getText().toString().trim();
        String sCity = etdonatebloodcity.getText().toString().trim();
        String sWeight = etdonatebloodweight.getText().toString().trim();
        String sBlood_Group = spinnerbloodGroupSpinner.getSelectedItem().toString();

        // 🔴 NAME
        if (TextUtils.isEmpty(sName)) {
            etdonatebloodName.setError("Enter full name");
            etdonatebloodName.requestFocus();
            return;
        }

        // 🔴 PHONE
        if (TextUtils.isEmpty(sPhone)) {
            etdonatebloodMobileNo.setError("Enter mobile number");
            etdonatebloodMobileNo.requestFocus();
            return;
        } else if (sPhone.length() != 10) {
            etdonatebloodMobileNo.setError("Enter valid 10 digit number");
            etdonatebloodMobileNo.requestFocus();
            return;
        }

        // 🔴 AGE
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

        // 🔴 WEIGHT
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

        // 🔴 BLOOD GROUP
        if (sBlood_Group.equals("Select Blood Group")) {
            Toast.makeText(getContext(), "Select blood group", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔴 GENDER
        int selectedId = rgdonatebloodselectgender.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Select gender", Toast.LENGTH_SHORT).show();
            return;
        }

        // 🔴 ADDRESS
        if (TextUtils.isEmpty(sAddress)) {
            etdonatebloodaddress.setError("Enter address");
            etdonatebloodaddress.requestFocus();
            return;
        }

        // 🔴 CITY
        if (TextUtils.isEmpty(sCity)) {
            etdonatebloodcity.setError("Enter city");
            etdonatebloodcity.requestFocus();
            return;
        }

        // 🔴 CHECKBOX
        if (!cbdonatebloodready.isChecked()) {
            Toast.makeText(getContext(), "Please confirm you are ready to donate blood", Toast.LENGTH_SHORT).show();
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

        params.put("gender", "gender");
        params.put("ready", "Yes");
        params.put("status", "Pending");

        client.post(Urls.DonateBloodWebService, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    String status = response.getString("success");
                    if (status.equals("1")) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Blood Donation Successfully Done", Toast.LENGTH_SHORT).show();
                        }
                        if (isAdded() && getActivity() != null) {
                            Intent i = new Intent(getActivity(), HomeActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtra("targetFragment", "Home");
                            startActivity(i);
                            getActivity().finish();
                        }
                    } else {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
