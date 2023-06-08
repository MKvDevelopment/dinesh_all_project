package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
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

import com.startapp.sdk.adsbase.StartAppAd;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class ReferActivity extends AppCompatActivity {

    private String standardPlanReferAmount = "30";
    private String advancePlanReferAmount = "70";
    private String superPremiumPlanReferAmount = "150";
    private String refercode, userplan, friendPlan, referby, friend_email, friend_wallet, friend_refer_count, user_refer_count;

    private Button referNow, verifyCode, upgradePlan;
    private EditText friendReferCode;

    private TextView tvrefercode, tvreferLable, plandetails;
    private ProgressDialog progress;
    private DocumentReference documentReference;
    private CollectionReference reference;
    private NetworkChangeReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppAd.showAd(this);
        setContentView(R.layout.activity_refer);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //progress dialog
        progress = new ProgressDialog(this);
        progress.setMessage("Fetching Info...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();


        Toolbar toolbar = findViewById(R.id.referToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initiate view
        friendReferCode = findViewById(R.id.refer_edit_text);
        tvreferLable = findViewById(R.id.refer_amount);
        tvrefercode = findViewById(R.id.user_refer_code);
        verifyCode = findViewById(R.id.verify_code_btn);
        referNow = findViewById(R.id.referFriendsBtn);
        upgradePlan = findViewById(R.id.upgradePlanBtn);
        plandetails = findViewById(R.id.textView15);

        //fetching data
        final String uid = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        documentReference = FirebaseFirestore.getInstance().collection("users").document(uid);

        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                progress.dismiss();
                if (documentSnapshot != null) {
                    refercode = documentSnapshot.getString("refer_code");
                    userplan = documentSnapshot.getString("plan");
                    user_refer_count = documentSnapshot.getString("total_refer_count");
                    setDataOnView(refercode, userplan);
                    invalidateOptionsMenu();

                    String referBy = documentSnapshot.getString("refered_by");
                    if ("empty".compareTo(referBy) != 0) {
                        updateReferedUI(referBy);
                    }

                } else {
                    Toast.makeText(ReferActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        //events
        verifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = friendReferCode.getText().toString().trim();
                if (code.isEmpty()) {
                    Toast.makeText(ReferActivity.this, "Enter Valid Code", Toast.LENGTH_LONG).show();
                } else if (code.length() < 7) {
                    Toast.makeText(ReferActivity.this, "Enter Valid Code", Toast.LENGTH_LONG).show();
                } else if (refercode.compareTo(code) == 0) {
                    Toast.makeText(ReferActivity.this, "Enter your friend's referral code.", Toast.LENGTH_SHORT).show();
                    return;

                } else {
                    progress.show();
                    verifingCode(code);
                }
            }
        });

        referNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "Hey!! friends - Today I find this amazing application work from home. Earn a lot of money from this app at home by smartphone. Use my refer code to get 150 Rs. My refer code:- "+refercode+" - "+MainActivity.RATE_US_URL);
                intent.putExtra(Intent.EXTRA_SUBJECT, MainActivity.RATE_US_URL);
                startActivity(Intent.createChooser(intent, "Share via"));
            }
        });

        upgradePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ReferActivity.this, UpgradePlanActivity.class));
                finish();
            }
        });

    }

    private void setDataOnView(String refercode, String userplan) {
        tvrefercode.setText("Your refer code :- " + refercode);

        switch (userplan) {
            case "Free Plan":
                friendReferCode.setVisibility(View.GONE);
                verifyCode.setVisibility(View.GONE);
                tvrefercode.setVisibility(View.GONE);
                tvreferLable.setVisibility(View.GONE);
                referNow.setVisibility(View.VISIBLE);
                upgradePlan.setVisibility(View.VISIBLE);
                plandetails.setText("You have Free Plan. You need to \n Upgrade plan to get Referl income.");
                break;

            case "Standard Plan":
                upgradePlan.setVisibility(View.GONE);
                plandetails.setText("You have Standard Plan. In this plan you get " + standardPlanReferAmount + " Rs. every successful refer.");
                break;

            case "Advance Plan":
                upgradePlan.setVisibility(View.GONE);
                plandetails.setText("You have Advance Plan. In this plan you get " + advancePlanReferAmount + " Rs. every successful refer.");
                break;

            case "Super Premium Plan":
                upgradePlan.setVisibility(View.GONE);
                plandetails.setText("You have Super Premium Plan. In this plan you get " + superPremiumPlanReferAmount + " Rs. every successful refer.");
                break;


        }

    }

    private void updateReferedUI(String referBy) {

        tvreferLable.setText("You have already been refered by\n" + referBy);
        verifyCode.setVisibility(View.GONE);
        friendReferCode.setVisibility(View.GONE);

    }

    private void verifingCode(final String code) {
        reference = FirebaseFirestore.getInstance().collection("users");
        reference
                .whereEqualTo("refer_code", code)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (!task.getResult().isEmpty()) {
                            progress.dismiss();
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                referby = snapshot.getString("name");
                                friend_email = snapshot.getString("email");
                                friend_wallet = snapshot.getString("wallet");
                                friendPlan = snapshot.getString("plan");
                                friend_refer_count = snapshot.getString("total_refer_count");
                                checkPlanDetail(friendPlan);
                            }
                        } else {
                            progress.dismiss();
                            Toast.makeText(ReferActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progress.dismiss();
                Toast.makeText(ReferActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkPlanDetail(String friendPlan) {

        switch (userplan) {
            case "Free Plan":
                if (friendPlan.equals("Free Plan")) {
                    String positive = "Upgrade now";
                    String neg = "cancel";
                    String message = "You and your friend both are in free plan. You need to upgrade Plan to get referl income.";
                    setAlertDialog(message, positive, neg);

                } else {
                    String positive = "Upgrade now";
                    String neg = "cancel";
                    String message = "You are in Free Plan but your friend is in " + friendPlan + ". You need to upgrade also to get referal income.";
                    setAlertDialog(message, positive, neg);
                }
                break;

            case "Standard Plan":
                if (friendPlan.equals("Standard Plan")) {
                    String user_amount = "30";
                    String friend_amount = "30";
                    addAmountInWallet(user_amount, friend_amount);
                } else {
                    String positive = "Upgrade now";
                    String neg = "cancel";
                    String message = "You are in Standard Plan. But your friend is in " + friendPlan + ". You need to upgrade same as your friend plan to get referal income.";
                    setAlertDialog(message, positive, neg);
                }
                break;

            case "Advance Plan":
                if (friendPlan.equals("Advance Plan")) {
                    String user_amount = "70";
                    String friend_amount = "70";
                    addAmountInWallet(user_amount, friend_amount);
                } else if (friendPlan.equals("Standard Plan")) {
                    String user_amount = "70";
                    String friend_amount = "30";
                    addAmountInWallet(user_amount, friend_amount);
                } else {
                    String positive = "Upgrade now";
                    String neg = "cancel";
                    String message = "You are in Advance Plan. But your friend is in " + friendPlan + ". You need to upgrade same as your friend plan to get referal income.";
                    setAlertDialog(message, positive, neg);
                }
                break;

            case "Super Premium Plan":
                if (friendPlan.equals("Super Premium Plan")) {
                    String user_amount = "150";
                    String friend_amount = "150";
                    addAmountInWallet(user_amount, friend_amount);
                } else if (friendPlan.equals("Advance Plan")) {
                    String user_amount = "150";
                    String friend_amount = "70";
                    addAmountInWallet(user_amount, friend_amount);
                } else if (friendPlan.equals("Standard Plan")) {
                    String user_amount = "150";
                    String friend_amount = "30";
                    addAmountInWallet(user_amount, friend_amount);
                }
                break;


        }

    }

    private void setAlertDialog(String message, String positive, String negative) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ReferActivity.this, "adding", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).setNegativeButton(negative, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    private void addAmountInWallet(String user_amount, String friend_amount) {

        //update xml view
        updateReferedUI(referby);
        //adding balance to user
        String balence = getIntent().getStringExtra("wallet");
        Float total_bal = Float.parseFloat(balence) + Float.parseFloat(user_amount);
        Map map = new HashMap<>();
        map.put("wallet", String.valueOf(total_bal));
        map.put("refered_by", referby);

        documentReference.update(map);

        //adding balance to friend wallet
        Float total_friend_amount = Float.parseFloat(friend_wallet) + Float.parseFloat(friend_amount);
        final int count = Integer.parseInt(friend_refer_count) + 1;
        Map map1 = new HashMap();
        map1.put("wallet", String.valueOf(total_friend_amount));
        map1.put("total_refer_count", String.valueOf(count));

        reference.document(friend_email).update(map1);
        invalidateOptionsMenu();
        progress.dismiss();
        Toast.makeText(ReferActivity.this, "Referral Successfully Added.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.balence) {
            item.setTitle("Your Referel :- " + user_refer_count);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (menu.getItem(0).getItemId() == R.id.balence) {
            menu.findItem(R.id.balence).setTitle("Your Referel :- " + user_refer_count);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
