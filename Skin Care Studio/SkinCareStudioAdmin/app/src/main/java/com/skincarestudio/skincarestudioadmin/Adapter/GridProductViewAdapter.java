package com.skincarestudio.skincarestudioadmin.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.skincarestudio.skincarestudioadmin.Model.Product_item_model;
import com.skincarestudio.skincarestudioadmin.R;
import com.squareup.picasso.Picasso;

public class GridProductViewAdapter extends FirestoreRecyclerAdapter<Product_item_model, GridProductViewAdapter.ViewHolder> {
    private AddWebRecyclerAdapter.OnItemClickListner listner;

    public GridProductViewAdapter(@NonNull FirestoreRecyclerOptions<Product_item_model> options) {
        super(options);
    }

    @NonNull
    @Override
    public GridProductViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_gird_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull GridProductViewAdapter.ViewHolder holder, final int position, @NonNull final Product_item_model model) {
        holder.title.setText(model.getProduct_title());
        holder.price.setText("Price:- " + model.getProduct_price() + "/-");
        Picasso.get().load(model.getProduct_img()).into(holder.imageView);
        holder.sbTitle.setText(model.getSb_title());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (position != RecyclerView.NO_POSITION&&listner!=null) {
                    listner.onItemClick(getSnapshots().getSnapshot(position),position);
                }
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView title, price, sbTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.h_s_product_title);
            sbTitle = itemView.findViewById(R.id.h_s_product_description);
            imageView = itemView.findViewById(R.id.h_s_product_image);
            price = itemView.findViewById(R.id.h_s_product_price);
        }
    }

    public interface OnItemClickListner {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }


    public void setOnItemClickListner(AddWebRecyclerAdapter.OnItemClickListner listner) {
        this.listner = listner;
    }
}
