package com.parth.raktsaathi;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

public class RequestBloodActivity extends AppCompatActivity {

    Spinner blood;
    EditText units, location;
    Button request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_blood);

        blood = findViewById(R.id.sp_blood);
        units = findViewById(R.id.et_units);
        location = findViewById(R.id.et_location);
        request = findViewById(R.id.btn_request);

        String[] bloodGroups = {"A+","A-","B+","B-","O+","O-","AB+","AB-"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, bloodGroups);

        blood.setAdapter(adapter);

        request.setOnClickListener(v -> sendRequest());
    }

    private void sendRequest() {

        String sBlood = blood.getSelectedItem().toString();
        String sUnits = units.getText().toString();
        String sLocation = location.getText().toString();

        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        params.put("user_id", "1");
        params.put("blood_group", sBlood);
        params.put("units", sUnits);
        params.put("location", sLocation);

        client.post(Urls.BloodRequestWebServiceAddress, params,
                new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Toast.makeText(RequestBloodActivity.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(RequestBloodActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}