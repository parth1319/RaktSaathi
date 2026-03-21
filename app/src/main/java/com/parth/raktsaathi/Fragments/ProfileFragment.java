package com.parth.raktsaathi.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.parth.raktsaathi.R;

import java.io.IOException;

public class ProfileFragment extends Fragment {

    Uri ImagePath;
    Bitmap bitmap;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ImageView ivMyProfileEditProfile, ivMyProfileSelectedProfile;
    Button btnMyProfilSelectProfilePhoto, btnMyProfileDeleteAccount;
    TextView tvMyProfileUserName, tvMyProfileMobileNo, tvMyProfileEmailid, tvMyProfilebBloodGroup, tvMyProfileUserCity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        editor = preferences.edit();

        ivMyProfileEditProfile = view.findViewById(R.id.ivMyProfileEditProfile);
        ivMyProfileSelectedProfile = view.findViewById(R.id.ivMyProfileSelectedProfile);
        btnMyProfilSelectProfilePhoto = view.findViewById(R.id.btnMyProfilSelectProfilePhoto);
        btnMyProfileDeleteAccount = view.findViewById(R.id.btnMyProfileDeleteAccount);
        tvMyProfileUserName = view.findViewById(R.id.tvMyProfileUserName);
        tvMyProfileMobileNo = view.findViewById(R.id.tvMyProfileMobileNo);
        tvMyProfileEmailid = view.findViewById(R.id.tvMyProfileEmailid);
        tvMyProfilebBloodGroup = view.findViewById(R.id.tvMyProfilebBloodGroup);
        tvMyProfileUserCity = view.findViewById(R.id.tvMyProfileUserCity);

        tvMyProfileUserName.setText("Username:" + preferences.getString("userName", ""));
        tvMyProfileMobileNo.setText("Mobile No:" + preferences.getString("userMobileNo", ""));
        tvMyProfileEmailid.setText("Email Id:" + preferences.getString("userEmail", ""));
        tvMyProfilebBloodGroup.setText("Blood Group:" + preferences.getString("userBloodGroup", ""));
        tvMyProfileUserCity.setText("City:" + preferences.getString("userCity", ""));

        btnMyProfilSelectProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        return view;
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Photo"), 999);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 999 && resultCode == Activity.RESULT_OK && data != null) {

            ImagePath = data.getData();

            try {
                if (getActivity() != null) {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), ImagePath);
                    ivMyProfileSelectedProfile.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
