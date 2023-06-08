package com.document.security.dilge;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.document.dbsecurity.R;
import com.google.firebase.database.FirebaseDatabase;
import com.document.security.model.C_Model;

import java.util.HashMap;
import java.util.Objects;

public class Edit_diloge extends DialogFragment {

    private static final int GELLRY_PIC = 1;
    private Toolbar toolbar;
    private EditText titel;
    private EditText dis;
    private Button update;
    private String mkey;
    private String s;
    private String type;
    private C_Model c_model;

    public Edit_diloge(String mkey, String s, String type, C_Model c_model) {
        this.mkey = mkey;
        this.s = s;
        this.type = type;
        this.c_model = c_model;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            Objects.requireNonNull(dialog.getWindow()).setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.AppTheme_Slide);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View myview = inflater.inflate(R.layout.edit_product, container, false);


        titel = (EditText) myview.findViewById(R.id.titel);
        dis = (EditText) myview.findViewById(R.id.dis);
        update = (Button) myview.findViewById(R.id.update);

        toolbar = myview.findViewById(R.id.toolbar);
        toolbar.setTitle("Edit");

        if(c_model.getType().equals("text")){
            dis.setVisibility(View.VISIBLE);
        }else {
            dis.setVisibility(View.GONE);
        }

        titel.setText(c_model.getTitle());
        dis.setText(c_model.getDis());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                dismiss();
                return true;
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mtitel=titel.getText().toString();
                String mdis=dis.getText().toString();

                if(mtitel.equals(c_model.getTitle()) && mdis.equals(c_model.getDis())){
                    dismiss();
                }else {
                if(mtitel.isEmpty()){
                    titel.setError("Enter Title Please");
                }else if(mdis.isEmpty()){
                    dis.setError("Enter Description Please");
                }else {
                    ProgressDialog dialog=new ProgressDialog(getContext());
                    dialog.setMessage("Please Wait...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("title",mtitel);
                    map.put("dis",mdis);
                    FirebaseDatabase.getInstance().getReference("child_data").child(mkey).child(s).updateChildren(map).addOnSuccessListener(aVoid -> {
                        dialog.dismiss();
                        Toast.makeText(getContext(),"Update Successful", Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
                }


                }
            }
        });

        return myview;
    }
}