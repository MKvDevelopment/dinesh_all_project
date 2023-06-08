package com.earnmoney.joinwithus.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import com.earnmoney.joinwithus.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class ContentActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        //waiting dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final WebView webVieww = findViewById(R.id.webView4);

        String name = getIntent().getExtras().getString("name");
        final String fileName = getIntent().getExtras().getString("file_name");

        Toolbar toolbar = findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("App_Utils").document(name);
        documentReference.addSnapshotListener((value, error) -> {
            progressDialog.dismiss();
            data = value.getString(fileName);
            webVieww.loadData(data, "text/html", null);
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}