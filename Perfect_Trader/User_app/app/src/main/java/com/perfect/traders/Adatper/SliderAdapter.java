package com.perfect.traders.Adatper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.perfect.traders.Model.LinkModel;
import com.perfect.traders.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class SliderAdapter extends SliderViewAdapter<SliderAdapter.Holder> {

    //int[] images;
    private final ArrayList<LinkModel> models;

    public SliderAdapter(ArrayList<LinkModel>images) {
        this.models = images;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_layout_item,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder viewHolder, int position) {
        Picasso.get().load(models.get(position).getLink()).into(viewHolder.imageView);
        viewHolder.imageView.setOnClickListener(view -> {
           // Toast.makeText(viewHolder.itemView.getContext(), "" +position, Toast.LENGTH_SHORT).show();

        });
    }

    @Override
    public int getCount() {
        return models.size();
    }

    public class Holder extends ViewHolder{
        private final ImageView imageView;
        public Holder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.iv_auto_image_slider);
        }
    }
}
