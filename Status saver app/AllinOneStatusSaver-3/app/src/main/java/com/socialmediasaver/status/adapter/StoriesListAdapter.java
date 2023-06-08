package com.socialmediasaver.status.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.DownloadActivity;
import com.socialmediasaver.status.databinding.ItemsWhatsappViewBinding;
import com.socialmediasaver.status.model.story.ItemModel;

import java.util.ArrayList;

import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaStories;
import static com.socialmediasaver.status.util.Utils.startDownload;

public class StoriesListAdapter extends RecyclerView.Adapter<StoriesListAdapter.ViewHolder> {
    private Context context;
    public static ArrayList<ItemModel> storyItemModelList;

    public StoriesListAdapter(Context context, ArrayList<ItemModel> list) {
        this.context = context;
        this.storyItemModelList = list;
    }

    @NonNull
    @Override
    public StoriesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        return new ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.items_whatsapp_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesListAdapter.ViewHolder viewHolder, int position) {
        ItemModel itemModel = storyItemModelList.get(position);
        try {
            if (itemModel.getMedia_type()==2) {
                viewHolder.binding.ivPlay.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.ivPlay.setVisibility(View.GONE);
            }
            Glide.with(context)
                    .load(itemModel.getImage_versions2().getCandidates().get(0).getUrl())
                    .into(viewHolder.binding.pcw);

        }catch (Exception ex){
            ex.printStackTrace();
        }



          viewHolder.binding.pcw.setOnClickListener(v -> {
            if (itemModel.getMedia_type()==2)
            {
                Intent intent=new Intent(viewHolder.itemView.getContext(), DownloadActivity.class);
                intent.putExtra("url",itemModel.getVideo_versions().get(0).getUrl());
                intent.putExtra("type",String.valueOf(position));
                viewHolder.itemView.getContext().startActivity(intent);
            }else {
                Intent intentt=new Intent(viewHolder.itemView.getContext(), DownloadActivity.class);
                intentt.putExtra("url",itemModel.getImage_versions2().getCandidates().get(0).getUrl());
                intentt.putExtra("type",String.valueOf(position));
                viewHolder.itemView.getContext().startActivity(intentt);
            }
        });
        viewHolder.binding.tvDownload.setOnClickListener(v -> {
            if (itemModel.getMedia_type()==2) {
                startDownload(itemModel.getVideo_versions().get(0).getUrl(),
                        RootDirectoryInstaStories, context,"story_"+itemModel.getId()+".mp4" );
            }else {
                startDownload(itemModel.getImage_versions2().getCandidates().get(0).getUrl(),
                        RootDirectoryInstaStories, context, "story_"+itemModel.getId()+".png");
            }
        });


    }
    @Override
    public int getItemCount() {
        return storyItemModelList == null ? 0 : storyItemModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         ItemsWhatsappViewBinding binding;
        public ViewHolder(ItemsWhatsappViewBinding mbinding) {
            super(mbinding.getRoot());
            this.binding = mbinding;
        }
    }
}