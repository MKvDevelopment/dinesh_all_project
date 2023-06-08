package com.document.security.adapter;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.document.dbsecurity.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.document.security.dilge.Edit_diloge;
import com.document.security.model.C_Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.CLIPBOARD_SERVICE;

public class C_Adapter extends RecyclerView.Adapter<C_Adapter.View_Holder> {
    List<String> key = new ArrayList<>();
    List<C_Model> data = new ArrayList<>();
    String type;
    String mkey;

    public C_Adapter(List<String> key, List<C_Model> data, String type, String mkey) {
        this.key = key;
        this.data = data;
        this.type = type;
        this.mkey=mkey;
    }

    @NonNull
    @Override
    public View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.c_layout, parent, false);
        return new View_Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull View_Holder holder, int position) {


        holder.title.setVisibility(View.GONE);
        holder.dis.setVisibility(View.GONE);
        holder.title.setText(data.get(position).getTitle());
        holder.dis.setText(data.get(position).getDis());

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) holder.itemView.getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("edited", Objects.requireNonNull(holder.title.getText()).toString());
                Objects.requireNonNull(clipboard).setPrimaryClip(clip);
                Toast.makeText(holder.itemView.getContext(), "Successfully copied", Toast.LENGTH_SHORT).show();

            }
        });

        holder.title.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, holder.title.getText().toString());
                sendIntent.setType("text/plain");
                holder.itemView.getContext().startActivity(sendIntent);

                return true;
            }
        });


        holder.dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) holder.itemView.getContext().getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("edited", Objects.requireNonNull(holder.dis.getText()).toString());
                Objects.requireNonNull(clipboard).setPrimaryClip(clip);
                Toast.makeText(holder.itemView.getContext(), "Successfully copied", Toast.LENGTH_SHORT).show();

            }
        });

        holder.dis.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, holder.dis.getText().toString());
                sendIntent.setType("text/plain");
                holder.itemView.getContext().startActivity(sendIntent);

                return true;
            }
        });

        holder.shear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, data.get(position).getLink());
                sendIntent.setType("text/plain");
                holder.itemView.getContext().startActivity(sendIntent);
            }
        });


        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.downloadFile(data.get(position).getLink(), key.get(position));
            }
        });

        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = data.get(position).getLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                holder.itemView.getContext().startActivity(i);
            }
        });
        holder.videp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String url = data.get(position).getLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                holder.itemView.getContext().startActivity(i);
                return true;
            }
        });
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaController mediaController = new MediaController(holder.itemView.getContext());
                mediaController.setAnchorView(holder.videp);
                holder.videp.setMediaController(mediaController);
                holder.videp.setVideoPath(data.get(position).getLink());
                holder.videp.start();
            }
        });
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = data.get(position).getLink();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                holder.itemView.getContext().startActivity(i);
            }
        });
        String mkey = type;
        if (mkey.equals("none")) {
            String mmkey = data.get(position).getType();

            if (mmkey.equals("none") || mmkey.equals("text")) {
                holder.img.setVisibility(View.GONE);
                holder.videoLayout.setVisibility(View.GONE);
                holder.download.setVisibility(View.GONE);
                holder.shear.setVisibility(View.GONE);
                holder.title.setVisibility(View.VISIBLE);
                holder.dis.setVisibility(View.VISIBLE);
                holder.view.setVisibility(View.GONE);
            } else if (mmkey.equals("image")) {
                holder.title.setVisibility(View.GONE);
                holder.dis.setVisibility(View.GONE);
                holder.img.setVisibility(View.VISIBLE);
                Picasso.get().load(data.get(position).getLink()).into(holder.img);
                holder.videoLayout.setVisibility(View.GONE);
                holder.download.setVisibility(View.VISIBLE);
                holder.shear.setVisibility(View.VISIBLE);
                holder.view.setVisibility(View.GONE);
            } else if (mmkey.equals("video")) {
                holder.title.setVisibility(View.VISIBLE);
                holder.dis.setVisibility(View.GONE);
                holder.img.setVisibility(View.GONE);
                holder.videoLayout.setVisibility(View.VISIBLE);
                holder.download.setVisibility(View.VISIBLE);
                holder.shear.setVisibility(View.VISIBLE);
                holder.view.setVisibility(View.GONE);
            } else if (mmkey.equals("pdf")) {
                holder.view.setVisibility(View.VISIBLE);
                holder.title.setVisibility(View.VISIBLE);
                holder.dis.setVisibility(View.GONE);
                holder.img.setVisibility(View.GONE);
                holder.videoLayout.setVisibility(View.GONE);
                holder.download.setVisibility(View.GONE);
                holder.shear.setVisibility(View.VISIBLE);
            }


        }
        else if (mkey.equals("image")) {
            holder.title.setVisibility(View.GONE);
            holder.dis.setVisibility(View.GONE);
            holder.img.setVisibility(View.VISIBLE);
            Picasso.get().load(data.get(position).getLink()).into(holder.img);
            holder.videoLayout.setVisibility(View.GONE);
            holder.download.setVisibility(View.GONE);
            holder.shear.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);
        }
        else if (mkey.equals("video")) {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setTextSize(18);
            holder.dis.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.VISIBLE);
            holder.download.setVisibility(View.GONE);
            holder.shear.setVisibility(View.VISIBLE);
            holder.view.setVisibility(View.GONE);
        }
        else if (mkey.equals("pdf")) {
            holder.title.setVisibility(View.VISIBLE);
            holder.dis.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.GONE);
            holder.download.setVisibility(View.GONE);
            holder.shear.setVisibility(View.VISIBLE);
            holder.view.setVisibility(View.VISIBLE);
        }
        else if (mkey.equals("text")) {

            holder.title.setVisibility(View.VISIBLE);
            holder.dis.setVisibility(View.VISIBLE);
            holder.img.setVisibility(View.GONE);
            holder.videoLayout.setVisibility(View.GONE);
            holder.download.setVisibility(View.GONE);
            holder.shear.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);

        }


        PopupMenu popup = new PopupMenu(holder.itemView.getContext(), holder.menu);
        if(data.get(position).getType().equals("image")){
            popup.getMenuInflater().inflate(R.menu.cmenuonlyedit, popup.getMenu());
        }else {
            popup.getMenuInflater().inflate(R.menu.cmenu, popup.getMenu());
        }

        holder.menu.setOnClickListener(v -> {
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.edit:
                        Edit_diloge diloge=new Edit_diloge(this.mkey,key.get(position),data.get(position).getType(),data.get(position));
                        diloge.show(((AppCompatActivity)holder.itemView.getContext()).getSupportFragmentManager(),"Edit");
                        break;
                    case R.id.delete:
                        holder.delete(this.mkey,key.get(position),data.get(position).getType());
                        break;
                }

                return true;
            });

            popup.show();//showing popup menu
        });


    }

    @Override
    public int getItemCount() {
        return key.size();
    }

    public class View_Holder extends RecyclerView.ViewHolder {
        private ProgressDialog dialog;
        private TextView title;
        private TextView dis;
        private ImageView img;
        private LinearLayout videoLayout;
        private VideoView videp;
        private Button play;
        private Button download;
        private Button shear;
        private Button view;
        private ImageView menu;

        public View_Holder(@NonNull View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.title);
            dis = (TextView) itemView.findViewById(R.id.dis);
            img = (ImageView) itemView.findViewById(R.id.img);
            videoLayout = (LinearLayout) itemView.findViewById(R.id.video_layout);
            videp = (VideoView) itemView.findViewById(R.id.videp);
            play = (Button) itemView.findViewById(R.id.play);
            download = (Button) itemView.findViewById(R.id.download);
            shear = (Button) itemView.findViewById(R.id.shear);
            view = (Button) itemView.findViewById(R.id.view);
            menu = (ImageView) itemView.findViewById(R.id.menu);
        }

        private void downloadFile(String link, String name) {
            dialog = new ProgressDialog(itemView.getContext());
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReferenceFromUrl(link);
            StorageReference islandRef = storageRef.child(name);

            File rootPath = new File(Environment.getExternalStorageDirectory(), "DB_doc");
            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }

            final File localFile = new File(rootPath, name);

            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.e("firebase ", ";local tem file created  created " + localFile.toString());
                    //  updateDb(timestamp,localFile.toString(),position);
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    dialog.dismiss();
                    Log.e("firebase ", ";local tem file not created  created " + exception.toString());
                }
            });
        }

        public void delete(String mkey, String s, String type) {
            DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("child_data").child(mkey).child(s);
            ProgressDialog dialog=new ProgressDialog(itemView.getContext());
            dialog.setMessage("Please Wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            if(type.equals("text")){
                reference.removeValue().addOnSuccessListener(aVoid -> {Toast.makeText(itemView.getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                dialog.dismiss();}).addOnFailureListener(e -> {Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();});
            }else {
                FirebaseStorage.getInstance().getReference().child("post").child(type).child(s).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        reference.removeValue().addOnSuccessListener(aVoid1 -> {Toast.makeText(itemView.getContext(), "Delete Success", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();}).addOnFailureListener(e -> {Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();});
                    }
                }).addOnFailureListener(e -> {Toast.makeText(itemView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();});
            }

        }
    }
}
