package com.parth.raktsaathi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class RequestBloodActivity extends AppCompatActivity {

    EditText patientName, hospitalName, city, hospitalAddress, contactPerson, contactNumber;
    Spinner bloodGroupRequiredSpinner, unitSpinner;
    Button submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_blood);

        patientName = findViewById(R.id.etrequestbloodpatientname);
        hospitalName = findViewById(R.id.etrequestbloodhospitalname);
        city = findViewById(R.id.etrequestbloodcity);
        hospitalAddress = findViewById(R.id.etrequestbloodhospitaladdress);
        contactPerson = findViewById(R.id.etrequestbloodcpname);
        contactNumber = findViewById(R.id.etrequestbloodcnumber);

        bloodGroupRequiredSpinner = findViewById(R.id.spinnerrequestbloodgroup);
        unitSpinner = findViewById(R.id.spinnerrequestbloodunit);

        submitBtn = findViewById(R.id.donatebloodBtn);

        // Blood group spinner
        String[] bloodGroups = {
                "Select Blood Group",
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        };

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                bloodGroups
        );
        bloodGroupRequiredSpinner.setAdapter(bloodAdapter);

        // Units spinner
        String[] units = {
                "Select Units",
                "1 Unit", "2 Units", "3 Units", "4 Units", "5 Units"
        };

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                units
        );
        unitSpinner.setAdapter(unitAdapter);

        submitBtn.setOnClickListener(v -> {

            String pName = patientName.getText().toString().trim();
            String hospital = hospitalName.getText().toString().trim();
            String cityName = city.getText().toString().trim();
            String address = hospitalAddress.getText().toString().trim();
            String cpName = contactPerson.getText().toString().trim();
            String cNumber = contactNumber.getText().toString().trim();

            String bloodGroup = bloodGroupRequiredSpinner.getSelectedItem().toString();
            String unit = unitSpinner.getSelectedItem().toString();

            // Validation
            if (pName.isEmpty() ||
                    hospital.isEmpty() ||
                    cityName.isEmpty() ||
                    address.isEmpty() ||
                    cpName.isEmpty() ||
                    cNumber.isEmpty() ||
                    bloodGroup.equals("Select Blood Group") ||
                    unit.equals("Select Units")) {

                Toast.makeText(RequestBloodActivity.this,
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            requestBlood();
        });
    }

    private void requestBlood() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("patientName", patientName.getText().toString().trim());
        params.put("hospitalName", hospitalName.getText().toString().trim());
        params.put("city", city.getText().toString().trim());
        params.put("hospitalAddress", hospitalAddress.getText().toString().trim());
        params.put("contactPerson", contactPerson.getText().toString().trim());
        params.put("contactNumber", contactNumber.getText().toString().trim());
        params.put("bloodGroup", bloodGroupRequiredSpinner.getSelectedItem().toString());
        params.put("unit", unitSpinner.getSelectedItem().toString());
        params.put("status", "Pending");

        client.post(Urls.RequestBloodWebService, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    String status = response.getString("success");

                    if (status.equals("1")) {
                        Toast.makeText(RequestBloodActivity.this,
                                "Blood Request Successfully Done", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(RequestBloodActivity.this, HomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();

                    } else {
                        Toast.makeText(RequestBloodActivity.this,
                                "Something Went Wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Toast.makeText(RequestBloodActivity.this,
                        "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}