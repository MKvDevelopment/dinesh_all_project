package com.skincarestudio.solution.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.skincarestudio.solution.Activty.ProductVideoActivity;
import com.skincarestudio.solution.Model.Product_item_model;
import com.skincarestudio.solution.R;
import com.squareup.picasso.Picasso;

public class PurchedRecyclerAdapter extends FirestoreRecyclerAdapter<Product_item_model, PurchedRecyclerAdapter.ViewHolder> {

    public PurchedRecyclerAdapter(@NonNull FirestoreRecyclerOptions<Product_item_model> options) {
        super(options);
    }

    @NonNull
    @Override
    public PurchedRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.purched_recycler_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    protected void onBindViewHolder(@NonNull final PurchedRecyclerAdapter.ViewHolder holder, int position, @NonNull final Product_item_model model) {

        holder.title.setText(model.getProduct_title());
        holder.sbtitle.setText(model.getSb_title());
        Picasso.get().load(model.getProduct_img()).into(holder.productImage);

        holder.download_image.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProductVideoActivity.class);
            intent.putExtra("link",model.getLink());
            view.getContext().startActivity(intent);
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView productImage, download_image;
        TextView title, sbtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            productImage = itemView.findViewById(R.id.imageView6);
            download_image = itemView.findViewById(R.id.imageView5);
            title = itemView.findViewById(R.id.textView11);
            sbtitle = itemView.findViewById(R.id.textView12);
        }
    }
}