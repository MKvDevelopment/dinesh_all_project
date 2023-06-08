package com.document.security.dilge;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import com.document.dbsecurity.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.document.security.model.F_Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class F_Edit_Diloge extends DialogFragment {
    private Toolbar toolbar;
    private EditText titel;
    private EditText dis;
    private Button update;
    private String key;
    private F_Model f_model;

    public F_Edit_Diloge(String s, F_Model f_model) {
        this.key=s;
        this.f_model=f_model;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
    }

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

        titel.setText(f_model.getName());
        dis.setText(f_model.getDis());

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mtitel=titel.getText().toString();
                String mdis=dis.getText().toString();
                if(!mdis.isEmpty()&& !mtitel.isEmpty()){

                    Map<String,Object> map=new HashMap<>();
                    map.put("name",mtitel);
                    map.put("dis",mdis);

                    FirebaseDatabase.getInstance().getReference("main_data").child(key).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                       dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        return myview;
    }

}
