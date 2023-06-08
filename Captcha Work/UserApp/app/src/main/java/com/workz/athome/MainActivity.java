package com.workz.athome;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        PaymentResultListener,
        IUnityAdsInitializationListener{

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private TextView tv_wallet, tv_captchaPrice, tv_captcha, tv_incre_income, tv_timeleftt;
    private Button btn_withdraw, btn_verify;
    private EditText ed_captcha;
    private ImageView img_refresh;
    private String wallet,app_version2;
    private String captcha_price, code, spinOnOff, querka_link;
    public static String  paytm_mid, paytm_callback_url,razorpay_key;
    private String enteredCode, total_install;
    private String withdraw_limit, withdraw_limit2, withdraw_limit3, withdraw_limit4, activation_fee, appOnOff, off_reason, app_version;
    private String add_promotion, add_app_link, add_image_link;
    String RewardAdId = "Rewarded_Android";
    String interstialAdId = "Interstitial_Android";
    private DocumentReference userRef;
    private DocumentReference dailyTask;
    private DocumentReference promotionRef;
    private DocumentReference adminRef;
    private ProgressDialog progressDialog;
    private CollectionReference promotionCollectionReference;
    private ActivityResultLauncher<Intent> promotionResultLauncher;
    private CircleImageView bonous_btnn,btn_removeAds;
    public static float tempWallet, tempCaptchaPrice;
    private IUnityAdsLoadListener loadListener;
    private IUnityAdsShowListener showListner;
    private  SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    String tag="aaaaaaaaaaaaaaaa  :- ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //off screenshot
        //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

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
                    generateCode();
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

        findViewById();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer,
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
        Log.d(tag, "Main:---------------------------------------------- ");
        Log.d(tag, "Main: uid:- "+uid);
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(uid);
        adminRef = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");
        dailyTask = FirebaseFirestore.getInstance().collection("Today_Task").document(uid);
        promotionRef = FirebaseFirestore.getInstance().collection("Promotion_user").document(uid);
        promotionCollectionReference = FirebaseFirestore.getInstance().collection("Promotion_user");

        getData();
        SwipeRefreshLayout swipeRefreshLayout=findViewById(R.id.swipeRefres);
        swipeRefreshLayout.setOnRefreshListener(() ->
                new Handler().postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            getData();
        },1000));


        promotionCollectionReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int size = queryDocumentSnapshots.size();
            //   Toast.makeText(this, "Total Promotion User is "+size, Toast.LENGTH_SHORT).show();
        });
        bonous_btnn.setOnClickListener(view -> {

            //progressDialog.show();
            new Handler().postDelayed(() -> {
                Toast.makeText(this, "Wait Here 2 Minutes.", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(querka_link));
                startActivity(i);

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

                SharedPreferences preferences = getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("time", subEndDate);
                editor.apply();
                bonous_btnn.setVisibility(View.GONE);
                addAmountForPromotion(20.0);
            }, 1000);
        });
        btn_removeAds.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Remove Ads!")
                    .setMessage("Remove ads for a lifetime. We know our ads irritate you. You can remove ads for just ₹ 70 and continue your work without interruption.")
                    .setCancelable(false)
                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                        if (razorpay_key.isEmpty()){
                            goForPayment("70");
                        }else {
                            startPayment("70");
                        }
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alert = builder.create();
            alert.show();


        });
        //event
        promotionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    updateUserPromationWallet();
                });

        img_refresh.setOnClickListener(view -> DisplayRewardedAd(RewardAdId));

        findViewById(R.id.btn_today).setOnClickListener(view ->
                checkUserDailyTask());

        findViewById(R.id.btn_spin).setOnClickListener(view ->
                checkSpinDataBase());

        findViewById(R.id.spin_card).setOnClickListener(view ->
                install_application());

        btn_verify.setOnClickListener(view -> {
            enteredCode = ed_captcha.getText().toString();

            if (Float.parseFloat(withdraw_limit) <= tempWallet) {
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

    public void startPayment(String v) {

        float price = Float.parseFloat(v) * 100;
        final Checkout co = new Checkout();
        co.setKeyID(razorpay_key);
        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", "Total payable amount");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("send_sms_hash",true);
            options.put("allow_rotation", true);
            options.put("amount", price);

            JSONObject preFill = new JSONObject();
           // preFill.put("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            preFill.put("email", generateRandomEmail()+"@gmail.com");
            preFill.put("contact","98"+randomMobileNoGenerate() );
            options.put("prefill", preFill);
            co.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void goForPayment(String price) {

        progressDialog.setMessage("Fetching Info...");
        progressDialog.show();
        String order_id = UUID.randomUUID().toString(); // It should be unique
        // int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(MainActivity.paytm_callback_url)
                .appendQueryParameter("ORDER_ID", order_id)
                .appendQueryParameter("CUST_ID", FirebaseAuth.getInstance().getUid())
                .appendQueryParameter("TXN_AMOUNT", price);

        String myUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, price, price);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
        {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }
        );
        queue.add(request);

    }

    private void processTxn(String orderid, String txnToken, String amount, String captchaPrice) {
        //String mid = "Dinesh18399201056005";

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, MainActivity.paytm_mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                assert bundle != null;
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {

                    editor.putString("removeAds","yes");
                    editor.commit();
                    getData();
                    Toast.makeText(MainActivity.this, "Ads Remove successfully", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Payment Cancelled by you !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "You Cancelled this Transaction", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.startTransaction(MainActivity.this, 121);
    }

    private void getData() {

        adminRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    activation_fee = documentSnapshot.getString("accunt_activation_fee");
                    // total_install = documentSnapshot.getString("total_install");
                    withdraw_limit = documentSnapshot.getString("withdraw_limit");
                    withdraw_limit2 = documentSnapshot.getString("withdraw_limit2");
                    withdraw_limit3 = documentSnapshot.getString("withdraw_limit3");
                    withdraw_limit4 = documentSnapshot.getString("withdraw_limit4");
                    razorpay_key = documentSnapshot.getString("razorpay_key");
                    appOnOff = documentSnapshot.getString("appOnOff");
                    spinOnOff = documentSnapshot.getString("spinonoff");
                    querka_link = documentSnapshot.getString("querka_link");
                    off_reason = documentSnapshot.getString("off_reason");
                    app_version = documentSnapshot.getString("app_version");
                    app_version2 = documentSnapshot.getString("app_version2");
                    add_promotion = documentSnapshot.getString("add_promotion");
                    add_image_link = documentSnapshot.getString("add_image_link");
                    add_app_link = documentSnapshot.getString("add_app_link");
                    paytm_mid = documentSnapshot.getString("paytm_mid");
                    paytm_callback_url = documentSnapshot.getString("paytm_callback_url");

                    Log.d(tag, "adminRef: activationFee:- "+activation_fee);
                    Log.d(tag, "adminRef: withdraw_limit:- "+withdraw_limit);
                    Log.d(tag, "adminRef: withdraw_limit2:- "+withdraw_limit2);
                    Log.d(tag, "adminRef: withdraw_limit3:- "+withdraw_limit3);
                    Log.d(tag, "adminRef: withdraw_limit4:- "+withdraw_limit4);
                    Log.d(tag, "adminRef: appOnOff:- "+appOnOff);
                    Log.d(tag, "adminRef: spinOnOff:- "+spinOnOff);
                    Log.d(tag, "adminRef: querka_link:- "+querka_link);
                    Log.d(tag, "adminRef: off_reason:- "+off_reason);
                    Log.d(tag, "adminRef: app_version:- "+app_version);
                    Log.d(tag, "adminRef: app_version2:- "+app_version2);
                    Log.d(tag, "adminRef: add_promotion:- "+add_promotion);
                    Log.d(tag, "adminRef: add_image_link:- "+add_image_link);
                    Log.d(tag, "adminRef: add_app_link:- "+add_app_link);
                    Log.d(tag, "adminRef: paytm_mid:- "+paytm_mid);
                    Log.d(tag, "adminRef: paytm_callback_url:- "+paytm_callback_url);

                   // Toast.makeText(this, "adminRef:- "+activation_fee+withdraw_limit+withdraw_limit2+withdraw_limit3+withdraw_limit4, Toast.LENGTH_SHORT).show();

                    showAppOffDialog();
                    checkForUpdate();
                    checkAdsInstallation();

                    userRef.get().addOnSuccessListener(documentSnapshot1 -> {
                        wallet = documentSnapshot1.getString("wallet");
                        captcha_price = documentSnapshot1.getString("captcha_price");
                       String email = documentSnapshot1.getString("email");

                     //   Toast.makeText(this, "UserRef:- "+wallet+captcha_price, Toast.LENGTH_SHORT).show();

                        Log.d(tag, "userRef: wallet:- "+wallet);
                        Log.d(tag, "userRef: captcha_price:- "+captcha_price);
                        Log.d(tag, "userRef: email:- "+email);
                        Log.d(tag, "---------------------------------------------------------------------------------");

                        tempWallet = Float.parseFloat(wallet);
                        tempCaptchaPrice = Float.parseFloat(captcha_price);
                        generateCode();
                        setDataOnViews();
                                 /* if (userName.isEmpty() || userMobile.isEmpty()) {
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

                        }*/
                    });
                });

    }

    private void addAmountForPromotion(double amount) {
        float totl = tempWallet + Float.parseFloat(String.valueOf(amount));
        tempWallet = totl;
        userRef.update("wallet", String.valueOf(totl));
        // Toast.makeText(this, "Amount Added Successfully!", Toast.LENGTH_SHORT).show();
    }

    private void setDateOnTime() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String subStartDate = simpleDateFormat1.format(calendar.getTime());

        SharedPreferences preferences = getPreferences(MODE_PRIVATE);

        if (preferences.getString("time", null) != null) {

            String timee = preferences.getString("time", null);

            try {
                Date currentDate = simpleDateFormat1.parse(subStartDate);
                Date nextDate = simpleDateFormat1.parse(timee);

                if (currentDate.compareTo(nextDate) < 0) {
                    //tomarrow is last date
                    progressDialog.dismiss();
                    bonous_btnn.setVisibility(View.GONE);
                    // Toast.makeText(this, "11", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                } else if (currentDate.compareTo(nextDate) > 0) {
                    //date is expired yesterday
                    //Toast.makeText(getApplicationContext(), "expired", Toast.LENGTH_SHORT).show();
                    bonous_btnn.setVisibility(View.VISIBLE);

                } else {
                    //date is same
                    progressDialog.dismiss();
                    bonous_btnn.setVisibility(View.VISIBLE);

                }
            } catch (ParseException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        } else {
            bonous_btnn.setVisibility(View.VISIBLE);
        }

        ///////////////////////////////////////
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
        if (add_promotion.equals("YES")) {
            promotionRef
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Log.d(tag, "promotionRef: document:- ");
                        if (documentSnapshot.exists()) {
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


                .setTitle("Install To Get ₹ 50!")
                .setMessage("Dear User, You have to Install this application and give 5 Start Rating on Playstore to Get \u20B9 50 in your wallet.")
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

                Log.d(tag, "dailyTask: time:- "+time);

                try {
                    Date firebaseDate = simpleDateFormat1.parse(subStartDate);
                    Date currentDate = simpleDateFormat1.parse(time);

                    if (firebaseDate.compareTo(currentDate) < 0) {
                        //tomarrow is last date
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                    } else if (firebaseDate.compareTo(currentDate) > 0) {
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
            case "3":
                Intent intent = new Intent(MainActivity.this, WithdrawActivity.class);
                intent.putExtra("fee", activation_fee);
                intent.putExtra("limit", withdraw_limit);
                startActivity(intent);
                break;
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
            case "15":
                Intent intent3 = new Intent(MainActivity.this, WithdrawActivity.class);
                intent3.putExtra("fee", activation_fee);
                intent3.putExtra("limit", withdraw_limit3);
                startActivity(intent3);
                break;
            case "35":
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
           /* if (sharedPreferences.getString("removeAds",null).equals("no")){
                DisplayRewardedAd(RewardAdId);
            }*/
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
                Float bal = tempWallet + tempCaptchaPrice;

                double bale = Double.parseDouble(String.valueOf(bal));
                bale = Math.round(bale * 100.00) / 100.00;

                userRef.update("wallet", String.valueOf(bale))
                        .addOnCompleteListener(task -> {
                            tempWallet = bal;
                            //  Toast.makeText(this, tempWallet+","+wallet, Toast.LENGTH_SHORT).show();
                            tv_wallet.setText("Wallet Balence :- \u20B9 " + tempWallet + "/-");

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

        tv_wallet.setText("Wallet Balance :- \u20B9 " + tempWallet + "/-");
        tv_captchaPrice.setText("Per Code Income :- \u20B9 " + captcha_price + "/-");

      //  String ads=  sharedPreferences.getString("removeAds",null);

        /*if (Float.parseFloat(wallet) > 10.0){
            if (ads.equals("yes")){
                btn_removeAds.setVisibility(View.GONE);
            }else {
                btn_removeAds.setVisibility(View.VISIBLE);
            }
        }*/

        if (Float.parseFloat(wallet) < 10.0) {
            tv_incre_income.setVisibility(View.GONE);
        } else {
            tv_incre_income.setVisibility(View.VISIBLE);
        }

        if (spinOnOff.equals("OFF")) {
            findViewById(R.id.btn_spin).setVisibility(View.GONE);

        } else if (tempWallet >= 10.0) {
            findViewById(R.id.btn_spin).setVisibility(View.VISIBLE);
            if (!querka_link.isEmpty()) {
                setDateOnTime();
            } else {
                bonous_btnn.setVisibility(View.GONE);
            }
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
        if (appOnOff.contains("OFF")) {
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
        bonous_btnn = findViewById(R.id.bonous_btn);
        btn_removeAds = findViewById(R.id.btn_removeAds);
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

    public static String randomMobileNoGenerate() {

        char[] Free_Plan_CODE = ("0123456789").toCharArray();
        // char[] Free_Plan_CODE = ("0123456789").toCharArray();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            result.append(Free_Plan_CODE[new Random().nextInt(Free_Plan_CODE.length)]);
        }
        return result.toString();
    }

    public static String generateRandomEmail() {

        char[] NUMBER = ("0123456789").toCharArray();
         char[] CHARACTER = ("abcdefghijklmnopqrstuvwxyz").toCharArray();

      //  StringBuilder no = new StringBuilder();
        StringBuilder charEmail = new StringBuilder();
       // String email=(charEmail.append(no.toString()))+"@gmail.com";


        for (int i = 0; i < 15; i++) {
            charEmail.append(CHARACTER[new Random().nextInt(CHARACTER.length)]);
        }

        for (int i = 0; i < 4; i++) {
            charEmail.append(NUMBER[new Random().nextInt(NUMBER.length)]);
        }
        return charEmail.toString();

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
    public void onInitializationComplete() {
        //  Toast.makeText(this, "init", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
        //   Toast.makeText(this, "fald", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccess(String s) {
        editor.putString("removeAds","yes");
        editor.commit();
        getData();
        Toast.makeText(MainActivity.this, "Ads Remove successfully", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
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
