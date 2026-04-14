package com.parth.raktsaathi.Fragments;

import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.loopj.android.http.*;
import com.parth.raktsaathi.*;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.SliderAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    TextView tvGreeting, tvUserName, tvActiveDonorsCount, tvActiveRequestsCount, btnViewAllCamps;
    LinearLayout upcomingContainer, bloodInventoryContainer;
    ViewPager2 slider;
    EditText etSearchDonors;

    String email = "";
    Handler handler = new Handler();
    JSONArray campsArray = new JSONArray();
    boolean isAllCampsVisible = false;

    public HomeFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sp.getString("email", "");

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvActiveDonorsCount = view.findViewById(R.id.tvActiveDonorsCount);
        tvActiveRequestsCount = view.findViewById(R.id.tvActiveRequestsCount);
        btnViewAllCamps = view.findViewById(R.id.btnViewAllCamps);
        upcomingContainer = view.findViewById(R.id.upcomingContainer);
        bloodInventoryContainer = view.findViewById(R.id.bloodInventoryContainer);
        slider = view.findViewById(R.id.imageSlider);
        etSearchDonors = view.findViewById(R.id.etSearchDonors);

        ImageView btnNotification = view.findViewById(R.id.btnNotification);
        Button btnCamp = view.findViewById(R.id.btnRegisterCamps);
        CardView btnHealth = view.findViewById(R.id.btnHealthTips);

        int[] images = {R.drawable.rs_homefragment_slider1, R.drawable.rs_homefragment_slider2, R.drawable.rs_homefragment_slider3};
        slider.setAdapter(new SliderAdapter(images));
        autoSlide(images.length);

        btnHealth.setOnClickListener(v -> startActivity(new Intent(getActivity(), HealthTipsActivity.class)));
        btnNotification.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationActivity.class)));
        btnCamp.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddCampActivity.class)));
        
        btnViewAllCamps.setOnClickListener(v -> {
            isAllCampsVisible = !isAllCampsVisible;
            btnViewAllCamps.setText(isAllCampsVisible ? "Show Less" : "View All");
            displayCamps();
        });

        etSearchDonors.setOnEditorActionListener((v, actionId, event) -> {
            String city = etSearchDonors.getText().toString().trim();
            if(!city.isEmpty()){
                Intent intent = new Intent(getActivity(), DonorActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
            }
            return true;
        });

        loadUser();
        loadBloodInventory();
        loadStats();
        loadUpcomingCamps();

        return view;
    }

    private void loadUser(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);
        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    tvUserName.setText("Hello, " + obj.getString("name") + "!");
                } catch (Exception e){ tvUserName.setText("Hello, User!"); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void loadStats(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Urls.GET_STATS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    tvActiveDonorsCount.setText(obj.getString("donors"));
                    tvActiveRequestsCount.setText(obj.getString("requests"));
                    
                    // Update blood group inventory real-time counts
                    if (obj.has("blood_counts")) {
                        updateBloodInventory(obj.getJSONObject("blood_counts"));
                    }
                } catch (Exception e){}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void loadBloodInventory() {
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        bloodInventoryContainer.removeAllViews();
        for (String bg : bloodGroups) {
            View itemView = getLayoutInflater().inflate(R.layout.item_blood_stat, bloodInventoryContainer, false);
            itemView.setTag(bg); // Tag to find this view later
            ((TextView)itemView.findViewById(R.id.tvBloodGroup)).setText(bg);
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), DonorActivity.class);
                intent.putExtra("blood_group", bg);
                startActivity(intent);
            });
            bloodInventoryContainer.addView(itemView);
        }
    }

    private void updateBloodInventory(JSONObject counts) {
        for (int i = 0; i < bloodInventoryContainer.getChildCount(); i++) {
            View itemView = bloodInventoryContainer.getChildAt(i);
            String bg = (String) itemView.getTag();
            TextView tvCount = itemView.findViewById(R.id.tvCount);
            if (counts.has(bg)) {
                try {
                    tvCount.setText(counts.getString(bg));
                } catch (Exception e) {
                    tvCount.setText("0");
                }
            }
        }
    }

    private void loadUpcomingCamps(){
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Urls.GET_CAMPS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    campsArray = new JSONArray(new String(responseBody));
                    displayCamps();
                } catch (Exception e){}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void displayCamps() {
        upcomingContainer.removeAllViews();
        try {
            List<JSONObject> sortedCamps = new ArrayList<>();
            for (int i = 0; i < campsArray.length(); i++) {
                sortedCamps.add(campsArray.getJSONObject(i));
            }
            
            Collections.sort(sortedCamps, (o1, o2) -> {
                try {
                    return o1.getString("camp_date").compareTo(o2.getString("camp_date"));
                } catch (Exception e) {
                    return 0;
                }
            });

            int limit = isAllCampsVisible ? sortedCamps.size() : Math.min(sortedCamps.size(), 2);
            for(int i=0; i < limit; i++){
                JSONObject obj = sortedCamps.get(i);
                View card = getLayoutInflater().inflate(R.layout.item_camps, upcomingContainer, false);
                ((TextView)card.findViewById(R.id.tvCampName)).setText(obj.getString("camp_name"));
                ((TextView)card.findViewById(R.id.tvCampDate)).setText("📅 " + obj.getString("camp_date"));
                ((TextView)card.findViewById(R.id.tvCampLocation)).setText("📍 " + obj.getString("location"));
                upcomingContainer.addView(card);
            }
        } catch (Exception e) {}
    }

    private void autoSlide(int size){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (slider != null && slider.getAdapter() != null) {
                    int current = slider.getCurrentItem();
                    slider.setCurrentItem((current + 1) % size);
                    handler.postDelayed(this, 3000);
                }
            }
        }, 3000);
    }
}