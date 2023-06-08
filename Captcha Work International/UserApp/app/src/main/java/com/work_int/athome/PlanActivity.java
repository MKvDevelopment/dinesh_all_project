package com.work_int.athome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class PlanActivity extends AppCompatActivity {

    public static final String clientKey = "AaJsE1zwiqvROd7-Swcp7ezoEdy4ovsqRmNoNFX2esza7Q_8T_e7cEH6DeVQvDt1tB8yDh3mc93URhcX";
    public static final int PAYPAL_REQUEST_CODE = 123;

    // Paypal Configuration Object
    private static PayPalConfiguration config = new PayPalConfiguration()
            // Start with mock environment.  When ready,
            // switch to sandbox (ENVIRONMENT_SANDBOX)
            // or live (ENVIRONMENT_PRODUCTION)
            .environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
            // on below line we are passing a client id.
            .clientId(clientKey);

    private DocumentReference userRef;
    private ProgressDialog progressDialog;
    private final int txnRequestCode = 110;
    private String price;

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

        userRef = FirebaseFirestore.getInstance().collection("User_List").document(userUid);
        userRef.addSnapshotListener((value, error) -> {
            price = value.getString("captcha_price");

            if (price.equals("15")) {
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
                alertDialog.setMessage("Are you Sure? You want to Upgrade In Basic Plan with $ 20.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Pay Now", (dialog, which) -> {
                    dialog.dismiss();
                    getPayment("20");
                    //pay with Paytm
                    // goForPayment("20", "7");
                    // userRef.update("captcha_price", captchaPrice);
                    Toast.makeText(this, "pending", Toast.LENGTH_SHORT).show();
                });
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();

            }
        });

        // premium plan
        findViewById(R.id.button1).setOnClickListener(v -> {
            if (price.equals("10")) {
                Toast.makeText(getApplicationContext(), "You have already this plan", Toast.LENGTH_LONG).show();
            } else {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(PlanActivity.this, R.style.AppCompatAlertDialogStyle);
                alertDialog.setTitle("Upgrade Now!");
                alertDialog.setMessage("Are you Sure? You want to Upgrade In Premium Plan with $ 35.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Pay Now", (dialog, which) -> {
                    dialog.dismiss();
                    getPayment("35");
                    //pay with Paytm
                    // goForPayment("35", "10");
                    // userRef.update("captcha_price", captchaPrice);
                    Toast.makeText(this, "pending", Toast.LENGTH_SHORT).show();

                });
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
            }
        });

        // super premium plan
        findViewById(R.id.button6).setOnClickListener(v -> {
            if (price.equals("15")) {
                //    Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
            } else {
                androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(PlanActivity.this, R.style.AppCompatAlertDialogStyle);
                alertDialog.setTitle("Upgrade Now!");
                alertDialog.setMessage("Are you Sure? You want to Upgrade In Super Premium Plan with $ 50.");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Pay Now", (dialog, which) -> {
                    dialog.dismiss();
                    getPayment("50");
                    //pay with Paytm
                    // goForPayment("50", "15");
                    // userRef.update("captcha_price", captchaPrice);
                    Toast.makeText(this, "pending", Toast.LENGTH_SHORT).show();

                });
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();
            }

        });

        // refund super plan
        findViewById(R.id.button7).setOnClickListener(v -> {
            refundRequest();
        });

    }

    private void getPayment(String amount) {
        // Creating a paypal payment on below line.
        PayPalPayment payment = new PayPalPayment(new BigDecimal(amount), "USD", "Course Fees",
                PayPalPayment.PAYMENT_INTENT_SALE);

        // Creating Paypal Payment activity intent
        Intent intent = new Intent(this, PaymentActivity.class);

        //putting the paypal configuration to the intent
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        // Putting paypal payment to the intent
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);

        // Starting the intent activity for result
        // the request code will be used on the method onActivityResult
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);

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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the result is from paypal
        if (requestCode == PAYPAL_REQUEST_CODE) {

            // If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {

                // Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                // if confirmation is not null
                if (confirm != null) {
                    try {
                        // Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        // on below line we are extracting json response and displaying it in a text view.
                        JSONObject payObj = new JSONObject(paymentDetails);
                        String payID = payObj.getJSONObject("response").getString("id");
                        String state = payObj.getJSONObject("response").getString("state");
                        // paymentTV.setText("Payment " + state + "\n with payment id is " + payID);

                        Toast.makeText(this, "Payment " + state + "\n with payment id is " + payID, Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        // handling json exception on below line
                        Log.e("Error", "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // on below line we are checking the payment status.
                Log.i("paymentExample", "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // on below line when the invalid paypal config is submitted.
                Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }


}