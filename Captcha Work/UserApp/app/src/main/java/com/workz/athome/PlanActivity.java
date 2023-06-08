package com.workz.athome;

import static com.workz.athome.MainActivity.generateRandomEmail;
import static com.workz.athome.MainActivity.randomMobileNoGenerate;
import static com.workz.athome.MainActivity.razorpay_key;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class PlanActivity extends AppCompatActivity implements PaymentResultListener {

    private DocumentReference userRef;
    private ProgressDialog progressDialog;
    private final int txnRequestCode = 110;
    private String price,cPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = findViewById(R.id.plandetail_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Fetching Data...");

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String tag="aaaaaaaaaaaaaaaa  :- ";
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(userUid);
        userRef.addSnapshotListener((value, error) -> {
            price = value.getString("captcha_price");

            Log.d(tag, "Plan Activity:- "+price);

            if (price.equals("35")) {
                findViewById(R.id.button6).setVisibility(View.GONE);
                findViewById(R.id.button7).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.button6).setVisibility(View.VISIBLE);
                findViewById(R.id.button7).setVisibility(View.GONE);
            }
        });

        allClickRef();


    }


    private void allClickRef() {
        //basic plan details
        findViewById(R.id.button22).setOnClickListener(v -> {

            if (price.equals("7")) {
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
            if (price.equals("15")) {
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
            if (price.equals("35")) {
                //    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            } else {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(PlanActivity.this, R.style.AppCompatAlertDialogStyle);
                alertDialog.setTitle("Upgrade Now!");
                alertDialog.setMessage("Are you Sure? You want to Upgrade In Super Premium Plan with ₹ 280.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Pay Now", (dialog, which) -> {
                    dialog.dismiss();

                    if (razorpay_key.isEmpty()){
                        //pay with Paytm
                        goForPayment("280", "35");
                    }else {
                        //pay with Razorpay
                        startPayment("280");
                        cPrice="35";
                    }
                });
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
            }
        });

        // refund super plan
        findViewById(R.id.button7).setOnClickListener(v -> {
            refundRequest();
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

                    userRef.update("captcha_price", captchaPrice);
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
        transactionManager.startTransaction(PlanActivity.this, txnRequestCode);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(PlanActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
            userRef.update("captcha_price", cPrice);
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show();
    }

  /*  private void showPopUp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.update_plan_layout, null, false);

        Button submit = view.findViewById(R.id.button3);
        Button cancel = view.findViewById(R.id.button2);

        ((RadioButton) view.findViewById(R.id.radio_first)).setText(radio1);
        ((RadioButton) view.findViewById(R.id.radio_second)).setText(radio2);
        ((RadioButton) view.findViewById(R.id.radio_third)).setText(radio3 + "\n& Ad's Free Plan");

        if (captcha_price.contains("5")) {
            view.findViewById(R.id.radio_first).setVisibility(View.VISIBLE);
            view.findViewById(R.id.radio_second).setVisibility(View.VISIBLE);
            view.findViewById(R.id.radio_third).setVisibility(View.VISIBLE);
        } else if (captcha_price.contains("10")) {
            view.findViewById(R.id.radio_first).setVisibility(View.GONE);
            view.findViewById(R.id.radio_second).setVisibility(View.VISIBLE);
            view.findViewById(R.id.radio_third).setVisibility(View.VISIBLE);
            ((RadioButton) view.findViewById(R.id.radio_second)).setChecked(true);


        } else if (captcha_price.contains("22")) {
            view.findViewById(R.id.radio_first).setVisibility(View.GONE);
            view.findViewById(R.id.radio_second).setVisibility(View.GONE);
            view.findViewById(R.id.radio_third).setVisibility(View.VISIBLE);
            ((RadioButton) view.findViewById(R.id.radio_third)).setChecked(true);
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
                    Intent intent = new Intent(MainActivity.this, PaymentActivity.class);
                    intent.putExtra("amount", price1);
                    intent.putExtra("page", "main");
                    intent.putExtra("plan", "1");
                    intent.putExtra("email", userEmail);
                    intent.putExtra("mobile", userMobile);
                    intent.putExtra("userName", userName);
                    startActivityForResult(intent, 1);
                    break;
                case 1:
                    Intent intent1 = new Intent(MainActivity.this, PaymentActivity.class);
                    intent1.putExtra("amount", price2);
                    intent1.putExtra("page", "main");
                    intent1.putExtra("plan", "2");
                    intent1.putExtra("email", userEmail);
                    intent1.putExtra("mobile", userMobile);
                    intent1.putExtra("userName", userName);
                    startActivityForResult(intent1, 1);
                    break;
                case 2:
                    Intent intent2 = new Intent(MainActivity.this, PaymentActivity.class);
                    intent2.putExtra("amount", price3);
                    intent2.putExtra("page", "main");
                    intent2.putExtra("plan", "3");
                    intent2.putExtra("email", userEmail);
                    intent2.putExtra("mobile", userMobile);
                    intent2.putExtra("userName", userName);
                    startActivityForResult(intent2, 1);
                    break;
            }

        });
        cancel.setOnClickListener(view1 -> custome_document_dialog.dismiss());

    }*/

}