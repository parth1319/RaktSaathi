package com.parth.raktsaathi.Fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.*;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;

import java.util.*;

import cz.msebera.android.httpclient.Header;

public class DonateFragment extends Fragment {

    EditText etName, etMobile;
    Spinner spAge, spBlood, spState, spCity;
    TextView tvAddress;
    Button btnSubmit;

    HashMap<String, String[]> cityMap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        // 🔥 Bind
        etName = view.findViewById(R.id.etName);
        etMobile = view.findViewById(R.id.etMobile);
        spAge = view.findViewById(R.id.spAge);
        spBlood = view.findViewById(R.id.spBlood);
        spState = view.findViewById(R.id.spState);
        spCity = view.findViewById(R.id.spCity);
        tvAddress = view.findViewById(R.id.tvAddress);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        // 🔥 BLOOD GROUP
        String[] blood = {"Select Blood","A+","B+","O+","AB+","A-","B-","O-","AB-"};
        spBlood.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, blood));

        // 🔥 AGE
        ArrayList<String> ages = new ArrayList<>();
        ages.add("Select Age");
        for(int i=18;i<=60;i++) ages.add(String.valueOf(i));

        spAge.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, ages));

        // 🔥 STATE
        String[] states = {"Select State","Maharashtra","UP","MP","Delhi","Gujarat","Karnataka"};
        spState.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, states));

        // 🔥 CITY MAP
        cityMap = new HashMap<>();
        cityMap.put("Maharashtra", new String[]{"Pune","Mumbai","Nagpur","Akola"});
        cityMap.put("UP", new String[]{"Lucknow","Ayodhya","Kanpur"});
        cityMap.put("MP", new String[]{"Bhopal","Indore"});
        cityMap.put("Delhi", new String[]{"New Delhi"});
        cityMap.put("Gujarat", new String[]{"Ahmedabad","Surat"});
        cityMap.put("Karnataka", new String[]{"Bangalore","Mysore"});

        // 🔥 STATE → CITY
        spState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {

                String state = spState.getSelectedItem().toString();

                if(cityMap.containsKey(state)){
                    spCity.setAdapter(new ArrayAdapter<>(requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            cityMap.get(state)));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 🔥 ADDRESS AUTO
        spCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view12, int position, long id) {
                String address = spCity.getSelectedItem()+" , "+spState.getSelectedItem();
                tvAddress.setText(address);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 🔥 SUBMIT
        btnSubmit.setOnClickListener(v -> {

            String name = etName.getText().toString();
            String mobile = etMobile.getText().toString();

            if(TextUtils.isEmpty(name)){
                etName.setError("Enter Name");
                return;
            }

            if(TextUtils.isEmpty(mobile)){
                etMobile.setError("Enter Mobile");
                return;
            }

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("name", name);
            params.put("age", spAge.getSelectedItem().toString());
            params.put("blood", spBlood.getSelectedItem().toString());
            params.put("mobile", mobile);
            params.put("city", spCity.getSelectedItem().toString());
            params.put("state", spState.getSelectedItem().toString());

            client.post(Urls.DONATE_BLOOD, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getContext(),"Donation Saved Successfully",Toast.LENGTH_LONG).show();

                    // 🔥 CLEAR FORM
                    etName.setText("");
                    etMobile.setText("");
                    spAge.setSelection(0);
                    spBlood.setSelection(0);
                    spState.setSelection(0);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getContext(),"Error saving data",Toast.LENGTH_SHORT).show();
                }
            });

        });

        return view;
    }
}