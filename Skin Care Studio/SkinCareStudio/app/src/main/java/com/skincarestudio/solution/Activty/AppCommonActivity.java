package com.skincarestudio.solution.Activty;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skincarestudio.solution.R;

import java.util.Objects;

public class AppCommonActivity extends AppCompatActivity {

    // private WebView webVieww;
    private ProgressDialog progressDialog;
    private String data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_common);

        //waiting dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final WebView webVieww = findViewById(R.id.webView4);
        final String document_name = getIntent().getStringExtra("about");
        final String field_name = getIntent().getStringExtra("field");


        Toolbar toolbar = findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(document_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("App_Utils").document(document_name);
        documentReference.addSnapshotListener((value, error) -> {
            progressDialog.dismiss();
            assert value != null;
            data = value.getString(field_name);
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