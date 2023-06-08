package com.document.security.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.document.dbsecurity.R;
import com.document.security.MainActivity1;
import com.document.security.dilge.F_Edit_Diloge;
import com.document.security.model.F_Model;

import java.util.ArrayList;
import java.util.List;

public class F_Adapter extends RecyclerView.Adapter<F_Adapter.View_Holder> {
    List<String> key =new ArrayList<>();
    List<F_Model> data =new ArrayList<>();

    public F_Adapter(List<String> key, List<F_Model> data) {
        this.key = key;
        this.data = data;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.f_lyout,parent,false);
       return new View_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {
        PopupMenu popup = new PopupMenu(holder.itemView.getContext(), holder.menu);
          popup.getMenuInflater().inflate(R.menu.onlyedit, popup.getMenu());

        holder.textView.setText(data.get(position).getName());
        holder.textView2.setText(data.get(position).getDis());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(holder.itemView.getContext(), MainActivity1.class);
                intent.putExtra("key",key.get(position));
                intent.putExtra("key1",data.get(position).getType());
                holder.itemView.getContext().startActivity(intent);
            }
        });

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popup.setOnMenuItemClickListener(item -> {
                    switch (item.getItemId()){
                        case R.id.edit:
                            F_Edit_Diloge diloge=new F_Edit_Diloge(key.get(position),data.get(position));
                            diloge.show(((AppCompatActivity)holder.itemView.getContext()).getSupportFragmentManager(),"Edit");
                            break;
                    }

                    return true;
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return key.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {
        private TextView textView;
        private TextView textView2;
        private ImageView menu;

        public View_Holder(@NonNull View itemView) {
            super(itemView);

            textView = (TextView) itemView.findViewById(R.id.textView);
            textView2 = (TextView)itemView.findViewById(R.id.textView2);
            menu = (ImageView) itemView.findViewById(R.id.menu);

        }
    }
}
