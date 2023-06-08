package com.socialmediasaver.status.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.SkuDetails;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.InAppPurchaseExampleActivity;
import com.socialmediasaver.status.interfaces.OnSubscriptionUpdated;

import java.util.List;

public class SubscriptionListAdapter extends RecyclerView.Adapter<SubscriptionListAdapter.MyViewHolder> {
    OnSubscriptionUpdated onSubscriptionUpdated;
    InAppPurchaseExampleActivity inAppPurchaseExampleActivity;
    List<SkuDetails> skuDetailsList;
    private int lastCheckedPosition = 0;
    private int row_index = 0;
    public SubscriptionListAdapter(InAppPurchaseExampleActivity inAppPurchaseExampleActivity, List<SkuDetails> skuDetailsList, OnSubscriptionUpdated onSubscriptionUpdated) {
        this.onSubscriptionUpdated = onSubscriptionUpdated;
        this.inAppPurchaseExampleActivity = inAppPurchaseExampleActivity;
        this.skuDetailsList = skuDetailsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscribe_items, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.subscription_title.setText(skuDetailsList.get(position).getTitle());
        holder.subscription_price.setText("Price : "+skuDetailsList.get(position).getPrice());
        holder.subscription_img.setChecked(position == lastCheckedPosition);
        if(row_index==position){
            holder.layout.setBackground(inAppPurchaseExampleActivity.getResources().getDrawable(R.drawable.rounded_button_shape));

        }else if (row_index==-1){
            holder.layout.setBackground(inAppPurchaseExampleActivity.getResources().getDrawable(R.drawable.rounded_button_shape));

        }
        else
        {
            holder.layout.setBackgroundColor(inAppPurchaseExampleActivity.getResources().getColor(R.color.white));

        }
        if (lastCheckedPosition==0) {
            row_index = 0;

            lastCheckedPosition = 0;

            //because of this blinking problem occurs so
            //i have a suggestion to add notifyDataSetChanged();
            //   notifyItemRangeChanged(0, list.length);//blink list problem
            onSubscriptionUpdated.onSubscribe(0);
            //notifyDataSetChanged();
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index=position;

                lastCheckedPosition = position;

                //because of this blinking problem occurs so
                //i have a suggestion to add notifyDataSetChanged();
                //   notifyItemRangeChanged(0, list.length);//blink list problem
                onSubscriptionUpdated.onSubscribe(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return skuDetailsList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView subscription_title,subscription_price;
        RadioButton subscription_img;
        RelativeLayout layout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            subscription_title = itemView.findViewById(R.id.subscription_title);
            subscription_price = itemView.findViewById(R.id.subscription_price);
            subscription_img = itemView.findViewById(R.id.subscription_img);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}
