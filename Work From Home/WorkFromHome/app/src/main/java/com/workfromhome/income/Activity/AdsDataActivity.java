package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
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

import java.util.HashMap;
import java.util.Map;

public class AdsDataActivity extends AppCompatActivity {


    private Toolbar adsDataToolbar;
    private ProgressDialog progressDialog;
    private TextView adsPlanName, perAdIncome, adsLimit, adSeenByUser, userWallet;
    private Button getRewardBtn;
    private ImageView skinImage;
    private String plan_name, total_ad_sen_by_user, per_ad_income, ad_limit, balence, email, withdraw_request_count;
    private FirebaseFirestore firebaseFirestore;
    private DocumentReference comnDataReference, userDataReference, adminRef;
    private NetworkChangeReceiver broadcastReceiver;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // StartAppAd.disableAutoInterstitial();
        setContentView(R.layout.activity_ads_data);

        withdraw_request_count = getIntent().getStringExtra("count");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //initiates views
        progressDialog = new ProgressDialog(AdsDataActivity.this);
        adsDataToolbar = (Toolbar) findViewById(R.id.ads_data_toolbar);
        skinImage = findViewById(R.id.imageView2);
        adsPlanName = (TextView) findViewById(R.id.ads_plan_name);
        perAdIncome = (TextView) findViewById(R.id.per_ad_income);
        getRewardBtn = (Button) findViewById(R.id.get_reward_btn);
        adsLimit = (TextView) findViewById(R.id.ads_limit);
        adSeenByUser = (TextView) findViewById(R.id.ad_seen_by_user);
        userWallet = findViewById(R.id.textView11);

