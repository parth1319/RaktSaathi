package com.parth.raktsaathi.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.*;
import android.widget.*;

import com.loopj.android.http.*;
import com.parth.raktsaathi.DonorAdapter;
import com.parth.raktsaathi.DonorModel;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Find_DonorFragment extends Fragment {

    Spinner spBlood;
    EditText etLocation;
    Button btnSearch;
    RecyclerView recyclerView;

    ArrayList<DonorModel> list;
    DonorAdapter adapter;

    public Find_DonorFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_find_donor, container, false);

        spBlood = view.findViewById(R.id.sp_blood);
        etLocation = view.findViewById(R.id.et_location);
        btnSearch = view.findViewById(R.id.btn_search);
        recyclerView = view.findViewById(R.id.recycler_donors);

        // Spinner setup
        String[] bloodGroups = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};
        ArrayAdapter<String> adapterSp = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, bloodGroups);
        spBlood.setAdapter(adapterSp);

        // RecyclerView setup
        list = new ArrayList<>();
        adapter = new DonorAdapter(list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        
        btnSearch.setOnClickListener(v -> fetchDonors());

        return view;
    }

    private void fetchDonors() {

        String blood = spBlood.getSelectedItem().toString();
        String location = etLocation.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();

        String url = Urls.Find_DonorFragmentWebServiceAddress + "?blood_group="
                + blood + "&location=" + location;

        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {

                list.clear();

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        String name = obj.getString("name");
                        String blood = obj.getString("blood_group");
                        String location = obj.getString("location");

                        list.add(new DonorModel(name, blood, location));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
