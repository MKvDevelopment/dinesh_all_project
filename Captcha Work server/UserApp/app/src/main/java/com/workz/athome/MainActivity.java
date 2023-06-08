package com.workz.athome;

import static com.workz.athome.Utils.Utils.hideKeyboard;
import static com.workz.athome.Utils.Utils.setUserSharedPreference;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;
import com.unity3d.ads.UnityAdsShowOptions;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.workz.athome.Model.AdminUtils.AdminRoot;
import com.workz.athome.Model.UserData.Root;
import com.workz.athome.Utils.ApiClient;
import com.workz.athome.Utils.ApiService;
import com.workz.athome.Utils.Utils;

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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        IUnityAdsInitializationListener {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private TextView tv_wallet, tv_captchaPrice, tv_captcha, tv_incre_income;
    private Button btn_withdraw, btn_verify;
    private EditText ed_captcha;
    private ImageView img_refresh;
    private String enteredCode, code;
    public static String withdraw_limit, withdraw_limit2, withdraw_limit3, withdraw_limit4, activation_fee, appOnOff, off_reason, app_version,
            add_promotion, add_app_link, grow_video_link, add_promotion_package, add_image_link, uid, total_install, paytm_mid, paytm_callback_url,
            spinOnOff, querka_link, app_version2, token, wallet, privacy_policy, terms_and_conditions;
    String RewardAdId = "Rewarded_Android";
    String InterstialAdId = "Interstitial_Android";
    private DocumentReference userRef;

    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> promotionResultLauncher;
    private CircleImageView bonous_btnn, btn_removeAds;
    private IUnityAdsLoadListener loadListener;
    private IUnityAdsShowListener showListner;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private ApiService apiServices;
    private SwipeRefreshLayout swipeRefreshLayout;

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
                  //  generateCode();
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
                    progressDialog.setMessage("Re-freshing code...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    img_refresh.setEnabled(false);
                    new Handler().postDelayed(MainActivity.this::generateCode, 1000);
                }
                //  Toast.makeText(getApplicationContext(), "skipped by user", Toast.LENGTH_SHORT).show();

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
        apiServices = ApiClient.getRetrofit().create(ApiService.class);
        preferences = getSharedPreferences(Utils.sharedPrefrenceName, MODE_PRIVATE);
        editor = preferences.edit();

        //  preferences = getSharedPreferences(Utils.sharedPrefrenceName, MODE_PRIVATE);
        token = preferences.getString("token", null);

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

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userRef = FirebaseFirestore.getInstance().collection("User_List").document(uid);

        getData();

        swipeRefreshLayout.setOnRefreshListener(this::getData
        );

        bonous_btnn.setOnClickListener(view -> {

            progressDialog.show();
            Toast.makeText(this, "Wait Here 2 Minutes.", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(() -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(querka_link));
                //startActivity(i);
                startActivityForResult(i, 20);

                bonous_btnn.setVisibility(View.GONE);

            }, 1000);
        });
        btn_removeAds.setOnClickListener(v -> {

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Remove Ads!")
                    .setMessage("Remove ads for a lifetime. We know our ads irritate you. You can remove ads for just ₹ 70 and continue your work without interruption.")
                    .setCancelable(false)
                    .setPositiveButton("Pay Now", (dialogInterface, i) -> goForPayment())
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog alert = builder.create();
            alert.show();


        });
        //event
        promotionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> updateUserPromationWallet());

        img_refresh.setOnClickListener(view -> DisplayRewardedAd(RewardAdId));

        findViewById(R.id.btn_today).setOnClickListener(view ->
        {
            Intent intent = new Intent(MainActivity.this, AdsTaskActivity.class);
            startActivityForResult(intent, 10);
        });

        findViewById(R.id.btn_spin).setOnClickListener(view ->
        {
            Intent intent = new Intent(MainActivity.this, SpinActivity.class);
            startActivityForResult(intent, 10);
        });

        findViewById(R.id.spin_card).setOnClickListener(view ->
                install_application());

        btn_verify.setOnClickListener(view -> {
            enteredCode = ed_captcha.getText().toString();

            if (Float.parseFloat(withdraw_limit) <= Float.parseFloat(preferences.getString("wallet", null))) {
                Toast.makeText(getApplicationContext(), "Now your are eligible to Withdraw!", Toast.LENGTH_SHORT).show();
            } else {
                if (!enteredCode.isEmpty()) {
                    waitingMethod();
                    hideKeyboard(this);
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter Code!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_withdraw.setOnClickListener(view -> checkWalletBalance());

        tv_incre_income.setOnClickListener(view -> {
            if (!preferences.getString("captcha_price", null).contains("40")) {
                // showPopUp();

                //Create the intent to go in the second activity
                Intent intent = new Intent(MainActivity.this, PlanActivity.class);
                // intent.putExtra("oldValue", "valueYouWantToChange");
                startActivityForResult(intent, 10); //I always put 0 for someIntValue


            } else {
                Toast.makeText(this, "Dear User, You have the Highest-Paid Plan of our Application.", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void goForPayment() {

        progressDialog.setMessage("Fetching Info...");
        progressDialog.show();
        String order_id = UUID.randomUUID().toString(); // It should be unique
        // int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(MainActivity.paytm_callback_url)
                .appendQueryParameter("ORDER_ID", order_id)
                .appendQueryParameter("CUST_ID", FirebaseAuth.getInstance().getUid())
                .appendQueryParameter("TXN_AMOUNT", "70");

        String myUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken);
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

    private void processTxn(String orderid, String txnToken) {
        //String mid = "Dinesh18399201056005";

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, MainActivity.paytm_mid, txnToken, "70", callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                assert bundle != null;
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {

                    removeAdsApiHit();
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

    private void removeAdsApiHit() {
        Call<Root> removeCall = apiServices.removeAds("Bearer " + token, "YES");
        removeCall.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                progressDialog.dismiss();
                if (response.code() == 200) {
                    editor.putString("remove_ads", "YES");
                    editor.commit();
                    findViewById(R.id.btn_removeAds).setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "Response Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Api Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {

        if (token == null) {
            //get user data from firebase and put it on server
            settingUserDataOnServer();
        } else {
            //get appUtils data from server
            Call<AdminRoot> call = apiServices.appUtils("Bearer " + token);

            call.enqueue(new Callback<AdminRoot>() {
                @Override
                public void onResponse(@NonNull Call<AdminRoot> call, @NonNull Response<AdminRoot> response) {

                    if (response.code() == 200) {
                        //result ok from server

                        assert response.body() != null;
                        activation_fee = response.body().getData().getAccunt_activation_fee();
                        add_app_link = response.body().getData().getAdd_app_link();
                        add_image_link = response.body().getData().getAdd_image_link();
                        add_promotion = response.body().getData().getAdd_promotion();
                        add_promotion_package = response.body().getData().getAdd_promotion_package();
                        appOnOff = response.body().getData().getAppOnOff();
                        off_reason = response.body().getData().getOff_reason();
                        app_version = response.body().getData().getApp_version();
                        app_version2 = response.body().getData().getApp_version2();
                        grow_video_link = response.body().getData().getGroww_video();
                        paytm_callback_url = response.body().getData().getPaytm_callback_url();
                        paytm_mid = response.body().getData().getPaytm_mid();
                        querka_link = response.body().getData().getQuerka_link();
                        spinOnOff = response.body().getData().getSpinonoff();
                        total_install = response.body().getData().getTotal_install();
                        withdraw_limit = response.body().getData().getWithdraw_limit();
                        withdraw_limit2 = response.body().getData().getWithdraw_limit2();
                        withdraw_limit3 = response.body().getData().getWithdraw_limit3();
                        withdraw_limit4 = response.body().getData().getWithdraw_limit4();
                        privacy_policy = response.body().getData().getPrivacy_policy();
                        terms_and_conditions = response.body().getData().getTerms_and_conditions();

                        showAppOffDialog();
                        checkForUpdate();
                        checkAdsInstallation();

                        //after getting user data
                        Call<Root> userCall = apiServices.getUserData("Bearer " + token);
                        userCall.enqueue(new Callback<Root>() {
                            @Override
                            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                                generateCode();
                                Root root = response.body();
                                Utils.updateUserSharedPreference(root, MainActivity.this);
                                setDataOnViews();

                                swipeRefreshLayout.setRefreshing(false);
                            }

                            @Override
                            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                                progressDialog.dismiss();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        });

                    } else {
                        // api error data fetch from firebase
                        getData();
                    }
                    progressDialog.dismiss();
                    swipeRefreshLayout.setRefreshing(false);
                }

                @Override
                public void onFailure(@NonNull Call<AdminRoot> call, @NonNull Throwable t) {
                    swipeRefreshLayout.setRefreshing(false);
                    Log.d("aaaaaaaaaaaa", "onResponse: error:-  " + t.getMessage());
                    Toast.makeText(MainActivity.this, "Api Error", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void settingUserDataOnServer() {
        // progressDialog.show();
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String remove_ads = "No";
                String finalActivation = documentSnapshot.getString("activation");
                String finalCaptcha_price = documentSnapshot.getString("captcha_price");
                String finalMobile = documentSnapshot.getString("mobile");
                String finalUserName = documentSnapshot.getString("userName");
                String finalWallet = documentSnapshot.getString("wallet");
                String finalUserEmail = documentSnapshot.getString("email");

                // Toast.makeText(MainActivity.this, "firebase", Toast.LENGTH_SHORT).show();
                FirebaseFirestore.getInstance()
                        .collection("Instant Activation")
                        .document(uid)
                        .get().addOnSuccessListener(documentSnapshot1 -> {
                            assert documentSnapshot1 != null;
                            String instantActivation;
                            //  Toast.makeText(MainActivity.this, "firebase2", Toast.LENGTH_SHORT).show();

                            if (documentSnapshot1.exists()) {
                                //getting instant activation data
                                instantActivation = documentSnapshot1.getString("activation");
                                // instantActivation.set(value.getString("activation"));
                            } else {
                                instantActivation = "NO";
                            }
                            FirebaseFirestore.getInstance()
                                    .collection("Promotion_user")
                                    .document(uid)
                                    .get()
                                    .addOnSuccessListener(documentSnapshot2 -> {
                                        String install;
                                        //    Toast.makeText(MainActivity.this, "firebase3", Toast.LENGTH_SHORT).show();

                                        assert documentSnapshot2 != null;
                                        if (documentSnapshot2.exists()) {
                                            //getting promotional user data
                                            install = documentSnapshot2.getString("install");

                                        } else {
                                            install = "NO";
                                            //  Toast.makeText(LoginActivity.this, "error.getMessage()2", Toast.LENGTH_SHORT).show();
                                        }
                                        FirebaseDatabase.getInstance().getReference().child("Spin_User").child(uid).get()
                                                .addOnSuccessListener(dataSnapshot -> {

                                                    String winning_balence, index;
                                                    if (dataSnapshot.exists()) {
                                                        SpinModel model = dataSnapshot.getValue(SpinModel.class);
                                                        assert model != null;
                                                        winning_balence = model.getWinning_balence();
                                                        index = model.getIndex();
                                                        // Toast.makeText(MainActivity.this, "firebase5", Toast.LENGTH_SHORT).show();

                                                    } else {
                                                        winning_balence = "1";
                                                        index = "0";
                                                    }
                                                    Call<Root> registerCall = apiServices.registerUser(
                                                            finalActivation,
                                                            finalCaptcha_price,
                                                            finalUserEmail,
                                                            finalMobile,
                                                            uid,
                                                            finalUserName,
                                                            finalWallet,
                                                            instantActivation,
                                                            install,
                                                            index,
                                                            winning_balence,
                                                            remove_ads,
                                                            "NO"
                                                    );
                                                    registerCall.enqueue(new Callback<Root>() {
                                                        @Override
                                                        public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                                                            Root root1 = response.body();
                                                            progressDialog.dismiss();
                                                            swipeRefreshLayout.setRefreshing(false);
                                                            // getData();
                                                            if (response.code() == 201) {
                                                                setUserSharedPreference(root1, MainActivity.this);
                                                            } else if (response.code() == 404) {
                                                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                                finish();
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                                                            progressDialog.dismiss();
                                                            swipeRefreshLayout.setRefreshing(false);
                                                            Toast.makeText(MainActivity.this, "onFail1:- " + t.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                })
                                                .addOnFailureListener(e -> {
                                                    progressDialog.dismiss();
                                                    swipeRefreshLayout.setRefreshing(false);
                                                    Toast.makeText(MainActivity.this, "fail2:-  " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    });
                        });
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }

        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());


    }

    private void updateWallet(String amount) {
        String wallet = preferences.getString("wallet", null);
        float totl = Float.parseFloat(wallet) + Float.parseFloat(amount);

        Call<Root> updateCall = apiServices.updatewallet("Bearer " + token, amount);
        updateCall.enqueue(new Callback<Root>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {

                if (response.code() == 200) {
                    editor.putString("wallet", String.valueOf(totl));
                    editor.commit();
                    generateCode();
                    setDataOnViews();

                } else {
                    Toast.makeText(MainActivity.this, "Wallet Update Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        });
        // Toast.makeText(this, "Amount Added Successfully!", Toast.LENGTH_SHORT).show();
    }

    private void updateUserPromationWallet() {
        progressDialog.setMessage("Updating Wallet");
        progressDialog.show();

        Call<Root> walletCall = apiServices.saveinstall(
                "Bearer " + token, "YES");

        walletCall.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                if (response.code() == 200) {
                    updateWallet("50.0");
                    findViewById(R.id.spin_card).setVisibility(View.GONE);
                } else {
                    Toast.makeText(MainActivity.this, "ss", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Api Error!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void checkAdsInstallation() {
        if (add_promotion.equals("YES")) {

            if (preferences.getString("install", null).equals("NO")) {
                //findViewById(R.id.spin_card).setVisibility(View.VISIBLE);
                if (isPackageExisted("com.socialmediasaver.status", this)) {
                    updateUserPromationWallet();
                } else {
                    ImageView imageView = findViewById(R.id.imgg);
                    Picasso.get().load(add_image_link).into(imageView);
                    findViewById(R.id.spin_card).setVisibility(View.VISIBLE);
                }
            } else {
                findViewById(R.id.spin_card).setVisibility(View.GONE);
            }
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

    public void DisplayRewardedAd(String videoAdId) {
        UnityAds.load(videoAdId, new UnityAdsLoadOptions(), loadListener);
        UnityAds.show(MainActivity.this, videoAdId, new UnityAdsShowOptions(), showListner);
    }

    private void checkWalletBalance() {

        switch (preferences.getString("captcha_price", null)) {
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
            if (preferences.getString("remove_ads", null).equals("NO")) {
                DisplayRewardedAd(RewardAdId);
            }
            ed_captcha.getText().clear();
            Toast.makeText(MainActivity.this, "Enter Correct Code", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    private void addMoneyCode() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Congratulation!");
        alertDialog.setMessage("Great! Code is Correct, Now Click on Add Money.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Add Money", (dialog, which) -> {
            if (preferences.getString("remove_ads", null).equals("NO")) {
                DisplayRewardedAd(InterstialAdId);
            }
            progressDialog.setMessage("Updating balance...");
            progressDialog.show();

            new Handler().postDelayed(() -> {

                updateWallet(preferences.getString("captcha_price", null));
             //   generateCode();
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
        preferences = getSharedPreferences(Utils.sharedPrefrenceName, MODE_PRIVATE);
        wallet = preferences.getString("wallet", null);
        String captcha_price = preferences.getString("captcha_price", null);
        String ads = preferences.getString("remove_ads", null);

        tv_wallet.setText("Wallet Balance :- \u20B9 " + wallet + "/-");
        tv_captchaPrice.setText("Per Code Income :- \u20B9 " + captcha_price + "/-");

        if (Float.parseFloat(wallet) > 10.0) {

            if (ads.equals("NO")) {
                btn_removeAds.setVisibility(View.GONE);
            } else {
                btn_removeAds.setVisibility(View.VISIBLE);
            }
        }

        if (Float.parseFloat(wallet) < 10.0) {
            tv_incre_income.setVisibility(View.GONE);
        } else {
            tv_incre_income.setVisibility(View.VISIBLE);
        }

        if (spinOnOff.equals("OFF")) {
            findViewById(R.id.btn_spin).setVisibility(View.GONE);

        } else if (Float.parseFloat(wallet) >= 10.0) {
            findViewById(R.id.btn_spin).setVisibility(View.VISIBLE);
            if (!querka_link.isEmpty()) {
                if (preferences.getString("is_button_pressed", null).equals("YES")) {
                    bonous_btnn.setVisibility(View.GONE);
                } else {
                    bonous_btnn.setVisibility(View.VISIBLE);
                }
            } else {
                bonous_btnn.setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.btn_spin).setVisibility(View.GONE);
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
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
        swipeRefreshLayout = findViewById(R.id.swipeRefres);
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
                intent2.putExtra("file_name", "terms");
                startActivity(intent2);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_policy:
                Intent intent1 = new Intent(MainActivity.this, ContentActivity.class);
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

        //get refresh from next page to back button click
        if (requestCode == 10) {
            setDataOnViews();
        } else if (requestCode == 20) {
            //update querka btn on server

            progressDialog.show();
            Call<Root> call = apiServices.bonusBtn("Bearer " + token, "YES");
            call.enqueue(new Callback<Root>() {
                @Override
                public void onResponse(Call<Root> call, Response<Root> response) {
                    progressDialog.dismiss();
                    if (response.code() == 200) {
                        editor.putString("is_button_pressed", "YES");
                        editor.commit();
                        updateWallet("20.0");
                    } else {
                        Toast.makeText(MainActivity.this, "Wallet Update Failed!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Root> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Failed!", Toast.LENGTH_SHORT).show();

                }
            });

        }
    }


    @Override
    public void onInitializationComplete() {
        //  Toast.makeText(this, "init", Toast.LENGTH_SHORT).show();
        DisplayRewardedAd(RewardAdId);
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
        // Toast.makeText(this, "fald", Toast.LENGTH_SHORT).show();
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
