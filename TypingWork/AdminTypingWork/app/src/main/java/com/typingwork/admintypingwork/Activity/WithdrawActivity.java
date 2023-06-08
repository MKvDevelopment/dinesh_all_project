package com.typingwork.admintypingwork.Activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.typingwork.admintypingwork.Adapter.WithdrawListAdapter;
import com.typingwork.admintypingwork.Model.WithdrawModel;
import com.typingwork.admintypingwork.R;
import com.typingwork.admintypingwork.databinding.ActivityWithdrawBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class WithdrawActivity extends AppCompatActivity implements WithdrawListAdapter.transactionAdapterListner {

    private ActivityWithdrawBinding binding;
    private WithdrawListAdapter adapter;
    private Calendar calendar;
    private String withdrawTime;
    private DocumentReference userRef, userWithdrawRef;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yy");
        withdrawTime = simpleDateFormat1.format(calendar.getTime());

        binding.textView10.setText(withdrawTime);
        binding.recHistory.setLayoutManager(new LinearLayoutManager(this));

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };

        setUpRecyclerView(binding.textView10.getText().toString().trim());


        binding.textView10.setOnClickListener(view ->
                new DatePickerDialog(WithdrawActivity.this,
                        AlertDialog.THEME_DEVICE_DEFAULT_DARK,
                        date,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                ).show());


    }

    private void updateLabel() {
        String myFormat = "dd-MM-yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        binding.textView10.setText(dateFormat.format(calendar.getTime()));
        setUpRecyclerView(binding.textView10.getText().toString());
        adapter.startListening();
    }

    public void setUpRecyclerView(String date) {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Withdraw").child(date);

        databaseReference.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
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

        FirebaseRecyclerOptions<WithdrawModel> options = new FirebaseRecyclerOptions.Builder<WithdrawModel>()
                .setQuery(databaseReference, WithdrawModel.class)
                .build();

        adapter = new WithdrawListAdapter(options);
        binding.recHistory.setAdapter(adapter);
        adapter.setListner(this);


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
    public void onClickListner(WithdrawModel model, int position) {
        progressDialog.show();

        String uid = model.getId();
        userRef = FirebaseFirestore.getInstance().collection("Users_list").document(uid);
        userWithdrawRef = FirebaseFirestore.getInstance().collection("Users_list").document(uid).collection("withdraw").document(uid);

        Map map = new HashMap();
        map.put("status", "Paid");
        databaseReference.child(uid).updateChildren(map, (error, databaseReference) -> {
            userRef.update("withdraw", "Success").addOnSuccessListener(unused -> {
                userWithdrawRef.update("status", "Success").addOnSuccessListener(unused1 -> {
                    progressDialog.dismiss();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        });


    }
}