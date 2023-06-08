package com.workz.athome;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.workz.athome.Model.UserData.Root;
import com.workz.athome.Utils.ApiClient;
import com.workz.athome.Utils.ApiService;
import com.workz.athome.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanActivity extends AppCompatActivity  {

    private ProgressDialog progressDialog;
    private String userCPrice,token;
    private SharedPreferences.Editor editor;
    private ApiService apiServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = findViewById(R.id.plandetail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Fetching Data...");

        apiServices = ApiClient.getRetrofit().create(ApiService.class);
        SharedPreferences preferences = getSharedPreferences(Utils.sharedPrefrenceName, MODE_PRIVATE);
        editor = preferences.edit();


        token = preferences.getString("token", null);
        userCPrice = preferences.getString("captcha_price", null);

        if (userCPrice.equals("35")) {
            findViewById(R.id.button6).setVisibility(View.GONE);
            findViewById(R.id.button7).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.button6).setVisibility(View.VISIBLE);
            findViewById(R.id.button7).setVisibility(View.GONE);
        }
        allClickRef();
    }


    private void allClickRef() {
        //basic plan details
        findViewById(R.id.button22).setOnClickListener(v -> {

            if (userCPrice.equals("7")) {
                Toast.makeText(getApplicationContext(), "Your have Already this Plan", Toast.LENGTH_LONG).show();
            } else {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(PlanActivity.this, R.style.AppCompatAlertDialogStyle);
                alertDialog.setTitle("Upgrade Now!");
                alertDialog.setMessage("Are you Sure? You want to Upgrade In Basic Plan with ₹ 80.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Pay Now", (dialog, which) -> {
                    dialog.dismiss();
                    //pay with Paytm
                    goForPayment("80", "7");
                });
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();

            }
        });

        // premium plan
        findViewById(R.id.button1).setOnClickListener(v -> {
            if (userCPrice.equals("15")) {
                Toast.makeText(getApplicationContext(), "You have already this plan", Toast.LENGTH_LONG).show();
            } else {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(PlanActivity.this, R.style.AppCompatAlertDialogStyle);
                alertDialog.setTitle("Upgrade Now!");
                alertDialog.setMessage("Are you Sure? You want to Upgrade In Premium Plan with ₹ 140.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Pay Now", (dialog, which) -> {
                    dialog.dismiss();
                    //pay with Paytm
                    goForPayment("140", "15");
                });
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
            }
        });

        // super premium plan
        findViewById(R.id.button6).setOnClickListener(v -> {
            if (userCPrice.equals("35")) {
                    Toast.makeText(getApplicationContext(), "You are already in Super Premium Plan", Toast.LENGTH_SHORT).show();
            } else {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(PlanActivity.this, R.style.AppCompatAlertDialogStyle);
                alertDialog.setTitle("Upgrade Now!");
                alertDialog.setMessage("Are you Sure? You want to Upgrade In Super Premium Plan with ₹ 280.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Pay Now", (dialog, which) -> {
                    dialog.dismiss();
                    goForPayment("280", "35");

                });
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
            }
        });

        // refund super plan
        findViewById(R.id.button7).setOnClickListener(v -> refundRequest());

    }


    private void refundRequest() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(PlanActivity.this, R.style.AppCompatAlertDialogStyle);
        alertDialog.setTitle("I Want Refund!");
        alertDialog.setMessage("Are you Sure? You want to Cancel this Plan and Get Refund.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            //pay with Paytm
            progressDialog.show();
            new Handler(Looper.myLooper()).postDelayed(() -> {
                progressDialog.dismiss();
                androidx.appcompat.app.AlertDialog.Builder alertDialog1 = new androidx.appcompat.app.AlertDialog.Builder(PlanActivity.this, R.style.AppCompatAlertDialogStyle);
                alertDialog1.setTitle("Request Received!");
                alertDialog1.setMessage("We received your refund request. Our team check if we found your refund is valid then you recevied your amount within 5-10 working days.");
                alertDialog1.setCancelable(false);
                alertDialog1.setPositiveButton("Ok", (dialog1, which1) -> {
                    dialog1.dismiss();
                    //pay with Paytm
                }).show();
            }, 2000);
        });
        alertDialog.setNegativeButton("No", (dialog, which) -> dialog.dismiss()).show();


    }

    private void goForPayment(String price, String cprice) {
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

        RequestQueue queue = Volley.newRequestQueue(PlanActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, price, cprice);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(PlanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
        {
            progressDialog.dismiss();
            Toast.makeText(PlanActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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

                    updateCaptchaPrice(captchaPrice);
                    Toast.makeText(PlanActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PlanActivity.this, "Payment Cancelled by you !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progressDialog.dismiss();
                Toast.makeText(PlanActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progressDialog.dismiss();
                Toast.makeText(PlanActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progressDialog.dismiss();
                Toast.makeText(PlanActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progressDialog.dismiss();
                Toast.makeText(PlanActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progressDialog.dismiss();
                Toast.makeText(PlanActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progressDialog.dismiss();
                Toast.makeText(PlanActivity.this, "You Cancelled this Transaction", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(PlanActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        int txnRequestCode = 110;
        transactionManager.startTransaction(PlanActivity.this, txnRequestCode);
    }

    private void updateCaptchaPrice(String captchaPrice) {

        Call<Root> rootCall=apiServices.updateUserPlan(
                "Bearer " +token,
                captchaPrice
        );
        rootCall.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                progressDialog.dismiss();
                if (response.code()==200){
                    editor.putString("captcha_price", captchaPrice);
                    editor.commit();
                }else {
                    Toast.makeText(PlanActivity.this, "Response Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(PlanActivity.this, "Api Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}