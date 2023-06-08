package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EverydayPayActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressDialog dialog;
    private Button payNow;
    private String everydayIncome, user_email, url, verifyUrl;
    private DocumentReference adminRef, orderidRef;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_everyday_pay);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //progress dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait!");
        dialog.setMessage("loading...");
        dialog.show();

        //Toolbar events
        toolbar = (Toolbar) findViewById(R.id.ed_payToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        payNow = findViewById(R.id.btn_everdayPlan);
        user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //fetching data from admin
        adminRef = FirebaseFirestore.getInstance().collection("App_utils").document("Totalincome");
        adminRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                everydayIncome = value.getString("Everydayincome");
                dialog.dismiss();
            }
        });

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // updateUserDetail();
                startPaytm("150");
            }
        });

    }

    private void startPaytm(final String money) {
        ProgressDialog dialog = new ProgressDialog(EverydayPayActivity.this);
        dialog.setMessage("Fetching Details....");
        dialog.show();

        final String mid = "Dinesh18399201056005";
        final String custmor_id = FirebaseAuth.getInstance().getUid();
        final String order_id = UUID.randomUUID().toString().substring(0, 28);
        // final String url = "https://haryanviexpress.com/work-paytm/generateChecksum.php";
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

                        paytmPGService.startPaymentTransaction(EverydayPayActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {

                                if (inResponse.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {

                                    Toast.makeText(EverydayPayActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                    dialog.setMessage("Updating your Plan...");
                                    dialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            addAdminIncomeValue(money);
                                            addPaymentDetail(order_id, "EveryDay Plan", user_email);
                                            updateUserDetail(dialog);
                                        }
                                    }, 3000);
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(EverydayPayActivity.this, "TXN_FAILED", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void networkNotAvailable() {
                                dialog.dismiss();
                                Toast.makeText(EverydayPayActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                dialog.dismiss();
                                Toast.makeText(EverydayPayActivity.this, "Authentication error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                dialog.dismiss();
                                Toast.makeText(EverydayPayActivity.this, "UI error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                dialog.dismiss();
                                Toast.makeText(EverydayPayActivity.this, "Webpage upload error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                dialog.dismiss();
                                Toast.makeText(EverydayPayActivity.this, "Transaction cancel", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                dialog.dismiss();
                                Toast.makeText(EverydayPayActivity.this, "Transaction cancel " + inErrorMessage, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(EverydayPayActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String subStartDate = simpleDateFormat1.format(calendar.getTime());

        orderidRef = FirebaseFirestore.getInstance().collection("Everyday_payment_order_id").document(order_id);
        Map map = new HashMap();
        map.put("order_id", order_id);
        map.put("purchage_date", subStartDate);
        map.put("user_updated_plan", update_plan);
        map.put("email", user_email);
        orderidRef.set(map);

    }

    private void updateUserDetail(ProgressDialog dialog) {

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String purchageDate = simpleDateFormat1.format(calendar.getTime());
        String subStartDate = null;

        try {
            Date date = simpleDateFormat1.parse(purchageDate);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            subStartDate = simpleDateFormat1.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            Date date = simpleDateFormat1.parse(purchageDate);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        String name = user.getDisplayName();

        Map map = new HashMap();
        map.put("email", email);
        map.put("name", name);
        map.put("wallet", "0.0");
        map.put("view_post", "0");
        map.put("token_id", id);
        map.put("status", "Unblock");
        map.put("reason", "The system deducts invalid activity on your everyday plan. It can be one account use in multiple devices. We Block your account for 24 hours. Tomorrow you can upgrade again. You can continue your work without any fraud activity. Work properly with us.");
        map.put("plan_start_date", subStartDate);

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("everyday_users").document(email);
        documentReference.set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    startActivity(new Intent(EverydayPayActivity.this, MainActivity.class));
                    finish();
                    Toast.makeText(EverydayPayActivity.this, "Successful Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addAdminIncomeValue(String money) {
        int total = Integer.parseInt(everydayIncome) + Integer.parseInt(money);
        adminRef.update("Everydayincome", String.valueOf(total));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        dialog.show();
        DocumentReference paytmref = FirebaseFirestore.getInstance().collection("App_utils").document("Paytm");
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