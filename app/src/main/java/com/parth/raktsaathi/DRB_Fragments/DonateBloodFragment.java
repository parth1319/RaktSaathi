package com.parth.raktsaathi.DRB_Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parth.raktsaathi.R;

import java.util.HashMap;
import java.util.Map;

public class DonateBloodFragment extends Fragment {

    EditText name, phone, age, address, city, weight, disease;
    RadioGroup genderGroup;
    CheckBox ready;
    Button registerBtn;
    Spinner spinnerbloodGroupSpinner;


    public DonateBloodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_donate_blood, container, false);

        // Connect XML IDs
        name = view.findViewById(R.id.etdonatebloodname);
        phone = view.findViewById(R.id.etdonatebloodphone);
        age = view.findViewById(R.id.etdonatebloodage);
        address = view.findViewById(R.id.etdonatebloodaddress);
        city = view.findViewById(R.id.etdonatebloodcity);
        weight = view.findViewById(R.id.etdonatebloodweight);
        disease = view.findViewById(R.id.etdonateblooddisease);

        genderGroup = view.findViewById(R.id.rgdonatebloodselectgender);

        ready = view.findViewById(R.id.cbdonatebloodready);

        registerBtn = view.findViewById(R.id.donatebloodBtn);
        spinnerbloodGroupSpinner = view.findViewById(R.id.spinnerbloodGroupSpinner);

        String[] bloodGroups = {"Select Blood Group",
                "A+",
                "A-",
                "B+",
                "B-",
                "AB+",
                "AB-",
                "O+",
                "O-"};

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                bloodGroups);

        spinnerbloodGroupSpinner.setAdapter(bloodAdapter);

        // Button Click
        registerBtn.setOnClickListener(v -> {

            String donorName = name.getText().toString().trim();
            String donorPhone = phone.getText().toString().trim();
            String donorAge = age.getText().toString().trim();
            String donorBlood = spinnerbloodGroupSpinner.getSelectedItem().toString();
            String donorAddress = address.getText().toString().trim();
            String donorCity = city.getText().toString().trim();
            String donorWeight = weight.getText().toString().trim();
            String donorDisease = disease.getText().toString().trim();

            int selectedId = genderGroup.getCheckedRadioButtonId();

            String gender;

            if (selectedId != -1) {
                RadioButton selectedGender = view.findViewById(selectedId);
                gender = selectedGender.getText().toString();
            } else {
                gender = "";
            }

            // Validation
            if (donorName.isEmpty() ||
                    donorPhone.isEmpty() ||
                    donorAge.isEmpty() ||
                    donorBlood.equals("Select Blood Group") ||
                    donorAddress.isEmpty() ||
                    donorCity.isEmpty()) {

                Toast.makeText(getActivity(),
                        "Please fill all required fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            if (!ready.isChecked()) {

                Toast.makeText(getActivity(),
                        "Please confirm you are ready to donate blood",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // API URL
            String url = "http://10.249.66.98/RaktsaathiAPI/donateblood.php";


                StringRequest request = new

                        StringRequest (Request.Method.POST, url,


                                response -> {

                        Toast.makeText(getActivity(),"Registration Successfully Done",
                                Toast.LENGTH_LONG).show();

                    },

                    error -> {

                        Toast.makeText(getActivity(),"Registration Successuflly Done",

                                Toast.LENGTH_LONG).show();

                    }) {

                @Override
                protected Map<String, String> getParams() {

                    Map<String, String> params = new HashMap<>();

                    params.put("name", donorName);
                    params.put("phone", donorPhone);
                    params.put("age", donorAge);
                    params.put("blood_group", donorBlood);
                    params.put("gender", gender);
                    params.put("address", donorAddress);
                    params.put("city", donorCity);
                    params.put("weight", donorWeight);
                    params.put("disease", donorDisease);

                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(request);

        });

        return view;
    }
}