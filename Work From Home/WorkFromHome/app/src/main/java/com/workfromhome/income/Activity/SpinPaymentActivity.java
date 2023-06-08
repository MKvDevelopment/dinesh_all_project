package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

public class SpinPaymentActivity extends AppCompatActivity {

    private EditText editText;
    private int amount;
    private ProgressDialog dialog;
    private Button  buyNow;
    private NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
    private DocumentReference documentReference, adminRef;
    private String user_email, user_bal,url, verifyUrl,spinIncome;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_payment);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);


        //progress dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait!");
        dialog.setMessage("loading...");
        dialog.show();


        //toolbar
        Toolbar toolbar = findViewById(R.id.spin_pyment_toolbar);
        toolbar.setTitle("Buy Now");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        documentReference = FirebaseFirestore.getInstance().collection("Spin_users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                user_bal=documentSnapshot.getString("spin_bal");
            }
        });
        user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //fetching data from admin
        adminRef = FirebaseFirestore.getInstance().collection("App_utils").document("Totalincome");
        adminRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                spinIncome = value.getString("SpinIncome");
                dialog.dismiss();
            }
        });

        editText = findViewById(R.id.editTextTextPersonName);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("1");
            }
        });
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("3");
            }
        });
        findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("5");
            }
        });

        findViewById(R.id.button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText.getText().toString().isEmpty()) {
                    Toast.makeText(SpinPaymentActivity.this, "Please enter valid value", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    //   Toast.makeText(AddMoneyActivity.this, "dddd", Toast.LENGTH_SHORT).show();
                    amount = Integer.valueOf(editText.getText().toString());

                    if (amount < 1) {
                        Toast.makeText(SpinPaymentActivity.this, "Minimum deposit limit is 1", Toast.LENGTH_SHORT).show();
                        return;
                    } else {

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SpinPaymentActivity.this);
                        alertDialogBuilder.setTitle("Buy Spin");
                        alertDialogBuilder
                                .setMessage("Are You want to sure?\n Please continue to Buy Spin and cancel for exit.")
                                .setCancelable(false)
                                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                       int money= Integer.parseInt(editText.getText().toString())*100;

                                       startPaytm(String.valueOf(money));

                                    }
                                })
                                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                }

            }
        });

    }
    private void startPaytm(final String money) {
        ProgressDialog dialog = new ProgressDialog(SpinPaymentActivity.this);
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

                        paytmPGService.startPaymentTransaction(SpinPaymentActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                            @Override
                            public void onTransactionResponse(Bundle inResponse) {

                                if (inResponse.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {

                                    Toast.makeText(SpinPaymentActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                    dialog.setMessage("Updating your balance...");
                                    dialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            addAdminIncomeValue(money);
                                            updateUserDetail(dialog);
                                        }
                                    }, 3000);
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(SpinPaymentActivity.this, "TXN_FAILED", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void networkNotAvailable() {
                                dialog.dismiss();
                                Toast.makeText(SpinPaymentActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void clientAuthenticationFailed(String inErrorMessage) {
                                dialog.dismiss();
                                Toast.makeText(SpinPaymentActivity.this, "Authentication error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void someUIErrorOccurred(String inErrorMessage) {
                                dialog.dismiss();
                                Toast.makeText(SpinPaymentActivity.this, "UI error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                                dialog.dismiss();
                                Toast.makeText(SpinPaymentActivity.this, "Webpage upload error " + inErrorMessage, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onBackPressedCancelTransaction() {
                                dialog.dismiss();
                                Toast.makeText(SpinPaymentActivity.this, "Transaction cancel", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                                dialog.dismiss();
                                Toast.makeText(SpinPaymentActivity.this, "Transaction cancel " + inErrorMessage, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SpinPaymentActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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

        int bal = Integer.parseInt(user_bal) + amount;

        documentReference.update("spin_bal", String.valueOf(bal)).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialog.dismiss();
                    finish();
                    Toast.makeText(SpinPaymentActivity.this, "Successful Updated", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addAdminIncomeValue(String money) {
        int total = Integer.parseInt(spinIncome) + Integer.parseInt(money);
        adminRef.update("SpinIncome", String.valueOf(total));
    }


    @Override
    protected void onStart() {
        super.onStart();

        dialog.show();
        DocumentReference paytmref = FirebaseFirestore.getInstance().collection("App_utils").document("Paytm");
        paytmref.addSnapshotListener((documentSnapshot, e) -> {

            url = documentSnapshot.getString("url");
            verifyUrl = documentSnapshot.getString("verifyurl");
            dialog.dismiss();
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