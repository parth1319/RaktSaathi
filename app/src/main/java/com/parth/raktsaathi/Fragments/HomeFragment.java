package com.parth.raktsaathi.Fragments;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
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

import cz.msebera.android.httpclient.Header;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";

    private TextView tvUserName, tvUserLocation;
    private Button btndonatebloodbtn, btnrequestbloodbtn;
    private LinearLayout layoutLocation;
    private ImageView ivHomeProfile, ivSearchMic;
    private EditText etSearchDonors;
    private SharedPreferences preferences;
    private TextToSpeech textToSpeech;

    private RecyclerView recyclerView;
    private ArrayList<DonorModel> donorList;
    private DonorAdapter donorAdapter;

    private static final int REQUEST_CODE_SPEECH_INPUT = 1;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views
        tvUserName = view.findViewById(R.id.tvHomeUserName);
        tvUserLocation = view.findViewById(R.id.tvUserLocation);
        layoutLocation = view.findViewById(R.id.layoutLocation);
        ivHomeProfile = view.findViewById(R.id.ivHomeProfile);

        btndonatebloodbtn = view.findViewById(R.id.btndonatebloodbtn);
        btnrequestbloodbtn = view.findViewById(R.id.btnrequestbloodbtn);

        etSearchDonors = view.findViewById(R.id.etSearchDonors);
        ivSearchMic = view.findViewById(R.id.ivSearchMic);

        // RecyclerView
        donorList = new ArrayList<>();
        donorAdapter = new DonorAdapter(getContext(), donorList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(donorAdapter);

        // SharedPreferences
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String username = preferences.getString("userName", "");
        tvUserName.setText(username.isEmpty() ? "Hi, User!" : "Hi, " + username);

        // Fetch City if logged in
        if (!username.isEmpty()) {
            fetchUserCity(username);
        } else {
            tvUserLocation.setText("Please Login");
        }

        // Location click
        layoutLocation.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyLocationActivity.class)));

        // Profile click
        ivHomeProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyProfileActivity.class)));

        // Mic / Voice input
        ivSearchMic.setOnClickListener(v -> startVoiceInput());

        // Load Donors
        loadDonorList();

        // Action buttons
        btndonatebloodbtn.setOnClickListener(v -> replaceFragment(new Find_DonorFragment()));
        btnrequestbloodbtn.setOnClickListener(v -> replaceFragment(new RequestsFragment()));

        return view;
    }

    // Fetch city using AsyncHttpClient
    private void fetchUserCity(String username) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("username", username);

        client.post(Urls.GetCityWebService, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String city = response.has("city") ? response.getString("city") : "City not found";
                    tvUserLocation.setText(city + ", Maharashtra");
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing city JSON", e);
                    tvUserLocation.setText("Error loading city");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.e(TAG, "City API Failure. Status: " + statusCode, throwable);
                tvUserLocation.setText("Server Error");
            }
        });
    }

    // Load donors using Volley
    private void loadDonorList() {
        String donorUrl = Urls.GetDonorsWebService;

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest donorRequest = new StringRequest(Request.Method.GET, donorUrl,
                this::handleDonorResponse,
                error -> Log.e(TAG, "Donor List API Error", error));

        queue.add(donorRequest);
    }

    // Parse donors JSON
    private void handleDonorResponse(String response) {
        try {
            JSONArray array = new JSONArray(response);
            donorList.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                donorList.add(new DonorModel(
                        obj.getString("username"),
                        obj.getString("mobileno"),
                        obj.getString("emailid"),
                        obj.getString("blood_group"),
                        obj.getString("address"),
                        obj.getString("city")
                ));
            }
            donorAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, "Error parsing donor list", e);
        }
    }

    // Voice input
    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, "hi-IN, mr-IN, en-US");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak Now");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Fragment replace helper
    private void replaceFragment(Fragment fragment) {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.homeFrameLayout, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String text = result.get(0);
                etSearchDonors.setText(text);
                speakText(text);
            }
        }
    }

    private void speakText(String text) {
        textToSpeech = new TextToSpeech(getActivity(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(new Locale("hi", "IN"));
                textToSpeech.setPitch(0.8f);
                textToSpeech.setSpeechRate(0.8f);
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });
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