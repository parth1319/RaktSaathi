package com.parth.raktsaathi.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parth.raktsaathi.NotificationActivity;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

public class DonateFragment extends Fragment {

    EditText etName, etMobile;
    Spinner spBlood, spState, spCity, spAge;
    LinearLayout formLayout;
    CardView successLayout;
    TextView tvStatusLink, tvAddress;
    Button btnSubmit;

    HashMap<String, String[]> cityMap;

    public DonateFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        etName = view.findViewById(R.id.etName);
        etMobile = view.findViewById(R.id.etMobile);
        spBlood = view.findViewById(R.id.spBlood);
        spState = view.findViewById(R.id.spState);
        spCity = view.findViewById(R.id.spCity);
        spAge = view.findViewById(R.id.spAge);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        formLayout = view.findViewById(R.id.formLayout);
        successLayout = view.findViewById(R.id.successLayout);
        tvStatusLink = view.findViewById(R.id.tvStatusLink);

        // 🔴 Blood
        String[] bloodGroups = {"Select Blood","A+","A-","B+","B-","O+","O-","AB+","AB-"};
        spBlood.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, bloodGroups));

        // 🟢 Age Spinner (18–60)
        ArrayList<String> ages = new ArrayList<>();
        ages.add("Select Age");
        for(int i=18; i<=60; i++){
            ages.add(String.valueOf(i));
        }

        spAge.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, ages));

        // 🔵 States
        String[] states = {"Select State","Maharashtra","Uttar Pradesh","Gujarat"};
        spState.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, states));

        // 🧠 City Map
        cityMap = new HashMap<>();
        cityMap.put("Maharashtra", new String[]{"Mumbai","Pune","Latur"});
        cityMap.put("Uttar Pradesh", new String[]{"Ayodhya","Lucknow"});
        cityMap.put("Gujarat", new String[]{"Ahmedabad","Surat"});

        // State → City
        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                String state = spState.getSelectedItem().toString();
                if(cityMap.containsKey(state)){
                    spCity.setAdapter(new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            cityMap.get(state)));
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // City → Address
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View v, int pos, long id) {
                String state = spState.getSelectedItem().toString();
                String city = spCity.getSelectedItem().toString();
                if(!state.equals("Select State")){
                    tvAddress.setText(city + ", " + state);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Status click
        tvStatusLink.setOnClickListener(v ->
                startActivity(new Intent(getContext(), NotificationActivity.class)));

        // Submit
        btnSubmit.setOnClickListener(v -> {

            String name = etName.getText().toString();
            String mobile = etMobile.getText().toString();
            String blood = spBlood.getSelectedItem().toString();
            String age = spAge.getSelectedItem().toString();
            String state = spState.getSelectedItem().toString();
            String city = spCity.getSelectedItem().toString();

            if(TextUtils.isEmpty(name)){
                etName.setError("Enter Name"); return;
            }

            if(age.equals("Select Age")){
                Toast.makeText(getContext(),"Select Age",Toast.LENGTH_SHORT).show();
                return;
            }

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("name", name);
            params.put("age", age);
            params.put("blood", blood);
            params.put("mobile", mobile);
            params.put("city", city);
            params.put("state", state);

            client.post(Urls.BloodDonorsWebSerivceAddress, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    String res = new String(responseBody).trim();

                    if(res.equalsIgnoreCase("success")){
                        formLayout.setVisibility(View.GONE);
                        successLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}
