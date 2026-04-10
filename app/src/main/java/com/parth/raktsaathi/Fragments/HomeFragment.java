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

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    TextView tvGreeting, tvUserName, tvDonors, tvRequests;
    LinearLayout upcomingContainer;
    ViewPager2 slider;

    String email = "";
    Handler handler = new Handler();

    public HomeFragment(){}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sp.getString("email", "");

        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvDonors = view.findViewById(R.id.tvDonors);
        tvRequests = view.findViewById(R.id.tvRequests);
        upcomingContainer = view.findViewById(R.id.upcomingContainer);
        slider = view.findViewById(R.id.imageSlider);

        CardView btnDonate = view.findViewById(R.id.btnDonate);
        CardView btnRequest = view.findViewById(R.id.btnRequest);
        CardView btnHealth = view.findViewById(R.id.btnHealthTips);
        ImageView btnNotification = view.findViewById(R.id.btnNotification);
        Button btnCamp = view.findViewById(R.id.btnRegisterCamps);

        // 🔥 SLIDER
        int[] images = {
                R.drawable.rs_homefragment_slider1,
                R.drawable.rs_homefragment_slider2,
                R.drawable.rs_homefragment_slider3
        };

        SliderAdapter adapter = new SliderAdapter(images);
        slider.setAdapter(adapter);
        autoSlide(images.length);

        // 🔥 CLICK EVENTS
        btnDonate.setOnClickListener(v -> loadFragment(new DonateFragment()));
        btnRequest.setOnClickListener(v -> loadFragment(new RequestFragment()));
        btnHealth.setOnClickListener(v -> startActivity(new Intent(getActivity(), HealthTipsActivity.class)));
        btnNotification.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationActivity.class)));
        btnCamp.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddCampActivity.class)));

        // 🔥 LOAD DATA
        loadUser();
        loadStats();
        loadUpcomingCamps();

        return view;
    }

    // 🔥 USER
    private void loadUser(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    String name = obj.getString("name");

                    tvGreeting.setText("Hi,");
                    tvUserName.setText(name);

                } catch (Exception e){
                    tvUserName.setText("User");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tvUserName.setText("User");
            }
        });
    }

    // 🔥 STATS
    private void loadStats(){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Urls.GET_STATS, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    tvDonors.setText(obj.getString("donors"));
                    tvRequests.setText(obj.getString("requests"));
                } catch (Exception e){}
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    // 🔥 PREMIUM UPCOMING CAMPS
    private void loadUpcomingCamps(){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Urls.GET_CAMPS, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    JSONArray arr = new JSONArray(new String(responseBody));
                    upcomingContainer.removeAllViews();

                    for(int i=0; i<arr.length(); i++){

                        JSONObject obj = arr.getJSONObject(i);

                        String name = obj.getString("camp_name");
                        String date = obj.getString("camp_date");
                        String location = obj.getString("location");

                        CardView card = new CardView(getContext());
                        card.setRadius(18);
                        card.setCardElevation(8);

                        LinearLayout layout = new LinearLayout(getContext());
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setPadding(25,25,25,25);
                        layout.setBackgroundColor(getResources().getColor(R.color.camp_card_bg));

                        TextView tvName = new TextView(getContext());
                        tvName.setText((i+1) + ". " + name);
                        tvName.setTextSize(16);
                        tvName.setTypeface(null, android.graphics.Typeface.BOLD);

                        TextView tvDate = new TextView(getContext());
                        tvDate.setText("📅 " + date);

                        TextView tvLocation = new TextView(getContext());
                        tvLocation.setText("📍 " + location);

                        layout.addView(tvName);
                        layout.addView(tvDate);
                        layout.addView(tvLocation);

                        card.addView(layout);

                        LinearLayout.LayoutParams params =
                                new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);

                        params.setMargins(0,12,0,0);
                        card.setLayoutParams(params);

                        upcomingContainer.addView(card);
                    }

                } catch (Exception e){
                    Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔥 FRAGMENT SWITCH
    private void loadFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    // 🔥 AUTO SLIDER
    private void autoSlide(int size){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int current = slider.getCurrentItem();
                slider.setCurrentItem((current + 1) % size);
                handler.postDelayed(this, 3000);
            }
        },3000);
    }
}