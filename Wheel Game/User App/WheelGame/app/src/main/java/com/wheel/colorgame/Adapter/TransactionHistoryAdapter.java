package com.wheel.colorgame.Adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wheel.colorgame.Model.HistoryModel;
import com.wheel.colorgame.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionHistoryAdapter extends RecyclerView.Adapter<TransactionHistoryAdapter.ViewHolder> {

    private ArrayList<HistoryModel> list;
    private String color;

    public TransactionHistoryAdapter(String color, ArrayList<HistoryModel> list) {
        this.list = list;
        this.color = color;
    }

    @NonNull
    @Override
    public TransactionHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Date date = new Date(Long.parseLong(list.get(position).getTime()));
        DateFormat f1 = new SimpleDateFormat("dd/MM/yy");
        DateFormat f2 = new SimpleDateFormat("hh:mm a");

        if (color == "1") {
            holder.c_layout.setBackgroundResource(R.drawable.gredient_pink_border);
        } else {
            holder.c_layout.setBackgroundResource(R.drawable.gredient_yellow_border);
            holder.tv_charge.setVisibility(View.VISIBLE);
            holder.tv_method.setVisibility(View.VISIBLE);
        }

        holder.tv_status.setText("Status:- " + list.get(position).getStatus());
        holder.tv_amount.setText("Amount:- Rs. " + list.get(position).getAmount());
        holder.tv_date.setText("Date:-" + f1.format(date));
        holder.tv_time.setText("Time:-" + f2.format(date));

        holder.tv_charge.setText("Charge:-" + list.get(position).getCharge() + "%");
        holder.tv_method.setText("Paytm/UPI:-" + list.get(position).getMethod());

        if (list.get(position).getStatus().contains("Success")) {
            holder.tv_status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
        } else if (list.get(position).getStatus().contains("Pending")) {
            holder.tv_status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue));
        } else {
            holder.tv_status.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_time, tv_date, tv_status, tv_amount;
        private TextView tv_method, tv_charge;
        private ConstraintLayout c_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            c_layout = itemView.findViewById(R.id.layout);
            tv_amount = itemView.findViewById(R.id.textView21);
            tv_time = itemView.findViewById(R.id.textView23);
            tv_date = itemView.findViewById(R.id.textView20);
            tv_status = itemView.findViewById(R.id.textView22);

            tv_method = itemView.findViewById(R.id.textView25);
            tv_charge = itemView.findViewById(R.id.textView24);
        }
    }
}
