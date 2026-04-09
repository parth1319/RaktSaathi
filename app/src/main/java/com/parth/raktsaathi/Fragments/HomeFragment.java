package com.parth.raktsaathi.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.loopj.android.http.*;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;
import com.parth.raktsaathi.SliderAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    TextView tvGreeting, tvDonors, tvRequests;
    LinearLayout campsContainer;
    ViewPager2 slider;

    String email = "";

    Handler handler = new Handler();

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sp.getString("email", "");

        // 🔥 IDs
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvDonors = view.findViewById(R.id.tvDonors);
        tvRequests = view.findViewById(R.id.tvRequests);
        campsContainer = view.findViewById(R.id.campsContainer);
        slider = view.findViewById(R.id.imageSlider);

        Button btnCampMain = view.findViewById(R.id.btnRegisterCamps);

        // 🔥 SLIDER
        int[] images = {
                R.drawable.rs_homefragment_slider1,
                R.drawable.rs_homefragment_slider2,
                R.drawable.rs_homefragment_slider3
        };

        SliderAdapter adapter = new SliderAdapter(images);
        slider.setAdapter(adapter);

        autoSlide(images.length);

        // 🔥 CAMP BUTTON
        btnCampMain.setOnClickListener(v ->
                registerCamp("City Blood Camp", "20 April", btnCampMain));

        // 🔥 LOAD DATA
        loadUserName();
        loadCamps();
        loadStats();

        // 🔥 AUTO REFRESH STATS
        autoRefreshStats();

        return view;
    }

    // 🔥 USERNAME STYLE
    private void loadUserName(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    JSONObject obj = new JSONObject(new String(responseBody));

                    String name = obj.getString("name");

                    String fullText = "Hi, " + name + " 👋";

                    SpannableString spannable = new SpannableString(fullText);

                    int start = fullText.indexOf(name);
                    int end = start + name.length();

                    spannable.setSpan(new StyleSpan(Typeface.BOLD),
                            start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tvGreeting.setText(spannable);

                } catch (Exception e){
                    tvGreeting.setText("Hi User 👋");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                tvGreeting.setText("Hi User 👋");
            }
        });
    }

    // 🔥 SLIDER AUTO
    private void autoSlide(int size){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if(slider == null) return;

                int current = slider.getCurrentItem();
                slider.setCurrentItem((current + 1) % size);

                handler.postDelayed(this, 3000);
            }
        }, 3000);
    }

    // 🔥 LOAD CAMPS
    private void loadCamps(){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Urls.GET_CAMPS, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    JSONArray arr = new JSONArray(new String(responseBody));

                    campsContainer.removeAllViews();

                    for(int i=0; i<arr.length(); i++){

                        JSONObject obj = arr.getJSONObject(i);

                        String campName = obj.getString("camp_name");
                        String campDate = obj.getString("camp_date");
                        String location = obj.getString("location");

                        View campView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_camps, campsContainer, false);

                        TextView tvName = campView.findViewById(R.id.tvCampName);
                        TextView tvDate = campView.findViewById(R.id.tvCampDate);
                        TextView tvLocation = campView.findViewById(R.id.tvCampLocation);
                        Button btnRegister = campView.findViewById(R.id.btnCampRegister);

                        tvName.setText((i+1) + ". " + campName);
                        tvDate.setText("Date: " + campDate);
                        tvLocation.setText("📍 " + location);

                        btnRegister.setOnClickListener(v ->
                                registerCamp(campName, campDate, btnRegister));

                        campsContainer.addView(campView);
                    }

                } catch (Exception e){
                    Toast.makeText(getContext(),"JSON Error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔥 REGISTER CAMP
    private void registerCamp(String campName, String date, Button btn){

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("camp_name", campName);
        params.put("camp_date", date);

        client.post(Urls.REGISTER_CAMP, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                // 🔥 DEBUG (IMPORTANT)
                Toast.makeText(getContext(), res, Toast.LENGTH_LONG).show();

                if(res.equalsIgnoreCase("success")){

                    btn.setText("Registered ✅");
                    btn.setEnabled(false);
                    btn.setBackgroundTintList(getResources().getColorStateList(R.color.green_success));

                } else {
                    Toast.makeText(getContext(),"Failed: " + res,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                Toast.makeText(getContext(),
                        "Error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // 🔥 LOAD STATS
    private void loadStats(){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Urls.GET_STATS, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    JSONObject obj = new JSONObject(new String(responseBody));

                    tvDonors.setText(obj.getString("donors"));
                    tvRequests.setText(obj.getString("requests"));

                } catch (Exception e){
                    Toast.makeText(getContext(),"Stats Error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"Server Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void autoRefreshStats(){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                loadStats();

                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}