package com.parth.raktsaathi;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {

    ImageView backBtn;
    RecyclerView rvNotifications;
    LinearLayout emptyLayout;
    NotificationAdapter adapter;
    List<NotificationModel> notificationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        backBtn = findViewById(R.id.btn_back);
        rvNotifications = findViewById(R.id.rvNotifications);
        emptyLayout = findViewById(R.id.emptyLayout);

        backBtn.setOnClickListener(v -> finish());

        setupRecyclerView();
        fetchNotifications(); 
    }

    private void setupRecyclerView() {
        notificationList = new ArrayList<>();
        adapter = new NotificationAdapter(notificationList);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        rvNotifications.setAdapter(adapter);
    }

    private void fetchNotifications() {

        String url = Urls.GET_NOTIFICATIONS + "?user_id=0"; 

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        notificationList.clear();

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            
                            notificationList.add(new NotificationModel(
                                    object.getString("id"),
                                    object.getString("title"),
                                    object.getString("message"),
                                    object.getString("time"),
                                    object.getInt("is_read") == 1,
                                    object.getString("type")
                            ));
                        }

                        if (notificationList.isEmpty()) {
                            rvNotifications.setVisibility(View.GONE);
                            emptyLayout.setVisibility(View.VISIBLE);
                        } else {
                            rvNotifications.setVisibility(View.VISIBLE);
                            emptyLayout.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(NotificationActivity.this, "Error parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(NotificationActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
