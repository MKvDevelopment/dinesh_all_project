package com.typingwork.jobathome.Activity;

import static com.typingwork.jobathome.Utils.Constant.generateRandomUserMarks;
import static com.typingwork.jobathome.Utils.Constant.isConnectionAvailable;
import static com.typingwork.jobathome.Utils.Constant.loadVideoAdsListner;
import static com.typingwork.jobathome.Utils.Constant.randomRobotCodeGererate;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.typingwork.jobathome.Model.UserDataModel;
import com.typingwork.jobathome.R;
import com.typingwork.jobathome.Utils.Constant;
import com.typingwork.jobathome.Utils.NetworkChangeReceiver;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        IUnityAdsInitializationListener, PaymentResultWithDataListener {

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;
    private ProgressDialog progressDialog;
    public static String FIREBASE_VERSION, FIREBASE_VERSION2, FIREBASE_VERSION3, WALLET_LIMIT, APP_ONOFF, APP_OFFREASON, Second_Withdraw_Limit, Third_Withdraw_Limit;
    private String userUid, querka_link;
    private TextView tv_sName, tv_sPass, tv_sMarks, tv_sCode, tv_fPrice, tv_wallet;
    private EditText ed_sName, ed_sPass, ed_sMarks, ed_sCode;
    private Button btn_fSubmit, btn_install;
    private ImageView imgRefreshBtn;
    private DocumentReference userRef, notiRefrence;
    public static String razorpay_upi, paytm_mid, paytm_url, watch_video_free_plan, razorpay_server_link, razorpay_secrate_key;
    public static UserDataModel userDataModel = new UserDataModel();

    private StringBuilder sbMarks = new StringBuilder();
    private StringBuilder sbName = new StringBuilder();
    private StringBuilder sbPassStatus = new StringBuilder();

    private ActivityResultLauncher<Intent> promotionResultLauncher;
    private CircleImageView bonous_btn;
    private ActivityResultLauncher<Intent> ratingResultLauncher;
    private String amount, wrong_entry_panelty_fee, ads_payment_fee, user_withdraw, user_activation, chat, chat_on_reason,
            chat_off_reason, chat_user_reason, spinIndex, remainSpin, Premium_Chat;
    private ActivityResultLauncher<Intent> upiPaneltyResultLauncher, upiAdsResultLauncher;
    private DatabaseReference databaseReference;
    public static String adminUid, adminName, adminStatus, adminImage, adminToken, paymentProofLink, total_ads_install;
    public static String VideoAdId = "Rewarded_Android";
    public static String InterstialAdId = "Interstitial_Android";


    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Checkout.preload(getApplicationContext());

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        findViews();
        Toolbar toolbar = findViewById(R.id.toolbarr);

        String GAME_ID = "4595794";
        UnityAds.initialize(getApplicationContext(), GAME_ID, false, this);

        FirebaseAnalytics.getInstance(this);

        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        Constant.showProgressDialog(progressDialog, "Please Wait", "Fetching Data...");
        progressDialog.show();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        userUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        notiRefrence = FirebaseFirestore.getInstance().collection("Notification").document(userUid);
        userRef = FirebaseFirestore.getInstance().collection("Users_list").document(userUid);
        DocumentReference adminRef = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");

        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("admin")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        //  reciverImage = snapshot.child("image").getValue().toString();
                        adminName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                        adminUid = Objects.requireNonNull(snapshot.child("uid").getValue()).toString();
                        adminStatus = Objects.requireNonNull(snapshot.child("online").getValue()).toString();
                        adminToken = Objects.requireNonNull(snapshot.child("token").getValue()).toString();
                        adminImage = Objects.requireNonNull(snapshot.child("image").getValue()).toString();
                        total_ads_install = Objects.requireNonNull(snapshot.child("total_ads_install").getValue()).toString();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        adminRef.addSnapshotListener((value1, error1) -> {
            assert value1 != null;
            Premium_Chat = value1.getString("Premium_Chat");
            FIREBASE_VERSION = value1.getString("app_version");
            FIREBASE_VERSION2 = value1.getString("app_version2");
            FIREBASE_VERSION3 = value1.getString("app_version3");
            WALLET_LIMIT = value1.getString("walletlimit");
            APP_ONOFF = value1.getString("appOnOff");
            APP_OFFREASON = value1.getString("appOffReason");
            razorpay_upi = value1.getString("razorpay_upi");
            razorpay_server_link = value1.getString("razorpay_server_link");
            razorpay_secrate_key = value1.getString("razorpay_secrate_key");
            paytm_mid = value1.getString("paytm_mid");
            paytm_url = value1.getString("paytm_url");
            chat = value1.getString("chat");
            querka_link = value1.getString("querka_link");
            paymentProofLink = value1.getString("paymentProofLink");
            chat_on_reason = value1.getString("chat_on_reason");
            chat_off_reason = value1.getString("chat_off_reason");
            chat_user_reason = value1.getString("chat_user_reason");
            ads_payment_fee = value1.getString("ads_payment_fee");
            wrong_entry_panelty_fee = value1.getString("wrong_entry_panelty_amount");
            Second_Withdraw_Limit = value1.getString("second_withdraw_limit");
            Third_Withdraw_Limit = value1.getString("third_withdraw_limit");
            watch_video_free_plan = value1.getString("watch_video_free_plan");

            String notification_image_link = value1.getString("notification_image_link");
            String noti_link = value1.getString("noti_link");

            try {
                Constant.CheckAppUpdate(MainActivity.this);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (APP_ONOFF.equals("Off")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                alertDialog.setPositiveButton("Exit Now", (dialog, which) -> {
                    dialog.dismiss();
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                });
                Constant.showAlertdialog(MainActivity.this, alertDialog, "App Close Alert!", APP_OFFREASON);
            }

            userRef.addSnapshotListener((value, error) -> {
                assert value != null;
                userDataModel.setPhone(value.getString("phone"));
                userDataModel.setPhoto(value.getString("photo"));
                userDataModel.setEmail(value.getString("email"));
                userDataModel.setName(value.getString("name"));
                userDataModel.setBlock(value.getString("block"));
                userDataModel.setbReason(value.getString("reason"));
                userDataModel.setWrong_entry(value.getString("wrong_entry"));
                userDataModel.setRight_entry(value.getString("right_entry"));
                userDataModel.setRating(value.getString("rating"));
                userDataModel.setInstal1(value.getString("instal1"));
                userDataModel.setInstal2(value.getString("instal2"));
                userDataModel.setRefer_code(value.getString("refer_code"));
                userDataModel.setRefered_by(value.getString("refered_by"));
                userDataModel.setPlan(value.getString("plan"));
                userDataModel.setWallet(value.getString("wallet"));
                userDataModel.setActivation(value.getString("activation"));
                user_activation = value.getString("activation");
                userDataModel.setWithdraw(value.getString("withdraw"));
                user_withdraw = value.getString("withdraw");
                userDataModel.setForm_price(value.getString("form_price"));
                userDataModel.setAds_activation(value.getString("ads_activation"));
                userDataModel.setFriend_uid(value.getString("friend_uid"));
                userDataModel.setDevice_id(value.getString("device_id"));
                remainSpin = value.getString("field1");
                spinIndex = value.getString("field2");

                checkuserToken(userDataModel.getDevice_id());

                if (remainSpin.equals("") && spinIndex.equals("")) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("field1", "1");
                    map.put("field2", "0");
                    userRef.update(map);
                }

                if (!userDataModel.getBlock().equals("UnBlock")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                    alertDialog.setPositiveButton("Exit Now", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    }).setNegativeButton("Contact Us", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        if (Float.parseFloat(userDataModel.getWallet()) >= 0.0) {
                            Toast.makeText(this, "You are Restricted!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {

                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.putExtra("id", adminUid);
                            intent.putExtra("token", adminToken);
                            startActivity(intent);
                            finish();
                        }
                    });
                    Constant.showAlertdialog(MainActivity.this, alertDialog, "Account Block!", userDataModel.getbReason());

                }
                if (userDataModel.getName().isEmpty()) {
                    updateUserProfile();
                }
                setDataOnViews();
                setNavigationData();

            });

            assert notification_image_link != null;
            if (!notification_image_link.isEmpty()) {
                notiRefrence.get().addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {

                        AlertDialog custome_dialog;
                        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                        View view = LayoutInflater.from(this).inflate(R.layout.noti_icon_layout, null, false);

                        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                        ImageView img = view.findViewById(R.id.notiImage);

                        builder.setView(view);
                        custome_dialog = builder.create();
                        custome_dialog.setCancelable(false);
                        custome_dialog.setCanceledOnTouchOutside(false);
                        custome_dialog.show();

                        Picasso.get()
                                .load(notification_image_link)
                                .placeholder(getDrawable(R.drawable.logo))
                                .into(img);

                        img.setOnClickListener(v -> {
                            custome_dialog.dismiss();

                            Uri uri = Uri.parse(noti_link); // missing 'http://' will cause crashed
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(intent);

                            Map<String, String> map = new HashMap<>();
                            map.put("watchNotification", "Yes");
                            map.put("uid", userUid);
                            notiRefrence.set(map);



                        });
                    }
                });
            }

        });

        allResultCallback();

        btn_fSubmit.setOnClickListener(view -> {
            //check withdraw conditon
            if (Float.parseFloat(userDataModel.getWallet()) >= 12.0 && userDataModel.getWithdraw().contains("No")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                alertDialog.setPositiveButton("Withdraw Now", (dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(MainActivity.this, WithdrawActivity.class));
                });
                alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                Constant.showAlertdialog(MainActivity.this, alertDialog, "Withdraw Money!", "Congrats! \uD83E\uDD73\uD83E\uDD73\nYou are now Eligible to Withdraw Money. Go and Withdraw your Money.");
            } else if (Integer.parseInt(userDataModel.getWrong_entry()) >= 50 && Float.parseFloat(userDataModel.getWallet()) >= 2950) {
                //check panelty system and pay in premium plan
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                alertDialog.setPositiveButton("Pay Penalty", (dialog, which) -> {
                    dialog.dismiss();


                   /* if(razorpay_upi.equals("")){
                        goForPayment(amount);
                    }else {
                        choosePaymentPopUpLayout(wrong_entry_panelty_fee);
                    }*/
                    choosePaymentPopUpLayout(wrong_entry_panelty_fee);


                });
                alertDialog.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
                Constant.showAlertdialog(MainActivity.this, alertDialog, "Alert!", "You crossed the Limit of the wrong entry. As a  penalty, you will pay ₹ " + wrong_entry_panelty_fee + ". After that, we will forgive you for all the wrong entries and allow you to continue your work.");
            } else {
                String nm = ed_sName.getText().toString().trim();
                String marks = ed_sMarks.getText().toString().trim();
                String pass = ed_sPass.getText().toString().trim();
                String ed_code = ed_sCode.getText().toString().trim();
                if (nm.isEmpty()) {
                    Toast.makeText(this, "Enter Student Name", Toast.LENGTH_SHORT).show();
                } else if (marks.isEmpty()) {
                    Toast.makeText(this, "Enter Student Marks", Toast.LENGTH_SHORT).show();
                } else if (pass.isEmpty()) {
                    Toast.makeText(this, "Enter Student Fail/Pass", Toast.LENGTH_SHORT).show();
                } else if (ed_code.isEmpty()
                        && userDataModel.getActivation().equals("Yes")
                        && Float.parseFloat(userDataModel.getWallet()) >= 3000) {
                    Toast.makeText(this, "Enter Security code", Toast.LENGTH_SHORT).show();
                } else {

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    //progress dialog show
                    Constant.showProgressDialog(progressDialog, "Please Wait", "Verifying Data...");
                    progressDialog.show();
                    //waiting 2 second for check form is correct of not
                    new Handler().postDelayed(() -> {
                        if (userDataModel.getActivation().equals("No") || userDataModel.getWithdraw().equals("No")) {
                            //User is in free Plan
                            if (userDataModel.getPlan().equals("Yes")) {
                                //user plan is yes and want work in free plan
                                if (Float.parseFloat(userDataModel.getWallet()) >= 2900.0) {
                                    //force for upgrade Plan
                                    if (Premium_Chat.equals("Off")) {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                                        alertDialogBuilder
                                                .setPositiveButton("Go to Premium",
                                                        (dialog, id) -> {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(MainActivity.this, PaymentActivity.class));
                                                        })
                                                .setNegativeButton("No", (dialog, id) -> {
                                                    dialog.cancel();
                                                    progressDialog.dismiss();
                                                });
                                        Constant.showAlertdialog(MainActivity.this, alertDialogBuilder, "Upgrade Premium!", "If you want to continue work and withdraw your money. Premium is compolsery. Amount is refundable on withdraw. After Premium you can continue your work and withdraw your money. You get Premium features also.");

                                    } else {
                                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
                                        alertDialogBuilder
                                                .setPositiveButton("Go to Premium",
                                                        (dialog, id) -> {
                                                            progressDialog.dismiss();
                                                            startActivity(new Intent(MainActivity.this, PaymentActivity.class));
                                                        })
                                                .setNeutralButton("Contact Us", (dialog, which) -> {
                                                    dialog.cancel();
                                                    progressDialog.dismiss();
                                                    if (Premium_Chat.equals("Off")) {
                                                        Toast.makeText(this, "Contact us 10 AM to 5 PM!", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        checkDatabaseForNewCustomer();
                                                    }
                                                })
                                                .setNegativeButton("No", (dialog, id) -> {
                                                    dialog.cancel();
                                                    progressDialog.dismiss();
                                                });
                                        Constant.showAlertdialog(MainActivity.this, alertDialogBuilder, "Upgrade Premium!", "If you want to continue work and withdraw your money. Premium is compolsery. Amount is refundable on withdraw. After Premium you can continue your work and withdraw your money. You get Premium features also.");
                                    }
                                } else {
                                    //wallet is below 2950 and user is working in free plan && wrong right entry also count
                                    if (!nm.contentEquals(sbName)) {
                                        progressDialog.dismiss();
                                        setPremiumIncorrectDialog(ed_sName);
                                        Toast.makeText(this, "Check Name Spelling!", Toast.LENGTH_SHORT).show();
                                    } else if (!marks.contentEquals(sbMarks)) {
                                        progressDialog.dismiss();
                                        setPremiumIncorrectDialog(ed_sMarks);
                                        Toast.makeText(this, "Enter Student Correct Marks", Toast.LENGTH_SHORT).show();

                                    } else if (!pass.contentEquals(sbPassStatus)) {
                                        progressDialog.dismiss();
                                        setPremiumIncorrectDialog(ed_sPass);
                                        Toast.makeText(this, "Check Fail/Pass Spelling!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        //show ads on correct captcha
                                        if (userDataModel.getAds_activation().equals("No")) {
                                            UnityAds.load(InterstialAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(MainActivity.this, InterstialAdId));
                                        }

                                        int ttl = Integer.parseInt(userDataModel.getRight_entry()) + 1;
                                        double bale = Double.parseDouble(userDataModel.getWallet()) + Double.parseDouble(userDataModel.getForm_price());
                                        bale = Math.round(bale * 100.00) / 100.00;

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("right_entry", String.valueOf(ttl));
                                        map.put("wallet", String.valueOf(bale));
                                        userRef.update(map).addOnSuccessListener(unused -> {
                                            progressDialog.dismiss();
                                            ed_sName.setText("");
                                            ed_sMarks.setText("");
                                            ed_sPass.setText("");
                                            Toast.makeText(this, "Sucessfully Wallet Update.", Toast.LENGTH_SHORT).show();
                                        }).addOnFailureListener(e -> {
                                            progressDialog.dismiss();
                                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                                    }
                                }

                            } else {
                                if (!nm.contentEquals(sbName)) {
                                    progressDialog.dismiss();
                                    setIncorrectDialog(ed_sName);
                                    Toast.makeText(this, "Check Name Spelling!", Toast.LENGTH_SHORT).show();
                                } else if (!marks.contentEquals(sbMarks)) {
                                    progressDialog.dismiss();
                                    setIncorrectDialog(ed_sMarks);
                                    Toast.makeText(this, "Enter Student Correct Marks", Toast.LENGTH_SHORT).show();
                                } else if (!pass.contentEquals(sbPassStatus)) {
                                    progressDialog.dismiss();
                                    setIncorrectDialog(ed_sPass);
                                    Toast.makeText(this, "Check Fail/Pass Spelling!", Toast.LENGTH_SHORT).show();
                                } else {
                                    //show ads on free plan captcha correct

                                    float total = Float.parseFloat(userDataModel.getWallet()) + Float.parseFloat(userDataModel.getForm_price());
                                    double bale = Double.parseDouble(String.valueOf(total));
                                    bale = Math.round(bale * 100.00) / 100.00;

                                    userRef.update("wallet", String.valueOf(bale)).addOnSuccessListener(unused -> {
                                        progressDialog.dismiss();
                                        ed_sName.setText("");
                                        ed_sMarks.setText("");
                                        ed_sPass.setText("");
                                        Toast.makeText(this, "Sucessfully Wallet Update.", Toast.LENGTH_SHORT).show();
                                    }).addOnFailureListener(e -> {
                                        progressDialog.dismiss();
                                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        }
                        else if (userDataModel.getActivation().equals("Yes") && Float.parseFloat(userDataModel.getWallet()) < 3000) {
                            //user is in premium plan but wallet less than 3000
                            if (!nm.contentEquals(sbName)) {
                                progressDialog.dismiss();
                                setPremiumIncorrectDialog(ed_sName);
                                Toast.makeText(this, "Check Name Spelling!", Toast.LENGTH_SHORT).show();
                            }
                            else if (!marks.contentEquals(sbMarks)) {
                                progressDialog.dismiss();
                                setPremiumIncorrectDialog(ed_sMarks);
                                Toast.makeText(this, "Enter Student Correct Marks", Toast.LENGTH_SHORT).show();

                            } else if (!pass.contentEquals(sbPassStatus)) {
                                progressDialog.dismiss();
                                setPremiumIncorrectDialog(ed_sPass);
                                Toast.makeText(this, "Check Fail/Pass Spelling!", Toast.LENGTH_SHORT).show();
                            } else {
                                //show ads on correct captcha

                                int ttl = Integer.parseInt(userDataModel.getRight_entry()) + 1;
                                double bale = Double.parseDouble(userDataModel.getWallet()) + Double.parseDouble(userDataModel.getForm_price());
                                bale = Math.round(bale * 100.00) / 100.00;

                                Map<String, Object> map = new HashMap<>();
                                map.put("right_entry", String.valueOf(ttl));
                                map.put("wallet", String.valueOf(bale));
                                userRef.update(map).addOnSuccessListener(unused -> {
                                    progressDialog.dismiss();
                                    ed_sName.setText("");
                                    ed_sMarks.setText("");
                                    ed_sPass.setText("");
                                    Toast.makeText(this, "Sucessfully Wallet Update.", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                        else if (userDataModel.getActivation().equals("Yes") && Float.parseFloat(userDataModel.getWallet()) >= 3000) {
                            //when user have activation and hi cross his wallet above 3000
                            //check user security code is write of wrong
                            if (!nm.contentEquals(sbName)
                                    || !marks.contentEquals(sbMarks)
                                    || !pass.contentEquals(sbPassStatus)
                                    || !ed_code.contentEquals(tv_sCode.getText().toString())) {
                                progressDialog.dismiss();

                                if (userDataModel.getAds_activation().equals("Yes")) {
                                    //show interstial only
                                    UnityAds.load(InterstialAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(MainActivity.this, InterstialAdId));

                                } else {
                                    //show video ads
                                    UnityAds.load(VideoAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(MainActivity.this, VideoAdId));
                                }
                                setAdsPremiumUserIncorrectDialog();
                                ed_sName.setText("");
                                ed_sCode.setText("");
                                ed_sMarks.setText("");
                                ed_sPass.setText("");
                            } else {
                                //if code is right
                                int ttl = Integer.parseInt(userDataModel.getRight_entry()) + 1;
                                double bale = Double.parseDouble(userDataModel.getWallet()) + Double.parseDouble(userDataModel.getForm_price());
                                bale = Math.round(bale * 100.00) / 100.00;

                                if (bale >= 3500.0) {
                                    updateWalletAlertList(String.valueOf(bale));
                                }

                                Map<String, Object> map = new HashMap<>();
                                map.put("right_entry", String.valueOf(ttl));
                                map.put("wallet", String.valueOf(bale));
                                userRef.update(map).addOnSuccessListener(unused -> {
                                    progressDialog.dismiss();
                                    ed_sName.setText("");
                                    ed_sCode.setText("");
                                    ed_sMarks.setText("");
                                    ed_sPass.setText("");
                                    Toast.makeText(this, "Sucessfully Wallet Update.", Toast.LENGTH_SHORT).show();
                                }).addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    }, 2000);
                }
            }
        });
        btn_install.setOnClickListener(vieww -> {
            AlertDialog custome_dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            View view = LayoutInflater.from(this).inflate(R.layout.show_install_layout, null, false);


            Button cancel = view.findViewById(R.id.appCompatButton3);
            Button submit = view.findViewById(R.id.appCompatButton2);

            builder.setView(view);
            custome_dialog = builder.create();
            custome_dialog.setCancelable(false);
            custome_dialog.setCanceledOnTouchOutside(false);
            custome_dialog.show();
            submit.setOnClickListener(v -> {
                custome_dialog.dismiss();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Constant.ADS_URL));
                    promotionResultLauncher.launch(intent);
                } catch (android.content.ActivityNotFoundException anfe) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Constant.ADS_URL));
                    promotionResultLauncher.launch(intent);
                }

            });
            cancel.setOnClickListener(view1 -> custome_dialog.dismiss());

        });
        findViewById(R.id.floatingActionButton2).setOnClickListener(view -> {

            if (chat.equals("Off")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
                alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                Constant.showAlertdialog(MainActivity.this, alertDialog, "We are Not Live!", chat_off_reason);

            } else {
                chatBtnCondition();
            }
        });
        findViewById(R.id.floatingActionButton3).setOnClickListener(view -> Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show());
        findViewById(R.id.floatingActionButton4).setOnClickListener(view -> Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show());
        findViewById(R.id.btnPremium).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PaymentActivity.class)));
        //refer btn
        findViewById(R.id.appCompatButto8).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ReferActivity.class)));
        findViewById(R.id.btnRemoveAds).setOnClickListener(view -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            alertDialog.setPositiveButton("Remove Ads", (dialog, which) -> {
                dialog.dismiss();

               /* if(razorpay_upi.equals("")){
                    goForPayment(ads_payment_fee);
                }else {
                    choosePaymentPopUpLayout(ads_payment_fee);
                }*/
                choosePaymentPopUpLayout(ads_payment_fee);
            });
            alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            Constant.showAlertdialog(MainActivity.this, alertDialog, "Remove Ad!!", "We know Ads irritate you. You can remove Ads for life time just in ₹ " + ads_payment_fee + " and continue work fast and without any problem. After remove Ads work speed will be increase 3x.");

        });

        findViewById(R.id.watchVideoFree).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            intent.putExtra("link", watch_video_free_plan);
            startActivity(intent);
        });
        tv_wallet.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, WithdrawActivity.class)));
        findViewById(R.id.btnSpin).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, SpinActivity.class)));
        findViewById(R.id.btnPaymentProff).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PaymentProofActivity.class)));
        imgRefreshBtn.setOnClickListener(view -> {
            //show video ads
            UnityAds.load(VideoAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(MainActivity.this, VideoAdId));
            if (Float.parseFloat(userDataModel.getWallet()) >= 3500.0) {
                tv_sCode.setText(randomRobotCodeGererate("paid"));
            } else {
                tv_sCode.setText(randomRobotCodeGererate("free"));
            }
            setDataOnViews();
        });

        findViewById(R.id.btn_proof).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PaymentProofActivity.class)));
        findViewById(R.id.btn_upgrade).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, PaymentActivity.class)));
        findViewById(R.id.btnWorkWithUs).setOnClickListener(view -> startActivity(new Intent(MainActivity.this, WorkWithUsActivity.class)));

        bonous_btn.setOnClickListener(view -> {

            bonous_btn.setVisibility(View.GONE);
            //progressDialog.show();
            new Handler().postDelayed(() -> {
                Toast.makeText(this, "Wait Here 2 Minutes.", Toast.LENGTH_LONG).show();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(querka_link));
                startActivity(i);

                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
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
                SharedPreferences sharedPreferences = getSharedPreferences("myData", MODE_PRIVATE);

                // SharedPreferences preferences = getPreferences(MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("time", subEndDate);
                editor.apply();
                // bonous_btn.setVisibility(View.GONE);
                addAmountForPromotion();
            }, 1000);
        });

    }

    private void addAmountForPromotion() {
        float totl = Float.parseFloat(userDataModel.getWallet()) + Float.parseFloat(String.valueOf(20.0));

        userRef.update("wallet", String.valueOf(totl));
        // Toast.makeText(this, "Amount Added Successfully!", Toast.LENGTH_SHORT).show();
    }

    private void chatBtnCondition() {

        if (Double.parseDouble(userDataModel.getWallet()) >= 3000.0) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
            Constant.showAlertdialog(MainActivity.this, alertDialog, "Technical Issue!", chat_user_reason);

        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            alertDialog.setPositiveButton("Chat Now", (dialog, which) -> {
                dialog.dismiss();
                checkDatabaseForNewCustomer();
            });
            alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            Constant.showAlertdialog(MainActivity.this, alertDialog, "We are Live!", chat_on_reason);
        }
    }

    private void updateWalletAlertList(String balence) {

        DocumentReference alertRef = FirebaseFirestore.getInstance().collection("Wallet_Alert_List").document(userUid);

        alertRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Map<String, Object> map = new HashMap<>();
                map.put("wallet", balence);
                map.put("time", Timestamp.now().getSeconds() + "000");

                alertRef.update(map);
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("wallet", balence);
                map.put("uid", userUid);
                map.put("time", Timestamp.now().getSeconds() + "000");
                map.put("email", userDataModel.getEmail());
                map.put("name", userDataModel.getName());
                map.put("photo", userDataModel.getPhoto());
                alertRef.set(map);
            }
        }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void allResultCallback() {
        // Handle the returned Uri
        // Picasso.get().load(uri).into(imageView);
        /*profileResultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                this::setImageOnServer);*/
        promotionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> updateUserPromationWallet());
        ratingResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    float total = (float) (Float.parseFloat(userDataModel.getWallet()) + 50.0);
                    double bale = Double.parseDouble(String.valueOf(total));
                    bale = Math.round(bale * 100.00) / 100.00;
                    Map<String, Object> map = new HashMap<>();
                    map.put("rating", "Yes");
                    map.put("wallet", String.valueOf(bale));
                    userRef.update(map);
                    Toast.makeText(this, "Wallet Update Successfully", Toast.LENGTH_SHORT).show();
                });
        //event
        upiPaneltyResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        if (data != null) {
                            String txt = data.getStringExtra("response");
                            ArrayList<String> datalist = new ArrayList<>();
                            datalist.add(txt);
                            upiPaymentDataOperation(datalist);
                        } else {
                            ArrayList<String> list = new ArrayList<>();
                            list.add("nothing");
                            upiPaymentDataOperation(list);
                        }
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("nothing");
                        upiPaymentDataOperation(list);
                    }
                });
        //event
        upiAdsResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        if (data != null) {
                            String txt = data.getStringExtra("response");
                            ArrayList<String> datalist = new ArrayList<>();
                            datalist.add(txt);
                            upiAdsPaymentDataOperation(datalist);
                        } else {
                            ArrayList<String> list = new ArrayList<>();
                            list.add("nothing");
                            upiAdsPaymentDataOperation(list);
                        }
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("nothing");
                        upiAdsPaymentDataOperation(list);
                    }
                });
    }

    private void setAdsPremiumUserIncorrectDialog() {

        int ttl = Integer.parseInt(userDataModel.getWrong_entry()) + 1;

        userRef.update("wrong_entry", String.valueOf(ttl));

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        alertDialog.setPositiveButton("Correct Now", (dialog, which) -> dialog.dismiss());
        Constant.showAlertdialog(MainActivity.this, alertDialog, "Incorrect Data!", "You have entered Wrong Data. Please Correct it.");

    }

    public void DisplayRewardedAd() {
        if (userDataModel.getActivation().equals("Yes")) {
            UnityAds.load(VideoAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(MainActivity.this, VideoAdId));
        } else {
            UnityAds.load(InterstialAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(MainActivity.this, InterstialAdId));
        }
    }

    private void checkDatabaseForNewCustomer() {
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child("Users")
                .child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                .get()
                .addOnSuccessListener(dataSnapshot -> {
                    if (dataSnapshot.exists()) {
                        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                        intent.putExtra("id", adminUid);
                        intent.putExtra("token", adminToken);
                        startActivity(intent);
                        progressDialog.dismiss();
                    } else {
                        takeChatUserDetail();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                });
    }

    private void takeChatUserDetail() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userUid);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(newToken -> {

            Date date = new Date();
            Map<String, String> map = new HashMap<>();
            map.put("img", userDataModel.getPhoto());
            map.put("name", userDataModel.getName());
            map.put("token", newToken);
            map.put("status", "online");
            map.put("email", userDataModel.getEmail());
            map.put("seen", "yes");
            map.put("date", String.valueOf(date.getTime()));
            map.put("last_msg", "Last Msg");
            map.put("uid", userUid);

            databaseReference.setValue(map).addOnCompleteListener(task1 -> {
                progressDialog.dismiss();
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("id", adminUid);
                intent.putExtra("token", adminToken);
                startActivity(intent);
            });

        });

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateUserProfile() {
        AlertDialog custome_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        View view = LayoutInflater.from(this).inflate(R.layout.option_icon_layout, null, false);

        EditText name = view.findViewById(R.id.textView16);
        EditText email = view.findViewById(R.id.textView18);
        EditText mobile = view.findViewById(R.id.textView17);
        Button submit = view.findViewById(R.id.btn1_submit);

        if (!userDataModel.getEmail().isEmpty()) {
            email.setText(userDataModel.getEmail());
            email.setEnabled(false);
        }
        if (!userDataModel.getPhone().isEmpty()) {
            mobile.setText(userDataModel.getPhone());
            mobile.setEnabled(false);
        }
        if (!userDataModel.getName().isEmpty()) {
            name.setText(userDataModel.getName());
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
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                progressDialog.setTitle("Please Wait!");
                progressDialog.setMessage("Uploading Data...");
                progressDialog.show();

                Map<String, Object> map = new HashMap<>();
                map.put("name", name.getText().toString().trim());
                map.put("email", email.getText().toString().trim());
                map.put("phone", mobile.getText().toString().trim());

                userRef.update(map).addOnSuccessListener(o -> {
                    progressDialog.dismiss();
                    custome_dialog.dismiss();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    custome_dialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
        Toast.makeText(this, "Complete Profile Data!", Toast.LENGTH_SHORT).show();
    }

    private void upiAdsPaymentDataOperation(ArrayList<String> datalist) {
        progressDialog.show();
        if (isConnectionAvailable(getApplicationContext())) {
            String str = datalist.get(0);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String[] response = str.split("&");
            for (String s : response) {
                String[] equalStr = s.split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else {
                        if (!equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase())) {
                            equalStr[0].toLowerCase();
                            "txnRef".toLowerCase();
                        }
                    }
                } else {
                    paymentCancel = "Payment cancelled.";
                }
            }

            if (status.equals("success")) {
                Toast.makeText(getApplicationContext(), "Video Ads Remove Successfully", Toast.LENGTH_SHORT).show();
                new Handler(Looper.myLooper()).postDelayed(() ->
                        userRef.update("ads_activation", "Yes").
                                addOnSuccessListener(unused -> progressDialog.dismiss()), 2000);
            } else if ("Payment cancelled.".equals(paymentCancel)) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Payment cancelled.", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Transaction failed.Please try com_new", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Internet connection is not available. Please check and try com_new", Toast.LENGTH_SHORT).show();
        }
    }

    private void choosePaymentPopUpLayout(String amount) {
        AlertDialog custome_document_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.update_plan_layout, null, false);

        Button submit = view.findViewById(R.id.button3);
        Button cancel = view.findViewById(R.id.button2);

        if (razorpay_upi.equals("")) {
            view.findViewById(R.id.radio_second).setVisibility(View.GONE);
            ((RadioButton)view.findViewById(R.id.radio_first)).setText("Pay with Paytm Wallet/UPI/Card");
        }

        builder.setView(view);
        custome_document_dialog = builder.create();
        custome_document_dialog.setCanceledOnTouchOutside(false);
        custome_document_dialog.show();

        submit.setOnClickListener(view1 -> {
            custome_document_dialog.dismiss();
            //check button position
            RadioGroup radioGroup = view.findViewById(R.id.radio_group);
            int id = radioGroup.getCheckedRadioButtonId();
            View dd = radioGroup.findViewById(id);
            int position = radioGroup.indexOfChild(dd);

            switch (position) {
                case 0:
                    // other gateway
                    goForPayment(amount);

                    break;
                case 1:
                    this.amount = amount;
                    //goForUPI(amount);
                    startPayment(amount);
                    break;
            }

        });
        cancel.setOnClickListener(view1 -> custome_document_dialog.dismiss());

    }

    private void upiPaymentDataOperation(ArrayList<String> datalist) {
        progressDialog.show();
        if (isConnectionAvailable(getApplicationContext())) {
            String str = datalist.get(0);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String[] response = str.split("&");
            for (String s : response) {
                String[] equalStr = s.split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else {
                        if (!equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase())) {
                            equalStr[0].toLowerCase();
                            "txnRef".toLowerCase();
                        }
                    }
                } else {
                    paymentCancel = "Payment cancelled.";
                }
            }

            if (status.equals("success")) {
                Toast.makeText(getApplicationContext(), "We Forgive your Wrong Entry", Toast.LENGTH_SHORT).show();
                new Handler(Looper.myLooper()).postDelayed(this::updateUserDetail, 2000);
            } else if ("Payment cancelled.".equals(paymentCancel)) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Payment cancelled.", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Transaction failed.Please try com_new", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Internet connection is not available. Please check and try com_new", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUserDetail() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("wrong_entry", "0");

        userRef.update(map1).addOnSuccessListener(unused -> progressDialog.dismiss());

    }

    private void goForUPI(String amount) {
        Uri uri = new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", razorpay_upi)
                .appendQueryParameter("pn", String.valueOf(R.string.app_name))
                .appendQueryParameter("tn", "")
                .appendQueryParameter("tr", "114356789")
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        //it will show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        //check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {

            if (amount.equals(ads_payment_fee)) {
                upiAdsResultLauncher.launch(chooser);
            } else {
                upiPaneltyResultLauncher.launch(chooser);
            }

        } else {
            Toast.makeText(getApplicationContext(), "This Is Not Working", Toast.LENGTH_SHORT).show();
        }
    }

    private void goForPayment(String price) {
        progressDialog.setMessage("Fetching Info...");
        progressDialog.show();
        String order_id = UUID.randomUUID().toString(); // It should be unique
        // int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(paytm_url)
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
                processTxn(order_id, txnToken, price);
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

    private void processTxn(String orderid, String txnToken, String amount) {

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, paytm_mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                assert bundle != null;
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {

                    if (amount.equals(ads_payment_fee)) {
                        Toast.makeText(MainActivity.this, "Video Ads Remove Successfully", Toast.LENGTH_SHORT).show();
                        new Handler(Looper.myLooper()).postDelayed(() -> userRef.update("ads_activation", "Yes").addOnSuccessListener(unused -> progressDialog.dismiss()), 2000);
                    } else {
                        Toast.makeText(getApplicationContext(), "We Forgive your Wrong Entry", Toast.LENGTH_SHORT).show();
                        new Handler(Looper.myLooper()).postDelayed(() -> updateUserDetail(), 2000);
                    }
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
        int txnRequestCode = 110;
        transactionManager.startTransaction(MainActivity.this, txnRequestCode);
    }

    private void setPremiumIncorrectDialog(EditText editText) {
        // show ads premium user incorrect captcha
        if (userDataModel.getAds_activation().equals("No")) {
            UnityAds.load(VideoAdId, new UnityAdsLoadOptions(), loadVideoAdsListner(MainActivity.this, VideoAdId));
        }

        int ttl = Integer.parseInt(userDataModel.getWrong_entry()) + 1;

        Map<String, Object> map = new HashMap<>();
        map.put("wrong_entry", String.valueOf(ttl));

        userRef.update(map);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        alertDialog.setPositiveButton("Correct Now", (dialog, which) -> {
            dialog.dismiss();
            editText.setText("");
        });
        Constant.showAlertdialog(MainActivity.this, alertDialog, "Incorrect Data!", "You have entered Wrong Data. Please Correct it.");
    }

    private void updateUserPromationWallet() {
        float total = (float) (Float.parseFloat(userDataModel.getWallet()) + 10.0);
        double bale = Double.parseDouble(String.valueOf(total));
        bale = Math.round(bale * 100.00) / 100.00;

        int totall = Integer.parseInt(total_ads_install) + 1;

        Map<String, Object> map1 = new HashMap<>();
        map1.put("total_ads_install", String.valueOf(totall));

        databaseReference.child("admin").updateChildren(map1);
        // adminRef.update("total_install", String.valueOf(totall));

        Map<String, Object> map = new HashMap<>();
        map.put("wallet", String.valueOf(bale));
        map.put("instal1", "Yes");
        userRef.update(map).addOnSuccessListener(unused -> Toast.makeText(this, "Wallet Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());


    }

    private void setImageOnServer(Uri img) {
        progressDialog.setMessage("Uploading Document...");
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("User_Document/").child(userUid);
        storageReference.putFile(img).addOnSuccessListener(taskSnapshot ->
                storageReference.getDownloadUrl().addOnCompleteListener(task ->
                        userRef.update("photo", task.getResult().toString()).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Upload Successfully!", Toast.LENGTH_SHORT).show();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(this, Objects.requireNonNull(task1.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }))).addOnProgressListener(snapshot -> {
            double progress
                    = (100.0
                    * snapshot.getBytesTransferred()
                    / snapshot.getTotalByteCount());
            progressDialog.setMessage(
                    "Uploaded "
                            + (int) progress + "%");
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void setIncorrectDialog(EditText editText) {
        //show ads on free plan captcha wrong

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        alertDialog.setPositiveButton("Correct Now", (dialog, which) -> {
            dialog.dismiss();

            editText.setText("");
        });
        Constant.showAlertdialog(MainActivity.this, alertDialog, "Incorrect Data!", "You have entered Wrong Data. Please Correct it.");
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(false);
        switch (item.getItemId()) {

            case R.id.nav_profile:
                updateUserProfile();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_withdraw:
                startActivity(new Intent(MainActivity.this, WithdrawActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_refer:
                startActivity(new Intent(MainActivity.this, ReferActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_terms:
                Intent intent2 = new Intent(MainActivity.this, ContentActivity.class);
                intent2.putExtra("name", "Terms and Conditions");
                intent2.putExtra("file_name", "terms");
                startActivity(intent2);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_policy:
                Intent intent1 = new Intent(MainActivity.this, ContentActivity.class);
                intent1.putExtra("name", "Privacy Policy");
                intent1.putExtra("file_name", "privacy");
                startActivity(intent1);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_delete:
                accountDeleteDialog();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_exit:
                drawerLayout.closeDrawer(GravityCompat.START);
                exitDialog();
                break;
        }

        return true;
    }

    private void accountDeleteDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        alertDialogBuilder
                .setPositiveButton("Delete Now", (dialog, id) -> {
                    dialog.dismiss();

                    DocumentReference deleteRef = FirebaseFirestore.getInstance().collection("Delete_Request").document(userUid);
                    Map<String, Object> map = new HashMap<>();
                    map.put("uid", userUid);
                    deleteRef.set(map).addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                        sendToLogin();
                    }).addOnFailureListener(e -> Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show());


                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        Constant.showAlertdialog(MainActivity.this, alertDialogBuilder, "Delete Account!", "Are you Sure? You want to Delete your account. If once an account Delete you will Never Recover It.");


    }

    private void sendToLogin() { //funtion
        GoogleSignInClient mGoogleSignInClient;
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1033214924788-f3jsvies1kavej49k060ar39naqp7q4d.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getBaseContext(), gso);
        //signout Google
        mGoogleSignInClient.signOut().addOnCompleteListener(MainActivity.this,
                task -> {
                    FirebaseAuth.getInstance().signOut(); //signout firebase
                    Intent setupIntent = new Intent(getBaseContext(), LoginActivity.class);
                    setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    setupIntent.putExtra("id", userUid);
                    startActivity(setupIntent);
                    finish();
                });
    }

    private void exitDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.MyDialogTheme);
        alertDialogBuilder
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        Constant.showAlertdialog(MainActivity.this, alertDialogBuilder, "Exit Confirmation!", "Are you Sure? You want to Exit!");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"UseCompatLoadingForColorStateLists", "UseCompatLoadingForDrawables", "SetTextI18n"})
    private void setDataOnViews() {

        if (user_activation.equals("Yes") || userDataModel.getPlan().equals("Yes")) {

            //check for btn spin visiblity
            if (user_activation.equals("Yes")) {
                if (Integer.parseInt(spinIndex) <= 9 && Double.parseDouble(userDataModel.getWallet()) >= Double.parseDouble(WALLET_LIMIT) && Double.parseDouble(userDataModel.getWallet()) <= 3600.0) {
                    findViewById(R.id.btnSpin).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.btnSpin).setVisibility(View.GONE);
                }
            } else {
                if (remainSpin.equals("")) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("field1", "1");
                    map.put("field2", "0");
                    userRef.update(map);
                    // setDataOnViews();
                } else {
                    if (Integer.parseInt(spinIndex) <= 9 && Double.parseDouble(userDataModel.getWallet()) >= Double.parseDouble(WALLET_LIMIT) && Double.parseDouble(userDataModel.getWallet()) <= 2800.0) {
                        findViewById(R.id.btnSpin).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.btnSpin).setVisibility(View.GONE);
                    }
                }

            }

            if (userDataModel.getPlan().equals("Yes")) {

                if (Float.parseFloat(userDataModel.getWallet()) >= Double.parseDouble(WALLET_LIMIT)) {
                    findViewById(R.id.btnPremium).setVisibility(View.VISIBLE);
                    findViewById(R.id.btnPaymentProff).setVisibility(View.VISIBLE);
                    findViewById(R.id.btnRemoveAds).setVisibility(View.VISIBLE);
                    ((Button) findViewById(R.id.btnRemoveAds)).setText("Remove ads for Lifetime");
                    findViewById(R.id.layout_premium).setVisibility(View.VISIBLE);

                } else {
                    findViewById(R.id.btnPremium).setVisibility(View.GONE);
                    findViewById(R.id.btnPaymentProff).setVisibility(View.GONE);
                    findViewById(R.id.btnRemoveAds).setVisibility(View.GONE);
                    findViewById(R.id.layout_premium).setVisibility(View.GONE);
                }

                findViewById(R.id.floatingActionButton2).setVisibility(View.VISIBLE);
                findViewById(R.id.floatingActionButton3).setVisibility(View.GONE);
                findViewById(R.id.floatingActionButton4).setVisibility(View.GONE);
                findViewById(R.id.appCompatButto8).setVisibility(View.GONE);
                findViewById(R.id.watchVideoFree).setVisibility(View.VISIBLE);

            } else {
                findViewById(R.id.btnRemoveAds).setVisibility(View.GONE);
                findViewById(R.id.floatingActionButton2).setVisibility(View.GONE);
                findViewById(R.id.floatingActionButton3).setVisibility(View.VISIBLE);
                findViewById(R.id.floatingActionButton4).setVisibility(View.VISIBLE);
                findViewById(R.id.appCompatButto8).setVisibility(View.VISIBLE);
                findViewById(R.id.watchVideoFree).setVisibility(View.GONE);

            }

            //setting Main page Cardview
            findViewById(R.id.mainViewStatusPending).setVisibility(View.GONE);
            findViewById(R.id.mainViewConstraint).setVisibility(View.VISIBLE);

            //set main page view according to wallet
            if (Double.parseDouble(userDataModel.getWallet()) >= 3000.0) {
                findViewById(R.id.btnRemoveAds).setVisibility(View.GONE);
                findViewById(R.id.textView10).setVisibility(View.VISIBLE);
                tv_sCode.setVisibility(View.VISIBLE);
                if (Float.parseFloat(userDataModel.getWallet()) >= 3500.0) {
                    tv_sCode.setText(randomRobotCodeGererate("paid"));
                } else {
                    tv_sCode.setText(randomRobotCodeGererate("free"));
                }
                findViewById(R.id.linearLayout4).setVisibility(View.VISIBLE);
            } else {
                // findViewById(R.id.btnRemoveAds).setVisibility(View.GONE);
                findViewById(R.id.textView10).setVisibility(View.GONE);
                tv_sCode.setVisibility(View.GONE);
                findViewById(R.id.linearLayout4).setVisibility(View.GONE);
            }

            findViewById(R.id.textView37).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textView37)).setText("Wrong entry = " + userDataModel.getWrong_entry());
            findViewById(R.id.myReferCode).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.myReferCode)).setText("Right entry = " + userDataModel.getRight_entry());

        } else {
            findViewById(R.id.floatingActionButton3).setVisibility(View.GONE);
            findViewById(R.id.floatingActionButton4).setVisibility(View.GONE);

            if (user_withdraw.equals("Pending")) {

                //card view visiblity
                findViewById(R.id.btnPremium).setVisibility(View.VISIBLE);
                findViewById(R.id.mainViewStatusPending).setVisibility(View.VISIBLE);
                findViewById(R.id.btnPaymentProff).setVisibility(View.VISIBLE);
                findViewById(R.id.mainViewConstraint).setVisibility(View.GONE);

                //text setting
                ((TextView) findViewById(R.id.textView28)).setText("Withdraw Pending !");
                ((TextView) findViewById(R.id.tv_status)).setText(R.string.activation_pending_english);
                findViewById(R.id.appCompatButton4).setBackground(getDrawable(R.drawable.btn_background));
                findViewById(R.id.appCompatButton5).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));

                findViewById(R.id.appCompatButton4).setOnClickListener(view -> {
                    findViewById(R.id.appCompatButton4).setBackground(getDrawable(R.drawable.btn_background));
                    findViewById(R.id.appCompatButton4).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.primary_dark));
                    findViewById(R.id.appCompatButton5).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));

                    ((TextView) findViewById(R.id.tv_status)).setText(R.string.activation_pending_english);
                });
                findViewById(R.id.appCompatButton5).setOnClickListener(view -> {
                    findViewById(R.id.appCompatButton5).setBackground(getDrawable(R.drawable.btn_background));
                    findViewById(R.id.appCompatButton5).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.primary_dark));
                    findViewById(R.id.appCompatButton4).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));


                    ((TextView) findViewById(R.id.tv_status)).setText(R.string.activation_pending_hindi);

                });

            } else if (user_withdraw.equals("Success")) {
                //card view visiblity
                findViewById(R.id.mainViewStatusPending).setVisibility(View.GONE);
                if (Float.parseFloat(userDataModel.getWallet()) >= Double.parseDouble(WALLET_LIMIT)) {
                    findViewById(R.id.btnPremium).setVisibility(View.VISIBLE);
                    findViewById(R.id.btnPaymentProff).setVisibility(View.VISIBLE);
                    findViewById(R.id.btnRemoveAds).setVisibility(View.VISIBLE);

                } else {
                    findViewById(R.id.btnPremium).setVisibility(View.GONE);
                    findViewById(R.id.btnPaymentProff).setVisibility(View.GONE);
                    findViewById(R.id.btnRemoveAds).setVisibility(View.GONE);
                }

                //text setting
                ((TextView) findViewById(R.id.textView28)).setText("Withdraw Success !");
                ((TextView) findViewById(R.id.tv_status)).setText(R.string.activation_success_english);
                findViewById(R.id.appCompatButton4).setBackground(getDrawable(R.drawable.btn_background));
                findViewById(R.id.appCompatButton5).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));

                findViewById(R.id.appCompatButton4).setOnClickListener(view -> {
                    findViewById(R.id.appCompatButton4).setBackground(getDrawable(R.drawable.btn_background));
                    findViewById(R.id.appCompatButton4).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.primary_dark));
                    findViewById(R.id.appCompatButton5).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));

                    ((TextView) findViewById(R.id.tv_status)).setText(R.string.activation_success_english);
                });
                findViewById(R.id.appCompatButton5).setOnClickListener(view -> {
                    findViewById(R.id.appCompatButton5).setBackground(getDrawable(R.drawable.btn_background));
                    findViewById(R.id.appCompatButton5).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.primary_dark));
                    findViewById(R.id.appCompatButton4).setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));


                    ((TextView) findViewById(R.id.tv_status)).setText(R.string.activation_success_hindi);

                });


            } else {
                findViewById(R.id.mainViewStatusPending).setVisibility(View.GONE);
                findViewById(R.id.mainViewConstraint).setVisibility(View.VISIBLE);
            }

        }

        if (userDataModel.getInstal1().contains("Yes")) {
            btn_install.setVisibility(View.GONE);
        } else {
            btn_install.setVisibility(View.VISIBLE);
        }

        if (userDataModel.getAds_activation().equals("Yes")) {
            findViewById(R.id.btnRemoveAds).setVisibility(View.GONE);
        }

        if (Float.parseFloat(userDataModel.getWallet()) > 50.0 && userDataModel.getRating().equals("No")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            alertDialog.setPositiveButton("Rate Now", (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                ratingResultLauncher.launch(intent);
                userRef.update("rating", "Yes");
            });
            Constant.showAlertdialog(MainActivity.this, alertDialog, "Rate Us!", "Hope you enjoying this app and work. Rate us 5 Stars and give a positive review on Play Store and get 50 Rs in your wallet.\n");

        }
        if (Float.parseFloat(userDataModel.getWallet()) >= Double.parseDouble(WALLET_LIMIT)) {
            if (!querka_link.isEmpty()) {
                setDateOnTime();
            } else {
                bonous_btn.setVisibility(View.GONE);
            }
        }

        if (Double.parseDouble(userDataModel.getWallet()) >= 300.0) {
            findViewById(R.id.btnWorkWithUs).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btnWorkWithUs).setVisibility(View.GONE);
        }

        tv_fPrice.setText("Income : ₹ " + userDataModel.getForm_price() + "/Form");
        tv_wallet.setText(" ₹ " + userDataModel.getWallet() + "/-");

        if (Float.parseFloat(userDataModel.getWallet()) >= 3500) {
            Typeface typeface = ResourcesCompat.getFont(getApplicationContext(), R.font.sachiko);
            tv_sName.setTypeface(typeface);
            tv_sName.setTextSize(20);
            tv_sMarks.setTypeface(typeface);
            tv_sMarks.setTextSize(20);
            tv_sPass.setTypeface(typeface);
            tv_sPass.setTextSize(20);
            tv_sCode.setTypeface(typeface);
            tv_sCode.setTextSize(20);


            Map<String, Object> map = new HashMap<>();
            map.put("wallet", userDataModel.getWallet());
            map.put("uid", userUid);

            CollectionReference walletRef = FirebaseFirestore.getInstance().collection("Wallet_Alert_List");
            walletRef.whereEqualTo("uid", userUid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.getResult().isEmpty()) {
                            walletRef.document(userUid).set(map);
                        } else {
                            walletRef.document(userUid).update(map);
                        }
                    });
        }

        if (sbPassStatus.length() == 0 && sbMarks.length() == 0 && sbName.length() == 0) {
            tv_sName.requestFocus();
            sbName.append(Constant.userName[Constant.randomGererate()]);
            tv_sName.setText(sbName);
            tv_sMarks.setText(sbMarks.append(generateRandomUserMarks()));
            if (Integer.parseInt(sbMarks.toString()) >= 165) {
                tv_sPass.setText("Pass");
                sbPassStatus.append("Pass");
            } else {
                sbPassStatus.append("Fail");
                tv_sPass.setText("Fail");
            }
            progressDialog.dismiss();
        } else {
            sbName.setLength(0);
            sbMarks.setLength(0);
            sbPassStatus.setLength(0);
            setDataOnViews();
        }
    }

    private void setDateOnTime() {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String subStartDate = simpleDateFormat1.format(calendar.getTime());

        //  SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences sharedPreferences = getSharedPreferences("myData", MODE_PRIVATE);

        if (sharedPreferences.getString("time", null) != null) {

            String timee = sharedPreferences.getString("time", null);

            try {
                Date currentDate = simpleDateFormat1.parse(subStartDate);
                Date nextDate = simpleDateFormat1.parse(timee);

                if (currentDate.compareTo(nextDate) < 0) {
                    //tomarrow is last date
                    progressDialog.dismiss();
                    bonous_btn.setVisibility(View.GONE);
                    // Toast.makeText(this, "11", Toast.LENGTH_SHORT).show();
                    // startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                } else if (currentDate.compareTo(nextDate) > 0) {
                    //date is expired yesterday
                    //Toast.makeText(getApplicationContext(), "expired", Toast.LENGTH_SHORT).show();
                    bonous_btn.setVisibility(View.VISIBLE);

                } else {
                    //date is same
                    progressDialog.dismiss();
                    bonous_btn.setVisibility(View.VISIBLE);

                }
            } catch (ParseException e) {
                e.printStackTrace();
                progressDialog.dismiss();
            }
        } else {
            bonous_btn.setVisibility(View.VISIBLE);
        }

        ///////////////////////////////////////
    }

    public void startPayment(String amount) {
        progressDialog.show();

        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setKeyID(razorpay_upi);//razorpay key
        try {
            JSONObject postData = new JSONObject();
            try {
                // int amount = 1;//edit amount
                postData.put("action", "create_order");
                postData.put("amount", "" + amount);//amount
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, String.valueOf(postData));
                OkHttpClient client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(razorpay_server_link)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String str = response.body().string();
                                JSONObject object = new JSONObject(str);
                                boolean success = object.getBoolean("success");
                                String result = object.getString("response");
                                if (success) {
                                    JSONObject options = new JSONObject();
                                    options.put("name", getString(R.string.app_name));//merchant name
                                    options.put("description", "Total payable amount");//
                                    options.put("send_sms_hash", true);
                                    options.put("allow_rotation", true);
                                    //You can omit the image option to fetch the image from dashboard
                                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                                    options.put("currency", "INR");
                                    options.put("amount", "" + Integer.parseInt(amount) * 100);//edit amount in paise
                                    options.put("order_id", "" + result);

                                    JSONObject preFill = new JSONObject();
                                    preFill.put("email", userDataModel.getEmail());
                                    preFill.put("contact", userDataModel.getPhone());
                                    options.put("prefill", preFill);

                                    activity.runOnUiThread(() -> {
                                        progressDialog.dismiss();
                                        co.open(activity, options);
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    if (result.isEmpty()) {
                                        result = "Something went wrong";
                                        //  Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
                                    }
                                    String finalResult = result;
                                    activity.runOnUiThread(() -> Toast.makeText(activity, finalResult, Toast.LENGTH_SHORT).show());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            progressDialog.dismiss();
                            activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
            e.printStackTrace();
        }

    }

    private void setNavigationData() {
        ImageView navImage = findViewById(R.id.nav_user_image);
        TextView navName = findViewById(R.id.tvname);
        TextView email = findViewById(R.id.textView);
        if (!userDataModel.getPhoto().isEmpty()) {
            Picasso.get().load(userDataModel.getPhoto()).placeholder(R.mipmap.ic_logo).into(navImage);
        }
        email.setText(userDataModel.getEmail());
        navName.setText(userDataModel.getName());

    }

    private void findViews() {
        bonous_btn = findViewById(R.id.bonous_btn);
        tv_sName = findViewById(R.id.textView3);
        tv_sMarks = findViewById(R.id.textView5);
        tv_sPass = findViewById(R.id.textView7);
        tv_sCode = findViewById(R.id.textView11);
        tv_fPrice = findViewById(R.id.textView12);
        ed_sName = findViewById(R.id.ed_name);
        ed_sPass = findViewById(R.id.ed_pass);
        ed_sMarks = findViewById(R.id.ed_mark);
        ed_sCode = findViewById(R.id.ed_robot);
        btn_fSubmit = findViewById(R.id.btnsubmit);
        btn_install = findViewById(R.id.appCompatButton);
        tv_wallet = findViewById(R.id.tol_wallet);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        imgRefreshBtn = findViewById(R.id.img_refresh);
    }

    private void checkuserToken(String deviceId) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(newToken -> {
            if (!deviceId.equals(newToken)) {
                userRef.update("device_id", newToken);
            }
        });
    }

    @Override
    public void onBackPressed() {
        exitDialog();
    }

    @Override
    public void onInitializationComplete() {
        DisplayRewardedAd();
        // Toast.makeText(this, "init complete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
        // Toast.makeText(this, "init failed", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        run_api(s);
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        /// Toast.makeText(this, "Payment Failed", Toast.LENGTH_SHORT).show();
        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Sorry, Payment Failed!", Toast.LENGTH_SHORT).show());

    }

    private void run_api(String s) {
        final Activity activity = this;
        JSONObject postData = new JSONObject();
        try {
            postData.put("action", "verify_order");
            postData.put("pay_id", s);
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, String.valueOf(postData));
            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(razorpay_server_link)//api URL
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String str = response.body().string();
                            JSONObject object = new JSONObject(str);
                            boolean success = object.getBoolean("success");
                            String result = object.getString("response");
                            if (success) {
                                activity.runOnUiThread(() -> {
                                    if (amount.equals(ads_payment_fee)) {
                                        Toast.makeText(MainActivity.this, "Video Ads Remove Successfully", Toast.LENGTH_SHORT).show();
                                        new Handler(Looper.myLooper()).postDelayed(() -> userRef.update("ads_activation", "Yes").addOnSuccessListener(unused -> progressDialog.dismiss()), 2000);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "We Forgive your Wrong Entry", Toast.LENGTH_SHORT).show();
                                        new Handler(Looper.myLooper()).postDelayed(() -> updateUserDetail(), 2000);
                                    }
                                });
                            } else {
                                if (result.isEmpty()) {
                                    result = "Something went wrong";
                                }
                                String finalResult = result;
                                activity.runOnUiThread(() -> Toast.makeText(activity, finalResult, Toast.LENGTH_SHORT).show());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Payment Failed", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Payment Failed", Toast.LENGTH_SHORT).show());
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
        }
    }
}