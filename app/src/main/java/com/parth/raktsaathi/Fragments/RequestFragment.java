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

public class RequestFragment extends Fragment {

    EditText etPatientName, etUnits, etMobile;
    Spinner spBlood, spState, spCity;
    TextView tvAddress;
    Button btnSubmit;
    HashMap<String, String[]> cityMap;

    public RequestFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        etPatientName = view.findViewById(R.id.etPatientName);
        etUnits = view.findViewById(R.id.etUnits);
        etMobile = view.findViewById(R.id.etMobile);
        spBlood = view.findViewById(R.id.spBlood);
        spState = view.findViewById(R.id.spState);
        spCity = view.findViewById(R.id.spCity);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // Blood Group
        String[] bloodGroups = {"Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        spBlood.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, bloodGroups));

        // States & Cities
        String[] states = {"Select State", "Maharashtra", "Uttar Pradesh", "Gujarat"};
        spState.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, states));

        cityMap = new HashMap<>();
        cityMap.put("Maharashtra", new String[]{"Latur", "Pune", "Mumbai", "Nagpur"});
        cityMap.put("Uttar Pradesh", new String[]{"Ayodhya", "Lucknow", "Kanpur"});
        cityMap.put("Gujarat", new String[]{"Ahmedabad", "Surat", "Rajkot"});

        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                String state = spState.getSelectedItem().toString();
                if (cityMap.containsKey(state)) {
                    spCity.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, cityMap.get(state)));
                }
                updateAddress();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                updateAddress();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Submit
        btnSubmit.setOnClickListener(v -> {
            String name = etPatientName.getText().toString();
            if (TextUtils.isEmpty(name)) { etPatientName.setError("Enter Name"); return; }

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("patient_name", name);
            params.put("blood", spBlood.getSelectedItem().toString());
            params.put("units", etUnits.getText().toString());
            params.put("mobile", etMobile.getText().toString());
            params.put("city", spCity.getSelectedItem().toString());
            params.put("state", spState.getSelectedItem().toString());

            client.post(Urls.BloodRequestWebServiceAddress, params, new AsyncHttpResponseHandler() {@Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getContext(), "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void updateAddress() {
        String state = spState.getSelectedItem().toString();
        String city = (spCity.getSelectedItem() != null) ? spCity.getSelectedItem().toString() : "";
        if (!state.equals("Select State")) {
            tvAddress.setText(city + ", " + state);
        } else {
            tvAddress.setText("");
        }
    }
}