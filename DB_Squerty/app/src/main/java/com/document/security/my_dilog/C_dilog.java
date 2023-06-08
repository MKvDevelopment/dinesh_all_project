package com.document.security.my_dilog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.document.dbsecurity.R;

public class C_dilog extends Dialog implements
        android.view.View.OnClickListener {

    public Activity c;
    private ProgressDialog dialog;

    public EditText editTextTextPersonName;
    public EditText editTextTextPersonName2;
    private RadioButton text;
    private RadioGroup rg;
    private Button selectFile;
    public Button submit;
    private Intent intent;
    public static int REQUSE_CODE = 0;

    public String key1;


    public C_dilog(Activity a, String key1) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.key1 = key1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.c_dilog);


        editTextTextPersonName = findViewById(R.id.editTextTextPersonName);
        editTextTextPersonName2 = findViewById(R.id.editTextTextPersonName2);
        RadioButton image = findViewById(R.id.image);
        RadioButton video = findViewById(R.id.video);
        RadioButton pdf = findViewById(R.id.pdf);
        text = findViewById(R.id.text);
        selectFile = findViewById(R.id.select_file);
        submit = findViewById(R.id.submit);
        rg = findViewById(R.id.rg);

        if (key1.equals("none")) {
            text.setChecked(true);
            get_File_Type();
            rg.setVisibility(View.VISIBLE);
        } else if (key1.equals("text")) {
            text.setChecked(true);
            get_File_Type();
            rg.setVisibility(View.GONE);

        } else {
            rg.setVisibility(View.GONE);
            switch (key1) {
                case "none":
                case "text":
                    editTextTextPersonName.setVisibility(View.VISIBLE);
                    editTextTextPersonName2.setVisibility(View.VISIBLE);

                    REQUSE_CODE = 0;
                    selectFile.setVisibility(View.GONE);
                    break;

                case "image":
                    REQUSE_CODE = 1;
                    editTextTextPersonName.setVisibility(View.GONE);
                    editTextTextPersonName2.setVisibility(View.GONE);
                    selectFile.setVisibility(View.VISIBLE);
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    selectFile.setVisibility(View.VISIBLE);

                    break;

                case "video":
                    REQUSE_CODE = 2;
                    editTextTextPersonName.setVisibility(View.VISIBLE);
                    editTextTextPersonName2.setVisibility(View.GONE);
                    selectFile.setVisibility(View.VISIBLE);
                    intent = new Intent();
                    intent.setType("video/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    break;

                case "pdf":
                    editTextTextPersonName.setVisibility(View.VISIBLE);
                    editTextTextPersonName2.setVisibility(View.GONE);
                    REQUSE_CODE = 3;
                    selectFile.setVisibility(View.VISIBLE);
                    intent = new Intent();
                    intent.setType("application/pdf");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    break;
            }

        }

        rg.setOnCheckedChangeListener((group, checkedId) -> get_File_Type());

        selectFile.setOnClickListener(v -> c.startActivityForResult(intent, REQUSE_CODE));

    }

    @SuppressLint("NonConstantResourceId")
    private void get_File_Type() {
        int a = rg.getCheckedRadioButtonId();
        switch (a) {
            case R.id.none:
                REQUSE_CODE = 0;
                selectFile.setVisibility(View.GONE);
                break;

            case R.id.image:
                REQUSE_CODE = 1;
                selectFile.setVisibility(View.VISIBLE);
                intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                selectFile.setVisibility(View.VISIBLE);

                break;

            case R.id.video:
                REQUSE_CODE = 2;
                selectFile.setVisibility(View.VISIBLE);
                intent = new Intent();

                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                break;

            case R.id.pdf:
                REQUSE_CODE = 3;
                selectFile.setVisibility(View.VISIBLE);
                intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                break;

        }

    }

    @Override
    public void onClick(View v) {

    }



}