package com.parth.raktsaathi.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.RequestAdapter;
import com.parth.raktsaathi.RequestModel;
import com.parth.raktsaathi.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DonateFragment extends Fragment {

    private EditText etName, etPhone, etAddress;
    private Spinner spAge, spBlood, spArea;
    private Button btnDonate;
    private LinearLayout formLayout, successMsg;
    private RecyclerView rvRequests;
    private RequestAdapter adapter;
    private List<RequestModel> requestList;
    private String userEmail = "";

    public DonateFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_donate, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        userEmail = sp.getString("email", "");

        // Initialize Views
        etName = v.findViewById(R.id.etName);
        etPhone = v.findViewById(R.id.etPhone);
        etAddress = v.findViewById(R.id.etAddress);
        spAge = v.findViewById(R.id.spAge);
        spBlood = v.findViewById(R.id.spBlood);
        spArea = v.findViewById(R.id.spArea);
        btnDonate = v.findViewById(R.id.btnDonate);
        formLayout = v.findViewById(R.id.formLayout);
        successMsg = v.findViewById(R.id.successMsg);
        rvRequests = v.findViewById(R.id.recyclerRequests);

        v.findViewById(R.id.btnBack).setOnClickListener(view -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        setupSpinners();
        loadActiveRequests();

        btnDonate.setOnClickListener(view -> submitDonation());

        return v;
    }

    private void setupSpinners() {
        // Age Spinner
        List<String> ages = new ArrayList<>();
        ages.add("Select Age");
        for(int i=18; i<=60; i++) ages.add(String.valueOf(i));
        
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, ages) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((android.widget.TextView) v).setTextColor(androidx.core.content.ContextCompat.getColor(getContext(), R.color.textPrimary));
                return v;
            }
            @Override
            public boolean isEnabled(int position) { return position != 0; }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                android.widget.TextView tv = (android.widget.TextView) view;
                tv.setTextColor(position == 0 ? android.graphics.Color.GRAY : androidx.core.content.ContextCompat.getColor(getContext(), R.color.textPrimary));
                view.setBackgroundColor(androidx.core.content.ContextCompat.getColor(getContext(), R.color.cardColor));
                return view;
            }
        };
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAge.setAdapter(ageAdapter);

        // Blood Group Spinner
        List<String> bloods = new ArrayList<>();
        bloods.add("Blood Group");
        bloods.add("A+"); bloods.add("A-"); bloods.add("B+"); bloods.add("B-");
        bloods.add("O+"); bloods.add("O-"); bloods.add("AB+"); bloods.add("AB-");
        
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, bloods) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((android.widget.TextView) v).setTextColor(androidx.core.content.ContextCompat.getColor(getContext(), R.color.textPrimary));
                return v;
            }
            @Override
            public boolean isEnabled(int position) { return position != 0; }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                android.widget.TextView tv = (android.widget.TextView) view;
                tv.setTextColor(position == 0 ? android.graphics.Color.GRAY : androidx.core.content.ContextCompat.getColor(getContext(), R.color.textPrimary));
                view.setBackgroundColor(androidx.core.content.ContextCompat.getColor(getContext(), R.color.cardColor));
                return view;
            }
        };
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBlood.setAdapter(bloodAdapter);

        // Area Spinner (Akola District Talukas)
        List<String> areas = new ArrayList<>();
        areas.add("Select Taluka");
        areas.add("Akola");
        areas.add("Akot");
        areas.add("Telhara");
        areas.add("Balapur");
        areas.add("Patur");
        areas.add("Murtizapur");
        areas.add("Barshitakli");
        
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, areas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((android.widget.TextView) v).setTextColor(androidx.core.content.ContextCompat.getColor(getContext(), R.color.textPrimary));
                return v;
            }
            @Override
            public boolean isEnabled(int position) { return position != 0; }
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                android.widget.TextView tv = (android.widget.TextView) view;
                tv.setTextColor(position == 0 ? android.graphics.Color.GRAY : androidx.core.content.ContextCompat.getColor(getContext(), R.color.textPrimary));
                view.setBackgroundColor(androidx.core.content.ContextCompat.getColor(getContext(), R.color.cardColor));
                return view;
            }
        };
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spArea.setAdapter(areaAdapter);
    }

    private void loadActiveRequests() {
        requestList = new ArrayList<>();
        rvRequests.setLayoutManager(new LinearLayoutManager(getContext()));
        
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Urls.GET_REQUESTS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject responseObj = new JSONObject(new String(responseBody));
                    if (responseObj.getString("status").equals("success")) {
                        JSONArray array = responseObj.getJSONArray("data");
                        requestList.clear();
                        for(int i=0; i<array.length(); i++){
                            JSONObject obj = array.getJSONObject(i);
                            requestList.add(new RequestModel(
                                    obj.getString("name"),
                                    obj.getString("mobile"),
                                    obj.getString("blood_group"),
                                    obj.getString("units"),
                                    obj.optString("district", ""),
                                    obj.optString("city", ""),
                                    obj.getString("address")
                            ));
                        }
                        adapter = new RequestAdapter(getContext(), requestList);
                        rvRequests.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void submitDonation() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if(name.isEmpty() || phone.isEmpty() || address.isEmpty() || 
           spAge.getSelectedItemPosition() == 0 || 
           spBlood.getSelectedItemPosition() == 0 || 
           spArea.getSelectedItemPosition() == 0){
            Toast.makeText(getContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show Progress
        android.app.ProgressDialog pd = new android.app.ProgressDialog(getContext());
        pd.setMessage("Registering as Donor...");
        pd.setCancelable(false);
        pd.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("mobile", phone);
        params.put("age", spAge.getSelectedItem().toString());
        params.put("blood", spBlood.getSelectedItem().toString()); 
        params.put("district", spArea.getSelectedItem().toString());
        params.put("city", spArea.getSelectedItem().toString());
        params.put("address", address);

        client.post(Urls.DONATE_BLOOD, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pd.dismiss();
                String response = new String(responseBody).trim();
                if(response.equalsIgnoreCase("success") || response.contains("success")){
                    formLayout.setVisibility(View.GONE);
                    successMsg.setVisibility(View.VISIBLE);
                    // Refresh the list to see if new requests arrived
                    loadActiveRequests();
                } else {
                    Toast.makeText(getContext(), "Error: " + response, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                pd.dismiss();
                Toast.makeText(getContext(), "Server Error! Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
