package com.parth.raktsaathi.DRB_Fragments;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.parth.raktsaathi.R;
import java.util.HashMap;
import java.util.Map;

public class RequestBloodFragment extends Fragment {

    EditText patientName, hospitalName, city, hospitalAddress, contactPerson, contactNumber;
    Spinner bloodGroupRequiredSpinner, unitSpinner;
    Button submitBtn;

    public RequestBloodFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_request_blood, container, false);

        patientName = view.findViewById(R.id.etrequestbloodpatientname);
        hospitalName = view.findViewById(R.id.etrequestbloodhospitalname);
        city = view.findViewById(R.id.etrequestbloodcity);
        hospitalAddress = view.findViewById(R.id.etrequestbloodhospitaladdress);
        contactPerson = view.findViewById(R.id.etrequestbloodcpname);
        contactNumber = view.findViewById(R.id.etrequestbloodcnumber);

        bloodGroupRequiredSpinner = view.findViewById(R.id.spinnerrequestbloodgroup);
        unitSpinner = view.findViewById(R.id.spinnerrequestbloodunit);

        submitBtn = view.findViewById(R.id.donatebloodBtn);


        String[] bloodGroups = {
                "Select Blood Group",
                "A+","A-","B+","B-","AB+","AB-","O+","O-"
        };

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                bloodGroups
        );

        bloodGroupRequiredSpinner.setAdapter(bloodAdapter);

        // Units Spinner
        String[] units = {
                "Select Units",
                "1 Unit","2 Units","3 Units","4 Units","5 Units"
        };

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                units
        );

        unitSpinner.setAdapter(unitAdapter);

        submitBtn.setOnClickListener(v -> {

            String pName = patientName.getText().toString().trim();
            String hospital = hospitalName.getText().toString().trim();
            String cityName = city.getText().toString().trim();
            String address = hospitalAddress.getText().toString().trim();
            String cpName = contactPerson.getText().toString().trim();
            String cNumber = contactNumber.getText().toString().trim();

            String bloodGroup = bloodGroupRequiredSpinner.getSelectedItem().toString();
            String unit = unitSpinner.getSelectedItem().toString();

            // Validation
            if (pName.isEmpty() ||
                    hospital.isEmpty() ||
                    cityName.isEmpty() ||
                    address.isEmpty() ||
                    cpName.isEmpty() ||
                    cNumber.isEmpty() ||
                    bloodGroup.equals("Select Blood Group") ||
                    unit.equals("Select Units")) {

                Toast.makeText(getActivity(),
                        "Please fill all fields",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // API URL
            String url = "http://10.0.2.2/RaktsaathiAPI/requestblood.php";

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                    },
                    error -> {
                        Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_LONG).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("patient_name", pName);
                    params.put("blood_group_required", bloodGroup);
                    params.put("units", unit);
                    params.put("hospital_name", hospital);
                    params.put("city", cityName);
                    params.put("hospital_address", address);
                    params.put("contact_person", cpName);
                    params.put("contact_number", cNumber);
                    return params;
                }
            };

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(request);

        });

        return view;
    }
}
