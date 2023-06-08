package admin.perfect.trader.Adatper;

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

import admin.perfect.trader.Model.TodayCallModel;
import admin.perfect.trader.R;

public class IntradayCallAdapter extends RecyclerView.Adapter<IntradayCallAdapter.ViewHolder> {

    private ArrayList<TodayCallModel> list;
    private IntradayCallAdapter.indradayAdapterListner listner;

    public IntradayCallAdapter(ArrayList<TodayCallModel> list) {
        this.list = list;
    }

    public void setListner(indradayAdapterListner listner) {
        this.listner = listner;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_data_item_display, parent, false);
        return new IntradayCallAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntradayCallAdapter.ViewHolder holder, int position) {

        Date date = new Date(Long.parseLong(list.get(position).getTime()));
        DateFormat f1 = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");


        holder.tvTime.setText(f1.format(date));
        holder.tv_cname.setText(list.get(position).getC_name());
        holder.tv_t1.setText(list.get(position).getTarget1());
        holder.tv_t2.setText(list.get(position).getTarget2());
        holder.tv_stoplos.setText(list.get(position).getStop_loss());
        if (list.get(position).getCall_type().contains("buy")) {
            holder.button_buy.setText("Intraday\nBUY");
            holder.button_buy.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.green));
        } else {
            holder.button_buy.setText("Intraday\nSELL");
            holder.button_buy.setBackgroundTintList(ContextCompat.getColorStateList(holder.itemView.getContext(), R.color.red));
        }

        if (!list.get(position).getT1().equals("")) {
            holder.img_t1.setVisibility(View.VISIBLE);
        }
        if (!list.get(position).getT2().equals("")) {
            holder.img_t2.setVisibility(View.VISIBLE);
        }
        if (!list.get(position).getSl().equals("")) {
            holder.img_sl.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(view -> {
            listner.setListner(position);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_cname, tv_t1, tv_t2, tv_stoplos, tvTime;
        private Button button_buy;
        private ImageView img_t1, img_t2, img_sl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_cname = itemView.findViewById(R.id.tv_comName);
            tv_t1 = itemView.findViewById(R.id.tv_target1);
            tv_t2 = itemView.findViewById(R.id.tv_target2);
            tv_stoplos = itemView.findViewById(R.id.tv_sl);
            img_t1 = itemView.findViewById(R.id.imageView6);
            img_t2 = itemView.findViewById(R.id.imageView7);
            img_sl = itemView.findViewById(R.id.imageView9);
            button_buy = itemView.findViewById(R.id.appCompatButton3);
            tvTime = itemView.findViewById(R.id.tvtime);

        }
    }

    public interface indradayAdapterListner {
        void setListner(int position);
    }
}
