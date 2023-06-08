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
import com.skincarestudio.solution.Activty.TipDetailActivity;
import com.skincarestudio.solution.Model.WebDataModel;
import com.skincarestudio.solution.R;
import com.squareup.picasso.Picasso;

public class AddWebRecyclerAdapter extends FirestoreRecyclerAdapter<WebDataModel, AddWebRecyclerAdapter.ViewHolder> {


    public AddWebRecyclerAdapter(@NonNull FirestoreRecyclerOptions<WebDataModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item_view, parent, false);
        return new ViewHolder(view);

    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull final WebDataModel model) {

        holder.title.setText(model.getProduct_title());
        Picasso.get().load(model.getProduct_img()).into(holder.imageView);
        holder.description.setText(model.getSb_title());


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), TipDetailActivity.class);
            intent.putExtra("title", model.getProduct_title());
            intent.putExtra("des", model.getProduct_des());
            intent.putExtra("sbtitle", model.getSb_title());
            intent.putExtra("img", model.getProduct_img());
            view.getContext().startActivity(intent);
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView title;
        private final TextView description;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textView6);
            description = itemView.findViewById(R.id.textView7);
            imageView = itemView.findViewById(R.id.imageView3);

        }


    }
}
