package com.onlineincome.earning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onlineincome.earning.OneTimeUtils.NetworkChangeReceiver;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskActivity extends AppCompatActivity {

    private String title, total_imp, user_email, task_no, completedTask, task_price, wallet,
            boost_plan, boostActivation, totalIncome,normalWithdrawLimit;
    private TextView totalImp, completeImp, text, boostDetail, walletDetail;
    private Button viewImp, boostPlan;
    private ImageView imageView;
    private ProgressDialog progressDialog;
    private NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
    private DocumentReference commonRef, adminRef, userRef, adminRef2,adminRef3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        title = getIntent().getStringExtra("task");
        task_no = getIntent().getStringExtra("task1");
        task_price = getIntent().getStringExtra("price");

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setTitle("Please wait");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        totalImp = findViewById(R.id.textView8);
        imageView = findViewById(R.id.imageView8);
        completeImp = findViewById(R.id.textView20);
        viewImp = findViewById(R.id.button1);
        text = findViewById(R.id.textView10);
        boostDetail = findViewById(R.id.textView22);
        boostPlan = findViewById(R.id.boost_plan);
        walletDetail = findViewById(R.id.textView21);

        Toolbar toolbar = findViewById(R.id.task_toolbar);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        adminRef = FirebaseFirestore.getInstance().collection("Task Details").document("task");
        adminRef2 = FirebaseFirestore.getInstance().collection("App_utils").document("withdraw");
        adminRef3 = FirebaseFirestore.getInstance().collection("App_utils").document("app_utils");
        commonRef = FirebaseFirestore.getInstance().collection("completed_task").document(user_email);
        userRef = FirebaseFirestore.getInstance().collection("users").document(user_email);


        commonRef.addSnapshotListener((value, error) -> {
            completedTask = value.getString(task_no);

            adminRef.addSnapshotListener((value1, error1) -> {
                progressDialog.dismiss();
                total_imp = value1.getString(task_no);

                userRef.addSnapshotListener((value2, error2) -> {
                    wallet = value2.getString("wallet");
                    boost_plan = value2.getString("boost");

                    adminRef2.addSnapshotListener((value3, error3) -> {
                        boostActivation = value3.getString("boost_activation");
                        totalIncome = value3.getString("total_income");
                        normalWithdrawLimit = value3.getString("normal_withdraw");
                        setData();
                    });

                });
            });
        });

        viewImp.setOnClickListener(v -> {

            if (Float.parseFloat(wallet)>=Float.parseFloat(normalWithdrawLimit))
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(TaskActivity.this);
                alertDialog.setTitle("Withdraw Enabled!");
                alertDialog.setMessage("Dear user, Now you are eligible to withdraw your money. Go for withdraw now ");
                alertDialog.setCancelable(false);
                alertDialog.setPositiveButton("Go Now", (dialog, which) -> {
                    dialog.dismiss();
                    startActivity(new Intent(TaskActivity.this,WithdrawActivity.class));
                }).show();
            }else {
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();

                viewImp.setVisibility(View.GONE);

                StartAppSDK.setTestAdsEnabled(false);
                StartAppAd.disableSplash();
                StartAppAd.showAd(TaskActivity.this);

                int total = Integer.parseInt(completedTask) + 1;

                new Handler().postDelayed(() -> {
                    commonRef.update(task_no, String.valueOf(total))
                            .addOnCompleteListener(task -> {

                                if (task.isSuccessful()) {
                                    if (Integer.parseInt(completedTask) >= Integer.parseInt(total_imp)) {
                                        imageView.setImageResource(R.drawable.ic_check_circle);
                                        text.setText("Task Completed :)");
                                        viewImp.setVisibility(View.INVISIBLE);
                                        progressDialog.dismiss();

                                        if (boost_plan.equals("YES")) {
                                            int totl = Integer.parseInt(task_price) * 2;
                                            int total_wallet = Integer.parseInt(wallet) + totl;
                                            userRef.update("wallet", String.valueOf(total_wallet));
                                        } else {
                                            int total_wallet = Integer.parseInt(wallet) + Integer.parseInt(task_price);
                                            userRef.update("wallet", String.valueOf(total_wallet));
                                        }
                                    } else {
                                        viewImp.setVisibility(View.VISIBLE);
                                        progressDialog.dismiss();
                                        Toast.makeText(this, "Complete Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    viewImp.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                    Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }, 5000);
            }
        });

        boostPlan.setOnClickListener(v -> {
            initiatepayment(boostActivation);
        });
    }


    private void setData() {

        if (boost_plan.equals("YES")) {
            boostPlan.setVisibility(View.GONE);
            boostDetail.setVisibility(View.VISIBLE);
            boostDetail.setText("( Booster Activated )");
            int total=Integer.parseInt(task_price)*2;
            walletDetail.setText("Complete this task and earn Rs. " + total + "/-");

        } else {
            walletDetail.setText("Complete this task and earn Rs. " + task_price + "/-");
            if (Float.parseFloat(wallet) >= 100.0) {
                boostPlan.setVisibility(View.VISIBLE);
                boostDetail.setVisibility(View.VISIBLE);
            } else {
                boostPlan.setVisibility(View.GONE);
                boostDetail.setVisibility(View.GONE);
            }
        }
        if (Integer.parseInt(completedTask) >= Integer.parseInt(total_imp)) {
            imageView.setImageResource(R.drawable.ic_baseline_check);
            text.setText("Task Completed :)");
            viewImp.setVisibility(View.INVISIBLE);
        }
        totalImp.setText(total_imp);
        completeImp.setText(completedTask);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initiatepayment(String amount) {

        progressDialog.setMessage("Fetching Details....");
        progressDialog.show();

        final String mid = "Dinesh18399201056005";
        final String custmor_id = FirebaseAuth.getInstance().getUid();
        final String order_id = UUID.randomUUID().toString().substring(0, 28);

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.PAYTM_URL, response -> {

            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("CHECKSUMHASH")) {
                    String CHECKSUMHASH = jsonObject.getString("CHECKSUMHASH");

                    PaytmPGService paytmPGService = PaytmPGService.getProductionService();
                    HashMap<String, String> paramMap = new HashMap<>();
                    //these are mandatory parameters
                    paramMap.put("MID", mid); //MID provided by paytm
                    paramMap.put("ORDER_ID", order_id);
                    paramMap.put("CUST_ID", custmor_id);
                    paramMap.put("CHANNEL_ID", "WAP");
                    paramMap.put("TXN_AMOUNT", amount);
                    paramMap.put("WEBSITE", "WEBSTAGING");
                    paramMap.put("CALLBACK_URL", MainActivity.PAYTM_VERIFY_URL);
                    paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
                    paramMap.put("INDUSTRY_TYPE_ID", "Retail");

                    PaytmOrder paytmOrder = new PaytmOrder(paramMap);
                    paytmPGService.initialize(paytmOrder, null);

                    paytmPGService.startPaymentTransaction(TaskActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                        @Override
                        public void onTransactionResponse(Bundle inResponse) {

                            if (inResponse.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {

                                Toast.makeText(TaskActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Uploading your request...");
                                progressDialog.show();
                                new Handler().postDelayed(() -> {
                                    addAdminIncomeValue(amount);
                                    updateUserBalance(progressDialog);
                                }, 3000);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(TaskActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void networkNotAvailable() {
                            progressDialog.dismiss();
                            Toast.makeText(TaskActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void clientAuthenticationFailed(String inErrorMessage) {
                            progressDialog.dismiss();
                            Toast.makeText(TaskActivity.this, "Authentication error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void someUIErrorOccurred(String inErrorMessage) {
                            progressDialog.dismiss();
                            Toast.makeText(TaskActivity.this, "UI error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                            progressDialog.dismiss();
                            Toast.makeText(TaskActivity.this, "Webpage upload error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onBackPressedCancelTransaction() {
                            progressDialog.dismiss();
                            Toast.makeText(TaskActivity.this, "Transaction cancel", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                            progressDialog.dismiss();
                            Toast.makeText(TaskActivity.this, "Transaction cancel " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            Log.d("Work", "Work... " + error.getMessage());
            Toast.makeText(TaskActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> paramMap = new HashMap<String, String>();
                //these are mandatory parameters
                paramMap.put("MID", mid); //MID provided by paytm
                paramMap.put("ORDER_ID", order_id);
                paramMap.put("CUST_ID", custmor_id);
                paramMap.put("CHANNEL_ID", "WAP");
                paramMap.put("TXN_AMOUNT", amount);
                paramMap.put("WEBSITE", "WEBSTAGING");
                paramMap.put("CALLBACK_URL", MainActivity.PAYTM_VERIFY_URL);
                //paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
                // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
                // paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
                //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                return paramMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void addAdminIncomeValue(String money) {
        float total = Float.parseFloat(totalIncome) + Float.parseFloat(money);
        adminRef3.update("total_income", String.valueOf(total));
    }

    private void updateUserBalance(final ProgressDialog progressDialog) {

        Map map = new HashMap();
        map.put("boost", "YES");

        userRef.update(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(TaskActivity.this, "Activate Successfully", Toast.LENGTH_SHORT).show();
            }
        });


    }

}