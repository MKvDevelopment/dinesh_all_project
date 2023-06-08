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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CommunityActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private String data, withdraw, totalIncome;
    private Button reviewbtn;
    private EditText postDataTitle, postDataDes;
    private DocumentReference appUtils, userRef, adminRef;
    private NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //waiting dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        final WebView webVieww = findViewById(R.id.webView4);
        reviewbtn = findViewById(R.id.post_review);

        Toolbar toolbar = findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Community");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        appUtils = FirebaseFirestore.getInstance().collection("App_utils").document("community");
        adminRef = FirebaseFirestore.getInstance().collection("App_utils").document("app_utils");
        userRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        appUtils.addSnapshotListener((value, error) -> {

            data = value.getString("data");
            webVieww.loadData(data, "text/html", null);

            userRef.addSnapshotListener((value1, error1) -> {
                withdraw = value1.getString("withdraw");

                adminRef.addSnapshotListener((value2, error2) -> {
                    totalIncome = value2.getString("total_income");
                    progressDialog.dismiss();
                });
            });
        });
        reviewbtn.setOnClickListener(v -> {
            if (withdraw.equals("YES")) {
                new AlertDialog.Builder(this)
                        .setTitle("Payment Pending...")
                        .setMessage("If you want to post here then you have to pay \u20B9 80. After that, you can share your feedback.")
                        .setCancelable(false)
                        .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                            startPaytm("5");
                        }).setNegativeButton("Cancel", (dialog, which) -> {

                }).show();
            } else {
                Toast.makeText(this, "First work with us at least 5-7 days. After that, you can post here.", Toast.LENGTH_LONG).show();
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

                    paytmPGService.startPaymentTransaction(CommunityActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                        @Override
                        public void onTransactionResponse(Bundle inResponse) {

                            if (inResponse.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {

                                Toast.makeText(CommunityActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                progressDialog.setCanceledOnTouchOutside(false);
                                progressDialog.setCancelable(false);
                                progressDialog.setMessage("Preparing your request...");
                                progressDialog.show();
                                new Handler().postDelayed(() -> {
                                    addAdminIncomeValue(money);
                                    getInput();
                                }, 3000);
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(CommunityActivity.this, "FAILED", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void networkNotAvailable() {
                            progressDialog.dismiss();
                            Toast.makeText(CommunityActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void clientAuthenticationFailed(String inErrorMessage) {
                            progressDialog.dismiss();
                            Toast.makeText(CommunityActivity.this, "Authentication error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void someUIErrorOccurred(String inErrorMessage) {
                            progressDialog.dismiss();
                            Toast.makeText(CommunityActivity.this, "UI error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                            progressDialog.dismiss();
                            Toast.makeText(CommunityActivity.this, "Webpage upload error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onBackPressedCancelTransaction() {
                            progressDialog.dismiss();
                            Toast.makeText(CommunityActivity.this, "Transaction cancel", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                            progressDialog.dismiss();
                            Toast.makeText(CommunityActivity.this, "Transaction cancel " + inErrorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            Log.d("Work", "Work... " + error.getMessage());
            Toast.makeText(CommunityActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                return paramMap;
            }
        };
        requestQueue.add(stringRequest);
    }

    private void addAdminIncomeValue(String money) {
        float total = Float.parseFloat(totalIncome) + Float.parseFloat(money);
        adminRef.update("total_income", String.valueOf(total));
    }

    private void getInput() {
        AlertDialog.Builder alert = new AlertDialog.Builder(CommunityActivity.this);
        View view = getLayoutInflater().inflate(R.layout.community_post_review, null);

        postDataDes = (EditText) view.findViewById(R.id.toast_edit_text_des);
        postDataTitle = (EditText) view.findViewById(R.id.toast_edit_text_title);

        Button btn_cancel = (Button) view.findViewById(R.id.toast_cancel);
        Button btn_submit = (Button) view.findViewById(R.id.toast_submit);

        alert.setView(view);

        final AlertDialog custom_dialog = alert.create();
        custom_dialog.setCanceledOnTouchOutside(false);
        custom_dialog.setCancelable(false);
        custom_dialog.show();

        btn_cancel.setOnClickListener(v -> custom_dialog.dismiss());

        btn_submit.setOnClickListener(v -> {

            if (postDataTitle.getText().toString().isEmpty() || postDataDes.getText().toString().isEmpty()) {
                postDataDes.setError("Empty!");
                postDataTitle.setError("Empty!");
                Toast.makeText(this, "Field is Empty", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setMessage("Uploading request...");
                progressDialog.show();
                custom_dialog.dismiss();
                new Handler().postDelayed(() -> {
                    Toast.makeText(this, "Successfully submitted. Pending approval.", Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }, 5000);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}