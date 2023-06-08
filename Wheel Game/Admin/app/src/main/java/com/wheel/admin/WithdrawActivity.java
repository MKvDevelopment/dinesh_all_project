package com.wheel.admin;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WithdrawActivity extends AppCompatActivity implements TransactionHistoryAdapter.transactionAdapterListner {

    private TransactionHistoryAdapter adapter;
    private RecyclerView recyclerView;
    private Calendar calendar;
    private String withdrawTime;
    private TextView tv_date;
    float total,total2;
    String amount, wallet;
    String totalPayout;
    private int year, month, day;
    private DocumentReference adminReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        tv_date = findViewById(R.id.textView10);

        calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yy");
        withdrawTime = simpleDateFormat1.format(calendar.getTime());

        tv_date.setText(withdrawTime);

        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            updateLabel();
        };


        adminReference = FirebaseFirestore.getInstance().collection("App_Utils").document("totalAmount");

        adminReference.addSnapshotListener((value, error) -> {
           totalPayout=value.getString("totalPayout");
            Toast.makeText(getApplicationContext(), totalPayout, Toast.LENGTH_SHORT).show();
        });

        setUpRecyclerView("1", "Paytm Request", tv_date.getText().toString());

        ((Button) findViewById(R.id.button)).setOnClickListener(view -> {
            setUpRecyclerView("1", "Paytm Request", tv_date.getText().toString());
            adapter.startListening();
        });
        ((Button) findViewById(R.id.button2)).setOnClickListener(view -> {
            setUpRecyclerView("2", "UPI Request", tv_date.getText().toString());
            adapter.startListening();
        });

        tv_date.setOnClickListener(view ->
                new DatePickerDialog(WithdrawActivity.this, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show());

    }

    private void updateLabel() {
        String myFormat = "dd-MM-yy";
        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
        tv_date.setText(dateFormat.format(calendar.getTime()));
        setUpRecyclerView("1", "Paytm Request", tv_date.getText().toString());
        adapter.startListening();
    }

    public void setUpRecyclerView(String color, String type, String date) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(type).child(date);
        databaseReference.get().addOnSuccessListener(dataSnapshot -> {
            if (dataSnapshot.exists()) {
                Toast.makeText(getApplicationContext(), "Data found", Toast.LENGTH_SHORT).show();
            } else {
                adapter.stopListening();
                Toast.makeText(getApplicationContext(), "No Data Found", Toast.LENGTH_SHORT).show();
            }

        }).addOnFailureListener(e -> {
            adapter.stopListening();
            Toast.makeText(getApplicationContext(), e.getMessage() + "dddd", Toast.LENGTH_SHORT).show();
        });

        recyclerView = findViewById(R.id.rec_history);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<HistoryModel> options = new FirebaseRecyclerOptions.Builder<HistoryModel>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child(type).child(date), HistoryModel.class)
                .build();

        adapter = new TransactionHistoryAdapter(color, options);
        recyclerView.setAdapter(adapter);
        adapter.setListner(this, type);

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
    public void onClickListner(int id, String item_id, String userUid, String type) {
        if (id == 1) {

            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User_List")
                    .document(userUid).collection("Withdraw history").document(item_id);

            documentReference.update("status", "Success");

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(type)
                    .child(tv_date.getText().toString()).child(item_id);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else {

            DocumentReference historyReference = FirebaseFirestore.getInstance().collection("User_List").document(userUid).collection("Withdraw history").document(item_id);
            DocumentReference userReference = FirebaseFirestore.getInstance().collection("User_List").document(userUid);

            historyReference.addSnapshotListener((value, error) -> {
                assert value != null;
                amount = value.getString("amount");
                total2 = Float.parseFloat(totalPayout) - Float.parseFloat(amount);
                Toast.makeText(getApplicationContext(), amount, Toast.LENGTH_SHORT).show();

                userReference.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        wallet = documentSnapshot.getString("wallet");
                        assert wallet != null;
                        total = Float.parseFloat(wallet) + Float.parseFloat(amount);
                    }
                });

            });

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(type)
                    .child(tv_date.getText().toString()).child(item_id);

            AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(WithdrawActivity.this);
            alertDialog1.setTitle("Reject Alert!");
            alertDialog1.setMessage("Are you Sure ?");
            alertDialog1.setCancelable(false);
            alertDialog1.setPositiveButton("Reject Now", (dialog1, which1) -> {

                userReference.update("wallet", String.valueOf(total));
                historyReference.update("status", "Rejected due to wrong UPI/Paytm No., Amount returned in your wallet");
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot appleSnapshot : snapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                adminReference.update("totalPayout",String.valueOf(total2));
            }).setNegativeButton("Cancel",(dialogInterface, i) -> {
                dialogInterface.dismiss();
            }).show();



        }
    }
}