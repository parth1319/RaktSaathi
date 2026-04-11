package com.parth.raktsaathi.Fragments;

import android.content.*;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.loopj.android.http.*;
import com.parth.raktsaathi.*;
import com.parth.raktsaathi.R;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    TextView tvName, tvMobile, tvEmail, tvBlood, tvCity, tvAddPhoto, tvPercent, tvProgress;
    TextView btnEditProfile, btnChangePass;
    MaterialButton btnLogout;

    // 🔥 NEW FIELDS
    LinearLayout btnSettingsToggle, btnAboutToggle;
    LinearLayout settingsContent;
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

        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        email = sp.getString("email", "");

        imgProfile = view.findViewById(R.id.imgProfile);
        tvAddPhoto = view.findViewById(R.id.tvAddPhoto);

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

        // 🔥 TOGGLE IDS
        btnSettingsToggle = view.findViewById(R.id.btnSettingsToggle);
        btnAboutToggle = view.findViewById(R.id.btnAboutToggle);
        settingsContent = view.findViewById(R.id.settingsContent);
        aboutContent = view.findViewById(R.id.aboutContent);
        arrowSettings = view.findViewById(R.id.arrowSettings);
        arrowAbout = view.findViewById(R.id.arrowAbout);
        switchDark = view.findViewById(R.id.switchDark);
        fabAddPhoto = view.findViewById(R.id.fabAddPhoto);

        // 🔥 SET SWITCH STATE
        int mode = sp.getInt("mode", 1); // 1 = Light, 2 = Dark
        switchDark.setChecked(mode == 2);

        tvEmail.setText(email);

        loadProfile();

        fabAddPhoto.setOnClickListener(v -> openGallery());
        imgProfile.setOnClickListener(v -> openGallery());

        btnLogout.setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(getContext())
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to sign out?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sp.edit().clear().apply();
                        startActivity(new Intent(getContext(), LoginActivity.class));
                        getActivity().finish();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        btnEditProfile.setOnClickListener(v -> showEditProfileDialog());

        btnChangePass.setOnClickListener(v ->
                startActivity(new Intent(getContext(), ChangePasswordActivity.class)));

        // 🔥 DARK MODE TOGGLE
        switchDark.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES);
                sp.edit().putInt("mode", 2).apply();
            } else {
                androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
                sp.edit().putInt("mode", 1).apply();
            }
        });

        // 🔥 TOGGLE CLICKS
        btnSettingsToggle.setOnClickListener(v -> {
            if(settingsContent.getVisibility() == View.GONE){
                settingsContent.setVisibility(View.VISIBLE);
                arrowSettings.setText("▲");
            } else {
                settingsContent.setVisibility(View.GONE);
                arrowSettings.setText("▼");
            }
        });

        btnAboutToggle.setOnClickListener(v -> {
            if(aboutContent.getVisibility() == View.GONE){
                aboutContent.setVisibility(View.VISIBLE);
                arrowAbout.setText("▲");
            } else {
                aboutContent.setVisibility(View.GONE);
                arrowAbout.setText("▼");
            }
        });

        return view;
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
                    JSONObject obj = new JSONObject(new String(responseBody));

                    String name = obj.optString("name", "");
                    String phone = obj.optString("phone", "");
                    String blood = obj.optString("blood_group", "");
                    String city = obj.optString("city", "");
                    String address = obj.optString("location", "");
                    String imagePath = obj.optString("profile_image", "");

                    // 🔥 NULL FIX
                    if(name.equals("null")) name = "";
                    if(phone.equals("null")) phone = "";
                    if(blood.equals("null")) blood = "";
                    if(city.equals("null")) city = "";
                    if(address.equals("null")) address = "";
                    if(imagePath.equals("null")) imagePath = "";

                    // 🔥 UI SET
                    tvName.setText(name.isEmpty() ? "User Name" : name);
                    tvMobile.setText("📞 " + (phone.isEmpty() ? "Not Added" : phone));
                    tvBlood.setText("🩸 " + (blood.isEmpty() ? "Not Added" : blood));

                    if (!address.isEmpty() && !city.isEmpty()) {
                        tvCity.setText("📍 " + address + ", " + city);
                    } else if (!address.isEmpty()) {
                        tvCity.setText("📍 " + address);
                    } else if (!city.isEmpty()) {
                        tvCity.setText("📍 " + city);
                    } else {
                        tvCity.setText("📍 Not Added");
                    }

                    // 🔥 IMAGE LOAD (PICASSO)
                    if(!imagePath.isEmpty()){
                        Picasso.get()
                                .load(Urls.BASE_URL + imagePath)
                                .placeholder(R.drawable.rs_profilelogo)
                                .error(R.drawable.rs_profilelogo)
                                .into(imgProfile);

                        tvAddPhoto.setText("Change Photo");
                    } else {
                        imgProfile.setImageResource(R.drawable.rs_profilelogo);
                        tvAddPhoto.setText("Add Profile Photo");
                    }

                    // 🔥 PROGRESS
                    int progress = 0;

                    if(!name.isEmpty()) progress += 20;
                    if(!phone.isEmpty()) progress += 20;
                    if(!email.isEmpty()) progress += 20;
                    if(!blood.isEmpty()) progress += 20;
                    if(!city.isEmpty()) progress += 20;

                    progressBar.setProgress(progress);
                    tvPercent.setText(progress + "%");

                    // 🔥 COLOR
                    if(progress < 40){
                        progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark)));
                    } else if(progress < 80){
                        progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(getContext(), android.R.color.holo_orange_dark)));
                    } else {
                        progressBar.setProgressTintList(android.content.res.ColorStateList.valueOf(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark)));
                    }

                    // 🔥 VERIFIED
                    if(progress >= 80){
                        imgVerified.setVisibility(View.VISIBLE);
                        imgVerified.setAlpha(0f);
                        imgVerified.animate().alpha(1f).setDuration(500);
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

    // 🔥 EDIT PROFILE DIALOG
    private void showEditProfileDialog() {
        android.app.Dialog dialog = new android.app.Dialog(getContext());
        dialog.setContentView(R.layout.dialog_edit_profile);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(true);

        EditText etName = dialog.findViewById(R.id.et_name);
        EditText etPhone = dialog.findViewById(R.id.et_phone);
        EditText etAddress = dialog.findViewById(R.id.et_address);
        Spinner spBlood = dialog.findViewById(R.id.sp_blood);
        Spinner spCity = dialog.findViewById(R.id.sp_city);
        Button btnSave = dialog.findViewById(R.id.btn_save);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);

        String[] bloodGroups = {"Select Blood Group", "A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-"};
        String[] cities = {
                "Select City",
                "Mumbai", "Pune", "Nagpur", "Thane", "Nashik",
                "Kalyan-Dombivli", "Vasai-Virar", "Pimpri-Chinchwad",
                "Aurangabad", "Navi Mumbai", "Solapur", "Mira-Bhayandar",
                "Bhiwandi", "Amravati", "Nanded", "Kolhapur", "Akola",
                "Ulhasnagar", "Sangli-Miraj-Kupwad", "Malegaon", "Jalgaon",
                "Dhule", "Ahmednagar", "Satara", "Chandrapur", "Parbhani",
                "Ichalkaranji", "Jalna", "Ambarnath", "Bhusawal", "Panvel",
                "Badlapur", "Beed", "Gondia", "Barshi", "Yavatmal",
                "Achalpur", "Osmanabad", "Nandurbar", "Wardha", "Udgir",
                "Hinganghat", "Other"
        };

        ArrayAdapter<String> bloodAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, bloodGroups);
        bloodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spBlood.setAdapter(bloodAdapter);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, cities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCity.setAdapter(cityAdapter);

        // Fetch Current Data
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("email", email);

        client.post(Urls.GET_PROFILE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject obj = new JSONObject(new String(responseBody));
                    etName.setText(obj.optString("name", ""));
                    etPhone.setText(obj.optString("phone", ""));
                    etAddress.setText(obj.optString("location", ""));

                    String blood = obj.optString("blood_group", "");
                    for (int i = 0; i < bloodGroups.length; i++) {
                        if (bloodGroups[i].equalsIgnoreCase(blood)) {
                            spBlood.setSelection(i);
                            break;
                        }
                    }

                    String city = obj.optString("city", "");
                    for (int i = 0; i < cities.length; i++) {
                        if (cities[i].equalsIgnoreCase(city)) {
                            spCity.setSelection(i);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String blood = spBlood.getSelectedItem().toString();
            String city = spCity.getSelectedItem().toString();

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty() || blood.equals("Select Blood Group") || city.equals("Select City")) {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            android.app.ProgressDialog pd = new android.app.ProgressDialog(getContext());
            pd.setMessage("Updating...");
            pd.show();

            RequestParams updateParams = new RequestParams();
            updateParams.put("email", email);
            updateParams.put("name", name);
            updateParams.put("phone", phone);
            updateParams.put("blood_group", blood);
            updateParams.put("city", city);
            updateParams.put("location", address); // address maps to 'location' in backend

            AsyncHttpClient updateClient = new AsyncHttpClient();
            updateClient.post(Urls.UPDATE_PROFILE, updateParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    pd.dismiss();
                    String res = new String(responseBody).trim();
                    if (res.equalsIgnoreCase("success")) {
                        Toast.makeText(getContext(), "Profile Updated ✅", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadProfile(); // Refresh Fragment UI
                    } else {
                        Toast.makeText(getContext(), "Failed: " + res, Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Server Error ❌", Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    // 🔥 GALLERY
    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101);
    }

    // 🔥 IMAGE RESULT
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101 && resultCode == getActivity().RESULT_OK && data != null){
            Uri sourceUri = data.getData();
            File tempFile = new File(getContext().getCacheDir(), "profile_crop.jpg");
            Uri destinationUri = Uri.fromFile(tempFile);

            UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(1000, 1000)
                    .start(getContext(), this);
        } else if (resultCode == getActivity().RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            imageUri = UCrop.getOutput(data);
            imgProfile.setImageURI(imageUri);
            uploadImage();
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
            Toast.makeText(getContext(), "Crop Error: " + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // 🔥 UPLOAD IMAGE
    private void uploadImage(){

        try{
            AsyncHttpClient client = new AsyncHttpClient();

            RequestParams params = new RequestParams();
            params.put("email", email);

            params.put("image",
                    getContext().getContentResolver().openInputStream(imageUri),
                    "profile.jpg");

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
}