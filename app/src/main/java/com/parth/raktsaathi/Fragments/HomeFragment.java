package com.parth.raktsaathi.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parth.raktsaathi.Donars.DonorAdapter;
import com.parth.raktsaathi.Donars.DonorModel;
import com.parth.raktsaathi.MyLocationActivity;
import com.parth.raktsaathi.MyProfileActivity;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    TextView tvUserName, txtCity;
    Button btndonatebloodbtn, btnrequestbloodbtn;
    LinearLayout layoutLocation;
    ImageView ivHomeProfile;
    SharedPreferences preferences;
    EditText etSearchDonors;
    ImageView ivSearchMic;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    TextToSpeech textToSpeech;

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

        etSearchDonors = view.findViewById(R.id.etSearchDonors);
        ivSearchMic = view.findViewById(R.id.ivSearchMic);
        tvUserName = view.findViewById(R.id.tvHomeUserName);
        txtCity = view.findViewById(R.id.txtCity);
        layoutLocation = view.findViewById(R.id.layoutLocation);
        ivHomeProfile = view.findViewById(R.id.ivHomeProfile);

        btndonatebloodbtn = view.findViewById(R.id.btndonatebloodbtn);
        btnrequestbloodbtn = view.findViewById(R.id.btnrequestbloodbtn);

        layoutLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyLocationActivity.class);
                startActivity(intent);
            }
        });

        ivHomeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActivity.class);
                startActivity(intent);
            }
        });

        ivSearchMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, "hi-IN, mr-IN, en-US");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");

                try {
                    startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);

                } catch (Exception e) {
                    Toast.makeText(getActivity(), "" + e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });

        // RecyclerView connect
        recyclerView = view.findViewById(R.id.rvHomeDonorList); 
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
                    txtCity.setText("Server Error");
                }
            });
        } else {
            txtCity.setText("Please Login");
        }

        RequestQueue queue = Volley.newRequestQueue(getContext());

        // Donor List API
        String donorUrl = Urls.GetDonorsWebService; 

        StringRequest donorRequest = new StringRequest(Request.Method.GET, donorUrl,
                this::onResponse,
                error -> {
                    Log.e(TAG, "Donor List API Error", error);
                });

        queue.add(donorRequest);

        btndonatebloodbtn.setOnClickListener(v -> {
            Fragment fragment = new DonateFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.homeFrameLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        btnrequestbloodbtn.setOnClickListener(v -> {
            Fragment fragment = new RequestsFragment();
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.homeFrameLayout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    private void onResponse(String response) {
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
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                String textToSpeak = result.get(0);
                etSearchDonors.setText(textToSpeak);

                textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            textToSpeech.setLanguage(new Locale("hi", "IN"));
                            textToSpeech.setPitch(0.8f);
                            textToSpeech.setSpeechRate(0.8f);
                            textToSpeech.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
