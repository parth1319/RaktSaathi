package com.parth.raktsaathi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CampActivity extends AppCompatActivity {

    LinearLayout campContainer;
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camp);

        campContainer = findViewById(R.id.campContainer);

        SharedPreferences sp = getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sp.getString("email", "");

        loadCamps();
    }

    // 🔥 LOAD CAMPS
    private void loadCamps(){

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(Urls.GET_CAMPS, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {

                    JSONArray arr = new JSONArray(new String(responseBody));
                    campContainer.removeAllViews();

                    for(int i=0; i<arr.length(); i++){

                        JSONObject obj = arr.getJSONObject(i);

                        String name = obj.getString("camp_name");
                        String date = obj.getString("camp_date");
                        String location = obj.getString("location");

                        View view = LayoutInflater.from(CampActivity.this)
                                .inflate(R.layout.item_camps, campContainer, false);

                        TextView tvName = view.findViewById(R.id.tvCampName);
                        TextView tvDate = view.findViewById(R.id.tvCampDate);
                        TextView tvLocation = view.findViewById(R.id.tvCampLocation);
                        Button btn = view.findViewById(R.id.btnCampRegister);

                        tvName.setText((i+1) + ". " + name);
                        tvDate.setText("Date: " + date);
                        tvLocation.setText("📍 " + location);

                        // 🔥 CHECK REGISTERED
                        checkRegistered(name, btn);

                        btn.setOnClickListener(v ->
                                registerCamp(name, date, btn));

                        campContainer.addView(view);
                    }

                } catch (Exception e){
                    Toast.makeText(CampActivity.this,"JSON Error",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(CampActivity.this,"Server Error",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(CampActivity.this,"Registered Successfully",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(CampActivity.this,"Already Registered",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(CampActivity.this,"Error",Toast.LENGTH_SHORT).show();
            }
        });
    }
}