package com.workfromhome.income.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EverydayWithdrawActivity extends AppCompatActivity {

    private EditText mobile;
    private Button withdrawbtn;
    private ProgressDialog progressDialog;
    private TextView totalReward, notic;
    private String user_email, balance, withdraw_limit;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference documentReference;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_everyday_withdraw);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //toolbar
        Toolbar toolbar = findViewById(R.id.ed_withdraw_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        //initiate views
        mobile = findViewById(R.id.ed_mobile_no);
        withdrawbtn = findViewById(R.id.ed_withdraw_money);
        totalReward = findViewById(R.id.ed_totalbal);
        notic = findViewById(R.id.ed_withdraw_limit);


        //initiate firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        documentReference = firebaseFirestore.collection("everyday_users").document(user_email);

        progressDialog.setMessage("Fetching your data...");
        progressDialog.show();

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    balance = documentSnapshot.getString("wallet");
                } else {
                    // progressDialog.dismiss();
                    Toast.makeText(EverydayWithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        DocumentReference dc = firebaseFirestore.collection("Plan").document("Everyday Plan");
        dc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //  progressDialog.dismiss();
                if (documentSnapshot != null) {
                    withdraw_limit = documentSnapshot.getString("Limit");
                    setData();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(EverydayWithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String date = dateFormat.format(new Date());

        //events
        withdrawbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mob = mobile.getText().toString();
                if (mob.isEmpty() || mob.length() < 10) {
                    Toast.makeText(EverydayWithdrawActivity.this, "Enter Valid details.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    checkPlan(mob, date);
                }
            }
        });
    }

    private void checkPlan(String mob, String date) {

        progressDialog.setMessage("Uploading your request....");
        progressDialog.show();
        sendDataToServer(mob, date);
        Toast.makeText(EverydayWithdrawActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
        updateUserBalance();
    }

    private void updateUserBalance() {
        Map map = new HashMap();
        map.put("view_post", "0");
        map.put("wallet", "0.0");
        documentReference.update(map);
        Toast.makeText(this, "wallet updated", Toast.LENGTH_SHORT).show();
    }

    private void sendDataToServer(String mob, String date) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss");
        String withdrawTime = simpleDateFormat1.format(calendar.getTime());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("everyday_withdraw_list").child(date);
        Map map1 = new HashMap();
        map1.put("email", user_email);
        map1.put("mobile", mob);
        map1.put("time", withdrawTime);
        map1.put("withdraw_amount", balance);
        databaseReference.push().setValue(map1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(EverydayWithdrawActivity.this);
                            alertDialog.setTitle("Thanks Dear!");
                            alertDialog.setMessage("We get your withdraw request. Money will be send in your account(Paytm,Google Pay) within 24 hours.");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).show();
                            Toast.makeText(EverydayWithdrawActivity.this, "Request sent successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EverydayWithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        mobile.setText("");
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //setting data to text view
    private void setData() {

        Double bale = Double.parseDouble(balance);
        bale = Math.round(bale * 100.00) / 100.00;
        progressDialog.dismiss();
        totalReward.setText("Rs. " + bale + "/-");
        notic.setText("2. In Every day Plan, Withdraw limit is Rs. " + withdraw_limit + "/-");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}