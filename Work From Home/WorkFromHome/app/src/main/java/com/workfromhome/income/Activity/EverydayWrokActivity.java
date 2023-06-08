package com.workfromhome.income.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class EverydayWrokActivity extends AppCompatActivity {

    private Button withdraw, viewPost;
    private ProgressDialog dialog;
    private Toolbar toolbar;
    private TextView subStartDat, walleet, totalpostView, remainBal, tv_email;
    private DocumentReference userReference, adminReflimit;
    private String appStatus, usubStartDate, wallet, totalPostView, amountLimit, postIncome, user_email, status, reason, token_id;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_everyday_wrok);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //  fullTimeUserAlert();
        viewPost = findViewById(R.id.ed_viewPost);
        tv_email = findViewById(R.id.edemail);
        subStartDat = findViewById(R.id.textView30);
        walleet = findViewById(R.id.textView32);
        totalpostView = findViewById(R.id.textView34);
        remainBal = findViewById(R.id.textView33);
        withdraw = findViewById(R.id.btn_ed_withdraw);


        StartAppAd.disableAutoInterstitial();
        StartAppAd.disableSplash();
        StartAppAd.enableConsent(this, false);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        adminReflimit = FirebaseFirestore.getInstance().collection("Plan").document("Everyday Plan");
        userReference = FirebaseFirestore.getInstance().collection("everyday_users").document(user_email);

        adminReflimit.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                amountLimit = documentSnapshot.getString("Limit");
                postIncome = documentSnapshot.getString("Postincome");
                appStatus = documentSnapshot.getString("workstatus");
                String email = documentSnapshot.getString("email");
                tv_email.setText(email);
            }
        });

        userReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {

                    usubStartDate = documentSnapshot.getString("plan_start_date");
                    wallet = documentSnapshot.getString("wallet");
                    status = documentSnapshot.getString("status");
                    reason = documentSnapshot.getString("reason");
                    token_id = documentSnapshot.getString("token_id");
                    totalPostView = documentSnapshot.getString("view_post");
                    setMainData();
                }
            }
        });


        //Toolbar events
        toolbar = (Toolbar) findViewById(R.id.ed_worktolbar);
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
                    startActivity(new Intent(EverydayWrokActivity.this, EverydayWithdrawActivity.class));
                } else {
                    Toast.makeText(EverydayWrokActivity.this, "You have not sufficient money to withdraw.  :(", Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewPost.setOnClickListener(view -> {
            viewPost.setEnabled(false);
            viewPost.setVisibility(View.GONE);
            if (appStatus.equals("OFF")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(EverydayWrokActivity.this);
                alertDialog.setTitle("Alert!");
                alertDialog.setMessage("Your today work time is completed. Opps!\n" +
                        "Today you have unable to complete target 550 Rs. Your Plan expired. If you want to work you can upgrade again and continue work from tomorrow.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("everyday_users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        documentReference.delete();
                        viewPost.setEnabled(true);
                        viewPost.setVisibility(View.VISIBLE);
                        finish();
                    }
                }).show();
            } else {
                boolean remain = Double.parseDouble(wallet) >= Double.parseDouble(amountLimit);
                if (remain) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(EverydayWrokActivity.this);
                    alertDialog.setTitle("Alert!");
                    alertDialog.setMessage("Now you are eligible to withdraw money.Please withdraw money before today 5:00 PM. After that your Plan will expire, and you can not withdraw.");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Withdraw Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            startActivity(new Intent(EverydayWrokActivity.this, EverydayWithdrawActivity.class));
                        }
                    }).show();
                } else {
                    if (status.equals("Block")) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EverydayWrokActivity.this);
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
                        checktokenId();
                    }
                }

            }
        });


    }

    private void checktokenId() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (token_id.contains(id)) {
            gettingAds();

        } else {
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(EverydayWrokActivity.this);
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
        StartAppAd.showAd(EverydayWrokActivity.this);

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setTitle("Please wait!");
        dialog.setMessage("loading...");
        dialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

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
                        Toast.makeText(EverydayWrokActivity.this, "Amount added in wallet :)", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }, 5000);

    }

    private void setMainData() {

        subStartDat.setText("Work Date :- " + usubStartDate);
        totalpostView.setText("View Post by You:- " + totalPostView);

        //setting balance on text view
        Double bale = Double.parseDouble(wallet);
        bale = Math.round(bale * 100.00) / 100.00;
        walleet.setText("Wallet:- " + bale + "/- Rs.");

        //maintain extra textview
        Double remain = Double.parseDouble(amountLimit) - Double.parseDouble(wallet);
        remain = Math.round(remain * 100.00) / 100.00;
        boolean on = remain <= 0.0;
        if (on) {
            remainBal.setVisibility(View.GONE);
        }
        remainBal.setText("You need Rs. " + String.valueOf(remain) + "/- more before \ntoday 5:00 PM. After that your plan will Expire.");

        Calendar calendarr = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd/MM/yyyy");
        String currentTime = simpleDateFormat2.format(calendarr.getTime());

        try {
            Date currentdate = simpleDateFormat2.parse(currentTime);
            Date enddate = simpleDateFormat2.parse(usubStartDate);

            if (currentdate.compareTo(enddate) < 0) {
                //  subEndDate is smaller then new date
                viewPost.setEnabled(false);
                androidx.appcompat.app.AlertDialog.Builder timealertDialog = new androidx.appcompat.app.AlertDialog.Builder(EverydayWrokActivity.this);
                timealertDialog.setTitle("Alert!");
                timealertDialog.setMessage("Your work will start tomorrow at 9:00 AM and work will end at 5:00 PM. Be ready on time");
                timealertDialog.setCancelable(false);
                timealertDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                }).show();

              } else if (currentdate.compareTo(enddate) > 0) {
                //end date is grater than current date
                deleteUser();
                // Toast.makeText(this, "case 2", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, "case 3", Toast.LENGTH_SHORT).show();
                viewPost.setEnabled(true);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            //    Toast.makeText(this, "huuuu", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();

    }

    private void deleteUser() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("everyday_users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        androidx.appcompat.app.AlertDialog.Builder timealertDialog = new androidx.appcompat.app.AlertDialog.Builder(EverydayWrokActivity.this);
        timealertDialog.setTitle("Alert!");
        timealertDialog.setMessage("Your Plan expired. Your work time was yesterday 9 Am to 5 Pm. If you want to work again then you need to upgrade Plan again.");
        timealertDialog.setCancelable(false);
        timealertDialog.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EverydayWrokActivity.this, "Your Plan is Expired:(", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EverydayWrokActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
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