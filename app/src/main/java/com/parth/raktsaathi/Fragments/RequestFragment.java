package com.parth.raktsaathi.Fragments;

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
import com.parth.raktsaathi.DonorAdapter;
import com.parth.raktsaathi.DonorModel;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class RequestFragment extends Fragment {

    private EditText etName, etPhone, etAddress;
    private Spinner spUnits, spBlood, spArea;
    private Button btnSubmit;
    private LinearLayout formLayout, successMsg;
    private RecyclerView rvDonors;
    private DonorAdapter adapter;
    private List<DonorModel> donorList;

    public RequestFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_requests, container, false);

        // Initialize Views
        etName = v.findViewById(R.id.etName);
        etPhone = v.findViewById(R.id.etPhone);
        etAddress = v.findViewById(R.id.etAddress);
        spUnits = v.findViewById(R.id.spUnits);
        spBlood = v.findViewById(R.id.spBlood);
        spArea = v.findViewById(R.id.spArea);
        btnSubmit = v.findViewById(R.id.btnSubmit);
        formLayout = v.findViewById(R.id.formLayout);
        successMsg = v.findViewById(R.id.successMsg);
        rvDonors = v.findViewById(R.id.recyclerDonors);

        setupSpinners();
        loadPotentialDonors();

        btnSubmit.setOnClickListener(view -> submitRequest());

        return v;
    }

    private void setupSpinners() {
        String[] units = {"1 Unit", "2 Units", "3 Units", "4 Units", "5 Units", "6+ Units"};
        spUnits.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, units));

        String[] bloods = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        spBlood.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, bloods));

        String[] areas = {"Mumbai", "Pune", "Nagpur", "Nashik", "Aurangabad", "Solapur", "Amravati"};
        spArea.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, areas));
    }

    private void loadPotentialDonors() {
        donorList = new ArrayList<>();
        rvDonors.setLayoutManager(new LinearLayoutManager(getContext()));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Urls.GET_DONORS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject responseObj = new JSONObject(new String(responseBody));
                    if (responseObj.getString("status").equals("success")) {
                        JSONArray array = responseObj.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            donorList.add(new DonorModel(
                                    obj.getString("name"),
                                    obj.getString("mobile"),
                                    obj.getString("blood_group"),
                                    obj.getString("district"),
                                    obj.getString("city"),
                                    obj.getString("address")
                            ));
                        }
                        adapter = new DonorAdapter(donorList);
                        rvDonors.setAdapter(adapter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void submitRequest() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String blood = spBlood.getSelectedItem().toString();
        String units = spUnits.getSelectedItem().toString();
        String area = spArea.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show Progress
        android.app.ProgressDialog pd = new android.app.ProgressDialog(getContext());
        pd.setMessage("Posting Request...");
        pd.setCancelable(false);
        pd.show();

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("mobile", phone);
        params.put("blood", blood);
        params.put("units", units);
        params.put("district", area);
        params.put("city", area);
        params.put("address", address);

        client.post(Urls.REQUEST_BLOOD, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                pd.dismiss();
                try {
                    String response = new String(responseBody).trim();
                    if (response.equalsIgnoreCase("success") || response.contains("success")) {
                        formLayout.setVisibility(View.GONE);
                        successMsg.setVisibility(View.VISIBLE);
                        loadPotentialDonors();
                    } else {
                        Toast.makeText(getContext(), "Error: " + response, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Response error", Toast.LENGTH_SHORT).show();
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
