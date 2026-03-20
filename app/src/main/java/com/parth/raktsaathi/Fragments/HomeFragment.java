package com.parth.raktsaathi.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parth.raktsaathi.DRB_Fragments.DonateBloodFragment;
import com.parth.raktsaathi.DRB_Fragments.RequestBloodFragment;
import com.parth.raktsaathi.Donars.DonorAdapter;
import com.parth.raktsaathi.Donars.DonorModel;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    TextView tvUserName, txtCity;
    Button btndonatebloodbtn, btnrequestbloodbtn;
    SharedPreferences preferences;

    // RecyclerView variables
    RecyclerView recyclerView;
    ArrayList<DonorModel> list;
    DonorAdapter adapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvUserName = view.findViewById(R.id.tvHomeUserName);
        txtCity = view.findViewById(R.id.txtCity);

        btndonatebloodbtn = view.findViewById(R.id.btndonatebloodbtn);
        btnrequestbloodbtn = view.findViewById(R.id.btnrequestbloodbtn);

        // RecyclerView connect
        recyclerView = view.findViewById(R.id.rvHomeDonorList); // Ensure this ID exists in fragment_home.xml
        list = new ArrayList<>();
        adapter = new DonorAdapter(getContext(), list);

        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String username = preferences.getString("userName", "");
        Log.d(TAG, "Username from preferences: " + username);

        if (username.isEmpty()) {
            tvUserName.setText("Hi, User");
        } else {
            tvUserName.setText("Hi, " + username);
        }

        if (!username.isEmpty()) {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("username", username);

            // City API
            client.post(Urls.GetCityWebService, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d(TAG, "City API Response: " + response.toString());
                    try {
                        if (response.has("city")) {
                            String city = response.getString("city");
                            txtCity.setText(city + ", Maharashtra");
                        } else {
                            txtCity.setText("City not found");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing city JSON", e);
                        txtCity.setText("Error loading city");
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.e(TAG, "City API Failure. Status: " + statusCode, throwable);
                    if (errorResponse != null) {
                        Log.e(TAG, "Error Response: " + errorResponse.toString());
                    }
                    txtCity.setText("Server Error");
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(TAG, "City API Failure (String). Status: " + statusCode + " Response: " + responseString, throwable);
                    txtCity.setText("Server Error");
                }
            });
        } else {
            txtCity.setText("Please Login");
        }

        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Donor List API
        String donorUrl = Urls.GetDonorsWebService; // Using central Urls class

        StringRequest donorRequest = new StringRequest(Request.Method.GET, donorUrl,
                this::onResponse,
                error -> {
                    Log.e(TAG, "Donor List API Error", error);
                    error.printStackTrace();
                });

        queue.add(donorRequest);

        btndonatebloodbtn.setOnClickListener(v -> {
            Fragment fragment = new DonateBloodFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.homeFrameLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnrequestbloodbtn.setOnClickListener(v -> {
            Fragment fragment = new RequestBloodFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.homeFrameLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void onResponse(String response) {
        Log.d(TAG, "Donor List Response: " + response);
        try {

            JSONArray array = new JSONArray(response);

            list.clear();
            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);

                String name = obj.getString("username");
                String mobile = obj.getString("mobileno");
                String email = obj.getString("emailid");
                String blood = obj.getString("blood_group");
                String address = obj.getString("address");
                String city = obj.getString("city");

                list.add(new DonorModel(name, mobile, email, blood, address, city));
            }

            adapter.notifyDataSetChanged();

        } catch (Exception e) {
            Log.e(TAG, "Error parsing donor list", e);
            e.printStackTrace();
        }

    }
}
