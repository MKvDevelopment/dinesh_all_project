package com.socialmediasaver.status.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.InAppPurchaseExampleActivity;
import com.socialmediasaver.status.model.SliderModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    private final ArrayList<SliderModel> models;
    Activity activity;

    public SliderAdapter(ArrayList<SliderModel> models, Activity activity) {
        this.models = models;
        this.activity = activity;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout_item, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Picasso.with(activity).load(models.get(position).getSlider()).into(viewHolder.imageView);

        viewHolder.imageView.setOnClickListener(view -> {

            if (!models.get(position).getUri_link().equals("")) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(models.get(position).getUri_link()));
                view.getContext().startActivity(browserIntent);

                FirebaseFirestore.getInstance()
                        .collection("App_utils")
                        .document("clicks")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String total = Objects.requireNonNull(Objects.requireNonNull(task.getResult()).get(String.valueOf(position))).toString();

                                int totalee = Integer.parseInt(total) + 1;
                                FirebaseFirestore.getInstance()
                                        .collection("App_utils")
                                        .document("clicks")
                                        .update(String.valueOf(position), String.valueOf(totalee));
                            }
                        });

            }else {
                view.getContext().startActivity(new Intent(view.getContext(), InAppPurchaseExampleActivity.class));
            }
        });
    }

    @Override
    public int getCount() {
        return models.size();
    }

    static class Holder extends SliderViewAdapter.ViewHolder {
        private final ImageView imageView;

        public Holder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_auto_image_slider);
        }
    }
}
