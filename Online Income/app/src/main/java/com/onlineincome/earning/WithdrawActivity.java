package com.onlineincome.earning;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onlineincome.earning.OneTimeUtils.NetworkChangeReceiver;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.startapp.sdk.adsbase.StartAppAd;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class WithdrawActivity extends AppCompatActivity {

    private EditText mobile;
    private Button withdrawbtn;
    private ProgressDialog progressDialog;
    private TextView totalReward;
    private CardView notice;
    private String user_email, balance, withdraw_request, normal_withdraw_limit, boost_limit, withdraw_acitvation, totalIncome;
    private DocumentReference userRef, adminRef,adminRef2;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        StartAppAd.showAd(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        //toolbar
        Toolbar toolbar = findViewById(R.id.withdraw_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait!");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Fetching your data...");
        progressDialog.show();

        user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        //initiate views
        mobile = findViewById(R.id.mobile_no);
        withdrawbtn = findViewById(R.id.btn_withdraw_money);
        totalReward = findViewById(R.id.total_reward_amount);
        notice = findViewById(R.id.pending_notice);

        //initiate firebase
        userRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        adminRef = FirebaseFirestore.getInstance().collection("App_utils").document("withdraw");
        userRef.addSnapshotListener((documentSnapshot, e) -> {
            balance = documentSnapshot.getString("wallet");
            withdraw_request = documentSnapshot.getString("withdraw");

            adminRef.addSnapshotListener((value, error) -> {
                normal_withdraw_limit = value.getString("normal_withdraw");
                boost_limit = value.getString("boost_withdraw");
                withdraw_acitvation = value.getString("withdraw_activation");

                adminRef2 = FirebaseFirestore.getInstance().collection("App_utils").document("app_utils");
                adminRef2.addSnapshotListener((value1, error1) -> {
                    totalIncome = value1.getString("total_income");
                    setData();
                });
            });
        });


        //events
        withdrawbtn.setOnClickListener(v -> {

            boolean bal = Float.parseFloat(normal_withdraw_limit) > Float.parseFloat(balance);
            final String mob = mobile.getText().toString();
            if (mob.isEmpty() || mob.length() < 10) {
                Toast.makeText(WithdrawActivity.this, "Enter Valid Mobile No.", Toast.LENGTH_SHORT).show();
                return;
            } else if (bal) {
                Toast.makeText(WithdrawActivity.this, "Minimum balance required \u20B9 "+normal_withdraw_limit, Toast.LENGTH_SHORT).show();
                return;
            } else if (withdraw_request.equals("NO")) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(WithdrawActivity.this);
                alertDialog.setTitle("Activation Pending!");
                alertDialog.setMessage("Dear user, we can see that you have not activate your account. Please activate your account with \u20B9 " + withdraw_acitvation + " to withdraw your money.");
                alertDialog.setCancelable(false);
                alertDialog.setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });
                alertDialog.setPositiveButton("Activate Now", (dialog, which) -> {
                    dialog.dismiss();
                    startPaytm(withdraw_acitvation);
                }).show();
            } else {
                Toast.makeText(this, "Activation Pending...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startPaytm(final String money) {
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
                    paramMap.put("TXN_AMOUNT", String.valueOf(money));
                    paramMap.put("WEBSITE", "WEBSTAGING");
                    paramMap.put("CALLBACK_URL", MainActivity.PAYTM_VERIFY_URL);
                    paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
                    paramMap.put("INDUSTRY_TYPE_ID", "Retail");

                    PaytmOrder paytmOrder = new PaytmOrder(paramMap);
                    paytmPGService.initialize(paytmOrder, null);

                    paytmPGService.startPaymentTransaction(WithdrawActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                        @Override
                        public void onTransactionResponse(Bundle inResponse) {

                            if (inResponse.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {

                                Toast.makeText(WithdrawActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Uploading your request...");
                                progressDialog.show();
                                new Handler().postDelayed(() -> {
                                    addAdminIncomeValue(money);
                                    updateUserBalance(progressDialog);
                                }, 3000);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(WithdrawActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void networkNotAvailable() {
                            progressDialog.dismiss();
                            Toast.makeText(WithdrawActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void clientAuthenticationFailed(String inErrorMessage) {
                            progressDialog.dismiss();
                            Toast.makeText(WithdrawActivity.this, "Authentication error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void someUIErrorOccurred(String inErrorMessage) {
                            progressDialog.dismiss();
                            Toast.makeText(WithdrawActivity.this, "UI error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                            progressDialog.dismiss();
                            Toast.makeText(WithdrawActivity.this, "Webpage upload error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onBackPressedCancelTransaction() {
                            progressDialog.dismiss();
                            Toast.makeText(WithdrawActivity.this, "Transaction cancel", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                            progressDialog.dismiss();
                            Toast.makeText(WithdrawActivity.this, "Transaction cancel " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            Log.d("Work", "Work... " + error.getMessage());
            Toast.makeText(WithdrawActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }) {
            @Override
            protected Map<String, String> getParams() {

                HashMap<String, String> paramMap = new HashMap<String, String>();
                //these are mandatory parameters
                paramMap.put("MID", mid); //MID provided by paytm
                paramMap.put("ORDER_ID", order_id);
                paramMap.put("CUST_ID", custmor_id);
                paramMap.put("CHANNEL_ID", "WAP");
                paramMap.put("TXN_AMOUNT", String.valueOf(money));
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
        adminRef2.update("total_income", String.valueOf(total));
    }

    private void updateUserBalance(final ProgressDialog progressDialog) {

        Map map = new HashMap();
        map.put("balence", "0.0");
        map.put("withdraw", "YES");

        userRef.update(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, "Activation Pending...", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (progressDialog.isShowing()) {
                Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //setting data to text view
    private void setData() {

        Double bale = Double.parseDouble(balance);
        bale = Math.round(bale * 100.00) / 100.00;
        progressDialog.dismiss();
        totalReward.setText("Rs. " + bale + "/-");
        if (withdraw_request.equals("YES"))
        {
            notice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (progressDialog.isShowing()) {
            Toast.makeText(this, "Please Wait...", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
            finish();
        }
    }

}