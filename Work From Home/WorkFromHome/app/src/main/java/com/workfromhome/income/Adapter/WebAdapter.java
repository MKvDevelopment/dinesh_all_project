package com.workfromhome.income.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.workfromhome.income.Activity.WebDataActivity;
import com.workfromhome.income.Model.WebModel;
import com.workfromhome.income.R;

import java.util.ArrayList;
import java.util.List;


public class WebAdapter extends RecyclerView.Adapter<WebAdapter.ViewHolder> {


    private final WebDataActivity.RecyclerViewItemClick clickInterface;
    private final List<WebModel> fix_list;

    public WebAdapter(ArrayList<WebModel> fix_list, WebDataActivity.RecyclerViewItemClick clickInterface) {
        this.clickInterface = clickInterface;
        this.fix_list = fix_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.simple_web_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
           holder.bind(fix_list.get(position));
    }

    @Override
    public int getItemCount() {
        return fix_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title, income;
        private ImageView taskDoneImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskDoneImage = itemView.findViewById(R.id.ic_check);
            title = itemView.findViewById(R.id.tv_web_title);
            income = itemView.findViewById(R.id.tv_webtime);
        }

        public void bind(final WebModel model) {
            if (model.isDone()) {
                itemView.setOnClickListener(v -> {
                    if (null != clickInterface) {
                        Toast.makeText(itemView.getContext(), "You have already done this work!", Toast.LENGTH_SHORT).show();
                    }
                });
                income.setVisibility(View.GONE);
                taskDoneImage.setVisibility(View.VISIBLE);
            } else {
                itemView.setOnClickListener(v -> {
                    if (null != clickInterface) {
                        clickInterface.onItemClickListner(model);
                    }
                });
                taskDoneImage.setVisibility(View.GONE);
                income.setText(model.getWeb_income());
                income.setBackgroundResource(R.drawable.oval_image_primary);
            }

            title.setText(model.getWeb_title());

        }

    }


}
