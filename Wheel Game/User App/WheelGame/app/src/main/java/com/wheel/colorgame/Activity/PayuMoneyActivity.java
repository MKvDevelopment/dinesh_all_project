package com.wheel.colorgame.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wheel.colorgame.R;
import com.wheel.colorgame.Utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PayuMoneyActivity extends AppCompatActivity {

    WebView webView;
    Context activity;
    int mId;

    private CollectionReference addBalanceRef;
    private DocumentReference totalBalRef, adminAmountRef, userRef;
    private String totalAdminAmount;
    private String totalAdminAmount2;
    // Final Variables
    private ProgressDialog progressDialog;
    private String mMerchantKey = "HuBpHuWn";  //put merchant key here
    private String mSalt = "0KOPEbDjr7";
    private String mBaseURL = "https://secure.payu.in";  // for live use these credentials

    private String mAction = ""; // For Final URL
    private String mTXNId; // This will create below randomly
    private String mHash; // This will create below randomly
    private String mProductInfo = "Cab Ride"; //Passing String only
    private String mFirstName; // From Previous Activity
    private String mEmailId; // From Previous Activity
    private double mAmount; // From Previous Activity
    private String mPhone; // From Previous Activity
    private String mFailedUrl = "";   // success url
    private String mSUrl;   // failed url
    //handler
    Handler mHandler = new Handler();
    private String friendUid;
    private String wallet;

    @SuppressLint({"JavascriptInterface", "WrongConstant"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payu_money);

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        Utils.showProgressDialog(this, progressDialog);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait");

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yy");
        String withdrawTime = simpleDateFormat1.format(calendar.getTime());

        adminAmountRef = FirebaseFirestore.getInstance().collection("Today_Income").document(withdrawTime);
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(uid);

        addBalanceRef = FirebaseFirestore.getInstance().collection("User_List").document(uid).collection("Add balance History");
        totalBalRef = FirebaseFirestore.getInstance().collection("App_Utils").document("totalAmount");


        totalBalRef.addSnapshotListener((value, error) -> {
            assert value != null;
            totalAdminAmount = value.getString("totalIncome");
        });

        adminAmountRef.addSnapshotListener((value, error) -> {
            assert value != null;
            if (value.exists()) {
                totalAdminAmount2 = value.getString("totalIncome");
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("totalIncome", "0.0");
                map.put("totalPayout", "0.0");
                adminAmountRef.set(map);
            }
        });

        webView = (WebView) findViewById(R.id.payumoney_webview);

        activity = getApplicationContext();

        //Bundle bundle = getIntent().getExtras();
        String amount = getIntent().getStringExtra("money");
        friendUid = getIntent().getStringExtra("id");
        wallet = getIntent().getStringExtra("wallet");
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");



        if (amount != null) {

            mFirstName = name;
            mEmailId = email;
            mAmount = Double.parseDouble(amount);
            mPhone = "98"+randomGererate();
            mSUrl = "https://www.payumoney.com/mobileapp/payumoney/success.php";
            mId = 12;

            //  Creating Transaction Id

            Random rand = new Random();
            String randomString = Integer.toString(rand.nextInt()) + (System.currentTimeMillis() / 1000L);
            mTXNId = hashCal("SHA-256", randomString).substring(0, 20);

            mAmount = new BigDecimal(mAmount).setScale(0, RoundingMode.UP).intValue();

            // Creating Hash Key
            mHash = hashCal("SHA-512", mMerchantKey + "|" +
                    mTXNId + "|" +
                    mAmount + "|" +
                    mProductInfo + "|" +
                    mFirstName + "|" +
                    mEmailId + "|||||||||||" +
                    mSalt);

            // Final Action URL...

            mAction = mBaseURL.concat("/_payment");

            //WebView Client
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    Toast.makeText(activity, "Oh no! " + error, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onReceivedSslError(WebView view,
                                               final SslErrorHandler handler, SslError error) {
//                    Toast.makeText(activity, "SSL Error! " + error, Toast.LENGTH_SHORT).show();
//                    handler.proceed();
                    final AlertDialog.Builder builder = new AlertDialog.Builder(PayuMoneyActivity.this);
                    builder.setMessage("SSL error generated!!!");
                    builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                    builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                    final AlertDialog dialog = builder.create();
                    dialog.show();

                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {

                    if (url.equals(mSUrl)) {

                        progressDialog.setMessage("Updating Balance");
                        progressDialog.show();
                        new Handler(Looper.myLooper()).postDelayed(() -> updateUserDetail(amount), 3000);

                        Toast.makeText(PayuMoneyActivity.this, "Transaction Successsfull", Toast.LENGTH_SHORT).show();
                    } else if (url.equals(mFailedUrl)) {
                        Toast.makeText(PayuMoneyActivity.this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                    }
                    super.onPageFinished(view, url);
                }

                @Override
                public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                    super.onReceivedClientCertRequest(view, request);
                }
            });

            webView.setVisibility(View.VISIBLE);
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setCacheMode(2);
            webView.getSettings().setDomStorageEnabled(true);
            webView.clearHistory();
            webView.clearCache(true);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setSupportZoom(false);
            webView.getSettings().setUseWideViewPort(false);
            webView.getSettings().setLoadWithOverviewMode(false);
            webView.addJavascriptInterface(new PayUJavaScriptInterface(PayuMoneyActivity.this), "PayUMoney");

            // Mapping Compulsory Key Value Pairs
            Map<String, String> mapParams = new HashMap<>();

            mapParams.put("key", mMerchantKey);
            mapParams.put("txnid", mTXNId);
            mapParams.put("amount", String.valueOf(mAmount));
            mapParams.put("productinfo", mProductInfo);
            mapParams.put("firstname", mFirstName);
            mapParams.put("email", mEmailId);
            mapParams.put("phone", mPhone);
            // mapParams.put("surl", "https://www.payumoney.com/mobileapp/payumoney/success.php");
            mapParams.put("surl", mSUrl);
            mapParams.put("furl", mFailedUrl);
            mapParams.put("hash", mHash);
            //mapParams.put("service_provider", mServiceProvider);
            //mapParams.put("action", "https://test.payu.in/_payment");

            webViewClientPost(webView, mAction, mapParams.entrySet());
        } else {
            Toast.makeText(activity, "Something went wrong, Try again.", Toast.LENGTH_LONG).show();
        }
    }


    public void webViewClientPost(WebView webView, String url, Collection<Map.Entry<String, String>> postData) {
        StringBuilder sb = new StringBuilder();

        sb.append("<html><head></head>");
        sb.append("<body onload='form1.submit()'>");
        sb.append(String.format("<form id='form1' action='%s' method='%s'>", url, "post"));

        for (Map.Entry<String, String> item : postData) {
            sb.append(String.format("<input name='%s' type='hidden' value='%s' />", item.getKey(), item.getValue()));
        }
        sb.append("</form></body></html>");

        Log.d("TAG", "webViewClientPost called: " + sb.toString());
        webView.loadData(sb.toString(), "text/html", "utf-8");
    }


    public String hashCal(String type, String str) {
        Log.d("HASH", str);
        byte[] hashSequence = str.getBytes();
        StringBuffer hexString = new StringBuffer();
        try {
            MessageDigest algorithm = MessageDigest.getInstance(type);
            algorithm.reset();
            algorithm.update(hashSequence);
            byte messageDigest[] = algorithm.digest();

            for (int i = 0; i < messageDigest.length; i++) {
                String hex = Integer.toHexString(0xFF & messageDigest[i]);
                if (hex.length() == 1)
                    hexString.append("0");
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException NSAE) {
        }
        return hexString.toString();
    }

    public class PayUJavaScriptInterface {
        Context mContext;

        PayUJavaScriptInterface(Context c) {
            mContext = c;
        }

        public void success(long id, final String paymentId) {
            mHandler.post(new Runnable() {

                public void run() {
                    mHandler = null;
                    Toast.makeText(PayuMoneyActivity.this, "Payment Successfully." + id, Toast.LENGTH_SHORT).show();
                    Log.d("PAYMENT_ID", paymentId);
                }
            });
        }
    }


    private void updateUserDetail(String amount) {

        if (!friendUid.isEmpty()) {

            DocumentReference friendReference = FirebaseFirestore.getInstance().collection("User_List").document(friendUid);
            friendReference.get().addOnSuccessListener(documentSnapshot -> {
                String total = documentSnapshot.getString("wallet");


                double amountt = Double.parseDouble(amount);
                double res = (amountt / 100.0f) * 10;  //calculate 10 %

                double percentAmount = Math.round(res * 100.00) / 100.00;  //finding round off value in 2 decimal point
                double totalFriendAmount = Double.parseDouble(total) + percentAmount;

                friendReference.update("wallet", String.valueOf(totalFriendAmount));

            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Friend Adding amount failed", Toast.LENGTH_SHORT).show();
            });
        }

        //total overall income for admin
        float totall = Float.parseFloat(totalAdminAmount) + Float.parseFloat(amount);
        totalBalRef.update("totalIncome", String.valueOf(totall));

        //today income day wise for admin
        float totalll = Float.parseFloat(totalAdminAmount2) + Float.parseFloat(amount);
        adminAmountRef.update("totalIncome", String.valueOf(totalll));

        final String uuid = UUID.randomUUID().toString().replace("-", "");
        progressDialog.show();
        float total = Float.parseFloat(wallet) + Float.parseFloat(amount);

        Map<String, Object> map = new HashMap<>();
        map.put("wallet", String.valueOf(total));

        Map<String, Object> map2 = new HashMap<>();
        map2.put("amount", amount);
        map2.put("charge", "");
        map2.put("method", "");
        map2.put("uid", uuid);
        map2.put("status", "Successfull");
        map2.put("time", Timestamp.now().getSeconds() + "000");

        userRef.update(map).addOnSuccessListener(command ->
                addBalanceRef.document(uuid).set(map2).addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Add Balance Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private String randomGererate() {

        char[] Free_Plan_CODE = ("0123456789").toCharArray();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 8; i++) {
            result.append(Free_Plan_CODE[new Random().nextInt(Free_Plan_CODE.length)]);
        }
        return result.toString();
    }

    @Override
    public void onBackPressed() {

        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(PayuMoneyActivity.this, R.style.AppCompatAlertDialogStyle);
        alertDialog.setTitle("Transaction Alert!");
        alertDialog.setMessage("Are you Sure? You want to cancel this transaction.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            super.onBackPressed();
        });
        alertDialog.setNegativeButton("No",(dialog, which) -> {
            dialog.dismiss();
        });
        Utils.showAlertdialog(PayuMoneyActivity.this, alertDialog, "Transaction Alert!", "Are you Sure? You want to cancel this transaction.");
    }
}