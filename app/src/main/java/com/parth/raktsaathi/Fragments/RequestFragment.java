package com.parth.raktsaathi.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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

    private EditText etName, etPhone;
    private AutoCompleteTextView etHospital;
    private Spinner spUnits, spBlood, spArea;
    private Button btnSubmit;
    private LinearLayout formLayout, successMsg;
    private RecyclerView rvDonors;
    private DonorAdapter adapter;
    private List<DonorModel> donorList;
    private String userEmail = "";

    public RequestFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_requests, container, false);

        android.content.SharedPreferences sp = getActivity().getSharedPreferences("user", android.content.Context.MODE_PRIVATE);
        userEmail = sp.getString("email", "");

        // Initialize Views
        etName = v.findViewById(R.id.etName);
        etPhone = v.findViewById(R.id.etPhone);
        etHospital = v.findViewById(R.id.etHospital);
        spUnits = v.findViewById(R.id.spUnits);
        spBlood = v.findViewById(R.id.spBlood);
        spArea = v.findViewById(R.id.spArea);
        btnSubmit = v.findViewById(R.id.btnSubmit);
        formLayout = v.findViewById(R.id.formLayout);
        successMsg = v.findViewById(R.id.successMsg);
        rvDonors = v.findViewById(R.id.recyclerDonors);

        v.findViewById(R.id.btnBack).setOnClickListener(view -> {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        setupSpinners();
        loadPotentialDonors();

        btnSubmit.setOnClickListener(view -> submitRequest());

        return v;
    }

    private void setupSpinners() {
        // Units Spinner
        List<String> unitList = new ArrayList<>();
        unitList.add("Select Units");
        unitList.add("1 Unit"); unitList.add("2 Units"); unitList.add("3 Units");
        unitList.add("4 Units"); unitList.add("5 Units"); unitList.add("6+ Units");

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, unitList) {
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
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnits.setAdapter(unitAdapter);

        // Blood Group Spinner
        List<String> bloodList = new ArrayList<>();
        bloodList.add("Blood Group");
        bloodList.add("A+"); bloodList.add("A-"); bloodList.add("B+"); bloodList.add("B-");
        bloodList.add("O+"); bloodList.add("O-"); bloodList.add("AB+"); bloodList.add("AB-");

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, bloodList) {
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

        // Area Spinner (Akola & Amravati Region - Extended)
        List<String> areas = new ArrayList<>();
        areas.add("Select Area/City");
        areas.add("Akola - Gorakshan Road");
        areas.add("Akola - Jatar Peth");
        areas.add("Akola - Kaulkhed");
        areas.add("Akola - Civil Lines");
        areas.add("Akola - Old City");
        areas.add("Akola - Ramdaspeth");
        areas.add("Amravati - Rajapeth");
        areas.add("Amravati - Badnera");
        areas.add("Amravati - Sai Nagar");
        areas.add("Amravati - Gadge Nagar");
        areas.add("Amravati - Rukhmini Nagar");
        areas.add("Akot");
        areas.add("Murtizapur");
        areas.add("Balapur");
        areas.add("Telhara");
        areas.add("Patur");
        areas.add("Barshitakli");
        areas.add("Anjangaon Surji");
        areas.add("Achalpur");
        areas.add("Daryapur");
        areas.add("Warud");
        areas.add("Morshi");
        areas.add("Chandur Bazar");
        areas.add("Dharni (Melghat)");
        
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

        // Dynamic Hospital Logic based on Area
        spArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateHospitalList(areas.get(i));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void updateHospitalList(String area) {
        List<String> hospitals = new ArrayList<>();
        if (area.contains("Akola")) {
            hospitals.add("Civil Hospital, Akola");
            hospitals.add("Government Medical College (GMC)");
            hospitals.add("Icon Hospital");
            hospitals.add("Ozone Hospital");
            hospitals.add("Suryachandra Hospital");
            hospitals.add("District Women Hospital");
            hospitals.add("Khandelwal Hospital");
            hospitals.add("Sarda Hospital");
            hospitals.add("Maitri Hospital");
        } else if (area.contains("Amravati")) {
            hospitals.add("District General Hospital, Amravati");
            hospitals.add("PDMC Hospital");
            hospitals.add("Radiant Hospital");
            hospitals.add("Vytal Hospital");
            hospitals.add("Best Care Hospital");
            hospitals.add("Dayasagar Hospital");
            hospitals.add("Bakatar Hospital");
        } else if (area.equals("Akot")) {
            hospitals.add("Sub-District Hospital, Akot");
            hospitals.add("Shraddha Hospital");
            hospitals.add("Civil Hospital, Akot");
        } else if (area.equals("Telhara")) {
            hospitals.add("Rural Hospital, Telhara");
            hospitals.add("Government Hospital, Telhara");
        } else if (area.equals("Balapur")) {
            hospitals.add("Rural Hospital, Balapur");
            hospitals.add("Municipal Hospital, Balapur");
        } else if (area.equals("Patur")) {
            hospitals.add("Rural Hospital, Patur");
            hospitals.add("Government Clinic, Patur");
        } else if (area.equals("Murtizapur")) {
            hospitals.add("Sub-District Hospital, Murtizapur");
            hospitals.add("Laxmibai Deshmukh Hospital");
            hospitals.add("Civil Hospital, Murtizapur");
        } else if (area.equals("Barshitakli")) {
            hospitals.add("Rural Hospital, Barshitakli");
        } else if (area.equals("Anjangaon Surji")) {
            hospitals.add("Rural Hospital, Anjangaon");
            hospitals.add("Suryawanshi Hospital");
        } else if (area.equals("Achalpur") || area.equals("Chandur Bazar")) {
            hospitals.add("Sub-District Hospital, Achalpur");
            hospitals.add("Finolex Hospital");
        } else if (area.equals("Daryapur")) {
            hospitals.add("Sub-District Hospital, Daryapur");
        } else if (area.equals("Warud") || area.equals("Morshi")) {
            hospitals.add("Rural Hospital, Warud");
            hospitals.add("Rural Hospital, Morshi");
        } else if (area.equals("Dharni (Melghat)")) {
            hospitals.add("Sub-District Hospital, Dharni");
            hospitals.add("Khandwa Road Hospital");
        } else {
            hospitals.add("Government Hospital");
            hospitals.add("Private Clinic");
        }

        ArrayAdapter<String> hospitalAdapter = new ArrayAdapter<>(getContext(), 
                android.R.layout.simple_dropdown_item_1line, hospitals);
        etHospital.setAdapter(hospitalAdapter);
    }

    private void loadPotentialDonors() {
        donorList = new ArrayList<>();
        rvDonors.setLayoutManager(new LinearLayoutManager(getContext()));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Urls.GET_DONORS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONArray array = new JSONArray(response); // Directly parse the JSONArray
                    donorList.clear();
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
        String hospital = etHospital.getText().toString().trim();
        String blood = spBlood.getSelectedItem().toString();
        String units = spUnits.getSelectedItem().toString();
        String area = spArea.getSelectedItem().toString();

        if (name.isEmpty() || phone.isEmpty() || hospital.isEmpty() || spArea.getSelectedItemPosition() == 0) {
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
        params.put("district", "Akola");
        params.put("city", area);
        params.put("address", hospital);

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
