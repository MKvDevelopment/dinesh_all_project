package com.skincarestudio.skincarestudioadmin.Activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageTask;
import com.skincarestudio.skincarestudioadmin.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditTipDetailActivity extends AppCompatActivity {

    private EditText title, subtitle, des;

    private StorageTask mUploadTask;
    private ProgressDialog progressDialog;
    private DocumentReference documentReference;

    public EditTipDetailActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tip_detail);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading data...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        final String tipId = getIntent().getStringExtra("id");
        String plan = getIntent().getStringExtra("plan");

        documentReference = FirebaseFirestore.getInstance().collection(plan).document(tipId);


        title = findViewById(R.id.editTextTextPersonName);
        subtitle = findViewById(R.id.editTextTextPersonName2);
        des = findViewById(R.id.editTextTextPersonName4);
        Button submit = findViewById(R.id.button3);
        ImageView imageView = findViewById(R.id.imageView3);

        Picasso.get().load(getIntent().getStringExtra("img")).into(imageView);
        title.setText(getIntent().getStringExtra("title"));
        subtitle.setText(getIntent().getStringExtra("sbtitle"));
        des.setText(getIntent().getStringExtra("des"));


        submit.setOnClickListener(view -> {
            if (mUploadTask != null && mUploadTask.isInProgress()) {
                Toast.makeText(EditTipDetailActivity.this, "previous upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                getNewData();
            }
        });


    }

    private void getNewData() {
        final String newTitle = title.getText().toString();
        final String newsubTitle = subtitle.getText().toString();
        final String newdes = des.getText().toString();

        final Map<String, Object> map = new HashMap<>();
        map.put("product_des", newdes);
        map.put("product_title", newTitle);
        map.put("sb_title", newsubTitle);
        documentReference
                .update(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(EditTipDetailActivity.this, "Tips Updated!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(EditTipDetailActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                });

    }

}