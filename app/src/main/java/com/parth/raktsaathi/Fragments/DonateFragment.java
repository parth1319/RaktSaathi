package com.parth.raktsaathi.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;

import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class DonateFragment extends Fragment {

    EditText etName, etAge, etMobile;
    Spinner spBlood, spState, spCity;
    TextView tvAddress;
    Button btnSubmit;

    HashMap<String, String[]> cityMap;

    public DonateFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        // Bind Views
        etName = view.findViewById(R.id.etName);
        etAge = view.findViewById(R.id.etAge);
        etMobile = view.findViewById(R.id.etMobile);
        spBlood = view.findViewById(R.id.spBlood);
        spState = view.findViewById(R.id.spState);
        spCity = view.findViewById(R.id.spCity);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // 🔴 Blood Spinner
        String[] bloodGroups = {
                "Select Blood Group","A+","A-","B+","B-","O+","O-","AB+","AB-"
        };

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                bloodGroups
        );
        spBlood.setAdapter(bloodAdapter);

        // 🔵 State Spinner
        String[] states = {
                "Select State",
                "Maharashtra",
                "Uttar Pradesh",
                "Madhya Pradesh",
                "Delhi",
                "Gujarat",
                "Karnataka"
        };

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                states
        );
        spState.setAdapter(stateAdapter);

        // 🧠 City Map
        cityMap = new HashMap<>();

        cityMap.put("Maharashtra", new String[]{
                "Mumbai","Pune","Nagpur","Nashik","Aurangabad","Solapur","Nashik",
                "Kolhapur","Latur","Amravati","Akola","Washim","Nanded","Sangli","Buldhana"
        });

        cityMap.put("Uttar Pradesh", new String[]{
                "Lucknow","Kanpur","Varanasi","Ayodhya","Agra","Meerut",
                "Allahabad","Noida","Ghaziabad","Gorakhpur"
        });

        cityMap.put("Madhya Pradesh", new String[]{
                "Bhopal","Indore","Gwalior","Jabalpur","Ujjain",
                "Sagar","Satna","Rewa"
        });

        cityMap.put("Delhi", new String[]{
                "New Delhi","North Delhi","South Delhi","East Delhi","West Delhi"
        });

        cityMap.put("Gujarat", new String[]{
                "Ahmedabad","Surat","Vadodara","Rajkot","Bhavnagar",
                "Jamnagar","Junagadh"
        });

        cityMap.put("Karnataka", new String[]{
                "Bangalore","Mysore","Mangalore","Hubli","Belgaum",
                "Davangere","Shimoga"
        });

        // 🔁 State → City
        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {

                String selectedState = spState.getSelectedItem().toString();

                if(cityMap.containsKey(selectedState)) {

                    String[] cities = cityMap.get(selectedState);

                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            cities
                    );

                    spCity.setAdapter(cityAdapter);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 🔁 City → Address
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view12, int position, long id) {

                String state = spState.getSelectedItem().toString();
                String city = spCity.getSelectedItem().toString();

                if(!state.equals("Select State")) {
                    tvAddress.setText(city + ", " + state);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 🚀 Submit
        btnSubmit.setOnClickListener(v -> {

            String name = etName.getText().toString().trim();
            String age = etAge.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String blood = spBlood.getSelectedItem().toString();
            String state = spState.getSelectedItem().toString();
            String city = spCity.getSelectedItem().toString();

            // Validation
            if(TextUtils.isEmpty(name)){
                etName.setError("Enter Name");
                return;
            }

            if(TextUtils.isEmpty(age)){
                etAge.setError("Enter Age");
                return;
            }

            if(blood.equals("Select Blood Group")){
                Toast.makeText(getContext(), "Select Blood Group", Toast.LENGTH_SHORT).show();
                return;
            }

            if(TextUtils.isEmpty(mobile)){
                etMobile.setError("Enter Mobile");
                return;
            }

            if(state.equals("Select State")){
                Toast.makeText(getContext(), "Select State", Toast.LENGTH_SHORT).show();
                return;
            }

            // API Call
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("name", name);
            params.put("age", age);
            params.put("blood", blood);
            params.put("mobile", mobile);
            params.put("city", city);
            params.put("state", state);

            client.post(Urls.DonateBloodWebServiceAddress, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String res = new String(responseBody).trim();

                    if(res.equalsIgnoreCase("success")){
                        Toast.makeText(getContext(), "Donor Registered", Toast.LENGTH_SHORT).show();

                        // Reset
                        etName.setText("");
                        etAge.setText("");
                        etMobile.setText("");
                        spBlood.setSelection(0);
                        spState.setSelection(0);
                        tvAddress.setText("Selected address will appear here");

                    } else {
                        Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
                }
            });

        });

        return view;
    }
}