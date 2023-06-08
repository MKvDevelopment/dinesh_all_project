package com.dinesh.adminwrokfromhome.Activities;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinesh.adminwrokfromhome.R;

public class FirebaseViewHolder extends RecyclerView.ViewHolder {
    TextView date;
    public FirebaseViewHolder(@NonNull View itemView) {
        super(itemView);
        date=itemView.findViewById(R.id.tv_date);
    }
}
