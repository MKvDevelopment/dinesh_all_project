package com.skincarestudio.solution.Activty;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.skincarestudio.solution.Adapter.AddWebRecyclerAdapter;
import com.skincarestudio.solution.Model.WebDataModel;
import com.skincarestudio.solution.R;
import com.skincarestudio.solution.Utils.NetworkChangeReceiver;

import java.util.Objects;

public class HairTipActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private AddWebRecyclerAdapter adapter;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_tip);

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        Toolbar toolbar=findViewById(R.id.hairTipRecyclerView_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Hair Tips");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.hairTipRecyclerView);
        progressDialog = new ProgressDialog(this);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("Hair Tips");

        setUpRecyclerView();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {

        Query query = collectionReference;
        FirestoreRecyclerOptions<WebDataModel> options = new FirestoreRecyclerOptions
                .Builder<WebDataModel>()
                .setQuery(query, WebDataModel.class)
                .build();

        adapter = new AddWebRecyclerAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();}

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
       // progressDialog.show();
        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

}