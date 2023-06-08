package com.perfect.traders.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.perfect.traders.Constant.NetworkChangeReceiver;
import com.perfect.traders.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MembershipActivity extends AppCompatActivity {

    private String first_plan_main_price, second_plan_main_price, third_plan_main_price, first_plan_sale_price,
            second_plan_sale_price, third_plan_sale_price, first_plan_off, second_plan_off, third_plan_off,
            super_premium_plan_amount, premium_plan_amount, basic_plan_amount;
    private DocumentReference planRef, upiRef, userRef;
    private ProgressDialog progressDialog;
    private final int txnRequestCode = 110;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        Toolbar toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);


        String id = FirebaseAuth.getInstance().getUid();
        planRef = FirebaseFirestore.getInstance().collection("App_Utils").document("Plan_Price");
        upiRef = FirebaseFirestore.getInstance().collection("App_Utils").document("utils");
        assert id != null;
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(id);

        planRef.addSnapshotListener((value, error) -> {
            first_plan_main_price = value.getString("1st_plan_main_price");
            first_plan_sale_price = value.getString("1st_plan_sale_price");
            first_plan_off = value.getString("1st_plan_off");
            second_plan_main_price = value.getString("2nd_plan_main_price");
            second_plan_sale_price = value.getString("2nd_plan_sale_price");
            second_plan_off = value.getString("2nd_plan_off");
            third_plan_main_price = value.getString("3rd_plan_main_price");
            third_plan_sale_price = value.getString("3rd_plan_sale_price");
            third_plan_off = value.getString("3rd_plan_off");
            setDataOnView();
        });
        upiRef.addSnapshotListener((value, error) -> {
            basic_plan_amount = value.getString("basic_plan_amount");
            premium_plan_amount = value.getString("premium_plan_amount");
            super_premium_plan_amount = value.getString("super_premium_plan_amount");
        });

        findViewById(R.id.appCompatButton4).setOnClickListener(view -> {
             goForPayment(first_plan_sale_price, "basic_plan_amount");
        });
        findViewById(R.id.appCompatButton5).setOnClickListener(view -> {
             goForPayment(second_plan_sale_price, "premium_plan_amount");
        });
        findViewById(R.id.appCompatButton6).setOnClickListener(view -> {
            goForPayment(third_plan_sale_price, "super_premium_plan_amount");
        });
    }

    private void setDataOnView() {

        ((TextView) findViewById(R.id.textView20)).setText("₹ " + first_plan_main_price + "/-");
        ((TextView) findViewById(R.id.textView22)).setText("₹ " + first_plan_sale_price + "/-");
        ((TextView) findViewById(R.id.textView23)).setText("(" + first_plan_off + ")");
        ((TextView) findViewById(R.id.textView28)).setText("₹ " + second_plan_main_price + "/-");
        ((TextView) findViewById(R.id.textView29)).setText("₹ " + second_plan_sale_price + "/-");
        ((TextView) findViewById(R.id.textView31)).setText("(" + second_plan_off + ")");
        ((TextView) findViewById(R.id.textView36)).setText("₹ " + third_plan_main_price + "/-");
        ((TextView) findViewById(R.id.textView38)).setText("₹ " + third_plan_sale_price + "/-");
        ((TextView) findViewById(R.id.textView39)).setText("(" + third_plan_off + ")");
    }

    private void goForPayment(String price,String planType) {

        progressDialog.setMessage("Fetching Info...");
        progressDialog.show();
        String order_id = UUID.randomUUID().toString(); // It should be unique
        // int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("paytm-gateway-server.glitch.me")
                .appendQueryParameter("ORDER_ID", order_id)
                .appendQueryParameter("CUST_ID", FirebaseAuth.getInstance().getUid())
                .appendQueryParameter("TXN_AMOUNT", price);

        String myUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(MembershipActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, price,planType);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(MembershipActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
        {
            progressDialog.dismiss();
            Toast.makeText(MembershipActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }
        );
        queue.add(request);

    }

    private void processTxn(String orderid, String txnToken, String amount, String planType) {
        String mid = "Dinesh18399201056005";

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                assert bundle != null;
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {
                    progressDialog.setMessage("Updating detail...");
                    progressDialog.show();
                    new Handler(Looper.myLooper()).postDelayed(() -> {

                        addAdminAmount(planType);

                    }, 3000);
                    Toast.makeText(getApplicationContext(), "Transaction successful.", Toast.LENGTH_SHORT).show();

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(MembershipActivity.this, "Payment Cancelled by you !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progressDialog.dismiss();
                Toast.makeText(MembershipActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progressDialog.dismiss();
                Toast.makeText(MembershipActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progressDialog.dismiss();
                Toast.makeText(MembershipActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progressDialog.dismiss();
                Toast.makeText(MembershipActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progressDialog.dismiss();
                Toast.makeText(MembershipActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progressDialog.dismiss();
                //   Toast.makeText(MainActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(MembershipActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.startTransaction(this, txnRequestCode);
    }



    private void addAdminAmount(String planType) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String subStartDate = simpleDateFormat1.format(calendar.getTime());
        String subEndDate = null;

        Map<String, Object> map = new HashMap<>();
        if (planType.equals("basic_plan_amount")) {
            int total = Integer.parseInt(basic_plan_amount) + Integer.parseInt(first_plan_sale_price);
            upiRef.update(planType, String.valueOf(total));
            map.put("plan", "Basic");
            try {
                Date date = simpleDateFormat1.parse(subStartDate);
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 90);
                subEndDate = simpleDateFormat1.format(calendar.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (planType.equals("premium_plan_amount")) {
            int total = Integer.parseInt(premium_plan_amount) + Integer.parseInt(second_plan_sale_price);
            upiRef.update(planType, String.valueOf(total));
            map.put("plan", "Premium");
            try {
                Date date = simpleDateFormat1.parse(subStartDate);
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 180);
                subEndDate = simpleDateFormat1.format(calendar.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (planType.equals("super_premium_plan_amount")) {
            int total = Integer.parseInt(super_premium_plan_amount) + Integer.parseInt(third_plan_sale_price);
            upiRef.update(planType, String.valueOf(total));
            map.put("plan", "Super_Premium");

            try {
                Date date = simpleDateFormat1.parse(subStartDate);
                calendar.setTime(date);
                calendar.add(Calendar.DATE, 360);
                subEndDate = simpleDateFormat1.format(calendar.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        map.put("subscritpion_end_date", subEndDate);
        map.put("subscritpion", "YES");

        userRef.update(map).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Data Update Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MembershipActivity.this, MainActivity.class));
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}