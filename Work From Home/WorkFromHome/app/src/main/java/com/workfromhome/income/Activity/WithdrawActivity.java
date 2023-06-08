package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.OneTimeActivity.TermsActivity;
import com.workfromhome.income.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.workfromhome.income.Activity.LoginActivity.db;


public class WithdrawActivity extends AppCompatActivity {

    private EditText mobile;
    private Button withdrawbtn;
    private ProgressDialog progressDialog;
    private TextView totalReward, notic;
    private String user_email, balance, withdraw_request_count, plan, first_withdraw_limit, second_limit;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private DocumentReference documentReference;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

         getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //toolbar
        Toolbar toolbar = findViewById(R.id.withdraw_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        //initiate views
        mobile = findViewById(R.id.mobile_no);
        withdrawbtn = findViewById(R.id.btn_withdraw_money);
        totalReward = findViewById(R.id.total_reward_amount);
        notic = findViewById(R.id.tv_withdraw_limit);

        //getting data from Main activtiy
        user_email = getIntent().getStringExtra("email");
        plan = getIntent().getStringExtra("plan");


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String date = dateFormat.format(new Date());

        //initiate firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        documentReference = firebaseFirestore.collection("users").document(user_email);

        progressDialog.setMessage("Fetching your data...");
        progressDialog.show();

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                // progressDialog.dismiss();
                if (documentSnapshot != null) {

                    withdraw_request_count = documentSnapshot.getString("withdraw_count");
                    plan = documentSnapshot.getString("plan");
                    balance = documentSnapshot.getString("wallet");


                } else {
                    // progressDialog.dismiss();
                    Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        DocumentReference dc = firebaseFirestore.collection("Plan").document(plan);
        dc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                //  progressDialog.dismiss();
                if (documentSnapshot != null) {

                    first_withdraw_limit = documentSnapshot.getString("first_withdraw_limit");
                    second_limit = documentSnapshot.getString("second_withdraw_limit");
                    setData();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        //events
        withdrawbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean bal = Float.parseFloat(first_withdraw_limit) > Float.parseFloat(balance);
                String mob = mobile.getText().toString();
                if (mob.isEmpty() || mob.length() < 10) {
                    Toast.makeText(WithdrawActivity.this, "Enter Valid Mobile No.", Toast.LENGTH_LONG).show();
                    return;
                } else if (bal) {
                    Toast.makeText(WithdrawActivity.this, "You have not sufficient money in wallet!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    checkPlan(mob, date);
                }
            }
        });
    }

