package com.parth.raktsaathi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonorAdapter extends RecyclerView.Adapter<DonorAdapter.ViewHolder> {

    List<DonorModel> list;

    public DonorAdapter(List<DonorModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, blood, location;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            blood = itemView.findViewById(R.id.tv_blood);
            location = itemView.findViewById(R.id.tv_location);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_donor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DonorModel model = list.get(position);
        holder.name.setText(model.name);
        holder.blood.setText(model.blood_group);
        holder.location.setText(model.location);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
