package com.parth.raktsaathi.Fragments;

import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.parth.raktsaathi.R;
import com.parth.raktsaathi.Urls;
import com.parth.raktsaathi.LoginActivity;
import com.parth.raktsaathi.ChangePasswordActivity;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    TextView tvName, tvMobile, tvEmail, tvBlood, tvCity, tvPercent, tvProgress;
    TextView btnEditProfile, btnChangePass;
    MaterialButton btnLogout;
    LinearLayout btnSettingsToggle, btnAboutToggle, settingsContent;
    TextView aboutContent, arrowSettings, arrowAbout;
    SwitchCompat switchDark;
    FloatingActionButton fabAddPhoto;
    CircleImageView imgProfile;
    ProgressBar progressBar;
    ImageView imgVerified;

    String email = "";
    Uri imageUri;

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 1. Get Email from Login Session
        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sp.getString("email", "");

        // 2. Initialize Views
        imgProfile = view.findViewById(R.id.imgProfile);
        tvName = view.findViewById(R.id.tvName);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBlood = view.findViewById(R.id.tvBlood);
        tvCity = view.findViewById(R.id.tvCity);
        tvPercent = view.findViewById(R.id.tvPercent);
        progressBar = view.findViewById(R.id.progressBar);
        tvProgress = view.findViewById(R.id.tvProgress);
        imgVerified = view.findViewById(R.id.imgVerified);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        btnChangePass = view.findViewById(R.id.btnChangePass);
        btnLogout = view.findViewById(R.id.btn_profile_logout);
        btnSettingsToggle = view.findViewById(R.id.btnSettingsToggle);
        btnAboutToggle = view.findViewById(R.id.btnAboutToggle);
        settingsContent = view.findViewById(R.id.settingsContent);
        aboutContent = view.findViewById(R.id.aboutContent);
        arrowSettings = view.findViewById(R.id.arrowSettings);
        arrowAbout = view.findViewById(R.id.arrowAbout);
        switchDark = view.findViewById(R.id.switchDark);
        fabAddPhoto = view.findViewById(R.id.fabAddPhoto);

        tvEmail.setText(email);

        // 3. Load Data
        if (!email.isEmpty()) {
            loadProfile();
        } else {
            Toast.makeText(getContext(), "Session Expired. Please Login.", Toast.LENGTH_SHORT).show();
        }

        // 4. Toggle Listeners
        btnSettingsToggle.setOnClickListener(v -> {
            if (settingsContent.getVisibility() == View.GONE) {
                settingsContent.setVisibility(View.VISIBLE);
                arrowSettings.setText("▲");
            } else {
                settingsContent.setVisibility(View.GONE);
                arrowSettings.setText("▼");
            }
        });

        btnAboutToggle.setOnClickListener(v -> {
            if (aboutContent.getVisibility() == View.GONE) {
                aboutContent.setVisibility(View.VISIBLE);
                arrowAbout.setText("▲");
            } else {
                aboutContent.setVisibility(View.GONE);
                arrowAbout.setText("▼");
            }
        });

        // 5. Dark Mode Toggle
        SharedPreferences themeSp = getActivity().getSharedPreferences("theme", Context.MODE_PRIVATE);
        boolean isDark = themeSp.getBoolean("isDark", false);
        switchDark.setChecked(isDark);

        switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            themeSp.edit().putBoolean("isDark", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // 6. Button Listeners
        btnLogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sp.edit().clear().apply();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());
        btnChangePass.setOnClickListener(v -> startActivity(new Intent(getContext(), ChangePasswordActivity.class)));
        fabAddPhoto.setOnClickListener(v -> {
            String[] options = {"Upload from Gallery", "Use Default Avatar"};
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Profile Photo")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            openGallery();
                        } else {
                            useDefaultAvatar();
                        }
                    })
                    .show();
        });

        return view;
    }

    private void loadProfile() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    JSONObject obj = new JSONObject(response);

                    // Use optString to avoid crashes if keys are missing
                    String name = obj.optString("name", "");
                    String phone = obj.optString("phone", "");
                    String blood = obj.optString("blood_group", "");
                    String location = obj.optString("location", "");
                    String image = obj.optString("profile_image", "");

                    // Update UI
                    tvName.setText(name.isEmpty() || name.equals("null") ? "Not Set" : name);
                    tvMobile.setText(phone.isEmpty() || phone.equals("null") ? "Not Set" : phone);
                    tvBlood.setText(blood.isEmpty() || blood.equals("null") ? "Not Set" : blood);
                    tvCity.setText(location.isEmpty() || location.equals("null") ? "Not Set" : location);

                    // Handle image and completion calculation
                    if (!image.isEmpty() && !image.equals("null") && !image.equals("default")) {
                        Picasso.get().load(Urls.BASE_URL + image).placeholder(R.drawable.rs_profilelogo).into(imgProfile);
                    } else {
                        imgProfile.setImageResource(R.drawable.rs_profilelogo);
                    }

                    // 🔥 Improved Progress Calculation (5 points x 20% = 100%)
                    int p = 0;
                    StringBuilder missingFields = new StringBuilder();

                    // 1. Name
                    if (!name.isEmpty() && !name.equals("null")) {
                        p += 20;
                    } else {
                        missingFields.append("Name, ");
                    }

                    // 2. Phone
                    if (!phone.isEmpty() && !phone.equals("null")) {
                        p += 20;
                    } else {
                        missingFields.append("Phone, ");
                    }

                    // 3. Blood Group
                    if (!blood.isEmpty() && !blood.equals("null")) {
                        p += 20;
                    } else {
                        missingFields.append("Blood Group, ");
                    }

                    // 4. Location/City
                    if (!location.isEmpty() && !location.equals("null")) {
                        p += 20;
                    } else {
                        missingFields.append("Location, ");
                    }

                    // 5. Profile Image (Only real uploaded photos count for 20%)
                    if (!image.isEmpty() && !image.equals("null") && !image.equals("default")) {
                        p += 20;
                    } else {
                        missingFields.append("Profile Photo, ");
                    }

                    progressBar.setProgress(p);
                    tvPercent.setText(p + "%");

                    // Set helpful message and Badge based on completion
                    if (p < 80) {
                        String msg = missingFields.toString();
                        if (msg.endsWith(", ")) {
                            msg = msg.substring(0, msg.length() - 2);
                        }
                        tvProgress.setText("Complete your profile: " + msg);
                        tvProgress.setTextColor(ContextCompat.getColor(getContext(), R.color.rs_buttons_colour));
                        imgVerified.setVisibility(View.GONE); // Hide badge if < 80%
                    } else if (p < 100) {
                        tvProgress.setText("You are now a Verified Donor! 🏆");
                        tvProgress.setTextColor(ContextCompat.getColor(getContext(), R.color.green_success));
                        imgVerified.setVisibility(View.VISIBLE); // Show badge at 80%
                    } else {
                        tvProgress.setText("Profile 100% Completed! 💎");
                        tvProgress.setTextColor(ContextCompat.getColor(getContext(), R.color.green_success));
                        imgVerified.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    Log.e("PROFILE_ERROR", e.getMessage());
                    Toast.makeText(getContext(), "Data Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Server Unreachable", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditProfileDialog() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
        TextInputEditText etName = dialogView.findViewById(R.id.et_name);
        TextInputEditText etPhone = dialogView.findViewById(R.id.et_phone);
        TextInputEditText etLocation = dialogView.findViewById(R.id.et_address);
        AutoCompleteTextView spBlood = dialogView.findViewById(R.id.sp_blood);
        AutoCompleteTextView spCity = dialogView.findViewById(R.id.sp_city);

        etName.setText(tvName.getText().toString());
        etPhone.setText(tvMobile.getText().toString());
        etLocation.setText(tvCity.getText().toString());

        // Setup Blood Group Adapter
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, bloodGroups);
        spBlood.setAdapter(bloodAdapter);
        spBlood.setText(tvBlood.getText().toString(), false);

        // Setup City Adapter (Talukas)
        String[] cities = {"Akola", "Akot", "Telhara", "Balapur", "Patur", "Murtizapur", "Barshitakli"};
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, cities);
        spCity.setAdapter(cityAdapter);
        spCity.setText(tvCity.getText().toString(), false);

        AlertDialog dialog = new MaterialAlertDialogBuilder(getContext())
                .setView(dialogView)
                .setCancelable(true)
                .create();

        dialogView.findViewById(R.id.btn_save).setOnClickListener(v -> {
            String name = etName.getText().toString();
            String phone = etPhone.getText().toString();
            String location = etLocation.getText().toString();
            String blood = spBlood.getText().toString();
            String city = spCity.getText().toString();
            
            updateProfile(name, phone, location, blood, city);
            dialog.dismiss();
        });

        dialogView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateProfile(String name, String phone, String location, String blood, String city) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("name", name);
        params.put("phone", phone);
        params.put("location", location);
        params.put("blood_group", blood);
        params.put("city", city);

        client.post(Urls.UPDATE_PROFILE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                loadProfile();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Update Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == getActivity().RESULT_OK && data != null) {
            Uri sourceUri = data.getData();
            File tempFile = new File(getContext().getCacheDir(), "profile_crop.jpg");
            UCrop.of(sourceUri, Uri.fromFile(tempFile)).withAspectRatio(1, 1).start(getContext(), this);
        } else if (resultCode == getActivity().RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imageUri = UCrop.getOutput(data);
            imgProfile.setImageURI(imageUri);
            uploadImage();
        }
    }

    private void useDefaultAvatar() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("image_type", "default"); // Tell PHP to set it to 'default' string
        
        client.post(Urls.UPLOAD_IMAGE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), "Default Avatar Applied!", Toast.LENGTH_SHORT).show();
                loadProfile(); // Refresh UI
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removePhoto() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("image_type", "remove"); // Flag to clear the field in DB
        
        client.post(Urls.UPLOAD_IMAGE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), "Photo Removed", Toast.LENGTH_SHORT).show();
                loadProfile(); // Refresh UI and recalculate progress
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Failed to remove photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImage() {
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();
            params.put("email", email);
            params.put("image", getContext().getContentResolver().openInputStream(imageUri), "profile.jpg");
            client.post(Urls.UPLOAD_IMAGE, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Toast.makeText(getContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
                    loadProfile();
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
            });
        } catch (Exception e) { e.printStackTrace(); }
    }
}
