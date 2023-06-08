package com.typingwork.admintypingwork.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.typingwork.admintypingwork.Adapter.AlertListAdapter;
import com.typingwork.admintypingwork.Model.AlertListModel;
import com.typingwork.admintypingwork.databinding.ActivityWalletAlertBinding;

import java.util.Objects;

public class WalletAlertActivity extends AppCompatActivity implements AlertListAdapter.transactionAdapterListner {

    private ActivityWalletAlertBinding binding;
    private AlertListAdapter adapter;
    private DocumentReference userRef, userWithdrawRef;
    private ProgressDialog progressDialog;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityWalletAlertBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarMyAlert);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        collectionReference = FirebaseFirestore.getInstance().collection("Wallet_Alert_List");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.onSaveInstanceState();
        binding.recHistory.setLayoutManager(linearLayoutManager);

        setUpRecyclerView(collectionReference.orderBy("time"));

    }

    private void setUpRecyclerView(com.google.firebase.firestore.Query time) {

        collectionReference.get().addOnSuccessListener(dataSnapshot -> {
            if (!dataSnapshot.isEmpty()) {
                progressDialog.dismiss();
                // Toast.makeText(getApplicationContext(), "Data found", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                adapter.stopListening();
                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            adapter.stopListening();
        });

        FirestoreRecyclerOptions<AlertListModel> options = new FirestoreRecyclerOptions
                .Builder<AlertListModel>()
                .setQuery(time, AlertListModel.class)
                .build();

       // binding.recHistory.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        adapter = new AlertListAdapter(options);
        binding.recHistory.setAdapter(adapter);
        adapter.setListner(this);
        binding.recHistory.setItemAnimator(null);
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

    //adapter listner

    @Override
    public void onClickListner(AlertListModel model, int position) {
        Intent intent=new Intent(WalletAlertActivity.this,UserDetailActivity.class);
        intent.putExtra("uid",model.getUid());
        startActivity(intent);
    }
}
