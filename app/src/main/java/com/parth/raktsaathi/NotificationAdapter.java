package com.parth.raktsaathi;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<NotificationModel> notificationList;

    public NotificationAdapter(List<NotificationModel> notificationList) {
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel model = notificationList.get(position);

        holder.tvTitle.setText(model.getTitle());
        holder.tvMessage.setText(model.getMessage());
        holder.tvTime.setText(model.getTime());


        if ("URGENT".equalsIgnoreCase(model.getType())) {
            holder.ivIcon.setImageResource(R.drawable.rs_notification);
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.rs_buttons_colour));
        } else if ("CAMP".equalsIgnoreCase(model.getType())) {
            holder.ivIcon.setImageResource(R.drawable.rs_notification);
            holder.ivIcon.setColorFilter(holder.itemView.getContext().getResources().getColor(R.color.blue_600));
        }


        holder.unreadIndicator.setVisibility(model.isRead() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvMessage, tvTime;
        ImageView ivIcon;
        View unreadIndicator;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvTime = itemView.findViewById(R.id.tvNotifTime);
            ivIcon = itemView.findViewById(R.id.ivNotifIcon);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
        }
    }
}
