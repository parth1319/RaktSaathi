package com.parth.raktsaathi;

import android.content.*;
import android.net.Uri;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.MyViewHolder> {

    Context context;
    List<RequestModel> list;

    public RequestAdapter(Context context, List<RequestModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, blood, units, address;
        Button call;

        public MyViewHolder(@NonNull View v) {
            super(v);

            name = v.findViewById(R.id.tvName);
            blood = v.findViewById(R.id.tvBlood);
            units = v.findViewById(R.id.tvUnits);
            address = v.findViewById(R.id.tvAddress);
            call = v.findViewById(R.id.btnCall);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        RequestModel m = list.get(position);

        holder.name.setText(m.getName());
        holder.blood.setText("Blood: " + m.getBlood_group());
        holder.units.setText("Units: " + m.getUnits());
        holder.address.setText(m.getAddress() + ", " + m.getDistrict());

        holder.call.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_DIAL);
            i.setData(Uri.parse("tel:" + m.getMobile()));
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}