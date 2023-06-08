package com.typingwork.jobathome.Activity;

import static com.typingwork.jobathome.Activity.MainActivity.paytm_mid;
import static com.typingwork.jobathome.Activity.MainActivity.paytm_url;
import static com.typingwork.jobathome.Activity.MainActivity.userDataModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.typingwork.jobathome.R;
import com.typingwork.jobathome.Utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;
import java.util.UUID;

public class WorkWithUsActivity extends AppCompatActivity {


    private DocumentReference userRef;
    private CollectionReference userRef2;
    private String userUid;
    private ProgressDialog progressDialog;
    private final int txnRequestCode = 110;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_with_us);

        //waiting dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);


        userUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = FirebaseFirestore.getInstance().collection("Users_list").document(userUid);
        userRef2 = FirebaseFirestore.getInstance().collection("Users_list");

        setSupportActionBar(findViewById(R.id.toolbarr_work));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (userDataModel.getPhoto().equals("Yes")) {
            findViewById(R.id.cardView6).setVisibility(View.GONE);
            findViewById(R.id.cardView7).setVisibility(View.VISIBLE);
        }else {
            findViewById(R.id.cardView6).setVisibility(View.VISIBLE);
            findViewById(R.id.cardView7).setVisibility(View.GONE);
        }

        findViewById(R.id.btn_join).setOnClickListener(v -> {
            progressDialog.show();
            goForPayment("2000");
        });

        findViewById(R.id.appCompatButton7).setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);

            alertDialog.setPositiveButton("Pay Now", (dialog, which) -> {
                dialog.dismiss();
                progressDialog.show();
                goForPayment("145");

            }).setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            Constant.showAlertdialog(WorkWithUsActivity.this, alertDialog, "Video Call!","You have to pay a Video call fee of Rs 145. After Payment, our Expert connect with you on a Video call.");

        });

        findViewById(R.id.appCompatButton6).setOnClickListener(v -> {
            String link = "https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "My Refer Link");
            intent.putExtra(Intent.EXTRA_TEXT, "Hey!! friends - Today i find this amazing Typing Work Application. Earn lot of money from this app at home by Typing Data with your Smartphone. Download app from here " + link + ". Use my refer code  to get Rs.100/-. My refer code :- " + userDataModel.getRefer_code() + " - ");
            startActivity(Intent.createChooser(intent, "Share via"));

        });

    }

    private void goForPayment(String price) {
        progressDialog.setMessage("Fetching Info...");
        progressDialog.show();
        String order_id = UUID.randomUUID().toString(); // It should be unique
        // int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(paytm_url)
                .appendQueryParameter("ORDER_ID", order_id)
                .appendQueryParameter("CUST_ID", FirebaseAuth.getInstance().getUid())
                .appendQueryParameter("TXN_AMOUNT", price);

        String myUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(WorkWithUsActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, price);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(WorkWithUsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
        {
            progressDialog.dismiss();
            Toast.makeText(WorkWithUsActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }
        );
        queue.add(request);
    }

    private void processTxn(String orderid, String txnToken, String amount) {

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, paytm_mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                assert bundle != null;
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {
                    Toast.makeText(getApplicationContext(), "Upgrade to Premium Successfully.", Toast.LENGTH_SHORT).show();
                    new Handler(Looper.myLooper()).postDelayed(() -> {
                       progressDialog.dismiss();

                        updateUserData(amount);


                    }, 2000);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(WorkWithUsActivity.this, "Payment Cancelled by you !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progressDialog.dismiss();
                Toast.makeText(WorkWithUsActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progressDialog.dismiss();
                Toast.makeText(WorkWithUsActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progressDialog.dismiss();
                Toast.makeText(WorkWithUsActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progressDialog.dismiss();
                Toast.makeText(WorkWithUsActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progressDialog.dismiss();
                Toast.makeText(WorkWithUsActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progressDialog.dismiss();
                Toast.makeText(WorkWithUsActivity.this, "You Cancelled this Transaction", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(WorkWithUsActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.startTransaction(WorkWithUsActivity.this, txnRequestCode);
    }

    private void updateUserData(String amount) {

        if (amount.equals("145")){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme);

            alertDialog.setPositiveButton("Ok", (dialog, which) -> {
                dialog.dismiss();
            });
            Constant.showAlertdialog(WorkWithUsActivity.this, alertDialog, "Video Call!","Your request was sent to your team. No need to pay again and again. You are in Queue. On your turn, our Expert will call you on Your registered Mobile No.");

        }else {
            userRef.update("photo","Yes").addOnSuccessListener(unused -> {
                findViewById(R.id.cardView6).setVisibility(View.GONE);
                findViewById(R.id.cardView7).setVisibility(View.VISIBLE);

                if (!userDataModel.getFriend_uid().isEmpty()) {
                    userRef2.document(userDataModel.getFriend_uid()).get().addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot!=null){
                            String  photo=documentSnapshot.getString("photo");
                            String  wallet=documentSnapshot.getString("wallet");

                            if (photo.equals("Yes")){
                                double bale = Double.parseDouble(wallet) + 1000;
                                bale = Math.round(bale * 100.00) / 100.00;
                                userRef2.document(userDataModel.getFriend_uid()).update("wallet", String.valueOf(bale)).addOnSuccessListener(unused1 -> {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(WorkWithUsActivity.this, MainActivity.class));
                                    finish();
                                });
                            }else {
                                progressDialog.dismiss();
                                startActivity(new Intent(WorkWithUsActivity.this, MainActivity.class));
                                finish();
                            }
                        }
                    });

                } else {
                    progressDialog.dismiss();
                    finish();
                }
            });
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}