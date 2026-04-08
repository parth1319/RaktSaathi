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

import java.io.File;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    TextView tvName, tvMobile, tvEmail, tvBlood, tvCity, tvAddPhoto;
    TextView btnLogout, btnEditProfile, btnChangePass, btnChangePhoto;

    LinearLayout btnSettingsToggle, btnAboutToggle, settingsContent;
    TextView arrowSettings, arrowAbout, aboutContent;

    CircleImageView imgProfile;

    String email = "";
    Uri imageUri;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sp.getString("email", "");

        // 🔥 IDs
        imgProfile = view.findViewById(R.id.imgProfile);
        tvAddPhoto = view.findViewById(R.id.tvAddPhoto);

        tvName = view.findViewById(R.id.tvName);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBlood = view.findViewById(R.id.tvBlood);
        tvCity = view.findViewById(R.id.tvCity);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePass = view.findViewById(R.id.btnChangePass);
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto);

        btnSettingsToggle = view.findViewById(R.id.btnSettingsToggle);
        btnAboutToggle = view.findViewById(R.id.btnAboutToggle);

        settingsContent = view.findViewById(R.id.settingsContent);
        aboutContent = view.findViewById(R.id.aboutContent);

        arrowSettings = view.findViewById(R.id.arrowSettings);
        arrowAbout = view.findViewById(R.id.arrowAbout);

        tvEmail.setText("📧 " + email);

        loadProfile();

        // 🔥 PHOTO CLICK
        tvAddPhoto.setOnClickListener(v -> openGallery());
        imgProfile.setOnClickListener(v -> openGallery());
        btnChangePhoto.setOnClickListener(v -> openGallery());

        // 🔥 SETTINGS TOGGLE
        btnSettingsToggle.setOnClickListener(v -> {
            if(settingsContent.getVisibility() == View.VISIBLE){
                settingsContent.setVisibility(View.GONE);
                arrowSettings.setText("▼");
            }else{
                settingsContent.setVisibility(View.VISIBLE);
                arrowSettings.setText("▲");
            }
        });

        // 🔥 ABOUT TOGGLE
        btnAboutToggle.setOnClickListener(v -> {
            if(aboutContent.getVisibility() == View.VISIBLE){
                aboutContent.setVisibility(View.GONE);
                arrowAbout.setText("▼");
            }else{
                aboutContent.setVisibility(View.VISIBLE);
                arrowAbout.setText("▲");
            }
        });

        // 🔥 LOGOUT
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", (d, w) -> {
                        sp.edit().clear().apply();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        // 🔥 EDIT PROFILE
        btnEditProfile.setOnClickListener(v ->
                startActivity(new Intent(getContext(), EditProfileActivity.class)));

        // 🔥 CHANGE PASSWORD
        btnChangePass.setOnClickListener(v ->
                startActivity(new Intent(getContext(), ChangePasswordActivity.class)));

        return view;
    }

    // 🔥 OPEN GALLERY
    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    // 🔥 LOAD PROFILE
    private void loadProfile(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try {
                    String res = new String(responseBody).trim();
                    JSONObject obj = new JSONObject(res);

                    tvName.setText(obj.optString("name"));
                    tvMobile.setText("📞 " + obj.optString("phone"));
                    tvBlood.setText("🩸 " + obj.optString("blood_group"));

                    String location = obj.optString("location");
                    if(location == null || location.isEmpty() || location.equals("null")){
                        location = "N/A";
                    }
                    tvCity.setText("📍 " + location);

                    String imagePath = obj.optString("profile_image");

                    if(imagePath != null && !imagePath.isEmpty() && !imagePath.equals("null")){

                        String fullUrl = Urls.BASE_URL + imagePath;

                        com.squareup.picasso.Picasso.get()
                                .load(fullUrl)
                                .placeholder(R.drawable.rs_profilelogo)
                                .error(R.drawable.rs_profilelogo)
                                .into(imgProfile);

                        tvAddPhoto.setText("Change Profile Photo");

                    }else{
                        imgProfile.setImageResource(R.drawable.rs_profilelogo);
                        tvAddPhoto.setText("Add Profile Photo");
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

    // 🔥 RESULT (NO CROP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == getActivity().RESULT_OK && data != null){

            imageUri = data.getData();

            imgProfile.setImageURI(imageUri);

            uploadImage();
        }
    }

    // 🔥 UPLOAD
    private void uploadImage(){

        try{

            AsyncHttpClient client = new AsyncHttpClient();

            RequestParams params = new RequestParams();
            params.put("email", email);

            // 🔥 INPUT STREAM METHOD (REAL FIX)
            params.put("image", getContext().getContentResolver().openInputStream(imageUri), "profile.jpg");

            client.post(Urls.UPLOAD_IMAGE, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getContext(),"Uploaded ✅",Toast.LENGTH_SHORT).show();
                    loadProfile();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getContext(),"Upload Failed ❌",Toast.LENGTH_SHORT).show();
                }
            });

        }catch(Exception e){
            e.printStackTrace();
            Toast.makeText(getContext(),"File Error ❌",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }
}