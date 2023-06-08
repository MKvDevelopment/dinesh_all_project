package com.skincarestudio.solution.Activty;

import android.annotation.SuppressLint;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.skincarestudio.solution.Adapter.GridProductViewAdapter;
import com.skincarestudio.solution.Model.Product_item_model;
import com.skincarestudio.solution.R;
import com.skincarestudio.solution.Utils.NetworkChangeReceiver;

import java.util.Objects;

public class ProudctActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GridProductViewAdapter adapter;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proudct);

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        Toolbar toolbar=findViewById(R.id.product_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Our Product's");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        recyclerView = findViewById(R.id.gridProduct_layout_gridview);
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        collectionReference = firestore.collection("product_list");

        setUpRecyclerView();

    }

    @SuppressLint("NotifyDataSetChanged")
    private void setUpRecyclerView() {

        Query query = collectionReference;
        FirestoreRecyclerOptions<Product_item_model> options = new FirestoreRecyclerOptions
                .Builder<Product_item_model>()
                .setQuery(query, Product_item_model.class)
                .build();

        adapter = new GridProductViewAdapter(options);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(null);
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onStart() {
        super.onStart();
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