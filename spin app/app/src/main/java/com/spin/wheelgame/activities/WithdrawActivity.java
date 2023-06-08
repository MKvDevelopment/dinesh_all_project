package com.spin.wheelgame.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.spin.wheelgame.R;
import com.spin.wheelgame.utils.NetworkChangeReceiver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WithdrawActivity extends AppCompatActivity {

    private EditText mobile;
    private Button withdrawbtn;
    private ProgressDialog progressDialog;
    private TextView totalReward;
    private int nextRow;
    private String user_email, balance, user_row, activation, depositBal;
    private DocumentReference documentReference;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        //initiate views
        mobile = findViewById(R.id.mobile_no);
        withdrawbtn = findViewById(R.id.btn_withdraw_money);
        totalReward = findViewById(R.id.total_reward_amount);

        //getting data from Main activtiy
        user_email = getIntent().getStringExtra("email");

        //toolbar
        Toolbar toolbar = findViewById(R.id.withdraw_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String date = dateFormat.format(new Date());

        //initiate firebase
        documentReference = FirebaseFirestore.getInstance().collection("users").document(user_email);

        progressDialog.setMessage("Fetching your data...");
        progressDialog.show();

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // progressDialog.dismiss();
                if (documentSnapshot != null) {
                    balance = documentSnapshot.getString("winning_balence");
                    user_row = documentSnapshot.getString("row");
                    activation = documentSnapshot.getString("Activation");
                    depositBal = documentSnapshot.getString("deposit_balence");
                    totalReward.setText("\u20B9 " + balance);
                    progressDialog.dismiss();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //events
        withdrawbtn.setOnClickListener(v -> {

            String mob = mobile.getText().toString();
            if (mob.isEmpty() || mob.length() < 10) {
                Toast.makeText(WithdrawActivity.this, "Enter Valid Mobile No.", Toast.LENGTH_LONG).show();
                return;
            } else if (Integer.parseInt(balance) < 100) {
                Toast.makeText(WithdrawActivity.this, "You have not sufficient money in wallet!", Toast.LENGTH_SHORT).show();
                return;
            } else if (Integer.parseInt(balance) >= 100 && Integer.parseInt(balance) < 500) {
                Toast.makeText(WithdrawActivity.this, "Minimum Balance required \u20B9 500", Toast.LENGTH_SHORT).show();
                return;
            } else if (activation.equals("YES")) {
                checkPlan(mob, date);
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(WithdrawActivity.this);
                alertDialog.setTitle("Account Not Activated!");
                alertDialog.setMessage("Before withdraw you must have to activate your account with Rs 350. After activate your account you can withdraw your money.");
                alertDialog.setCancelable(false);
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });
                alertDialog.setPositiveButton("Continue", (dialog, which) -> {
                   dialog.dismiss();
                    progressDialog.show();
                    progressDialog.setMessage("Checking Your Deposit Balance. Please Wait....");
                    progressDialog.setCancelable(false);
                    progressDialog.setCanceledOnTouchOutside(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Integer.parseInt(depositBal) >= 350) {
                                int total=Integer.parseInt(depositBal)-350;
                                Map map=new HashMap();
                                map.put("Activation","YES");
                                map.put("deposit_balence",String.valueOf(total));
                                documentReference.update(map).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        progressDialog.dismiss();
                                        Toast.makeText(WithdrawActivity.this, "Activation Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(WithdrawActivity.this);
                                alertDialog.setTitle("Low Balance!");
                                alertDialog.setMessage("You required Rs 350/- to activate your account.");
                                alertDialog.setCancelable(false);
                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.setPositiveButton("Add Now", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(WithdrawActivity.this, AddMoneyActivity.class));
                                        finish();
                                        dialog.dismiss();
                                    }
                                }).show();

                            }
                        }
                    }, 3000);
                }).show();

            }
        });

    }

    private void checkPlan(String mob, String date) {

        progressDialog.setMessage("Uploading your request....");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        sendDataToServer(mob, date);
        Toast.makeText(WithdrawActivity.this, "Please wait", Toast.LENGTH_SHORT).show();

    }

    private void sendDataToServer(String mob, String date) {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("HH:mm:ss");
        String withdrawTime = simpleDateFormat1.format(calendar.getTime());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("withdraw_list").child(date);
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

                            if (user_row.equals("1")) {
                                nextRow = 2;
                            } else {
                                nextRow = 1;
                            }

                            Map map = new HashMap();
                            map.put("index", "0");
                            map.put("row", String.valueOf(nextRow));
                            map.put("winning_balence", "0");
                            map.put("Activation", "NO");

                            documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WithdrawActivity.this);
                                        alertDialog.setTitle("Thanks Dear!");
                                        alertDialog.setMessage("We get your withdraw request. Money will be send in your Paytm wallet within 24 hours.");
                                        alertDialog.setCancelable(false);
                                        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                progressDialog.dismiss();
                                                startActivity(new Intent(WithdrawActivity.this, Spin2Activity.class));
                                                finish();
                                                dialog.dismiss();
                                            }
                                        }).show();
                                        Toast.makeText(WithdrawActivity.this, "Request sent successfully.", Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(WithdrawActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(WithdrawActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
