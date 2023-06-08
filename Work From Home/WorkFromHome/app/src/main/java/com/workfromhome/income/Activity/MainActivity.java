package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.startapp.sdk.adsbase.StartAppAd;
import com.workfromhome.income.OneTimeActivity.ContactActivity;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.OneTimeActivity.PrivacyActivity;
import com.workfromhome.income.OneTimeActivity.TermsActivity;
import com.workfromhome.income.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String RATE_US_URL, HOW_TO_WORK_LINK, CALL, EMAIL, WHATSAPP, firebase_app_version;

    private android.app.AlertDialog alertDialog;
    private FirebaseFirestore db;
    private DocumentReference documentReference1, documentReference2, adminReflimit, adminRef, everydayuser, spinUser;
    private CollectionReference collectionReference1, reference2;
    private NetworkChangeReceiver broadcastReceiver;
    private String stauts, reason;
    private SharedPreferences sharedPref;
    private CollectionReference appBlockReference, spinCol;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView walletBalance, user_plan;
    public static String userBlockReason, userStatus;
    private CardView bannerPermotation;
    private String user_name, email, balence, plan_name, image, usr_rate_count, appStatus,
            withdraw_request_count, everdayBtnVisiblity, taskRefresh, everydaytime, spinBtnVisiblity, querkalink;
    private Button withdraw, upgradePlan, today_work1, today_work2, howToWork, referEarn, fullTimeWork, everyday, spin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StartAppAd.disableSplash();
        StartAppAd.enableAutoInterstitial();
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        findView();

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //progress bar
        alertDialog = new SpotsDialog.Builder().setContext(MainActivity.this)
                .setTheme(R.style.Custom)
                .setCancelable(false)
                .build();
        alertDialog.setCanceledOnTouchOutside(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home_icon);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                , drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        db = FirebaseFirestore.getInstance();
        //check app on/Off condition
        appBlockReference = FirebaseFirestore.getInstance().collection("App_utils");

        sharedPref = getSharedPreferences("skin", MODE_PRIVATE);

        //server work
        collectionReference1 = db.collection("users");
        reference2 = db.collection("common_data");
        spinCol = db.collection("Spin_users");

        documentReference1 = collectionReference1.document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        documentReference2 = reference2.document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        adminRef = db.collection("App_utils").document("App_status");
        everydayuser = db.collection("Plan").document("Everyday Plan");

        adminRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                taskRefresh = documentSnapshot.getString("task_refresh");
                everdayBtnVisiblity = documentSnapshot.getString("everydayplanstatus");
                spinBtnVisiblity = documentSnapshot.getString("funbtnstatus");

                if (spinBtnVisiblity.equals("ON")) {
                    spin.setVisibility(View.VISIBLE);
                } else {
                    spin.setVisibility(View.GONE);
                }

                if (everdayBtnVisiblity.equals("ON")) {
                    everyday.setVisibility(View.VISIBLE);
                } else {
                    everyday.setVisibility(View.GONE);
                }

            }
        });

        everydayuser.addSnapshotListener((documentSnapshot, e) -> everydaytime = documentSnapshot.getString("workstatus"));

        adminReflimit = FirebaseFirestore.getInstance().collection("Plan").document("Full_Time_Plan");

        adminReflimit.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                appStatus = documentSnapshot.getString("workstatus");
            }
        });

        //Events listners
        upgradePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UpgradePlanActivity.class));
            }
        });

        fullTimeWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkFullTimeUserExit();
            }
        });

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("plan", plan_name);
                startActivity(intent);
            }
        });

        referEarn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ReferActivity.class);
                intent.putExtra("wallet", balence);
                startActivity(intent);
            }
        });

        everyday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEverydayUserExit();
                // startActivity(new Intent(MainActivity.this, EverydayPayActivity.class));
            }
        });

        howToWork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.HOW_TO_WORK_LINK));
                startActivity(intent);
            }
        });

        today_work1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkRefereshOnOff();
            }
        });

        today_work2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                adminRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                        if (documentSnapshot.getString("task_refresh").equals("ON")) {
                            Intent intent = new Intent(MainActivity.this, WebDataActivity.class);
                            intent.putExtra("plan", plan_name);
                            intent.putExtra("bale", balence);
                            intent.putExtra("email", email);
                            intent.putExtra("count", withdraw_request_count);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Please wait..... Task is Refreshing...", Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });

        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserForSpin();
            }
        });

        bannerPermotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(querkalink));
                startActivity(intent);
            }
        });

    }

    private String randomGererate() {

        char[] position = ("12345").toCharArray();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 1; i++) {
            result.append(position[new Random().nextInt(position.length)]);
        }
        return result.toString();
    }

    private void checkUserForSpin() {

        if (Float.parseFloat(balence) >= 5.0) {

            alertDialog.setTitle("Please Wait!");
            alertDialog.setMessage("Fetching Detail...");
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            spinCol
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.getResult().isEmpty()) {

                            Map map = new HashMap();
                            map.put("email", email);
                            map.put("spin_row", randomGererate());
                            map.put("spin_bal", "0");
                            map.put("winning_bal", "0");
                            map.put("index", "0");

                            DocumentReference documentReference = spinCol.document(email);
                            documentReference.set(map).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    alertDialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, SpinActivity.class));
                                } else {
                                    alertDialog.dismiss();
                                    Toast.makeText(MainActivity.this, task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

                        } else {
                            alertDialog.dismiss();
                            startActivity(new Intent(MainActivity.this, SpinActivity.class));
                        }
                    });
        } else {
            androidx.appcompat.app.AlertDialog.Builder timealertDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
            timealertDialog.setTitle("Alert!");
            timealertDialog.setMessage("Need minimum Rs. 5 in your wallet to activate this work.");
            timealertDialog.setCancelable(false);
            timealertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).show();
        }


    }

    private void checkEverydayUserExit() {

        alertDialog.setTitle("Please Wait!");
        alertDialog.setMessage("Fetching Detail...");
        alertDialog.show();
        CollectionReference userReference = FirebaseFirestore.getInstance().collection("everyday_users");
        userReference
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            startActivity(new Intent(MainActivity.this, EverydayPayActivity.class));
                            alertDialog.dismiss();

                        } else {
                            if (everydaytime.equals("OFF")) {
                                alertDialog.dismiss();
                                androidx.appcompat.app.AlertDialog.Builder timealertDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                                timealertDialog.setTitle("Alert!");
                                timealertDialog.setMessage("Work time is 9:00 AM to 5:00 PM.");
                                timealertDialog.setCancelable(false);
                                timealertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            } else {
                                checkTokenIdForEveryday();
                            }
                        }
                    }
                });


    }

    private void checkRefereshOnOff() {

        if (taskRefresh.equals("ON")) {
            Intent intent = new Intent(MainActivity.this, AdsDataActivity.class);
            intent.putExtra("plan", plan_name);
            intent.putExtra("email", email);
            intent.putExtra("wallet", balence);
            intent.putExtra("count", withdraw_request_count);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Please wait..... Task is Refreshing...", Toast.LENGTH_LONG).show();
        }

    }

    private void checkFullTimeUserExit() {
        alertDialog.setTitle("Please Wait!");
        alertDialog.setMessage("Fetching Detail...");
        alertDialog.show();
        CollectionReference userReference = FirebaseFirestore.getInstance().collection("full_time_user");
        userReference
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.getResult().isEmpty()) {

                            if (appStatus.equals("OFF")) {
                                alertDialog.dismiss();
                                androidx.appcompat.app.AlertDialog.Builder timealertDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                                timealertDialog.setTitle("Alert!");
                                timealertDialog.setMessage("Work time is 9:00 AM to 5:00 PM.");
                                timealertDialog.setCancelable(false);
                                timealertDialog.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            } else {
                                //check device id for multiple devices
                                checkTokenIdForFullTime();
                            }
                        } else {
                            startActivity(new Intent(MainActivity.this, FullTimePayActivity.class));
                            alertDialog.dismiss();
                        }
                    }
                });


    }

    //check token id for full time user multipele device
    private void checkTokenIdForFullTime() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("full_time_user");
        collectionReference
                .whereEqualTo("token_id", id)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.getResult().isEmpty()) {
                            alertDialog.dismiss();
                            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                            alert.setTitle("Alert!");
                            alert.setMessage("If you want to login to this device, then you automatically logout from all other devices. Are you sure want to login in this device?");
                            alert.setCancelable(false);
                            alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    alertDialog.show();
                                    DocumentReference documentReference = FirebaseFirestore.getInstance().collection("full_time_user").document(email);
                                    documentReference.update("token_id", id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            alertDialog.dismiss();
                                            startActivity(new Intent(MainActivity.this, FullTimeActivity.class));
                                        }
                                    });
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).show();


                        } else {
                            alertDialog.dismiss();
                            startActivity(new Intent(MainActivity.this, FullTimeActivity.class));
                        }
                    }
                });

    }

    //check token id for full time user multipele device
    private void checkTokenIdForEveryday() {
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("everyday_users");
        collectionReference
                .whereEqualTo("token_id", id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().isEmpty()) {
                        alertDialog.dismiss();
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setTitle("Alert!");
                        alert.setMessage("If you want to login to this device, then you automatically logout from all other devices. Are you sure want to login in this device?");
                        alert.setCancelable(false);
                        alert.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                alertDialog.show();
                                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("everyday_users").document(email);
                                documentReference.update("token_id", id).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        alertDialog.dismiss();
                                        startActivity(new Intent(MainActivity.this, EverydayWrokActivity.class));
                                    }
                                });
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();


                    } else {
                        alertDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, EverydayWrokActivity.class));
                    }
                });

    }

    private void checkForUpdate() {

        String app_version = String.valueOf(getCurrentAppVersionCode());

        if (!app_version.contains(firebase_app_version)) {

            new AlertDialog.Builder(this).setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //go to the playstore link
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(RATE_US_URL));
                    startActivity(intent);
                    finish();
                }
            }).setTitle("Update Available!")
                    .setMessage("New Version is Available. Update now otherwise you can not access our Application")
                    .setCancelable(false).show();
        }

    }

    private int getCurrentAppVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    //check common data is available
    private void checkdatabase(DocumentReference reference) {

        reference.get().addOnCompleteListener(task -> {
            if (task.getResult().exists()) {
                alertDialog.dismiss();

            } else {
                createdatabase();
            }
        });

    }

    //if common data is not available then create new data
    private void createdatabase() {

        final Map map = new HashMap();
        map.put("ad_seen_by_user", "0");
        map.put("web_visit_count", "0");

        documentReference2.set(map)
                .addOnSuccessListener(aVoid -> alertDialog.dismiss());

    }

    //getting data from server
    private void getDataFromServer(DocumentReference reference) {

        alertDialog.setMessage("Loading....");
        alertDialog.show();
        checkdatabase(documentReference2);

        reference
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot != null) {
                        user_name = documentSnapshot.getString("name");
                        plan_name = documentSnapshot.getString("plan");
                        email = documentSnapshot.getString("email");
                        image = documentSnapshot.getString("image");
                        balence = documentSnapshot.getString("wallet");
                        withdraw_request_count = documentSnapshot.getString("withdraw_count");
                        usr_rate_count = documentSnapshot.getString("user_rate_count");
                        userBlockReason = documentSnapshot.getString("block_reason");
                        userStatus = documentSnapshot.getString("user_status");

                        setMainData(balence, plan_name);
                        setHeaderData(email, image, user_name);

                    } else {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


    private void setMainData(String bal, String plan) {
        user_plan.setText(plan);
        if (bal == null) {
            bal = "0.0";
        }
        Double bale = Double.parseDouble(bal);
        bale = Math.round(bale * 100.00) / 100.00;
        walletBalance.setText("Rs. " + bale + "/-");
        checkUserBlock();

    }

    //setting navigation header data
    private void setHeaderData(String eml, String img, String nm) {
        ImageView headerImage;
        TextView headerEmail, headerName;

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View view = navigationView.getHeaderView(0);

        headerName = view.findViewById(R.id.nav_user_name);
        headerEmail = view.findViewById(R.id.nav_user_email);
        headerImage = view.findViewById(R.id.nav_user_image);

        headerName.setText(nm);
        headerEmail.setText(eml);
        Picasso.get().load(img).into(headerImage);
    }

    //initiate all views
    private void findView() {
        spin = findViewById(R.id.btn_spin);
        fullTimeWork = findViewById(R.id.btn_fulTime);
        withdraw = findViewById(R.id.withdraw_btn);
        upgradePlan = findViewById(R.id.upgrade_btn);
        today_work1 = findViewById(R.id.btn_task1);
        today_work2 = findViewById(R.id.btn_task2);
        howToWork = findViewById(R.id.how_to_work);
        referEarn = findViewById(R.id.refer_btn);
        walletBalance = findViewById(R.id.wallet);
        user_plan = findViewById(R.id.plan);
        everyday = findViewById(R.id.btn_everyday);
        bannerPermotation=findViewById(R.id.queraBanner);
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);
        return true;
    }*/

    /*  @Override
      public boolean onOptionsItemSelected(@NonNull MenuItem item) {
          if (item.getItemId() == R.id.main_noti) {
              startActivity( new Intent(MainActivity.this,NotificatonActivity.class));
          }
          return true;
      }*/
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            /*case R.id.menu_noti:{
                startActivity( new Intent(MainActivity.this, NotificatonActivity.class));
                break;
            }*/
            case R.id.menu_getMoreMoney:
                checkPlanForGetMoreMoney(plan_name);
                navigationView.getMenu().getItem(0).setChecked(false);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_withdraw:
                Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("plan", plan_name);
                startActivity(intent);
                navigationView.getMenu().getItem(1).setChecked(false);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_refer:
                Intent intents = new Intent(MainActivity.this, ReferActivity.class);
                intents.putExtra("wallet", balence);
                startActivity(intents);
                navigationView.getMenu().getItem(2).setChecked(false);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_contect_by_mail:
                checkPlanforMail(plan_name);
                navigationView.getMenu().getItem(3).setChecked(false);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_watsp:
                checkPlanForWatsp(plan_name);
                navigationView.getMenu().getItem(4).setChecked(false);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_call_us:
                checkPlanForCall(plan_name);
                navigationView.getMenu().getItem(5).setChecked(false);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_terms:
                startActivity(new Intent(this, TermsActivity.class));
                navigationView.getMenu().getItem(6).setChecked(false);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_privacy_policy:
                startActivity(new Intent(this, PrivacyActivity.class));
                navigationView.getMenu().getItem(7).setChecked(false);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;


            case R.id.menu_logout:
                finish();
                break;
        }
        return true;
    }

    private void checkPlanForGetMoreMoney(String plan) {

        final int rate_count = Integer.parseInt(usr_rate_count);
        if (rate_count == 0) {

            boolean bal = Float.parseFloat(balence) >= 3.0;
            if (bal) {
                switch (plan) {

                    case "Free Plan":
                        float amount = 2;
                        calculateDataForGetMoreMoney("free_plan", rate_count, amount);
                        break;

                    case "Standard Plan":
                        float amoun = 25;
                        calculateDataForGetMoreMoney("standard_plan", rate_count, amoun);
                        break;

                    case "Advance Plan":
                        float amout = 50;
                        calculateDataForGetMoreMoney("advance_plan", rate_count, amout);
                        break;

                    case "Super Premium Plan":
                        float amunt = 100;
                        calculateDataForGetMoreMoney("premium_plan", rate_count, amunt);
                        break;

                }
            } else {
                Toast.makeText(this, "You need minimum 3 Rs. to use this feature.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "You have already completed this task", Toast.LENGTH_SHORT).show();
        }

    }

    private void calculateDataForGetMoreMoney(String plan, int rate_count, float amount) {
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle("Please Wait!");
        dialog.setMessage("Fetching your Information....");
        dialog.show();

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("App_utils").document("Rate_Income");
        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            dialog.dismiss();
            String message = documentSnapshot.getString(plan);
            setAlertForRateUs(rate_count, message, amount);
        });
    }

    private void setAlertForRateUs(final int rate_count, String message, final float amount) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Alert!!");
        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("Continue", (dialog, which) -> {
            //go to the playstore link
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(RATE_US_URL));
            startActivity(intent);
            //update balance
            Float total_bal = Float.parseFloat(balence) + amount;
            int total_count = rate_count + 1;
            Map map = new HashMap();
            map.put("wallet", String.valueOf(total_bal));
            map.put("user_rate_count", String.valueOf(total_count));

            documentReference1.update(map);
            dialog.dismiss();
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();


    }

    //check plan for drawer menu
    private void checkPlanforMail(String plan) {

        if (plan.equals("Free Plan")) {

            String alertTitle = "Alert!";
            String msg = "You are not able to use this facility.\n If you want to use this you need to Upgrade Atleast Standard Plan.";
            String btnCancelText = "Upgrade Later";
            String btnUpgradeText = "Upgrade Now";
            Class clas_name = UpgradePlanActivity.class;
            setAlertForEmail(msg, alertTitle, btnUpgradeText, btnCancelText, clas_name);

        } else if (plan.equals("Standard Plan")) {

            String alertTitle = "Warning!";
            String msg = EMAIL;
            String btnCancelText = "Cancel";
            String btnContinueText = "Continue";
            Class clas_name = ContactActivity.class;

            setAlertForEmail(msg, alertTitle, btnContinueText, btnCancelText, clas_name);

        } else if (plan.equals("Advance Plan")) {
            String alertTitle = "Warning!";
            String msg = EMAIL;
            String btnCancelText = "Cancel";
            String btnContinueText = "Continue";
            Class clas_name = ContactActivity.class;
            setAlertForEmail(msg, alertTitle, btnContinueText, btnCancelText, clas_name);

        } else if (plan.equals("Super Premium Plan")) {
            String alertTitle = "Warning!";
            String msg = EMAIL;
            String btnCancelText = "Cancel";
            String btnContinueText = "Continue";

            Class clas_name = ContactActivity.class;
            setAlertForEmail(msg, alertTitle, btnContinueText, btnCancelText, clas_name);

        }


    }

    private void setAlertForEmail(String message, String title, final String btnPositiveText, String btnNegativeText, final Class clas_nm) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(btnPositiveText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (btnPositiveText.equals("Upgrade Now")) {
                    startActivity(new Intent(MainActivity.this, UpgradePlanActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Now you can contact our Team", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }).setNegativeButton(btnNegativeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();


    }

    private void checkPlanForWatsp(String plan_name) {

        if (plan_name.equals("Free Plan")) {
            String alertTitle = "Alert!";
            String msg = "You are not able to use this facility.\n If you want to use this you need to Upgrade Atleast Advance Plan.";
            String btnCancelText = "Upgrade Later";
            String btnUpgradeText = "Upgrade Now";
            setAlertForWatsp(msg, alertTitle, btnUpgradeText, btnCancelText);

        } else if (plan_name.equals("Standard Plan")) {
            String alertTitle = "Warning!";
            String msg = "You are not able to use this facility.\n If you want to use this you need to Upgrade Atleast Advance Plan.";
            String btnCancelText = "Cancel";
            String btnContinueText = "Upgrade Now";
            setAlertForWatsp(msg, alertTitle, btnContinueText, btnCancelText);

        } else if (plan_name.equals("Advance Plan")) {
            String alertTitle = "Warning!";
            String msg = WHATSAPP;
            String btnCancelText = "Cancel";
            String btnContinueText = "Continue";
            setAlertForWatsp(msg, alertTitle, btnContinueText, btnCancelText);

        } else if (plan_name.equals("Super Premium Plan")) {
            String alertTitle = "Warning!";
            String msg = WHATSAPP;
            String btnCancelText = "Cancel";
            String btnContinueText = "Continue";
            setAlertForWatsp(msg, alertTitle, btnContinueText, btnCancelText);
        }
    }

    private void setAlertForWatsp(String msg, String alertTitle, final String btnContinueText, String btnCancelText) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(alertTitle);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(btnContinueText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (btnContinueText.equals("Upgrade Now")) {
                    startActivity(new Intent(MainActivity.this, UpgradePlanActivity.class));

                } else {
                    Toast.makeText(MainActivity.this, "Now you can contact our Team", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }).setNegativeButton(btnCancelText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();


    }

    private void checkPlanForCall(String plan_name) {

        if (plan_name.equals("Free Plan")) {

            String alertTitle = "Alert!";
            String msg = "You are not able to use this facility.\n If you want to use this you need to Upgrade Super Premium Plan.";
            String btnCancelText = "Upgrade Later";
            String btnUpgradeText = "Upgrade Now";
            setAlertForCall(msg, alertTitle, btnUpgradeText, btnCancelText);

        } else if (plan_name.equals("Standard Plan")) {

            String alertTitle = "Warning!";
            String msg = "You are not able to use this facility.\n If you want to use this you need to Upgrade Super Premium Plan.";
            String btnCancelText = "Cancel";
            String btnContinueText = "Upgrade Now";
            setAlertForCall(msg, alertTitle, btnContinueText, btnCancelText);

        } else if (plan_name.equals("Advance Plan")) {
            String alertTitle = "Warning!";
            String msg = "You are not able to use this facility.\n If you want to use this you need to Upgrade Super Premium Plan.";
            String btnCancelText = "Cancel";
            String btnContinueText = "Upgrade Now";
            setAlertForCall(msg, alertTitle, btnContinueText, btnCancelText);

        } else if (plan_name.equals("Super Premium Plan")) {
            String alertTitle = "Warning!";
            String msg = CALL;
            String btnCancelText = "Cancel";
            String btnContinueText = "Continue";
            setAlertForCall(msg, alertTitle, btnContinueText, btnCancelText);

        }


    }

    private void setAlertForCall(String msg, String alertTitle, final String btnContinueText, String btnCancelText) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(alertTitle);
        alertDialog.setMessage(msg);
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton(btnContinueText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (btnContinueText.equals("Upgrade Now")) {
                    startActivity(new Intent(MainActivity.this, UpgradePlanActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Now you can contact our Team", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }).setNegativeButton(btnCancelText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();


    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Are you sure! You want to Exit?");
        alertDialog.setMessage("Click yes to exit!");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                MainActivity.super.onBackPressed();

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        final DocumentReference documentReference = appBlockReference.document("App_status");
        //fetch admin persional data
        final DocumentReference adminDataReference = appBlockReference.document("admin_email");
        adminDataReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    RATE_US_URL = documentSnapshot.getString("rate_us_link");
                    HOW_TO_WORK_LINK = documentSnapshot.getString("work_link");
                    CALL = documentSnapshot.getString("call");
                    EMAIL = documentSnapshot.getString("email");
                    WHATSAPP = documentSnapshot.getString("watsp");
                    firebase_app_version = documentSnapshot.getString("app_version");

                    checkForUpdate();
                }
            }
        });

        getDataFromServer(documentReference1);

        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot != null) {
                    stauts = documentSnapshot.getString("App_is");
                    reason = documentSnapshot.getString("reason");
                    String permotation = documentSnapshot.getString("querka");
                    querkalink = documentSnapshot.getString("querkalink");

                    if (stauts.contains("OFF")) {
                        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(MainActivity.this);
                        alertDialog.setTitle("Alert!");
                        alertDialog.setMessage(reason);
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Exit Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).show();
                    }else if (permotation.contains("ON")) {
                        bannerPermotation.setVisibility(View.VISIBLE);
                    }else if (permotation.contains("OFF")){
                        bannerPermotation.setVisibility(View.GONE);
                    }

                }
            }
        });

    }

    private void checkUserBlock() {
        if (userStatus.equals("Block")) {

            androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this);
            alertDialog.setTitle("Account Disabled!");
            alertDialog.setMessage(userBlockReason);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Exit Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            }).show();
        }
        return;

    }

}
