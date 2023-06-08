package com.perfect.traders.Adatper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.perfect.traders.Constant.App_Utils;
import com.perfect.traders.Model.CommunityModel;
import com.perfect.traders.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.ViewHolder> {

    private ArrayList<CommunityModel> list;

    public CommunityAdapter(ArrayList<CommunityModel> list) {
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_recycle_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunityAdapter.ViewHolder holder, int position) {

        try {
            holder.time.setText(App_Utils.getTimeAgo(Long.parseLong(list.get(position).getTime())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.title.setText(list.get(position).getTitle());
        holder.desc.setText(list.get(position).getDes());
        Picasso.get().load(list.get(position).getImg()).placeholder(R.drawable.ic_launcher_foreground).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, desc, time;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.textView2);
            desc = itemView.findViewById(R.id.tv_desc);
            time = itemView.findViewById(R.id.textView3);
            imageView = itemView.findViewById(R.id.circleImageView);
        }
    }
}
