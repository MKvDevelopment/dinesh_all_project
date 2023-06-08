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
import com.typingwork.admintypingwork.Model.SearchListModel;
import com.typingwork.admintypingwork.R;
import com.typingwork.admintypingwork.Utils.Constant;

import java.util.ArrayList;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.ViewHolder> {

    private ArrayList<SearchListModel> list;
    private SearchListAdapter.searchListAdapterListner listAdapterListner;

    public SearchListAdapter(ArrayList<SearchListModel> list) {
        this.list = list;
    }

    public void setListAdapterListner(searchListAdapterListner listAdapterListner) {
        this.listAdapterListner = listAdapterListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wallet_alert_layout_item, parent, false);
        return new SearchListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(list.get(position).getName());
        holder.tv_amount.setText("Rs. " + list.get(position).getWallet());
        holder.tv_email.setText("Withdraw:- "+list.get(position).getWithdraw());
        holder.tv_time.setText("Wrong:- "+list.get(position).getWrong_entry()+",  Right:- "+list.get(position).getRight_entry());

        if (list.get(position).getPhoto().equals("")){
            //Picasso.get().load(list.get(position).getPhoto()).into(holder.imageView);
            holder.imageView.setImageResource(R.mipmap.ic_logo);

        }else {
            Picasso.get().load(list.get(position).getPhoto()).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(v -> {
            listAdapterListner.onClickListner(position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_email, tv_name, tv_amount, tv_time;
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

    public interface searchListAdapterListner {
        void onClickListner(int position);
    }

}
