package com.wheel.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TransactionHistoryAdapter extends FirebaseRecyclerAdapter<HistoryModel, TransactionHistoryAdapter.ViewHolder> {

    private String color;
    private transactionAdapterListner listner;
    private String type;

    public void setListner(transactionAdapterListner listner, String type) {
        this.listner = listner;
        this.type = type;
    }

    public TransactionHistoryAdapter(String color, @NonNull FirebaseRecyclerOptions<HistoryModel> options) {
        super(options);
        this.color = color;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull HistoryModel model) {

        Date date = new Date(Long.parseLong(model.getTime()));
        DateFormat f1 = new SimpleDateFormat("dd-MM-yy");
        DateFormat f2 = new SimpleDateFormat("hh:mm a");

        if (color == "1") {
            holder.c_layout.setBackgroundResource(R.drawable.gredient_yellow_border);
        } else {
            holder.c_layout.setBackgroundResource(R.drawable.gredient_pink_border);
        }

        holder.tv_status.setText("Status:- " + model.getStatus());
        holder.tv_amount.setText("Amount:- Rs. " + model.getAmount());
        holder.tv_date.setText("Date:-" + f1.format(date));
        holder.tv_time.setText("Time:-" + f2.format(date));
        holder.tv_method.setVisibility(View.VISIBLE);

        if (model.getType().toLowerCase().contains("upi")){
            holder.tv_method.setText("UPI:-" + model.getMethod());
        }else {
            holder.tv_method.setText("Paytm:-" + model.getMethod());
        }

        holder.btn_pay.setOnClickListener(v -> {
            listner.onClickListner(1,model.getItem_id(), model.getUid(),type);

        });

        holder.btn_history.setOnClickListener(v -> {
            listner.onClickListner(2,model.getItem_id(),model.getUid(),type);

        });

    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_time, tv_date, tv_status, tv_amount;
        private TextView tv_method;
        private Button btn_pay,btn_history;
        private ConstraintLayout c_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            c_layout = itemView.findViewById(R.id.layout);
            tv_amount = itemView.findViewById(R.id.textView21);
            tv_time = itemView.findViewById(R.id.textView23);
            tv_date = itemView.findViewById(R.id.textView20);
            tv_status = itemView.findViewById(R.id.textView22);
            btn_pay = itemView.findViewById(R.id.button3);
            btn_history = itemView.findViewById(R.id.appCompatButton);

            tv_method = itemView.findViewById(R.id.textView24);
        }
    }

    public interface transactionAdapterListner {
        void onClickListner(int id,String item_id,String uid,String type);
    }
}
