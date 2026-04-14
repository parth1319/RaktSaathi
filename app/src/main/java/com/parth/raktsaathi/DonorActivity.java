package com.parth.raktsaathi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class DonorActivity extends AppCompatActivity {

    private RecyclerView rvDonors;
    private EditText etSearch;
    private ProgressBar progressBar;
    private ImageView btnBack;
    private TextView tvTitle;
    private View layoutEmpty;
    
    private DonorAdapter adapter;
    private List<DonorModel> donorList = new ArrayList<>();
    private String selectedBloodGroup = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donor);

        rvDonors = findViewById(R.id.rvDonors);
        etSearch = findViewById(R.id.etSearch);
        progressBar = findViewById(R.id.progressBar);
        btnBack = findViewById(R.id.btnBack);
        tvTitle = findViewById(R.id.tvTitle);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        btnBack.setOnClickListener(v -> finish());

        selectedBloodGroup = getIntent().getStringExtra("blood_group");
        String cityQuery = getIntent().getStringExtra("city");
        boolean searchMode = getIntent().getBooleanExtra("search_mode", false);

        if (selectedBloodGroup != null && !selectedBloodGroup.isEmpty()) {
            tvTitle.setText(selectedBloodGroup + " Donors");
        } else if (cityQuery != null && !cityQuery.isEmpty()) {
            tvTitle.setText("Donors in " + cityQuery);
            etSearch.setText(cityQuery);
        } else if (searchMode) {
            tvTitle.setText("Search Donors");
            etSearch.requestFocus();
        }

        rvDonors.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DonorAdapter(donorList);
        rvDonors.setAdapter(adapter);

        loadDonors(etSearch.getText().toString().trim());

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadDonors(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadDonors(String query) {
        progressBar.setVisibility(View.VISIBLE);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        String url;

        if (query.isEmpty()) {
            // Load all or by blood group using existing fetch_donors.php
            url = Urls.GET_DONORS;
            if (selectedBloodGroup != null && !selectedBloodGroup.isEmpty()) {
                params.put("blood_group", selectedBloodGroup);
            }
        } else {
            // Use NEW search_donors.php for filtering
            url = Urls.SEARCH_DONORS;
            params.put("search_query", query);
            if (selectedBloodGroup != null && !selectedBloodGroup.isEmpty()) {
                params.put("blood_group", selectedBloodGroup);
            }
        }

        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progressBar.setVisibility(View.GONE);
                try {
                    donorList.clear();
                    JSONArray arr = new JSONArray(new String(responseBody));
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        donorList.add(new DonorModel(
                                obj.getString("name"),
                                obj.getString("mobile"),
                                obj.getString("blood_group"),
                                obj.getString("district"),
                                obj.getString("city"),
                                obj.getString("address")
                        ));
                    }
                    adapter.notifyDataSetChanged();
                    
                    if (donorList.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvDonors.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rvDonors.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(DonorActivity.this, "Failed to load donors", Toast.LENGTH_SHORT).show();
            }
        });
    }
}