    private void checkPlan(String mob, String date) {

        switch (plan) {
            case "Free Plan":
                boolean bal = Float.parseFloat(second_limit) > Float.parseFloat(balance);

                if (Integer.parseInt(withdraw_request_count) >= 1) {

                    if (bal) {
                        Toast.makeText(WithdrawActivity.this, "You have not sufficient money in wallet!", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                        alertDialog.setTitle("Alert!");
                        alertDialog.setMessage("You are in Free Plan. You can't withdraw at time, you need to upgrade in any Plan.");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Upgrade Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(WithdrawActivity.this, UpgradePlanActivity.class));
                                finish();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }

                } else {

                    progressDialog.setMessage("Uploading your request....");
                    progressDialog.show();
                    checkexitingMobileno(mob, date);
                }
                break;
            case "Standard Plan":
                progressDialog.setMessage("Uploading your request....");
                progressDialog.show();
                sendDataToServer(mob, date, "Standard withdraw list");
                Toast.makeText(WithdrawActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                updateUserBalance();
                break;
            case "Advance Plan":
                progressDialog.setMessage("Uploading your request....");
                progressDialog.show();
                sendDataToServer(mob, date, "Advance withdraw list");
                Toast.makeText(WithdrawActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                updateUserBalance();
                break;
            case "Super Premium Plan":
                progressDialog.setMessage("Uploading your request....");
                progressDialog.show();
                sendDataToServer(mob, date, "Premium withdraw list");
                Toast.makeText(WithdrawActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                updateUserBalance();
                break;
        }


    }

    private void checkexitingMobileno(String mob, String date) {

        CollectionReference previusmob = FirebaseFirestore.getInstance().collection("All_Mobile");
        previusmob
                .whereEqualTo("mobile", mob)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (!task.getResult().isEmpty()) {
                            progressDialog.dismiss();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(WithdrawActivity.this);
                            dialog.setCancelable(false);
                            dialog.setTitle("Policy Voilation Alert!");
                            dialog.setMessage("Your are using previous account paytm no. You can't use same paytm no. in multiple account. This is against our Policy. Due to Policy Voilation Your account is Blocked.");
                            dialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    Map map = new HashMap();
                                    map.put("block_reason", "Your account is blocked due to using multiple account with same Paytm no. This is against our Policy. If you want to earn money, work properly with previous account.");
                                    map.put("user_status", "Block");
                                    documentReference.update(map);
                                    finish();
                                    dialogInterface.dismiss();
                                }
                            }).show();


                        } else {

                            final Map<String, Object> map = new HashMap<>();

                            map.put("mobile", mob);

                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("All_Mobile").document();
                            documentReference
                                    .set(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                sendDataToServer(mob, date, "Free withdraw list");
                                                Toast.makeText(WithdrawActivity.this, "Please wait", Toast.LENGTH_SHORT).show();
                                                updateUserBalance();
                                            }
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();

                                    Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });


    }

    private void updateUserBalance() {

        int withdraw_count = Integer.parseInt(withdraw_request_count) + 1;

        Map map = new HashMap();
        map.put("withdraw_count", String.valueOf(withdraw_count));
        map.put("wallet", "0.0");

        documentReference.update(map);

        Toast.makeText(this, "wallet updated", Toast.LENGTH_SHORT).show();
    }

    private void sendDataToServer(String mob, String date, String path) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(path).child(date);
        int withdraw_count = Integer.parseInt(withdraw_request_count) + 1;

        Map map1 = new HashMap();
        map1.put("mobile", mob);
        map1.put("withdraw_count", String.valueOf(withdraw_count));
        map1.put("wallet", balance);
        map1.put("email", user_email);

        databaseReference
                .push()
                .setValue(map1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(WithdrawActivity.this);
                            alertDialog.setTitle("Thanks Dear!");
                            alertDialog.setMessage("We get your withdraw request. Money will be send in your Paytm Wallet within 24 hours.");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).show();
                            Toast.makeText(WithdrawActivity.this, "Request sent successfully.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
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
    protected void onStart() {
        super.onStart();

        checkplanForRegularAlert(getIntent().getStringExtra("plan"));

    }

    //setting data to text view
    private void setData() {

        Double bale = Double.parseDouble(balance);
        bale = Math.round(bale * 100.00) / 100.00;
        progressDialog.dismiss();
        totalReward.setText("Rs. " + bale + "/-");
        notic.setText("2. In " + plan + " Your Withdraw limit is Rs. " + first_withdraw_limit + "/-");
        if (plan.equals("Free Plan")) {
            if (Integer.parseInt(withdraw_request_count) > 0) {
                notic.setText("2. In " + plan + " Your Withdraw limit is Rs. " + second_limit + "/-");
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void checkplanForRegularAlert(String plan_name) {

        if (plan_name.equals("Standard Plan")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Alert!");
            alertDialog.setMessage("You are in Standard plan. Your withdraw limit is 3500 Rs. We give work accourding to availability of work. It can be decrease or increase. Upgrade amount is non refundable. We already mention all this in terms and conditions go and read carefully.");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Read More", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(WithdrawActivity.this, TermsActivity.class));
                    dialog.dismiss();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        } else if (plan_name.equals("Advance Plan")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Alert!");
            alertDialog.setMessage("You are in Advance plan. Your withdraw limit is 5500 Rs. We give work accourding to availability of work. It can be decrease or increase. Upgrade amount is non refundable. We already mention all this in terms and conditions go and read carefully.");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Read More", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(WithdrawActivity.this, TermsActivity.class));
                    dialog.dismiss();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();


        } else if (plan_name.equals("Super Premium Plan")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Alert!");
            alertDialog.setMessage("You are in Super Premium plan. Your withdraw limit is 7500 Rs. We give work accourding to availability of work. It can be decrease or increase. Upgrade amount is non refundable. We already mention all this in terms and conditions go and read carefully.");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Read More", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(WithdrawActivity.this, TermsActivity.class));
                    dialog.dismiss();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();

        }

    }

}

