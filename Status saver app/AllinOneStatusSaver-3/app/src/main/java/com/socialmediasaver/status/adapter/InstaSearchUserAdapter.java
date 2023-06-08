package com.socialmediasaver.status.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.InstaSearchUserActivity;
import com.socialmediasaver.status.activity.InstaSearchUserDetailsActivity;
import com.socialmediasaver.status.model.InstagramSearch.InstagramSearchModel;

import java.util.List;

public class InstaSearchUserAdapter extends RecyclerView.Adapter<InstaSearchUserAdapter.MyViewHolder> {
    List<InstagramSearchModel.User> users;
    InstaSearchUserActivity instaSearchUserActivity;
    public InstaSearchUserAdapter(InstaSearchUserActivity instaSearchUserActivity, List<InstagramSearchModel.User> users) {
        this.users= users;
        this.instaSearchUserActivity = instaSearchUserActivity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.insta_search_user_model, parent, false);
        MyViewHolder myVirewHolder = new MyViewHolder(view);
        return myVirewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.userName.setText(users.get(position).getUser().getUsername());

        holder.Name.setText(users.get(position).getUser().getFull_name());
        Glide.with(instaSearchUserActivity).load(users.get(position).getUser().getProzile_pic_url())
                .thumbnail(0.2f).into(holder.story_icon);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(instaSearchUserActivity, InstaSearchUserDetailsActivity.class);
                intent.putExtra("userName",users.get(position).getUser().getUsername());
                intent.putExtra("Name",users.get(position).getUser().getFull_name());
                instaSearchUserActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView userName,Name;
        ImageView story_icon;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.place);
            Name = itemView.findViewById(R.id.address);
            story_icon = itemView.findViewById(R.id.story_icon);
        }
    }
}
