package com.workfromhome.income.Activity;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.startapp.sdk.adsbase.StartAppAd;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UpgradePlanActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    private Toolbar allPlantoolbar;
    private String user_current_plan, advanceIncome, premiumIncome, standardIncome, user_email, url, verifyUrl;
    private Button btnStandardPlan, btnAdvancePlan, btnPremimumPlan;
    private DocumentReference documentReference, plan_check_refer, resetDataReference, adminRef, orderidRef, paytmref;
    private NetworkChangeReceiver broadcastReceiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade_plan);

         getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        StartAppAd.disableAutoInterstitial();
        StartAppAd.disableSplash();
        StartAppAd.enableConsent(this, false);
        StartAppAd.disableAutoInterstitial();

        adminRef = FirebaseFirestore.getInstance().collection("App_utils").document("Totalincome");
        adminRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                advanceIncome = value.getString("Advanceincome");
                premiumIncome = value.getString("Premiumincome");
                standardIncome = value.getString("Standardincome");
            }
        });

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //progress dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait!");
        dialog.setMessage("loading...");

        allPlantoolbar = (Toolbar) findViewById(R.id.AllPlantoolbar);
        btnStandardPlan = (Button) findViewById(R.id.btn_standard_plan);
        btnAdvancePlan = (Button) findViewById(R.id.btn_advance_plan);
        btnPremimumPlan = (Button) findViewById(R.id.btn_premimum_plan);

        //Toolbar events
        setSupportActionBar(allPlantoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        plan_check_refer = FirebaseFirestore.getInstance().collection("users").document(user_email);

        plan_check_refer.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    user_current_plan = documentSnapshot.getString("plan");
                    manage_button_for_user(user_current_plan);
                }
            }
        });


        btnStandardPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaytm("200", "Standard Plan");
            }
        });
        btnAdvancePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaytm("500", "Advance Plan");
            }
        });
        btnPremimumPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPaytm("1000", "Super Premium Plan");
            }
        });

    }

    private void manage_button_for_user(String user_current_plan) {

        switch (user_current_plan) {
            case "Free Plan":
                break;
            case "Standard Plan":
                btnStandardPlan.setVisibility(View.GONE);
                break;
            case "Advance Plan":
                btnStandardPlan.setVisibility(View.GONE);
                btnAdvancePlan.setVisibility(View.GONE);
                break;
            case "Super Premium Plan":
                btnAdvancePlan.setVisibility(View.GONE);
                btnStandardPlan.setVisibility(View.GONE);
                btnPremimumPlan.setVisibility(View.GONE);
                break;
        }

    }

    private void startPaytm(final String money, final String update_plan) {
        dialog.show();
        final String mid = "Dinesh18399201056005";
        final String custmor_id = FirebaseAuth.getInstance().getUid();
        final String order_id = UUID.randomUUID().toString().substring(0, 28);
        //   final String url = "https://haryanviexpress.com/work-paytm/generateChecksum.php";
        //  final String verifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

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
                        paramMap.put("CALLBACK_URL", verifyUrl);
                        paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
                        paramMap.put("INDUSTRY_TYPE_ID", "Retail");

                        PaytmOrder paytmOrder = new PaytmOrder(paramMap);
                        paytmPGService.initialize(paytmOrder, null);

                        paytmPGService.startPaymentTransaction(UpgradePlanActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {
                                dialog.dismiss();

                                if (inResponse.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {
                                    Toast.makeText(UpgradePlanActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                    dialog.setMessage("Updating your Plan...");
                                    dialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialog.dismiss();

                                            addAdminIncomeValue(money, update_plan);
                                            addPaymentDetail(order_id, update_plan, user_email);

                                            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                            //upgrade plan details
                                            documentReference = FirebaseFirestore.getInstance().collection("users").document(email);
                                            Map map = new HashMap();
                                            map.put("plan", update_plan);

                                            documentReference.update(map);
                                            Toast.makeText(UpgradePlanActivity.this, "Your Plan is upgrade in " + update_plan, Toast.LENGTH_SHORT).show();

                                            //reset web data according to plan
                                            resetDataReference = FirebaseFirestore.getInstance().collection("common_data").document(email);
                                            resetDataReference.delete();

                                            finish();
                                        }
                                    }, 3000);


                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(UpgradePlanActivity.this, "TXN_FAILED", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void networkNotAvailable() {
                                dialog.dismiss();
                                Toast.makeText(UpgradePlanActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                dialog.dismiss();
                                Toast.makeText(UpgradePlanActivity.this, "Authentication error " + inErrorMessage, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                dialog.dismiss();
                                Toast.makeText(UpgradePlanActivity.this, "UI error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                dialog.dismiss();
                                Toast.makeText(UpgradePlanActivity.this, "Webpage upload error " + inErrorMessage, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                dialog.dismiss();
                                Toast.makeText(UpgradePlanActivity.this, "Transaction cancel", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                dialog.dismiss();
                                Toast.makeText(UpgradePlanActivity.this, "Transaction cancel " + inErrorMessage, Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.d("Work", "Work... " + error.getMessage());
                Toast.makeText(UpgradePlanActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                HashMap<String, String> paramMap = new HashMap<String, String>();
                //these are mandatory parameters
                paramMap.put("MID", mid); //MID provided by paytm
                paramMap.put("ORDER_ID", order_id);
                paramMap.put("CUST_ID", custmor_id);
                paramMap.put("CHANNEL_ID", "WAP");
                paramMap.put("TXN_AMOUNT", String.valueOf(money));
                paramMap.put("WEBSITE", "WEBSTAGING");
                paramMap.put("CALLBACK_URL", verifyUrl);
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

    private void addPaymentDetail(String order_id, String update_plan, String user_email) {

        orderidRef = FirebaseFirestore.getInstance().collection("payment_order_id").document(order_id);
        Map map = new HashMap();
        map.put("order_id", order_id);
        map.put("user_updated_plan", update_plan);
        map.put("email", user_email);
        orderidRef.set(map);

    }

    private void addAdminIncomeValue(String money, String update_plan) {

        if (update_plan.equals("Standard Plan")) {
            int total = Integer.parseInt(standardIncome) + Integer.parseInt(money);
            adminRef.update("Standardincome", String.valueOf(total));

        } else if (update_plan.equals("Advance Plan")) {
            int total = Integer.parseInt(advanceIncome) + Integer.parseInt(money);
            adminRef.update("Advanceincome", String.valueOf(total));

        } else if (update_plan.equals("Super Premium Plan")) {
            int total = Integer.parseInt(premiumIncome) + Integer.parseInt(money);
            adminRef.update("Premiumincome", String.valueOf(total));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        dialog.dismiss();
    }

    @Override
    protected void onStart() {
        super.onStart();

        dialog.show();
        paytmref = FirebaseFirestore.getInstance().collection("App_utils").document("Paytm");
        paytmref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                url = documentSnapshot.getString("url");
                verifyUrl = documentSnapshot.getString("verifyurl");
                dialog.dismiss();
            }
        });


    }
}
