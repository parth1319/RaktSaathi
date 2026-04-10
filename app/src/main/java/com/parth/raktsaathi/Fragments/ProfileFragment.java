package com.parth.raktsaathi.Fragments;

import android.app.AlertDialog;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.loopj.android.http.*;
import com.parth.raktsaathi.*;
import com.parth.raktsaathi.R;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    TextView tvName, tvMobile, tvEmail, tvBlood, tvCity, tvAddPhoto;
    TextView btnLogout, btnEditProfile, btnChangePass;

    ProgressBar progressBar;
    TextView tvProgress;
    ImageView imgVerified;

    CircleImageView imgProfile;

    String email = "";
    Uri imageUri;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sp.getString("email", "");

        imgProfile = view.findViewById(R.id.imgProfile);
        tvAddPhoto = view.findViewById(R.id.tvAddPhoto);

        tvName = view.findViewById(R.id.tvName);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBlood = view.findViewById(R.id.tvBlood);
        tvCity = view.findViewById(R.id.tvCity);

        progressBar = view.findViewById(R.id.progressBar);
        tvProgress = view.findViewById(R.id.tvProgress);
        imgVerified = view.findViewById(R.id.imgVerified);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePass = view.findViewById(R.id.btnChangePass);

        tvEmail.setText("📧 " + email);

        loadProfile();

        tvAddPhoto.setOnClickListener(v -> openGallery());
        imgProfile.setOnClickListener(v -> openGallery());

        btnLogout.setOnClickListener(v -> {
            sp.edit().clear().apply();
            startActivity(new Intent(getContext(), LoginActivity.class));
            getActivity().finish();
        });

        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(getContext(), EditProfileActivity.class)));

        btnChangePass.setOnClickListener(v ->
                startActivity(new Intent(getContext(), ChangePasswordActivity.class)));

        return view;
    }

    // 🔥 LOAD PROFILE + PROGRESS + VERIFIED
    private void loadProfile(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    JSONObject obj = new JSONObject(new String(responseBody));

                    String name = obj.optString("name");
                    String phone = obj.optString("phone");
                    String blood = obj.optString("blood_group");
                    String city = obj.optString("location");

                    tvName.setText(name);
                    tvMobile.setText("📞 " + phone);
                    tvBlood.setText("🩸 " + blood);
                    tvCity.setText("📍 " + city);

                    // 🔥 PROGRESS CALCULATION
                    int progress = 0;

                    if(!name.isEmpty()) progress += 20;
                    if(!phone.isEmpty()) progress += 20;
                    if(!email.isEmpty()) progress += 20;
                    if(!blood.isEmpty()) progress += 20;
                    if(!city.isEmpty()) progress += 20;

                    progressBar.setProgress(progress);
                    tvProgress.setText("Profile Complete: " + progress + "%");

                    // 🔥 VERIFIED BADGE
                    if(progress >= 80){
                        imgVerified.setVisibility(View.VISIBLE);
                    } else {
                        imgVerified.setVisibility(View.GONE);
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

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }
}