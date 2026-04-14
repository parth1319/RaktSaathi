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

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.*;
import com.parth.raktsaathi.*;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.SliderAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
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

        // Initialize Views
        tvGreeting = view.findViewById(R.id.tvGreeting);
        tvUserName = view.findViewById(R.id.tvUserName);
        tvActiveDonorsCount = view.findViewById(R.id.tvActiveDonorsCount);
        tvActiveRequestsCount = view.findViewById(R.id.tvActiveRequestsCount);
        btnViewAllCamps = view.findViewById(R.id.btnViewAllCamps);
        upcomingContainer = view.findViewById(R.id.upcomingContainer);
        bloodInventoryContainer = view.findViewById(R.id.bloodInventoryContainer);
        slider = view.findViewById(R.id.imageSlider);
        etSearchDonors = view.findViewById(R.id.etSearchDonors);

        Button btnCamp = view.findViewById(R.id.btnRegisterCamps);
        CardView btnHealth = view.findViewById(R.id.btnHealthTips);

        // Quick Actions Wiring
        CardView btnQuickRequest = view.findViewById(R.id.btnQuickRequest);
        CardView btnQuickDonate = view.findViewById(R.id.btnQuickDonate);
        CardView btnQuickCamps = view.findViewById(R.id.btnQuickCamps);

        btnQuickRequest.setOnClickListener(v -> {
            // Navigate to Requests Fragment via HomeActivity
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).bottomNav.setSelectedItemId(R.id.homebottomnavRequests);
            }
        });

        btnQuickDonate.setOnClickListener(v -> {
            if (getActivity() instanceof HomeActivity) {
                ((HomeActivity) getActivity()).bottomNav.setSelectedItemId(R.id.homebottomnavDonate);
            }
        });

        btnQuickCamps.setOnClickListener(v -> {
            btnViewAllCamps.performClick();
            // Scroll to camps
            view.post(() -> {
                int y = upcomingContainer.getTop();
                ((ScrollView)view).smoothScrollTo(0, y - 100);
            });
        });

        // Slider Setup
        int[] images = {R.drawable.rs_homefragment_slider1, R.drawable.rs_homefragment_slider2, R.drawable.rs_homefragment_slider3};
        slider.setAdapter(new SliderAdapter(images));
        autoSlide(images.length);

        // Click Events
        btnHealth.setOnClickListener(v -> startActivity(new Intent(getActivity(), HealthTipsActivity.class)));
        btnCamp.setOnClickListener(v -> startActivity(new Intent(getActivity(), AddCampActivity.class)));
        
        btnViewAllCamps.setOnClickListener(v -> {
            isAllCampsVisible = !isAllCampsVisible;
            btnViewAllCamps.setText(isAllCampsVisible ? "View Less" : "View All");
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

        // Load Data
        loadUser();
        loadBloodInventory(); // Setup the UI for inventory first
        loadStats();         // Update the counts from DB
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
                    
                    // Set General Stats
                    tvActiveDonorsCount.setText(obj.optString("donors", "0"));
                    tvActiveRequestsCount.setText(obj.optString("requests", "0"));
                    
                    // Update Blood Inventory (Horizontal ScrollView)
                    if (obj.has("blood_counts")) {
                        updateBloodInventory(obj.getJSONObject("blood_counts"));
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
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
            List<JSONObject> filteredCamps = new ArrayList<>();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
            java.util.Date today = new java.util.Date();
            
            // Set today to start of day for accurate comparison
            Calendar cal = Calendar.getInstance();
            cal.setTime(today);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long todayMillis = cal.getTimeInMillis();

            for (int i = 0; i < campsArray.length(); i++) {
                JSONObject obj = campsArray.getJSONObject(i);
                String campDateStr = obj.getString("camp_date");
                try {
                    java.util.Date cDate = sdf.parse(campDateStr);
                    if (cDate != null && cDate.getTime() >= todayMillis) {
                        filteredCamps.add(obj);
                    }
                } catch (Exception e) {
                    // Fallback: If date format is wrong, still show it so it doesn't look "broken"
                    filteredCamps.add(obj);
                }
            }
            
            // Sort by date: closest to today first
            Collections.sort(filteredCamps, (o1, o2) -> {
                try {
                    return o1.getString("camp_date").compareTo(o2.getString("camp_date"));
                } catch (Exception e) { return 0; }
            });

            // Hide "View All" button if there's only 1 or 0 camps
            if (filteredCamps.size() <= 1) {
                btnViewAllCamps.setVisibility(View.GONE);
            } else {
                btnViewAllCamps.setVisibility(View.VISIBLE);
            }

            // "View Less" shows 1 camp, "View All" shows all
            int limit = isAllCampsVisible ? filteredCamps.size() : Math.min(filteredCamps.size(), 1);
            
            if (filteredCamps.isEmpty()) {
                TextView tvNoCamps = new TextView(getContext());
                tvNoCamps.setText("No upcoming camps found");
                tvNoCamps.setGravity(Gravity.CENTER);
                tvNoCamps.setPadding(0, 50, 0, 50);
                tvNoCamps.setTextColor(getResources().getColor(R.color.textSecondary));
                upcomingContainer.addView(tvNoCamps);
                return;
            }

            for(int i=0; i < limit; i++){
                JSONObject obj = filteredCamps.get(i);
                View card = getLayoutInflater().inflate(R.layout.item_camps, upcomingContainer, false);
                
                // Using optString with your exact database column names
                ((TextView)card.findViewById(R.id.tvCampName)).setText(obj.optString("camp_name", "Upcoming Camp"));
                
                String dateStr = obj.optString("camp_date", "");
                String dateToShow = dateStr;
                try {
                    java.util.Date d = sdf.parse(dateStr);
                    if (d != null) {
                        dateToShow = new java.text.SimpleDateFormat("dd MMM, yyyy", java.util.Locale.getDefault()).format(d);
                    }
                } catch(Exception e){}
                
                // Clean text without the '17' emoji
                ((TextView)card.findViewById(R.id.tvCampDate)).setText(dateToShow);
                
                // Match your database 'location' column
                String loc = obj.optString("location", "Venue Not Set");
                ((TextView)card.findViewById(R.id.tvCampLocation)).setText(loc);

                // --- SECURITY LOGIC: Show Edit button ONLY if it's YOUR camp ---
                ImageView btnEdit = card.findViewById(R.id.btnEditCamp);
                String campOwnerEmail = obj.optString("email", "");

                if (campOwnerEmail.equalsIgnoreCase(email)) {
                    btnEdit.setVisibility(View.VISIBLE);
                    btnEdit.setOnClickListener(v -> {
                        showEditDialog(obj);
                    });
                } else {
                    btnEdit.setVisibility(View.GONE);
                    // If not owner, allow them to Register for the camp
                    card.setOnClickListener(v -> {
                        checkAndRegisterForCamp(obj);
                    });
                }

                upcomingContainer.addView(card);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkAndRegisterForCamp(JSONObject camp) {
        String campId = camp.optString("id", "");
        if (campId.isEmpty()) return;

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("camp_id", campId);
        params.put("email", email);

        // 1. First check if already registered
        client.post(Urls.CHECK_REGISTER, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody).trim();
                if (res.equalsIgnoreCase("registered")) {
                    Toast.makeText(getContext(), "You are already registered for this camp! 😊", Toast.LENGTH_SHORT).show();
                } else {
                    // 2. If not, show confirmation to register
                    new android.app.AlertDialog.Builder(getContext())
                            .setTitle("Join Blood Camp")
                            .setMessage("Do you want to register for " + camp.optString("camp_name") + "?")
                            .setPositiveButton("Register Now", (dialog, which) -> {
                                registerUserForCamp(campId);
                            })
                            .setNegativeButton("Maybe Later", null)
                            .show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void registerUserForCamp(String campId) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("camp_id", campId);
        params.put("email", email);

        client.post(Urls.REGISTER_CAMP, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (new String(responseBody).trim().equalsIgnoreCase("success")) {
                    Toast.makeText(getContext(), "Registration Successful! See you there! 🩸", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });
    }

    private void showEditDialog(JSONObject camp) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_camp, null);
        
        TextInputEditText etName = dialogView.findViewById(R.id.etCampName);
        TextInputEditText etLoc = dialogView.findViewById(R.id.etLocation);
        TextInputEditText etDate = dialogView.findViewById(R.id.etDate);
        TextInputEditText etEmail = dialogView.findViewById(R.id.etEmail);
        Button btnSave = dialogView.findViewById(R.id.btnSubmit);
        
        btnSave.setText("Update Camp Details");

        try {
            etName.setText(camp.getString("camp_name"));
            etLoc.setText(camp.getString("location"));
            etDate.setText(camp.getString("camp_date"));
            etEmail.setText(camp.getString("email"));
            etEmail.setEnabled(false); // Still locked
            
            etDate.setOnClickListener(v -> {
                Calendar cal = Calendar.getInstance();
                new android.app.DatePickerDialog(getContext(), (view, year, month, day) -> {
                    String date = String.format(java.util.Locale.getDefault(), "%d-%02d-%02d", year, (month + 1), day);
                    etDate.setText(date);
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
            });

            android.app.Dialog dialog = new android.app.Dialog(getContext());
            dialog.setContentView(dialogView);
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.setCancelable(true);
            
            btnSave.setOnClickListener(v -> {
                String name = etName.getText().toString().trim();
                String loc = etLoc.getText().toString().trim();
                String date = etDate.getText().toString().trim();
                
                if(name.isEmpty() || loc.isEmpty() || date.isEmpty()){
                    Toast.makeText(getContext(), "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                try {
                    // CRITICAL: Ensure your get_camps.php sends 'id'
                    String campId = camp.optString("id", "");
                    if(campId.isEmpty()){
                        // Debugging: Show all available keys in the JSONObject
                        java.util.Iterator<String> keys = camp.keys();
                        StringBuilder sb = new StringBuilder("Keys: ");
                        while(keys.hasNext()) sb.append(keys.next()).append(", ");
                        Toast.makeText(getContext(), "ID missing. Found " + sb.toString(), Toast.LENGTH_LONG).show();
                        return;
                    }

                    params.put("id", campId);
                    params.put("camp_name", name);
                    params.put("location", loc);
                    params.put("camp_date", date);

                    client.post(Urls.UPDATE_CAMP, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String res = new String(responseBody).trim();
                            android.util.Log.d("UpdateCamp", "Server Response: " + res);
                            
                            if (res.equalsIgnoreCase("success")) {
                                Toast.makeText(getContext(), "✅ Updated Successfully", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                loadUpcomingCamps(); // Refresh the list
                            } else {
                                Toast.makeText(getContext(), "Server Error: " + res, Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getContext(), "Network Error: Update Failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) { e.printStackTrace(); }
            });

            dialog.show();
        } catch (Exception e) { e.printStackTrace(); }
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