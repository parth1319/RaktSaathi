package com.parth.raktsaathi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.IOException;

public class MyProfileActivity extends Activity {

    Uri ImagePath;
    Bitmap bitmap;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ImageView ivMyProfileEditProfile, ivMyProfileSelectedProfile;
    Button btnMyProfilSelectProfilePhoto, btnMyProfileDeleteAccount;
    TextView tvMyProfileUserName, tvMyProfileMobileNo, tvMyProfileEmailid, tvMyProfilebBloodGroup, tvMyProfileUserCity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myprofile);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        ivMyProfileEditProfile = findViewById(R.id.ivMyProfileEditProfile);
        ivMyProfileSelectedProfile = findViewById(R.id.ivMyProfileSelectedProfile);
        btnMyProfilSelectProfilePhoto = findViewById(R.id.btnMyProfilSelectProfilePhoto);
        btnMyProfileDeleteAccount = findViewById(R.id.btnMyProfileDeleteAccount);
        tvMyProfileUserName = findViewById(R.id.tvMyProfileUserName);
        tvMyProfileMobileNo = findViewById(R.id.tvMyProfileMobileNo);
        tvMyProfileEmailid = findViewById(R.id.tvMyProfileEmailid);
        tvMyProfilebBloodGroup = findViewById(R.id.tvMyProfilebBloodGroup);
        tvMyProfileUserCity = findViewById(R.id.tvMyProfileUserCity);


        tvMyProfileUserName.setText("Username:"+preferences.getString("userName", ""));
        tvMyProfileMobileNo.setText("Mobile No:"+preferences.getString("userMobileNo", ""));
        tvMyProfileEmailid.setText("Email Id:"+preferences.getString("userEmail", ""));
        tvMyProfilebBloodGroup.setText("Blood Group:"+preferences.getString("userBloodGroup", ""));
        tvMyProfileUserCity.setText("City:"+preferences.getString("userCity", ""));

        btnMyProfilSelectProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });
    }

    private void showImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Photo"), 999);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 999 && resultCode == RESULT_OK && data != null) {

            ImagePath=Uri.parse(data.getData().toString());
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),ImagePath);
                ivMyProfileSelectedProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}