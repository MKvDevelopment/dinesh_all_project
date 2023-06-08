package com.dinesh.adminwrokfromhome.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinesh.adminwrokfromhome.Models.AllPlanDataModel;
import com.dinesh.adminwrokfromhome.Activities.PlanDetailActivity;
import com.dinesh.adminwrokfromhome.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AllPlanAdapter extends FirestoreRecyclerAdapter<AllPlanDataModel, AllPlanAdapter.ViewHolder> {

    public AllPlanAdapter(@NonNull FirestoreRecyclerOptions<AllPlanDataModel> options) {
        super(options);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.update_recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final AllPlanDataModel model) {

        holder.name.setText(model.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlanDetailActivity.class);
                intent.putExtra("name", model.getName());
                v.getContext().startActivity(intent);
            }
        });
    }

    public void deleteItems(int position)
    {
       getSnapshots().getSnapshot(position).getReference().delete();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.plan_detail);

        }
    }
}
