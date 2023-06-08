package com.dinesh.adminwrokfromhome.Adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinesh.adminwrokfromhome.Activities.GetDataActivity;
import com.dinesh.adminwrokfromhome.Activities.UserDetailActivity;
import com.dinesh.adminwrokfromhome.Models.GetDataModel;
import com.dinesh.adminwrokfromhome.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class GetRecyclerviewAdapter extends FirestoreRecyclerAdapter<GetDataModel, GetRecyclerviewAdapter.ViewHolder> {


    public GetRecyclerviewAdapter(@NonNull FirestoreRecyclerOptions<GetDataModel> options) {
        super(options);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, final int position, @NonNull final GetDataModel model) {

        GetDataActivity.progressDialog.dismiss();
        holder.plan.setText(model.getPlan());
        holder.email.setText(model.getEmail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserDetailActivity.class);
                intent.putExtra("email", model.getEmail());
                v.getContext().startActivity(intent);
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView email, plan, wallet, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            email = itemView.findViewById(R.id.user_email);
            plan = itemView.findViewById(R.id.user_plan);
            time = itemView.findViewById(R.id.time_stamp);

        }
    }

}
