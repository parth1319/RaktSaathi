package com.parth.raktsaathi.Fragments;

import android.app.AlertDialog;
import android.content.*;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.loopj.android.http.*;
import com.parth.raktsaathi.*;
import com.parth.raktsaathi.R;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import cz.msebera.android.httpclient.Header;

public class ProfileFragment extends Fragment {

    ImageView imgProfile;
    TextView tvName, tvMobile, tvEmail, tvBlood, tvCity, btnLogout, btnChangePhoto;
    Button btnEdit;

    String user;
    Bitmap bitmap;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        imgProfile = view.findViewById(R.id.imgProfile);
        btnChangePhoto = view.findViewById(R.id.btnChangePhoto);

        tvName = view.findViewById(R.id.tvName);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBlood = view.findViewById(R.id.tvBlood);
        tvCity = view.findViewById(R.id.tvCity);

        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnLogout);

        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        user = sp.getString("user_input","");

        loadProfile();

        // 🔥 CHANGE PHOTO
        btnChangePhoto.setOnClickListener(v -> pickImage());

        // 🔥 LOGOUT FIXED
        btnLogout.setOnClickListener(v -> {

            new AlertDialog.Builder(getContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")

                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())

                    .setPositiveButton("Logout", (dialog, which) -> {

                        SharedPreferences sp1 = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp1.edit();

                        editor.clear();
                        editor.commit(); // 🔥 important

                        Intent i = new Intent(getActivity(), IntroScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(i);
                        getActivity().finish();
                    })
                    .show();
        });

        return view;
    }

    // 🔥 LOAD PROFILE
    private void loadProfile(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("user_input", user);

        client.post(Urls.Get_ProfileWebServiceAddress, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try{
                    String res = new String(responseBody);

                    if(res.equals("not_found")) return;

                    JSONObject obj = new JSONObject(res);

                    tvName.setText(obj.getString("name"));
                    tvMobile.setText("📞 " + obj.getString("mobile"));
                    tvEmail.setText("📧 " + obj.getString("email"));
                    tvBlood.setText("🩸 " + obj.getString("blood_group"));
                    tvCity.setText("📍 " + obj.getString("city"));

                    String img = obj.optString("profile_image","");

                    if(!img.equals("")){
                        Glide.with(getContext())
                                .load(Urls.Get_ProfileWebServiceAddress + img)
                                .placeholder(R.drawable.rs_profilelogo)
                                .into(imgProfile);
                    }

                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"Error loading profile",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔥 PICK IMAGE
    private void pickImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,100);
    }

    @Override
    public void onActivityResult(int req, int res, Intent data){
        super.onActivityResult(req,res,data);

        if(req==100 && res==getActivity().RESULT_OK){
            try{
                Uri uri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),uri);

                imgProfile.setImageBitmap(bitmap);

                uploadImage();

            }catch(Exception e){ e.printStackTrace(); }
        }
    }

    // 🔥 UPLOAD IMAGE
    private void uploadImage(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();

        params.put("user_input", user);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,70,stream);

        String encoded = Base64.encodeToString(stream.toByteArray(),Base64.DEFAULT);

        params.put("image", encoded);

        client.post(Urls.UploadImageWebServiceAddress, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(),"Image Uploaded",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"Upload Failed",Toast.LENGTH_SHORT).show();
            }
        });
    }
}