package com.socialmediasaver.status.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.databinding.ItemsWhatsappViewBinding;
import com.socialmediasaver.status.model.WhatsappStatusModel;

import java.util.ArrayList;

import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappShow;

public class WhatsappStatusAdapter extends RecyclerView.Adapter<WhatsappStatusAdapter.ViewHolder> {
    WhatsappDownloderAdapterListner listner;

    public void setListner(WhatsappDownloderAdapterListner listner) {
        this.listner = listner;
    }
    private Context context;
    private ArrayList<WhatsappStatusModel> fileArrayList;
    private LayoutInflater layoutInflater;
    public String SaveFilePath = RootDirectoryWhatsappShow+ "/";
    public WhatsappStatusAdapter(Context context, ArrayList<WhatsappStatusModel> files) {
        this.context = context;
        this.fileArrayList = files;
    }

    @NonNull
    @Override
    public WhatsappStatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(viewGroup.getContext());
        }
        return new ViewHolder(DataBindingUtil.inflate(layoutInflater, R.layout.items_whatsapp_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WhatsappStatusAdapter.ViewHolder viewHolder, int position) {
        WhatsappStatusModel fileItem = fileArrayList.get(position);
        if (fileItem.getUri().toString().endsWith(".mp4")){
            viewHolder.binding.ivPlay.setVisibility(View.VISIBLE);
        }else {
            viewHolder.binding.ivPlay.setVisibility(View.GONE);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Glide.with(context)
                    .load(fileItem.getUri())
                    .into(viewHolder.binding.pcw);
        }else {
            Glide.with(context)
                    .load(fileItem.getPath())
                    .into(viewHolder.binding.pcw);
        }

       viewHolder.binding.pcw.setOnClickListener(v -> {
                   listner.onItemClickListner(position);
       });

    }
    @Override
    public int getItemCount() {
        return fileArrayList == null ? 0 : fileArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemsWhatsappViewBinding binding;

        public ViewHolder(ItemsWhatsappViewBinding mbinding) {
            super(mbinding.getRoot());
            this.binding = mbinding;
        }
    }

    public interface WhatsappDownloderAdapterListner{
        void onItemClickListner(int position);
    }

}