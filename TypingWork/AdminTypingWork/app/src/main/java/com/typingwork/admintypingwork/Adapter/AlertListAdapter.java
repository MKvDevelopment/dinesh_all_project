package com.typingwork.admintypingwork.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;
import com.typingwork.admintypingwork.Model.AlertListModel;
import com.typingwork.admintypingwork.R;
import com.typingwork.admintypingwork.Utils.Constant;

public class AlertListAdapter extends FirestoreRecyclerAdapter<AlertListModel,AlertListAdapter.ViewHolder> {

    private AlertListAdapter.transactionAdapterListner listner;

    public AlertListAdapter(@NonNull FirestoreRecyclerOptions<AlertListModel> options) {
        super(options);
    }

    public void setListner(AlertListAdapter.transactionAdapterListner listner) {
        this.listner = listner;
    }


    @Override
    protected void onBindViewHolder(@NonNull AlertListAdapter.ViewHolder holder, int position, @NonNull AlertListModel model) {

        holder.tv_name.setText( model.getName());
        holder.tv_amount.setText("Rs. " + model.getWallet());
        holder.tv_email.setText(model.getEmail());
        holder.tv_time.setText(model.getTime());
        holder.tv_time.setText(Constant.getTimeAgo(Long.parseLong(model.getTime())));


       /* if (model.getPhoto().equals("No")){
            holder.imageView.setImageResource(R.mipmap.ic_logo);
        }else {
            Picasso.get().load(model.getPhoto()).into(holder.imageView);
        }
*/
        holder.itemView.setOnClickListener(v -> {
            listner.onClickListner(model, position);
        });
    }

    @NonNull
    @Override
    public AlertListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_alert_layout_item, parent, false);
        return new AlertListAdapter.ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_email, tv_name, tv_amount,tv_time;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_email = itemView.findViewById(R.id.textView9);
            tv_name = itemView.findViewById(R.id.textView8);
            tv_amount = itemView.findViewById(R.id.tv_wallet);
            tv_time = itemView.findViewById(R.id.tv_time);
            imageView = itemView.findViewById(R.id.img_user);

        }
    }

    public interface transactionAdapterListner {
        void onClickListner(AlertListModel model, int position);
    }

}
