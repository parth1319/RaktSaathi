package com.parth.raktsaathi;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    Context context;
    List<RequestModel> list;

    public RequestAdapter(Context context, List<RequestModel> list) {
        this.context = context;
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvBlood, tvAddress, tvUnits;
        ImageButton btnCall;

        public ViewHolder(View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvBlood = itemView.findViewById(R.id.tvBlood);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvUnits = itemView.findViewById(R.id.tvUnits);
            btnCall = itemView.findViewById(R.id.btnCall);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RequestModel model = list.get(position);

        holder.tvName.setText(model.getName());
        holder.tvBlood.setText(model.getBlood_group());
        holder.tvUnits.setText("Required: " + model.getUnits() + " Units");
        holder.tvAddress.setText(model.getAddress());

        holder.btnCall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + model.getMobile()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
