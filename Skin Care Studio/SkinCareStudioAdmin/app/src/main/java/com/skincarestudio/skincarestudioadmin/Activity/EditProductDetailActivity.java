package com.skincarestudio.skincarestudioadmin.Activity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageTask;
import com.skincarestudio.skincarestudioadmin.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProductDetailActivity extends AppCompatActivity {

    private EditText title, sbtitle, price, link, des;
    private StorageTask mUploadTask;
    private ProgressDialog progressDialog;
    private DocumentReference documentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_detail);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        String id = getIntent().getStringExtra("id");
        documentReference = FirebaseFirestore.getInstance().collection("product_list").document(id);


        ImageView imageView = findViewById(R.id.imageView4);
        title = findViewById(R.id.editTextTextPersonName3);
        sbtitle = findViewById(R.id.editTextTextPersonName5);
        price = findViewById(R.id.editTextTextPersonName6);
        link = findViewById(R.id.editTextTextPersonName7);
        des = findViewById(R.id.editTextTextPersonName8);
        Button submit = findViewById(R.id.button4);


        Picasso.get().load(getIntent().getStringExtra("img")).into(imageView);
        title.setText(getIntent().getStringExtra("title"));
        sbtitle.setText(getIntent().getStringExtra("sbtitle"));
        price.setText(getIntent().getStringExtra("price"));
        link.setText(getIntent().getStringExtra("link"));
        des.setText(getIntent().getStringExtra("des"));

        submit.setOnClickListener(view -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(EditProductDetailActivity.this, "previous upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                getNewData();
            }
        });
    }

    private void getNewData() {
        String newTitle = title.getText().toString();
        String pric = price.getText().toString();
        String lnk = link.getText().toString();
        String newsubTitle = sbtitle.getText().toString();
        String newdes = des.getText().toString();


        Map<String, Object> map = new HashMap<>();
        map.put("product_des", newdes);
        map.put("product_price", pric);
        map.put("link", lnk);
        map.put("product_title", newTitle);
        map.put("sb_title", newsubTitle);

        documentReference
                .update(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(EditProductDetailActivity.this, "Product Updated!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditProductDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }


}