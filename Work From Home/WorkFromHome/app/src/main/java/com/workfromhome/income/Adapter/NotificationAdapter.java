package com.workfromhome.income.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.workfromhome.income.Model.Notification;
import com.workfromhome.income.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final List<Notification> notifications;
    private final Activity activity;

    public NotificationAdapter(List<Notification> notifications, Activity activity) {
        this.notifications = notifications;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final TextView title;
        private final TextView date;
        private final TextView description;
        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            title = view.findViewById(R.id.noti_title);
            date = view.findViewById(R.id.noti_date);
            description = view.findViewById(R.id.noti_description);
        }
        public void bind(final Notification notification){
            description.setText(notification.getDescription());
            title.setText(notification.getTitle());
            date.setText(notification.getDate());
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FancyGifDialog build = new FancyGifDialog.Builder(activity)
                            .setTitle(notification.getTitle())
                            .setMessage(notification.getDescription())
                            .setPositiveBtnBackground("#ff0109")
                            .setPositiveBtnText("Ok")
                            .isCancellable(false)
                            .setGifResource(R.drawable.trophy)
                            .OnPositiveClicked(new FancyGifDialogListener() {
                                @Override
                                public void OnClick() {

                                }
                            })
                            .setNegativeBtnText("Cancel")
                            .OnNegativeClicked(new FancyGifDialogListener() {
                                @Override
                                public void OnClick() {
                                }
                            }).build();
                }
            });
        }

    }
}
