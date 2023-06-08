package com.skincarestudio.solution.Adapter;

import android.annotation.SuppressLint;
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
import com.skincarestudio.solution.Activty.ProductDetailActivity;
import com.skincarestudio.solution.Model.Product_item_model;
import com.skincarestudio.solution.R;
import com.squareup.picasso.Picasso;

public class GridProductViewAdapter extends FirestoreRecyclerAdapter<Product_item_model, GridProductViewAdapter.ViewHolder> {


    public GridProductViewAdapter(@NonNull FirestoreRecyclerOptions<Product_item_model> options) {
        super(options);
    }

    @NonNull
    @Override
    public GridProductViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_gird_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull GridProductViewAdapter.ViewHolder holder, final int position, @NonNull final Product_item_model model) {
        holder.title.setText(model.getProduct_title());
        holder.price.setText("Price:- " + model.getProduct_price() + " Rs.");
        Picasso.get().load(model.getProduct_img()).into(holder.imageView);
        holder.description.setText(model.getSb_title());


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ProductDetailActivity.class);
            intent.putExtra("title", model.getProduct_title());
            intent.putExtra("des", model.getProduct_des());
            intent.putExtra("link", model.getLink());
            intent.putExtra("sbtitle", model.getSb_title());
            intent.putExtra("position", String.valueOf(position));
            intent.putExtra("price", model.getProduct_price());
            intent.putExtra("img", model.getProduct_img());
            view.getContext().startActivity(intent);

        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView title,price,description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.h_s_product_title);
            description = itemView.findViewById(R.id.h_s_product_description);
            imageView = itemView.findViewById(R.id.h_s_product_image);
            price = itemView.findViewById(R.id.h_s_product_price);
        }
    }
}
