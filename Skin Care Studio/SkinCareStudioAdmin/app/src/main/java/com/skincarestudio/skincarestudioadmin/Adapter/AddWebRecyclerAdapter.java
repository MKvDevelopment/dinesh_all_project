package com.skincarestudio.skincarestudioadmin.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.skincarestudio.skincarestudioadmin.Activity.AddTipsActivity;
import com.skincarestudio.skincarestudioadmin.Activity.TipDetailActivity;
import com.skincarestudio.skincarestudioadmin.Model.WebDataModel;
import com.skincarestudio.skincarestudioadmin.R;
import com.squareup.picasso.Picasso;

public class AddWebRecyclerAdapter extends FirestoreRecyclerAdapter<WebDataModel, AddWebRecyclerAdapter.ViewHolder> {

    private OnItemClickListner listner;

    public AddWebRecyclerAdapter(@NonNull FirestoreRecyclerOptions<WebDataModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, final int position, @NonNull final WebDataModel model) {

        holder.title.setText(model.getProduct_title());
        Picasso.get().load(model.getProduct_img()).into(holder.imageView);
        holder.description.setText(model.getSb_title());


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position != RecyclerView.NO_POSITION&&listner!=null) {
                    listner.onItemClick(getSnapshots().getSnapshot(position),position);
                }
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_view, parent, false);
        return new ViewHolder(view);

    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title, description;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView);
            description = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }

    public interface OnItemClickListner {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }


    public void setOnItemClickListner(OnItemClickListner listner) {
        this.listner = listner;
    }
}