        //getting data from main activity
        plan_name = getIntent().getStringExtra("plan");
        balence = getIntent().getStringExtra("wallet");
        //Toolbar
        setSupportActionBar(adsDataToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initiate firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        adminRef = firebaseFirestore.collection("App_utils").document("App_status");
        comnDataReference = firebaseFirestore.collection("common_data").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        userDataReference = firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        sharedPreferences = getSharedPreferences("skin", MODE_PRIVATE);


        getRewardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRewardBtn.setEnabled(false);
                getRewardBtn.setVisibility(View.GONE);
                gettingAds();
            }
        });

        skinImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(AdsDataActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Alert!");
                dialog.setMessage("Install this app and run minimum 2 minutes to get Rs. 2. This is Promotional App. Our Responsibility is till Download this Application");
                dialog.setPositiveButton("Install Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        skinAlertDialog();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

            }
        });
    }

    private void skinAlertDialog() {

        progressDialog.setTitle("Please Wait?");
        progressDialog.show();

        String url = "https://play.google.com/store/apps/details?id=com.skincarestudio.solution";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //adding balence in wallet
                Float total_bal = Float.parseFloat(balence) + 2;

                userDataReference.update("wallet", String.valueOf(total_bal))
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    // Toast.makeText(AdsDataActivity.this, "Amount added in wallet :)", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("visited", "1");
                editor.commit();
                skinImage.setVisibility(View.GONE);

            }
        }, 3);


    }

    private void gettingAds() {

        checkdatabase(comnDataReference);

        int limit = Integer.parseInt(ad_limit);
        int seen = Integer.parseInt(total_ad_sen_by_user);

        if (seen >= limit) {
            getRewardBtn.setText("Today's Work Completed");
            Toast.makeText(AdsDataActivity.this, "Today Limit over ", Toast.LENGTH_SHORT).show();
        } else {
            // NOTE always use test ads during development and testing
            StartAppSDK.setTestAdsEnabled(false);
            StartAppAd.disableSplash();
            StartAppAd.showAd(AdsDataActivity.this);
            addingData();
        }
    }

    private void addingData() {
        //ads increment in firestore
        ProgressDialog progressDialog = new ProgressDialog(AdsDataActivity.this);
        progressDialog.setTitle("Please wait!");
        progressDialog.setMessage("Loading data...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int totalSeen = Integer.parseInt(total_ad_sen_by_user);
                String totalAds = String.valueOf(totalSeen + 1);
                comnDataReference.update("ad_seen_by_user", totalAds);

                //adding balence in wallet
                Float total_bal = Float.parseFloat(balence) + Float.parseFloat(per_ad_income);

                userDataReference.update("wallet", String.valueOf(total_bal));
                Toast.makeText(AdsDataActivity.this, "Amount added in wallet :)", Toast.LENGTH_SHORT).show();
                setdataOnView(ad_limit, per_ad_income, progressDialog);
                checkwithdrawLimit();

                getRewardBtn.setEnabled(true);
                getRewardBtn.setVisibility(View.VISIBLE);
            }
        }, 5000);

    }

    private void checkwithdrawLimit() {

        email = getIntent().getStringExtra("email");
        if (plan_name.equals("Free Plan")) {

            boolean bal = Float.parseFloat(balence) >= 7;

            if (withdraw_request_count.equals("0")) {
                if (bal) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                    alertDialog.setTitle("Alert!");
                    alertDialog.setMessage("You are eligible to withdraw your money.");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Withdraw Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(AdsDataActivity.this, WithdrawActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("plan", plan_name);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        }
    }

    private void setdataOnView(String ads_seen, String income, ProgressDialog progressDialog) {
        //from admin
        perAdIncome.setText(income + " Paisa");
        adsLimit.setText(ads_seen);
        //from user
        adSeenByUser.setText(total_ad_sen_by_user);
        adsPlanName.setText(plan_name);
        progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.show();
        progressDialog.setTitle("Please wait!");
        progressDialog.setMessage("Loading data...");
        progressDialog.setCancelable(false);

        checkSharedPref();
        checkTaskRef(adminRef);
        comnDataReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {

                    total_ad_sen_by_user = documentSnapshot.getString("ad_seen_by_user");

                    DocumentReference dc = firebaseFirestore.collection("Plan").document(plan_name);
                    dc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            if (documentSnapshot != null) {

                                userDataReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                        balence = documentSnapshot.getString("wallet");
                                        Double bale = Double.parseDouble(balence);
                                        bale = Math.round(bale * 100.00) / 100.00;
                                        userWallet.setText("Rs. " + bale + "/-");

                                        setdataOnView(ad_limit, per_ad_income, progressDialog);

                                    }
                                });
                                ad_limit = documentSnapshot.getString("total_ad_seen");
                                per_ad_income = documentSnapshot.getString("per_ad_income");

                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(AdsDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AdsDataActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkTaskRef(DocumentReference adminRef) {
        adminRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                String appstatus = documentSnapshot.getString("task_refresh");
                if (appstatus.equals("OFF")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdsDataActivity.this);
                    alertDialog.setTitle("Please Wait!");
                    alertDialog.setMessage("Our app is Refresh your Previous Task. Please wait until task is refreshed (Almost 15 minutes).");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    }).show();

                }
            }
        });


    }

    private void checkSharedPref() {
        if (plan_name.equals("Free Plan")) {

            if (withdraw_request_count.equals("0")) {

                boolean bal = Float.parseFloat(balence) >= 1.0;
                if (bal) {
                    String s = sharedPreferences.getString("visited", "");

                    if (s.equals("")) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("visited", "0");
                        editor.commit();

                    } else {
                        if (s.equals("0")) {
                            skinImage.setVisibility(View.VISIBLE);
                            //   Toast.makeText(this, "0", Toast.LENGTH_SHORT).show();
                        } else if (s.equals("1")) {
                            skinImage.setVisibility(View.GONE);
                            // Toast.makeText(this, "1", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    skinImage.setVisibility(View.GONE);
                }

            } else {
                skinImage.setVisibility(View.GONE);
            }
        } else if (plan_name.equals("Standard Plan")) {
            skinImage.setVisibility(View.GONE);
        } else if (plan_name.equals("Advance Plan")) {
            skinImage.setVisibility(View.GONE);
        } else if (plan_name.equals("Super Premium Plan")) {
            skinImage.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //check common data is available
    private void checkdatabase(DocumentReference reference) {

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    //  progressDialog.dismiss();

                } else {
                    createdatabase();
                }
            }
        });

    }

    //if common data is not available then create new data
    private void createdatabase() {

        final Map map = new HashMap();
        map.put("ad_seen_by_user", "0");
        map.put("web_visit_count", "0");

        comnDataReference.set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //   progressDialog.dismiss();
                    }
                });

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkwithdrawLimit();
    }
}
