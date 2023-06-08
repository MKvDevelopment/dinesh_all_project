package com.work_int.athome;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IUnityAdsInitializationListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private TextView tv_wallet, tv_captchaPrice, tv_captcha, tv_incre_income;
    private Button btn_withdraw, btn_verify;
    private EditText ed_captcha;
    private ImageView img_refresh;
    private String wallet, whatsp_support_link;
    private String captcha_price, code, spinOnOff;
    public static String userName, userEmail, userMobile;
    private String enteredCode, total_install;
    private String withdraw_limit, withdraw_limit2, withdraw_limit3, withdraw_limit4, activation_fee, appOnOff, off_reason, app_version;
    private String add_promotion, add_app_link, add_image_link;
    String RewardAdId = "Rewarded_Android";
    String interstialAdId = "Interstitial_Android";
    private DocumentReference userRef, dailyTask, promotionRef, adminRef;
    private ProgressDialog progressDialog;
    private CollectionReference promotionCollectionReference;
    private ActivityResultLauncher<Intent> promotionResultLauncher;

    private IUnityAdsLoadListener loadListener;
    private IUnityAdsShowListener showListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //off screenshot
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //title color change
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(R.color.primary_dark));

        String GAME_ID = "4595794";
        UnityAds.initialize(getApplicationContext(), GAME_ID, false, this);

        showListner = new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {
                // Toast.makeText(getApplicationContext(), "Unity ads failed Show listner ="+unityAdsShowError, Toast.LENGTH_SHORT).show();
                if (unityAdsShowError.equals(UnityAds.UnityAdsShowError.NOT_READY) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.NO_CONNECTION) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.NOT_INITIALIZED) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.INTERNAL_ERROR)) {
                }

            }

            @Override
            public void onUnityAdsShowStart(String s) {
                Toast.makeText(getApplicationContext(), "Watch Complete to Get Reward", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnityAdsShowClick(String s) {
                //  Toast.makeText(getApplicationContext(), "Show click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnityAdsShowComplete(String adUnitId, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {

                if (unityAdsShowCompletionState.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                    //Toast.makeText(getApplicationContext(), "Ads Completed", Toast.LENGTH_SHORT).show();

                    progressDialog.show();

                    progressDialog.setMessage("Re-freshing code...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    img_refresh.setEnabled(false);
                    new Handler().postDelayed(MainActivity.this::generateCode, 1000);
                } else {
                    //  Toast.makeText(getApplicationContext(), "skipped by user", Toast.LENGTH_SHORT).show();
                }
            }
        };
        loadListener = new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String s) {
                //  Toast.makeText(getApplicationContext(), "Ad Load listner", Toast.LENGTH_SHORT).show();
                //   UnityAds.show(AdsTaskActivity.this,AdId,new UnityAdsShowOptions(),showListner);
            }

            @Override
            public void onUnityAdsFailedToLoad(String addId, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {
                //  Toast.makeText(getApplicationContext(), "Unity Ads load failed listner  =="+s1, Toast.LENGTH_SHORT).show();
            }
        };

        //Banner Ads
        String bannerAdId = "Banner_Android";
        UnityBannerListener bannerListener = new UnityBannerListener();
        BannerView topBanner = new BannerView(this, bannerAdId, new UnityBannerSize(320, 50));
        // Set the listener for banner lifcycle events:
        topBanner.setListener(bannerListener);
        ((ViewGroup) findViewById(R.id.banner_ad_layout)).addView(topBanner);
        topBanner.load();
        //Banner Ads

        findViewById();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                , drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(Color.WHITE);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userRef = FirebaseFirestore.getInstance().collection("User_List").document(uid);
        adminRef = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");
        dailyTask = FirebaseFirestore.getInstance().collection("Today_Task").document(uid);
        promotionRef = FirebaseFirestore.getInstance().collection("Promotion_user").document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()));
        promotionCollectionReference = FirebaseFirestore.getInstance().collection("Promotion_user");


        promotionCollectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int size = queryDocumentSnapshots.size();
            //   Toast.makeText(this, "Total Promotion User is "+size, Toast.LENGTH_SHORT).show();
        });

        //event
        promotionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    updateUserPromationWallet();
                });

        img_refresh.setOnClickListener(view -> {
            DisplayRewardedAd(RewardAdId);
        });

        findViewById(R.id.btn_today).setOnClickListener(view ->
                checkUserDailyTask());

        findViewById(R.id.btn_spin).setOnClickListener(view ->
                checkSpinDataBase());

        findViewById(R.id.spin_card).setOnClickListener(view ->
                install_application());
        findViewById(R.id.textView25).setOnClickListener(view ->
        {
            Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatsp_support_link));
            startActivity(myIntent);
        });

        btn_verify.setOnClickListener(view -> {
            enteredCode = ed_captcha.getText().toString();

            if (Float.parseFloat(withdraw_limit) <= Float.parseFloat(wallet)) {
                Toast.makeText(getApplicationContext(), "Now your are eligible to Withdraw!", Toast.LENGTH_SHORT).show();
            } else {
                if (!enteredCode.isEmpty()) {
                    waitingMethod();
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter Code!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        btn_withdraw.setOnClickListener(view -> checkWalletBalance());

        tv_incre_income.setOnClickListener(view -> {
            if (!captcha_price.contains("40")) {
                // showPopUp();
                startActivity(new Intent(MainActivity.this, PlanActivity.class));

            } else {
                Toast.makeText(this, "Dear User, You have the Highest-Paid Plan of our Application.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateUserPromationWallet() {
        float total = (float) (Float.parseFloat(wallet) + 50.0);
        double bale = Double.parseDouble(String.valueOf(total));
        bale = Math.round(bale * 100.00) / 100.00;

        Map<String, Object> map = new HashMap<>();
        map.put("wallet", String.valueOf(bale));
        userRef.update(map).addOnSuccessListener(unused -> Toast.makeText(this, "Wallet Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());

        Map<String, Object> map1 = new HashMap<>();
        map1.put("install", "YES");
        promotionRef.set(map1);

    }

    private void checkAdsInstallation() {
        if (add_promotion.equals("Yes")) {
            promotionRef.addSnapshotListener((value, error) -> {
                assert value != null;
                if (value.exists()) {
                    findViewById(R.id.spin_card).setVisibility(View.GONE);
                } else {

                    if (isPackageExisted("com.socialmediasaver.status", this)) {
                        updateUserPromationWallet();
                    } else {
                        ImageView imageView = findViewById(R.id.imgg);
                        Picasso.get().load(add_image_link).into(imageView);
                        findViewById(R.id.spin_card).setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            findViewById(R.id.spin_card).setVisibility(View.GONE);
        }

    }

    private void install_application() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Install To Get $ 50!")
                .setMessage("Dear User, You have to Install this application and give 5 Start Rating on Playstore to Get $ 50 in your wallet.")
                .setCancelable(false)
                .setPositiveButton("Install Now", (dialogInterface, i) -> {
                    //go to the playstore link
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(add_app_link));
                        promotionResultLauncher.launch(intent);
                    } catch (android.content.ActivityNotFoundException anfe) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(add_app_link));
                        promotionResultLauncher.launch(intent);
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void checkSpinDataBase() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference("Spin_User").child(uid);

        firebaseDatabase.get().addOnSuccessListener(dataSnapshot -> {

            if (dataSnapshot.exists()) {
                Intent intent = new Intent(MainActivity.this, SpinActivity.class);
                startActivity(intent);
                progressDialog.dismiss();
            } else {

                Map map = new HashMap();
                map.put("uid", uid);
                map.put("winning_balence", "1");
                map.put("index", "0");

                firebaseDatabase.setValue(map).addOnSuccessListener(unused -> {
                    Intent intent = new Intent(MainActivity.this, SpinActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
            }

        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    public void DisplayRewardedAd(String videoAdId) {
        UnityAds.load(videoAdId, new UnityAdsLoadOptions(), loadListener);
        UnityAds.show(MainActivity.this, videoAdId, new UnityAdsShowOptions(), showListner);
    }

    private void checkUserDailyTask() {

        progressDialog.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String subStartDate = simpleDateFormat1.format(calendar.getTime());
        String subEndDate = null;
        try {
            Date date = simpleDateFormat1.parse(subStartDate);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            subEndDate = simpleDateFormat1.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String finalSubEndDate = subEndDate;


        Map map = new HashMap();
        map.put("task1", "");
        map.put("task2", "");
        map.put("task3", "");
        map.put("task4", "");
        map.put("task5", "");
        map.put("task6", "");
        map.put("task7", "");
        map.put("task8", "");
        map.put("task9", "");
        map.put("task10", "");
        map.put("time", finalSubEndDate);

        dailyTask.get().addOnSuccessListener((value) -> {
            if (value.exists()) {
                //check task refresh data
                String time = value.getString("time");
                try {
                    Date date1 = simpleDateFormat1.parse(subStartDate);
                    Date date2 = simpleDateFormat1.parse(time);

                    if (date1.compareTo(date2) < 0) {
                        //tomarrow is last date
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                    } else if (date1.compareTo(date2) > 0) {
                        //date is expired yesterday
                        //Toast.makeText(getApplicationContext(), "expired", Toast.LENGTH_SHORT).show();
                        dailyTask.set(map).addOnSuccessListener(unused -> {
                            startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                    } else {
                        //date is same
                        progressDialog.dismiss();
                        dailyTask.set(map).addOnSuccessListener(unused -> {
                            startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            } else {
                //set daily task data of user on firebase
                dailyTask.set(map).addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void checkWalletBalance() {

        switch (captcha_price) {
            case "5":
                Intent intent5 = new Intent(MainActivity.this, WithdrawActivity.class);
                intent5.putExtra("fee", activation_fee);
                intent5.putExtra("limit", withdraw_limit);
                startActivity(intent5);
                break;
            case "7":
                Intent intent2 = new Intent(MainActivity.this, WithdrawActivity.class);
                intent2.putExtra("fee", activation_fee);
                intent2.putExtra("limit", withdraw_limit2);
                startActivity(intent2);
                break;
            case "10":
                Intent intent3 = new Intent(MainActivity.this, WithdrawActivity.class);
                intent3.putExtra("fee", activation_fee);
                intent3.putExtra("limit", withdraw_limit3);
                startActivity(intent3);
                break;
            case "15":
                Intent intent4 = new Intent(MainActivity.this, WithdrawActivity.class);
                intent4.putExtra("fee", activation_fee);
                intent4.putExtra("limit", withdraw_limit4);
                startActivity(intent4);
                break;
        }

    }

    private void updateUserPlan(String newPrice) {

        userRef
                .update("captcha_price", newPrice)
                .addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Update Successfully", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    private void waitingMethod() {
        progressDialog.setMessage("Code checking, Please wait ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.show();

        btn_verify.setEnabled(false);
        img_refresh.setEnabled(false);

        new Handler().postDelayed(() ->
                checkCaptchaCode(enteredCode), 2500);
    }

    private void checkCaptchaCode(String enteredCode) {
        progressDialog.dismiss();
        btn_verify.setEnabled(true);
        img_refresh.setEnabled(true);
        if (enteredCode.equals(code)) {
            //if code is right
            addMoneyCode();

        } else {
            DisplayRewardedAd(RewardAdId);
            ed_captcha.getText().clear();
            Toast.makeText(MainActivity.this, "Enter Correct Code", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void addMoneyCode() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Congratulation!");
        alertDialog.setMessage("Code is Correct");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("AddMoney", (dialog, which) -> {

            progressDialog.setMessage("Updating balance...");
            progressDialog.show();
            new Handler().postDelayed(() -> {
                Float bal = Float.parseFloat(wallet) + Float.parseFloat(captcha_price);

                double bale = Double.parseDouble(String.valueOf(bal));
                bale = Math.round(bale * 100.00) / 100.00;

                userRef.update("wallet", String.valueOf(bale))
                        .addOnCompleteListener(task -> {
                            tv_wallet.setText("Wallet Balence :- $ " + wallet + "/-");

                            Toast.makeText(MainActivity.this, "Amount added successfully! :)", Toast.LENGTH_SHORT).show();
                            generateCode();
                        });
            }, 3000);


        });
        AlertDialog alert = alertDialog.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));

    }

    @SuppressLint("SetTextI18n")
    private void setDataOnViews() {

        tv_wallet.setText("Wallet Balance :-$ " + wallet + "/-");
        tv_captchaPrice.setText("Per Code Income :- $ " + captcha_price + "/-");

        if (Float.parseFloat(wallet) < 10.0) {
            tv_incre_income.setVisibility(View.GONE);
        } else {
            tv_incre_income.setVisibility(View.VISIBLE);
        }

        if (spinOnOff.equals("OFF")) {
            findViewById(R.id.btn_spin).setVisibility(View.GONE);

        } else if (Float.parseFloat(wallet) >= 10.0) {
            findViewById(R.id.btn_spin).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btn_spin).setVisibility(View.GONE);
        }

    }

    public static boolean isPackageExisted(String targetPackage, Context context) {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }

    private int getCurrentAppVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void checkForUpdate() {

        String app_versionn = String.valueOf(getCurrentAppVersionCode());
        if (!app_versionn.contains(app_version)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Update Available!")
                    .setMessage("New Version is Available. Update now otherwise you can not access our Application")
                    .setCancelable(false)
                    .setPositiveButton("Update Now", (dialogInterface, i) -> {
                        //go to the playstore link
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                        startActivity(intent);
                    });
            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));

        }

    }

    private void showAppOffDialog() {
        if (appOnOff.contains("Off")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle("Sorry!")
                    .setMessage(off_reason)
                    .setPositiveButton("Exit", (dialogInterface, i) -> {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    });
            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));

        }
    }

    private void findViewById() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        tv_wallet = findViewById(R.id.textView2);
        tv_captcha = findViewById(R.id.textView4);
        tv_captchaPrice = findViewById(R.id.textView3);
        tv_incre_income = findViewById(R.id.textView5);
        btn_verify = findViewById(R.id.appCompatButton);
        btn_withdraw = findViewById(R.id.btn_withdraw);
        ed_captcha = findViewById(R.id.editText);
        img_refresh = findViewById(R.id.imageView2);

    }

    private void generateCode() {
        progressDialog.setMessage("Re-freshing code ...");
        progressDialog.show();

        new Handler().postDelayed(() -> {
            img_refresh.setEnabled(true);
            btn_verify.setEnabled(true);
            ed_captcha.getText().clear();
            code = randomGererate();
            tv_captcha.setText(code);

            progressDialog.dismiss();

        }, 2500);
    }

    private String randomGererate() {
        String LETTERS = "abcdefghijkmnopqrstuvwxy";

        char[] Free_Plan_CODE = (("0123456789") + LETTERS.toLowerCase() + LETTERS.toUpperCase()).toCharArray();
        // char[] Free_Plan_CODE = ("0123456789").toCharArray();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            result.append(Free_Plan_CODE[new Random().nextInt(Free_Plan_CODE.length)]);
        }
        return result.toString();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(false);
        switch (item.getItemId()) {
            case R.id.nav_widrow_money:
                checkWalletBalance();
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_terms:
                Intent intent2 = new Intent(MainActivity.this, ContentActivity.class);
                intent2.putExtra("name", "Terms and Conditions");
                intent2.putExtra("file_name", "terms");
                startActivity(intent2);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_policy:
                Intent intent1 = new Intent(MainActivity.this, ContentActivity.class);
                intent1.putExtra("name", "Privacy Policy");
                intent1.putExtra("file_name", "privacy");
                startActivity(intent1);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_exit:
                drawer.closeDrawer(GravityCompat.START);
                exitDialog();
                break;
        }

        return true;
    }

    private void exitDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Exit Confirmation");
        alertDialogBuilder
                .setMessage("Click yes to Exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int txnRequestCode = 110;
        if (requestCode == txnRequestCode && data != null) {
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
        } else if (requestCode == 1 && data != null) {

            int plan = Integer.parseInt(data.getStringExtra("plan"));
            new Handler().postDelayed(() -> {
                progressDialog.setMessage("Updating Plan...");
                if (plan == 1) {
                    updateUserPlan("10");
                } else if (plan == 2) {
                    updateUserPlan("22");
                } else if (plan == 3) {
                    updateUserPlan("30");
                }

            }, 2000);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adminRef.addSnapshotListener((value1, error1) -> {
            assert value1 != null;
            withdraw_limit = value1.getString("withdraw_limit");
            withdraw_limit2 = value1.getString("withdraw_limit2");
            withdraw_limit3 = value1.getString("withdraw_limit3");
            withdraw_limit4 = value1.getString("withdraw_limit4");

            spinOnOff = value1.getString("spinonoff");
            activation_fee = value1.getString("accunt_activation_fee");
            appOnOff = value1.getString("appOnOff");
            total_install = value1.getString("total_install");
            off_reason = value1.getString("off_reason");
            app_version = value1.getString("app_version");
            add_promotion = value1.getString("add_promotion");
            add_image_link = value1.getString("add_image_link");
            add_app_link = value1.getString("add_app_link");
            whatsp_support_link = value1.getString("whatsp_support_link");

            showAppOffDialog();
            checkForUpdate();
            checkAdsInstallation();
            userRef.addSnapshotListener((value, error) -> {
                assert value != null;
                wallet = value.getString("wallet");
                captcha_price = value.getString("captcha_price");
                userName = value.getString("userName");
                userEmail = value.getString("email");
                userMobile = value.getString("mobile");
                generateCode();
                setDataOnViews();
                if (userName.isEmpty() || userMobile.isEmpty()) {
                    AlertDialog custome_dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                    View view = LayoutInflater.from(this).inflate(R.layout.option_icon_layout, null, false);

                    EditText name = view.findViewById(R.id.textView16);
                    EditText email = view.findViewById(R.id.textView18);
                    EditText mobile = view.findViewById(R.id.textView17);
                    Button submit = view.findViewById(R.id.btn1_submit);

                    if (!userEmail.isEmpty()) {
                        email.setText(userEmail);
                        email.setEnabled(false);
                    }
                    if (!userMobile.isEmpty()) {
                        mobile.setText(userMobile);
                        mobile.setEnabled(false);
                    }
                    builder.setView(view);
                    custome_dialog = builder.create();
                    custome_dialog.setCancelable(false);
                    custome_dialog.setCanceledOnTouchOutside(false);
                    custome_dialog.show();


                    submit.setOnClickListener(v -> {
                        if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty() || mobile.getText().toString().isEmpty()) {
                            Toast.makeText(getApplicationContext(), "All Field's Compulsory!", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.setTitle("Please Wait!");
                            progressDialog.setMessage("Uploading Data...");
                            progressDialog.show();

                            Map map = new HashMap();
                            map.put("userName", name.getText().toString().trim());
                            map.put("email", email.getText().toString().trim());
                            map.put("mobile", mobile.getText().toString().trim());

                            userRef.update(map).addOnSuccessListener(o -> {
                                progressDialog.dismiss();
                                custome_dialog.dismiss();
                            }).addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });

                }
            });
        });
    }

    @Override
    public void onInitializationComplete() {
        //  Toast.makeText(this, "init", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
        //   Toast.makeText(this, "fald", Toast.LENGTH_SHORT).show();
    }
}

// Implement listener methods:
class UnityBannerListener implements BannerView.IListener {
    @Override
    public void onBannerLoaded(BannerView bannerAdView) {
        // Called when the banner is loaded.
        // Toast.makeText(bannerAdView.getContext(), "loaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
        //  Log.d("SupportTest", "Banner Error" + s);
        //  Toast.makeText(bannerAdView.getContext(), "failed:- " + errorInfo.errorMessage, Toast.LENGTH_SHORT).show();
        // Note that the BannerErrorInfo object can indicate a no fill (see API documentation).
    }

    @Override
    public void onBannerClick(BannerView bannerAdView) {
        // Called when a banner is clicked.
    }

    @Override
    public void onBannerLeftApplication(BannerView bannerAdView) {
        // Called when the banner links out of the application.
        //   Toast.makeText(bannerAdView.getContext(), "left", Toast.LENGTH_SHORT).show();
    }
}
