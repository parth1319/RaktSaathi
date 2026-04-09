package com.parth.raktsaathi.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.loopj.android.http.*;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.*;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    TextView tvGreeting, tvUserName, tvDonors, tvRequests;
    ViewPager2 slider;
    LinearLayout campsContainer;

    String email = "";
    Handler handler = new Handler();

    public HomeFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sp.getString("email", "");

        // IDs
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvDonors = view.findViewById(R.id.tvDonors);
        tvRequests = view.findViewById(R.id.tvRequests);
        slider = view.findViewById(R.id.imageSlider);
        campsContainer = view.findViewById(R.id.campsContainer);

        CardView btnDonate = view.findViewById(R.id.btnDonate);
        CardView btnRequest = view.findViewById(R.id.btnRequest);
        CardView btnHealth = view.findViewById(R.id.btnHealthTips);
        ImageView btnNotification = view.findViewById(R.id.btnNotification);
        Button btnCampMain = view.findViewById(R.id.btnRegisterCamps);

        // SLIDER
        int[] images = {
                R.drawable.rs_homefragment_slider1,
                R.drawable.rs_homefragment_slider2,
                R.drawable.rs_homefragment_slider3
        };

        SliderAdapter adapter = new SliderAdapter(images);
        slider.setAdapter(adapter);

        autoSlide(images.length);

        // CLICK EVENTS
        btnDonate.setOnClickListener(v -> loadFragment(new DonateFragment()));
        btnRequest.setOnClickListener(v -> loadFragment(new RequestFragment()));
        btnHealth.setOnClickListener(v -> startActivity(new Intent(getActivity(), HealthTipsActivity.class)));
        btnNotification.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationActivity.class)));
        btnCampMain.setOnClickListener(v -> startActivity(new Intent(getActivity(), CampActivity.class)));

        // LOAD
        loadUser();
        loadStats();
        loadCamps();
        autoRefresh();

        return view;
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

                        String name = obj.getString("camp_name");
                        String date = obj.getString("camp_date");
                        String location = obj.getString("location");

                        View view = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_camps, campsContainer, false);

                        TextView tvName = view.findViewById(R.id.tvCampName);
                        TextView tvDate = view.findViewById(R.id.tvCampDate);
                        TextView tvLocation = view.findViewById(R.id.tvCampLocation);
                        Button btn = view.findViewById(R.id.btnCampRegister);

                        tvName.setText((i+1) + ". " + name);
                        tvDate.setText("Date: " + date);
                        tvLocation.setText("📍 " + location);

                        // 🔥 CHECK ALREADY REGISTERED
                        checkRegistered(name, btn);

                        btn.setOnClickListener(v ->
                                registerCamp(name, date, btn));

                        campsContainer.addView(view);
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

    // 🔥 CHECK REGISTERED
    private void checkRegistered(String campName, Button btn){

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("camp_name", campName);

        client.post(Urls.CHECK_REGISTER, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                if(res.equalsIgnoreCase("registered")){
                    btn.setText("Registered ✅");
                    btn.setEnabled(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    // 🔥 REGISTER CAMP
    private void registerCamp(String name, String date, Button btn){

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("camp_name", name);
        params.put("camp_date", date);

        client.post(Urls.REGISTER_CAMP, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String res = new String(responseBody);

                if(res.equalsIgnoreCase("success")){
                    btn.setText("Registered ✅");
                    btn.setEnabled(false);
                    Toast.makeText(getContext(),"Registered Successfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(),"Already Registered",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // USER
    private void loadUser(){
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    tvGreeting.setText("Hi,");
                    tvUserName.setText(obj.getString("name"));
                } catch (Exception e){}
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    // STATS
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

    private void autoRefresh(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadStats();
                handler.postDelayed(this, 5000);
            }
        },5000);
    }

    private void autoSlide(int size){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(slider == null) return;
                int current = slider.getCurrentItem();
                slider.setCurrentItem((current + 1) % size);
                handler.postDelayed(this, 4000);
            }
        },4000);
    }

    private void loadFragment(Fragment fragment){
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.homeFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacksAndMessages(null);
    }
}