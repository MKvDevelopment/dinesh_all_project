package admin.perfect.trader.Adatper;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import admin.perfect.trader.Activity.IntradayDetailActivity;
import admin.perfect.trader.Activity.PositionalDetailActivity;
import admin.perfect.trader.Activity.UserCommunityDetailActivity;
import admin.perfect.trader.Model.PositionalModel;
import admin.perfect.trader.R;

public class PositionalAdapter extends RecyclerView.Adapter<PositionalAdapter.ViewHolder> {

    private ArrayList<PositionalModel> list;
    private String data;

    public PositionalAdapter(ArrayList<PositionalModel> list,String data) {
        this.list = list;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.positional_call_item_display, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Date sdate = new Date(Long.parseLong(list.get(position).getsDate()));
        Date edate = new Date(Long.parseLong(list.get(position).geteDate()));
        DateFormat f1 = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");

        if (list.get(position).getStatus().contains("In Progress")){
            holder.tvEtime.setText("Completed On: In Progress");
        }else {
            holder.tvEtime.setText("Completed On: "+f1.format(edate));
        }

        holder.tvStime.setText("Start At: "+f1.format(sdate));

        holder.tv_cname.setText(list.get(position).getcName());
        holder.tv_t1.setText(list.get(position).getTarget1());
        holder.tv_t2.setText(list.get(position).getTarget2());
        holder.tv_stoplos.setText(list.get(position).getStoploss());
        if (list.get(position).getCallType().contains("buy")){
            holder.button_buy.setText("Intraday\nBUY");
            holder.button_buy.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(),R.color.green));
        }else {
            holder.button_buy.setText("Intraday\nSELL");
            holder.button_buy.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(),R.color.red));
        }

        if (!list.get(position).getT1().equals("")){
            holder.img_t1.setVisibility(View.VISIBLE);
        }
        if (!list.get(position).getT2().equals("")){
            holder.img_t2.setVisibility(View.VISIBLE);
        }
        if (!list.get(position).getSl().equals("")){
            holder.img_sl.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            Intent intent=new Intent(view.getContext(), PositionalDetailActivity.class);
            intent.putExtra("id",list.get(position).getUid());
            intent.putExtra("data",data);
            view.getContext().startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_cname, tv_t1, tv_t2, tv_stoplos,tvStime,tvEtime;
        private Button button_buy;
        private ImageView img_t1,img_t2,img_sl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_cname = itemView.findViewById(R.id.tv_comName);
            tv_t1 = itemView.findViewById(R.id.tv_target1);
            tv_t2 = itemView.findViewById(R.id.tv_target2);
            tv_stoplos = itemView.findViewById(R.id.tv_sl);
            img_t1 = itemView.findViewById(R.id.imageView6);
            img_t2 = itemView.findViewById(R.id.imageView7);
            img_sl = itemView.findViewById(R.id.imageView9);
            tvStime = itemView.findViewById(R.id.tv_stime);
            tvEtime = itemView.findViewById(R.id.tv_etime);
            button_buy = itemView.findViewById(R.id.appCompatButton3);
        }
    }
}
