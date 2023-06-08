package com.typingwork.admintypingwork.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.typingwork.admintypingwork.Model.WithdrawModel;
import com.typingwork.admintypingwork.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class WithdrawListAdapter extends FirebaseRecyclerAdapter<WithdrawModel, WithdrawListAdapter.ViewHolder> {

    private transactionAdapterListner listner;

    public void setListner(transactionAdapterListner listner) {
        this.listner = listner;
    }

    public WithdrawListAdapter(@NonNull FirebaseRecyclerOptions<WithdrawModel> options) {
        super(options);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.withdraw_layout_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull WithdrawModel model) {

        if (model.getStatus().equals("Pay")){
            holder.btn_pay.setText("Pay");
            holder.btn_pay.setEnabled(true);
            holder.btn_pay.setBackgroundColor(holder.itemView.getResources().getColor(R.color.primary_more_dark));
            holder.btn_pay.setTextColor(holder.itemView.getResources().getColor(R.color.black));
        }else{
            holder.btn_pay.setText("Paid");
            holder.btn_pay.setBackgroundColor(holder.itemView.getResources().getColor(R.color.grey));
            holder.btn_pay.setTextColor(holder.itemView.getResources().getColor(R.color.white));
            holder.btn_pay.setEnabled(false);
        }
        holder.tv_upi.setText("UPI :- " + model.getUpi());
        holder.tv_amount.setText("Rs. " + model.getAmount());
        holder.tv_email.setText("Email:- " + model.getEmail());

        holder.btn_pay.setOnClickListener(v -> {
            listner.onClickListner(model, position);
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_email, tv_upi, tv_amount;
        private Button btn_pay;
        private ConstraintLayout c_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_email = itemView.findViewById(R.id.tv_email);
            tv_upi = itemView.findViewById(R.id.tv_upiId);
            tv_amount = itemView.findViewById(R.id.tv_amount);
            btn_pay = itemView.findViewById(R.id.appCompatButton);

        }
    }

    public interface transactionAdapterListner {
        void onClickListner(WithdrawModel model,int position);
    }
}
