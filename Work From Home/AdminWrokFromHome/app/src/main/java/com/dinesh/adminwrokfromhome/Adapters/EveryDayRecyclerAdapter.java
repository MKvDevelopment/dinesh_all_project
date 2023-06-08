package com.dinesh.adminwrokfromhome.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinesh.adminwrokfromhome.Models.EveryDayModel;
import com.dinesh.adminwrokfromhome.Models.Product_item_model;
import com.dinesh.adminwrokfromhome.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class EveryDayRecyclerAdapter extends FirestoreRecyclerAdapter<EveryDayModel, EveryDayRecyclerAdapter.ViewHolder> {

    private EveryDayRecyclerAdapter.OnItemClickListner listner;

    public EveryDayRecyclerAdapter(@NonNull FirestoreRecyclerOptions<EveryDayModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull EveryDayModel model) {

        holder.email.setText(model.getEmail());
        holder.startDate.setText(model.getPlan_start_date());
        holder.wallet.setText("Rs.: " + model.getWallet() + "/-");
        holder.viewpost.setText("Post:- "+model.getView_post());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position != RecyclerView.NO_POSITION && listner != null) {
                    listner.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_gird_item, parent, false);
        return new EveryDayRecyclerAdapter.ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView email, startDate, wallet, viewpost;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            viewpost = itemView.findViewById(R.id.textView29);
            wallet = itemView.findViewById(R.id.textView28);
            email = itemView.findViewById(R.id.textView22);
            startDate = itemView.findViewById(R.id.textView23);

        }
    }

    public interface OnItemClickListner {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }


    public void setOnItemClickListner(EveryDayRecyclerAdapter.OnItemClickListner listner) {
        this.listner = listner;
    }


}
