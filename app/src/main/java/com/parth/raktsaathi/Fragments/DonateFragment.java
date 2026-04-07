package com.parth.raktsaathi.Fragments;

import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.loopj.android.http.*;
import com.parth.raktsaathi.*;
import com.parth.raktsaathi.R;

import org.json.*;

import java.util.*;

import cz.msebera.android.httpclient.Header;

public class DonateFragment extends Fragment {

    Spinner spAge, spBlood, spDistrict;
    EditText etName, etMobile, etAddress;
    TextView tvFinalAddress, successMsg;
    Button btnDonate;
    RecyclerView recyclerRequests;

    String selectedDistrict = "";

    List<DonorModel> list = new ArrayList<>();
    DonorAdapter adapter;

    public DonateFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        // 🔥 IDS
        spAge = view.findViewById(R.id.spAge);
        spBlood = view.findViewById(R.id.spBlood);
        spDistrict = view.findViewById(R.id.spArea);

        etName = view.findViewById(R.id.etName);
        etMobile = view.findViewById(R.id.etPhone);
        etAddress = view.findViewById(R.id.etAddress);

        tvFinalAddress = view.findViewById(R.id.tvFinalAddress);
        btnDonate = view.findViewById(R.id.btnDonate);
        successMsg = view.findViewById(R.id.successMsg);

        recyclerRequests = view.findViewById(R.id.recyclerRequests);
        recyclerRequests.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DonorAdapter(list);
        recyclerRequests.setAdapter(adapter);

        // 🔥 AGE
        ArrayList<String> ageList = new ArrayList<>();
        ageList.add("Select Age");
        for(int i=19;i<=55;i++) ageList.add(String.valueOf(i));

        spAge.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, ageList));

        // 🔥 BLOOD
        String[] blood = {"Select Blood","A+","A-","B+","B-","O+","O-","AB+","AB-"};
        spBlood.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, blood));

        // 🔥 AREA (Akola)
        String[] Area = {"Select Area","Akola City","Akot","Balapur","Murtizapur","Patur","Barshitakli","Telhara"};

        spDistrict.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, Area));

        spDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                selectedDistrict = Area[position];
                updateAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etAddress.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateAddress();
            }
        });

        btnDonate.setOnClickListener(v -> submitData());

        loadDonors();

        return view;
    }

    private void updateAddress() {
        String addr = etAddress.getText().toString();

        if(!addr.isEmpty() && !selectedDistrict.equals("Select Area")){
            tvFinalAddress.setText(addr + ", " + selectedDistrict + ", Akola");
        }
    }

    private void submitData() {

        String name = etName.getText().toString();
        String mobile = etMobile.getText().toString();
        String age = spAge.getSelectedItem().toString();
        String bloodGroup = spBlood.getSelectedItem().toString();
        String address = etAddress.getText().toString();

        if(name.isEmpty() || mobile.isEmpty() || age.equals("Select Age")
                || bloodGroup.equals("Select Blood") || selectedDistrict.equals("Select Area")
                || address.isEmpty()){

            Toast.makeText(getContext(), "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("name", name);
        params.put("mobile", mobile);
        params.put("age", age);
        params.put("blood", bloodGroup);
        params.put("district", selectedDistrict);
        params.put("address", address);
        params.put("city", "Akola"); // 🔥 FIX

        client.post(Urls.DONATE_BLOOD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                if(res.contains("success")){
                    successMsg.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Donor Added ✅", Toast.LENGTH_SHORT).show();

                    list.clear();
                    loadDonors();

                } else {
                    Toast.makeText(getContext(), "Error ❌", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDonors() {

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Urls.GET_DONORS, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String res = new String(responseBody);

                    JSONObject obj = new JSONObject(res);
                    JSONArray arr = obj.getJSONArray("data");

                    for(int i=0;i<arr.length();i++){

                        JSONObject o = arr.getJSONObject(i);

                        list.add(new DonorModel(
                                o.getString("name"),
                                o.getString("mobile"),
                                o.getString("blood_group"),
                                o.getString("district"),
                                o.getString("city"),
                                o.getString("address")
                        ));
                    }

                    adapter.notifyDataSetChanged();

                } catch (Exception e){
                    Toast.makeText(getContext(),"JSON Error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT).show();
            }
        });
    }
}