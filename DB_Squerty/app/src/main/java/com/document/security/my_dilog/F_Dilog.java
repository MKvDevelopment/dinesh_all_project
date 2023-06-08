package com.document.security.my_dilog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.document.dbsecurity.R;
import com.document.security.model.F_Model;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;

public class F_Dilog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    private EditText editTextTextPersonName;
    private EditText editTextTextPersonName2;
    private Button button2;
    private   ProgressDialog  dialog;
    private RadioGroup rg;
    private RadioButton none;
    private RadioButton image;
    private RadioButton video;
    private RadioButton pdf;
    private RadioButton text;
    String key="";
    public F_Dilog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.f_diloge);


        rg = (RadioGroup) findViewById(R.id.rg);
        none = (RadioButton) findViewById(R.id.none);
        text = (RadioButton) findViewById(R.id.text);
        image = (RadioButton) findViewById(R.id.image);
        video = (RadioButton) findViewById(R.id.video);
        pdf = (RadioButton) findViewById(R.id.pdf);

        editTextTextPersonName = (EditText) findViewById(R.id.editTextTextPersonName);
        editTextTextPersonName2 = (EditText) findViewById(R.id.editTextTextPersonName2);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        none.setChecked(true);
        get_File_Type();
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                get_File_Type();
            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    private void get_File_Type() {
        int a= rg.getCheckedRadioButtonId();
        switch (a){
            case R.id.none:
              key="none";
                break;
            case R.id.text:
                key="text";
                break;
            case R.id.image:
                key="image";
                break;

            case R.id.video:
                key="video";
                break;

            case R.id.pdf:
                key="pdf";
                break;

        }

    }


    @Override
    public void onClick(View v) {

        String title=editTextTextPersonName.getText().toString();
        String dis=editTextTextPersonName2.getText().toString();
        if(title.isEmpty()){
            editTextTextPersonName.setError("Please Enter Something");
        }else if(dis.isEmpty()){
            editTextTextPersonName2.setError("Please Enter Something");
        }else {
            dialog = new ProgressDialog(c);
            dialog.setMessage("Please wait...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            F_Model f_model=new F_Model(title,dis,key);
            FirebaseDatabase.getInstance().getReference().child("main_data").push().setValue(f_model).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    dialog.dismiss();
                    Toast.makeText(c, "Data Entered", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(c, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}