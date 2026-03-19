package com.parth.raktsaathi.DRB_Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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

public class RequestBloodFragment extends Fragment {

    EditText patientName, hospitalName, city, hospitalAddress, contactPerson, contactNumber;
    Spinner bloodGroupRequiredSpinner, unitSpinner;
    Button submitBtn;

    public RequestBloodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_request_blood, container, false);

        patientName = view.findViewById(R.id.etrequestbloodpatientname);
        hospitalName = view.findViewById(R.id.etrequestbloodhospitalname);
        city = view.findViewById(R.id.etrequestbloodcity);
        hospitalAddress = view.findViewById(R.id.etrequestbloodhospitaladdress);
        contactPerson = view.findViewById(R.id.etrequestbloodcpname);
        contactNumber = view.findViewById(R.id.etrequestbloodcnumber);

        bloodGroupRequiredSpinner = view.findViewById(R.id.spinnerrequestbloodgroup);
        unitSpinner = view.findViewById(R.id.spinnerrequestbloodunit);

        submitBtn = view.findViewById(R.id.donatebloodBtn);


        String[] bloodGroups = {
                "Select Blood Group",
                "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
        };

        if (getContext() != null) {
            ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    bloodGroups
            );
            bloodGroupRequiredSpinner.setAdapter(bloodAdapter);
        }

        // Units Spinner
        String[] units = {
                "Select Units",
                "1 Unit", "2 Units", "3 Units", "4 Units", "5 Units"
        };

        if (getContext() != null) {
            ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                    getContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    units
            );
            unitSpinner.setAdapter(unitAdapter);
        }

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

                if (getContext() != null) {
                    Toast.makeText(getContext(),
                            "Please fill all fields",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }

            requestBlood();
        });

        return view;
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
                super.onSuccess(statusCode, headers, response);

                try {
                    String status = response.getString("success");
                    if (status.equals("1")) {
                        if (getContext() != null) {
                            Toast.makeText(getContext(), "Blood Request Successfully Done", Toast.LENGTH_SHORT).show();
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
