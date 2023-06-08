package com.dinesh.adminwrokfromhome.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinesh.adminwrokfromhome.Models.WebDataModel;
import com.dinesh.adminwrokfromhome.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AddWebRecyclerAdapter extends FirestoreRecyclerAdapter<WebDataModel,AddWebRecyclerAdapter.ViewHolder> {


    public AddWebRecyclerAdapter(@NonNull FirestoreRecyclerOptions<WebDataModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull WebDataModel model) {

        holder.weblink.setText(model.getWeblink());

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.web_recycler_item, parent, false);
        return new ViewHolder(view);

    }

    public void deleteItems(int position)
    {
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView weblink;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            weblink=itemView.findViewById(R.id.textView3);
        }


    }
}
