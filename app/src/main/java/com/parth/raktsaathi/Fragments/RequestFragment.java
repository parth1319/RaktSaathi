package com.parth.raktsaathi.Fragments;

import android.os.Bundle;
import android.text.*;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import com.loopj.android.http.*;
import com.parth.raktsaathi.*;
import com.parth.raktsaathi.R;

import org.json.*;

import java.util.*;

import cz.msebera.android.httpclient.Header;

public class RequestFragment extends Fragment {

    EditText etName, etMobile, etAddress, etUnits;
    Spinner spBlood, spArea;
    TextView tvFinalAddress;
    Button btnRequest;

    LinearLayout formLayout;
    androidx.cardview.widget.CardView successCard;

    RecyclerView recycler;
    List<DonorModel> list = new ArrayList<>();
    DonorAdapter adapter;

    String selectedArea = "";
    String selectedBlood = ""; // 🔥 FILTER

    public RequestFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_requests, container, false);

        // 🔥 IDS
        etName = view.findViewById(R.id.etName);
        etMobile = view.findViewById(R.id.etMobile);
        etAddress = view.findViewById(R.id.etAddress);
        etUnits = view.findViewById(R.id.etUnits);

        spBlood = view.findViewById(R.id.spBlood);
        spArea = view.findViewById(R.id.spArea);

        tvFinalAddress = view.findViewById(R.id.tvFinalAddress);
        btnRequest = view.findViewById(R.id.btnRequest);

        formLayout = view.findViewById(R.id.formLayout);
        successCard = view.findViewById(R.id.successCard);

        recycler = view.findViewById(R.id.recyclerDonors);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new DonorAdapter(list);
        recycler.setAdapter(adapter);

        // 🔥 BLOOD GROUP
        String[] blood = {
                "Select Blood Group",
                "A+","A-","B+","B-","O+","O-","AB+","AB-"
        };

        spBlood.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, blood));

        // 🔥 AREA
        String[] area = {
                "Select Area",
                "Akola City",
                "Akot",
                "Balapur",
                "Murtizapur",
                "Patur",
                "Barshitakli",
                "Telhara"
        };

        spArea.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, area));

        // 🔥 AREA SELECT
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view1, int position, long id) {
                selectedArea = area[position];
                updateAddress();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // 🔥 ADDRESS TYPE
        etAddress.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                updateAddress();
            }
        });

        // 🔥 BUTTON CLICK
        btnRequest.setOnClickListener(v -> submitRequest());

        // 🔥 LOAD DONORS
        loadDonors();

        return view;
    }

    // 🔥 ADDRESS COMBINE
    private void updateAddress() {

        String addr = etAddress.getText().toString();

        if(!addr.isEmpty() && !selectedArea.equals("Select Area")){
            tvFinalAddress.setText(addr + ", " + selectedArea + ", Akola");
        }
    }

    // 🔥 SUBMIT REQUEST
    private void submitRequest() {

        String name = etName.getText().toString();
        String mobile = etMobile.getText().toString();
        String blood = spBlood.getSelectedItem().toString();
        String address = etAddress.getText().toString();
        String units = etUnits.getText().toString();


        if(name.isEmpty() || mobile.isEmpty() || units.isEmpty()
                || blood.equals("Select Blood Group")
                || selectedArea.equals("Select Area")
                || address.isEmpty()){

            Toast.makeText(getContext(),"Fill all fields",Toast.LENGTH_SHORT).show();
            return;
        }
        selectedBlood = blood;


        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("name", name);
        params.put("mobile", mobile);
        params.put("blood", blood);
        params.put("district", selectedArea);
        params.put("city", "Akola");
        params.put("address", address);
        params.put("units", units);

        client.post(Urls.REQUEST_BLOOD, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                if(res.trim().equalsIgnoreCase("success")){

                    // 🔥 hide form
                    formLayout.setVisibility(View.GONE);

                    successCard.setVisibility(View.VISIBLE);

                    successCard.setAlpha(0f);
                    successCard.animate().alpha(1f).setDuration(600);

                    loadDonors();

                } else {
                    Toast.makeText(getContext(),"Error ❌",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDonors(){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Urls.GET_DONORS, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try{
                    String res = new String(responseBody);

                    JSONObject obj = new JSONObject(res);
                    JSONArray arr = obj.getJSONArray("data");

                    list.clear();

                    List<DonorModel> tempList = new ArrayList<>();

                    for(int i=0;i<arr.length();i++){

                        JSONObject o = arr.getJSONObject(i);

                        String donorBlood = o.getString("blood_group");

                        Log.d("JSON_DATA", o.toString());

                        DonorModel model = new DonorModel(
                                o.getString("name"),
                                o.getString("mobile"),
                                donorBlood,
                                o.getString("district"),
                                o.getString("city"),
                                o.getString("address")
                        );

                        // 🔥 matching donors
                        if(donorBlood.equals(selectedBlood)){
                            list.add(model);
                        }

                        // 🔥 store all donors
                        tempList.add(model);
                    }

                    if(list.size() == 0){
                        list.addAll(tempList);
                    }

                    adapter.notifyDataSetChanged();
                }catch(Exception e){
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