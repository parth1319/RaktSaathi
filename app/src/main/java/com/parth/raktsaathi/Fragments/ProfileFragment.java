package com.parth.raktsaathi.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.loopj.android.http.*;
import com.parth.raktsaathi.*;
import com.parth.raktsaathi.R;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileFragment extends Fragment {

    ImageView imgProfile;
    TextView tvName, tvMobile, tvEmail, tvBlood, tvCity, btnLogout;
    Button btnEdit;

    String user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // 🔥 BIND
        imgProfile = view.findViewById(R.id.imgProfile);
        tvName = view.findViewById(R.id.tvName);
        tvMobile = view.findViewById(R.id.tvMobile);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvBlood = view.findViewById(R.id.tvBlood);
        tvCity = view.findViewById(R.id.tvCity);

        btnEdit = view.findViewById(R.id.btnEdit);
        btnLogout = view.findViewById(R.id.btnLogout);

        // 🔥 GET USER
        SharedPreferences sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        user = sp.getString("user_input","");

        // 🔥 LOAD PROFILE DATA
        loadProfile();

        // 🔥 EDIT PROFILE CLICK
        btnEdit.setOnClickListener(v -> openEditDialog());

        // 🔥 LOGOUT WITH POPUP
        btnLogout.setOnClickListener(v -> {

            new AlertDialog.Builder(getContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")

                    .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())

                    .setPositiveButton("Logout", (dialog, which) -> {

                        SharedPreferences sp1 = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                        sp1.edit().clear().apply();

                        Intent i = new Intent(getActivity(), IntroScreenActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    })
                    .show();
        });

        return view;
    }

    // 🔥 LOAD PROFILE FROM API
    private void loadProfile(){

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("user_input", user);

        client.post(Urls.Get_ProfileWebServiceAddress, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                try{
                    JSONObject obj = new JSONObject(new String(responseBody));

                    tvName.setText(obj.getString("name"));
                    tvMobile.setText("📞 " + obj.getString("mobile"));
                    tvEmail.setText("📧 " + obj.getString("email"));
                    tvBlood.setText("🩸 " + obj.getString("blood_group"));
                    tvCity.setText("📍 " + obj.getString("city"));

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

    // 🔥 EDIT PROFILE DIALOG
    private void openEditDialog(){

        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_edit_profile);

        EditText etName = dialog.findViewById(R.id.etName);
        EditText etMobile = dialog.findViewById(R.id.etMobile);
        Spinner spBlood = dialog.findViewById(R.id.spBlood);
        EditText etCity = dialog.findViewById(R.id.etCity);
        Button btnSave = dialog.findViewById(R.id.btnSave);

        // 🔥 BLOOD LIST
        String[] blood = {"A+","B+","O+","AB+","A-","B-","O-","AB-"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, blood);
        spBlood.setAdapter(adapter);

        // 🔥 AUTO FILL DATA
        etName.setText(tvName.getText().toString());

        // "📞 9876543210" → number extract
        String mobile = tvMobile.getText().toString().replace("📞 ", "");
        etMobile.setText(mobile);

        String city = tvCity.getText().toString().replace("📍 ", "");
        etCity.setText(city);

        String bloodValue = tvBlood.getText().toString().replace("🩸 ", "");

        // 🔥 SET SPINNER SELECTION
        for(int i=0;i<blood.length;i++){
            if(blood[i].equals(bloodValue)){
                spBlood.setSelection(i);
                break;
            }
        }

        // 🔥 SAVE
        btnSave.setOnClickListener(v -> {

            AsyncHttpClient client = new AsyncHttpClient();
            RequestParams params = new RequestParams();

            params.put("user_input", user);
            params.put("name", etName.getText().toString());
            params.put("mobile", etMobile.getText().toString());
            params.put("blood_group", spBlood.getSelectedItem().toString());
            params.put("city", etCity.getText().toString());

            client.post(Urls.Update_ProfileWebServiceAddress, params, new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                    Toast.makeText(getContext(),"Profile Updated",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                    loadProfile(); // 🔥 refresh
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getContext(),"Update Failed",Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}
