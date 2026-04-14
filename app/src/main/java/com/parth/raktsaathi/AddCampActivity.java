package com.parth.raktsaathi;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.loopj.android.http.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class AddCampActivity extends AppCompatActivity {

    EditText etName, etLocation, etDate, etEmail;
    Button btnSubmit;
    ImageView ivCampPhoto;
    LinearLayout placeholderLayout;
    String encodedImage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_camp);

        etName = findViewById(R.id.etCampName);
        etLocation = findViewById(R.id.etLocation);
        etDate = findViewById(R.id.etDate);
        etEmail = findViewById(R.id.etEmail);
        
        // Fetch logged-in email and LOCK it
        android.content.SharedPreferences sp = getSharedPreferences("user", android.content.Context.MODE_PRIVATE);
        String userEmail = sp.getString("email", "");
        etEmail.setText(userEmail);
        etEmail.setEnabled(false); // User cannot change this
        etEmail.setFocusable(false);

        btnSubmit = findViewById(R.id.btnSubmit);
        ivCampPhoto = findViewById(R.id.ivCampPhoto);
        placeholderLayout = findViewById(R.id.placeholderLayout);
        CardView btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());
        etDate.setOnClickListener(v -> openDatePicker());
        
        btnSelectPhoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 100);
        });

        btnSubmit.setOnClickListener(v -> addCamp());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ivCampPhoto.setImageBitmap(bitmap);
                placeholderLayout.setVisibility(View.GONE);
                
                // Convert to Base64
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                byte[] imageBytes = baos.toByteArray();
                encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openDatePicker(){
        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        int m = calendar.get(Calendar.MONTH);
        int d = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(this, (view, year, month, day) -> {
            // Use String.format to ensure 2 digits for month and day (e.g. 2024-05-09)
            String date = String.format(java.util.Locale.getDefault(), "%d-%02d-%02d", year, (month + 1), day);
            etDate.setText(date);
        }, y, m, d);
        dp.show();
    }

    private void addCamp(){
        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String date = etDate.getText().toString().trim();

        if(name.isEmpty() || location.isEmpty() || date.isEmpty()){
            Toast.makeText(this,"Fill all required fields",Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("camp_name", name);
        params.put("location", location);
        params.put("camp_date", date);
        params.put("email", etEmail.getText().toString());
        params.put("photo", encodedImage); // Sending Base64 image

        client.post(Urls.ADD_CAMP, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String res = new String(responseBody);
                if(res.equalsIgnoreCase("success")){
                    Toast.makeText(AddCampActivity.this, "✅ Camp Registered Successfully", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddCampActivity.this, "Failed: " + res, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(AddCampActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}