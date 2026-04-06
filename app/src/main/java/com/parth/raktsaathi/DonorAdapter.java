package com.parth.raktsaathi;

import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;

import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    List<DonorModel> list;

    public DonorAdapter(List<DonorModel> list){
        this.list = list;
    }

    // 🔥 VIEW HOLDER
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, blood, location, address;
        Button btnCall;

        public ViewHolder(View v){
            super(v);

            name = v.findViewById(R.id.tvName);
            blood = v.findViewById(R.id.tvBlood);
            location = v.findViewById(R.id.tvLocation);
            address = v.findViewById(R.id.tvAddress);
            btnCall = v.findViewById(R.id.btnCall);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donor, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        DonorModel d = list.get(position);

        // 🔥 SET DATA
        h.name.setText(d.getName());
        h.blood.setText("🩸 " + d.getBlood_group());
        h.location.setText("📍 " + d.getCity() + ", " + d.getDistrict());
        h.address.setText("🏠 " + d.getAddress());

        // 🔥 CALL BUTTON
        h.btnCall.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + d.getMobile()));
            v.getContext().startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}