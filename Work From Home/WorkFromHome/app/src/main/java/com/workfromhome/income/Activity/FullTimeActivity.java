package com.workfromhome.income.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FullTimeActivity extends AppCompatActivity {

    private Button withdraw, viewPost;
    private ProgressDialog dialog;
    private Toolbar toolbar;
    private TextView subStartDat, subEndDat, walleet, totalpostView, remainBal, tv_email, tv_mob,dateEndAlert;
    private DocumentReference userReference, adminReflimit;
    private String appStatus, usubEndDate, usubStartDate, wallet, totalPostView, amountLimit, postIncome, user_email, status, reason, tokenid;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_time);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //  fullTimeUserAlert();
        viewPost = findViewById(R.id.viewPost);
        tv_mob = findViewById(R.id.ftno);
        tv_email = findViewById(R.id.ftemail);
        subEndDat = findViewById(R.id.textView22);
        subStartDat = findViewById(R.id.textView21);
        walleet = findViewById(R.id.textView23);
        totalpostView = findViewById(R.id.textView24);
        remainBal = findViewById(R.id.textView25);
        withdraw = findViewById(R.id.btnwithdraw);
        dateEndAlert=findViewById(R.id.alertdate);

        StartAppAd.disableAutoInterstitial();
        StartAppAd.disableSplash();
        StartAppAd.enableConsent(this, false);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        adminReflimit = FirebaseFirestore.getInstance().collection("Plan").document("Full_Time_Plan");
        userReference = FirebaseFirestore.getInstance().collection("full_time_user").document(user_email);

        adminReflimit.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                amountLimit = documentSnapshot.getString("Limit");
                postIncome = documentSnapshot.getString("Postincome");
                appStatus = documentSnapshot.getString("workstatus");
                String email = documentSnapshot.getString("email");
                String no = documentSnapshot.getString("whatsaap");
                tv_email.setText(email);
                tv_mob.setText(no);
            }
        });

        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {

                    usubEndDate = documentSnapshot.getString("plan_end_date");
                    usubStartDate = documentSnapshot.getString("plan_start_date");
                    wallet = documentSnapshot.getString("wallet");
                    status = documentSnapshot.getString("status");
                    reason = documentSnapshot.getString("reason");
                    tokenid = documentSnapshot.getString("token_id");
                    totalPostView = documentSnapshot.getString("view_post");
                    setMainData();
                }
            }
        });


        //Toolbar events
        toolbar = (Toolbar) findViewById(R.id.fullworktolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //progress dialog
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait!");
        dialog.setMessage("loading...");
        dialog.show();


        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean remain = Double.parseDouble(wallet) >= Double.parseDouble(amountLimit);
                if (remain) {
                    startActivity(new Intent(FullTimeActivity.this, FullPlanWithdrawActivity.class));
                } else {
                    Toast.makeText(FullTimeActivity.this, "You have not sufficient money to withdraw.  :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewPost.setEnabled(false);
                viewPost.setVisibility(View.GONE);

                if (appStatus.equals("OFF")) {
                    androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(FullTimeActivity.this);
                    alertDialog.setTitle("Alert!");
                    alertDialog.setMessage("Work time is 9:00 AM to 5:00 PM.");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Exit Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            viewPost.setEnabled(true);
                            viewPost.setVisibility(View.VISIBLE);
                            finish();
                        }
                    }).show();
                } else {
                    if (status.equals("Block")) {
                        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(FullTimeActivity.this);
                        alertDialog.setTitle("Block!");
                        alertDialog.setMessage(reason);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).show();

                    } else {
                        //check device id for multiple devices
                        checktokenId();
                    }
                }
            }
        });

    }

    private void checktokenId() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (tokenid.contains(id)) {
            gettingAds();

        } else {
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(FullTimeActivity.this);
            alert.setTitle("Alert!");
            alert.setMessage("Your account is Login in another device. Now you can't work in your device. Sorry!");
            alert.setCancelable(false);
            alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogin, int which) {
                    dialogin.dismiss();
                    dialog.dismiss();
                    finish();
                }
            }).show();


        }

    }


    private void gettingAds() {
        // NOTE always use test ads during development and testing
        StartAppSDK.setTestAdsEnabled(false);
        StartAppAd.disableSplash();
        StartAppAd.showAd(FullTimeActivity.this);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait!");
        dialog.setMessage("loading...");
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // addingData();
                //ads increment in firestore
                int totalSeen = Integer.parseInt(totalPostView);
                //adding balence in wallet
                Float total_bal = Float.parseFloat(wallet) + Float.parseFloat(postIncome);
                String totalAds = String.valueOf(totalSeen + 1);
                Map map = new HashMap();
                map.put("view_post", totalAds);
                map.put("wallet", String.valueOf(total_bal));
                //  map.put("view_post", totalAds);
                userReference.update(map).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        dialog.dismiss();
                        viewPost.setEnabled(true);
                        viewPost.setVisibility(View.VISIBLE);
                        Toast.makeText(FullTimeActivity.this, "Amount added in wallet :)", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 5000);

    }

    @SuppressLint("SetTextI18n")
    private void setMainData() {

        subStartDat.setText("Plan Start :- " + usubStartDate);
        subEndDat.setText("Plan End :- " + usubEndDate);
        totalpostView.setText("View Post by You:- " + totalPostView);

        //setting balance on text view
        double bale = Double.parseDouble(wallet);
        bale = Math.round(bale * 100.00) / 100.00;
        walleet.setText("Wallet:- " + bale + "/- Rs.");

        //maintain extra textview
        Double remain = Double.parseDouble(amountLimit) - Double.parseDouble(wallet);
        remain = Math.round(remain * 100.00) / 100.00;
        boolean on = remain <= 0.0;
        if (on) {
            remainBal.setVisibility(View.GONE);
        }
        remainBal.setText("You need Rs. " + String.valueOf(remain) + "/- more before \nSubscription End");

        Calendar calendarr = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        String currentTime = simpleDateFormat2.format(calendarr.getTime());

        //  Toast.makeText(this, currentTime + " --" + usubEndDate, Toast.LENGTH_LONG).show();
        try {
            Date date1 = simpleDateFormat2.parse(currentTime);
            Date date2 = simpleDateFormat2.parse(usubEndDate);


            if (date1.compareTo(date2) < 0) {
                //  subEndDate is smaller then new date
                //  Toast.makeText(FullTimeActivity.this, " date1 is smaller than  date2", Toast.LENGTH_SHORT).show();
            } else if (date1.compareTo(date2) > 0) {
                deleteUser();

            } else {
                dateEndAlert.setText("Your Plan will Expiring Tommorrow. Please complete your task as soon as possible if you have not completed.");
               /* AlertDialog.Builder alertDialog = new AlertDialog.Builder(FullTimeActivity.this);
                alertDialog.setTitle("Alert!");
                alertDialog.setMessage("");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();*/
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "huuuu", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();

    }

    private void deleteUser() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("full_time_user").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        AlertDialog.Builder timealertDialog = new AlertDialog.Builder(FullTimeActivity.this);
        timealertDialog.setTitle("Alert!");
        timealertDialog.setMessage("Your Plan expired. Your work time of one month had completed yesterday. If you want to work again then you need to upgrade Plan again.");
        timealertDialog.setCancelable(false);
        timealertDialog.setPositiveButton("Exit", (dialog, which) -> {
            dialog.dismiss();
            documentReference.delete().addOnSuccessListener(aVoid1 -> {
                Toast.makeText(FullTimeActivity.this, "Your Plan is Expired:(", Toast.LENGTH_SHORT).show();
                finish();
            }).addOnFailureListener(e -> Toast.makeText(FullTimeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
        }).show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}