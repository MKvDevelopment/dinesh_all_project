package com.spin.wheelgame.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.paytm.pgsdk.TransactionManager;
import com.spin.wheelgame.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class AddMoneyActivity extends AppCompatActivity {

    private String amunt;
    private DocumentReference documentReference, adminRef;
    private String depositbal, winBal, url, spinIncome, card_visiblity;
    private EditText editText;
    private ProgressDialog dialog;
    private int txnRequestCode = 110;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //toolbar
        Toolbar toolbar = findViewById(R.id.addMoney_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adminRef = FirebaseFirestore.getInstance().collection("App_utlil").document("total_amount");
        documentReference = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        dialog = new ProgressDialog(AddMoneyActivity.this);
        dialog.setMessage("Fetching Details...");
        dialog.setTitle("Please Wait");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);

        DocumentReference paytmref = FirebaseFirestore.getInstance().collection("App_utlil").document("Paytm");
        paytmref.addSnapshotListener((documentSnapshot, e) -> {

            url = documentSnapshot.getString("urlnew");
        });

        adminRef.addSnapshotListener((value, error) -> spinIncome = value.getString("amount"));

        editText = findViewById(R.id.edit_amount);

        findViewById(R.id.amount_50).setOnClickListener(v ->
                editText.setText("50"));
        findViewById(R.id.amount_100).setOnClickListener(v ->
                editText.setText("100"));
        findViewById(R.id.amount_200).setOnClickListener(v ->
                editText.setText("200"));
        findViewById(R.id.btn_deposit).setOnClickListener(v -> {
            amunt = editText.getText().toString();
            if (editText.getText().toString().isEmpty()) {
                Toast.makeText(AddMoneyActivity.this, "Minimum amount required 20 \u20B9", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(editText.getText().toString()) < 20) {
                Toast.makeText(AddMoneyActivity.this, "Minimum amount required 20 \u20B9", Toast.LENGTH_LONG).show();
            } else {
                dialog.show();
                postDataUsingVolley(amunt);

            }

        });
    }
    private void postDataUsingVolley(String amount) {
        dialog.setMessage("Fetching Info...");
        //dialog.show();
        String order_id = UUID.randomUUID().toString(); // It should be unique
        // int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(url)
                .appendQueryParameter("ORDER_ID", order_id)
                .appendQueryParameter("CUST_ID", FirebaseAuth.getInstance().getUid())
                .appendQueryParameter("TXN_AMOUNT", amount);

        String myUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(AddMoneyActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            dialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, amount);
            } catch (JSONException e) {
                dialog.dismiss();

                Toast.makeText(AddMoneyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        },
                error ->
                Toast.makeText(AddMoneyActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void processTxn(String orderid, String txnToken, String amount) {
        String mid = "Dinesh18399201056005";

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {
                    Toast.makeText(AddMoneyActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                    dialog.show();

                    new Handler().postDelayed(() -> {
                        dialog.dismiss();
                        addAdminIncomeValue(amount);
                        addAdminIncomeValue(amount);
                        updateUserDetail(dialog);

                    }, 3000);
                } else {
                    Toast.makeText(AddMoneyActivity.this, "Payment Cancelled by you !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                dialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                Toast.makeText(AddMoneyActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                dialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                dialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                dialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                dialog.dismiss();
                //   Toast.makeText(MainActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                dialog.dismiss();
                Toast.makeText(AddMoneyActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.startTransaction(this, txnRequestCode);
    }


    public void refreshWalletUI() {
        ((TextView) findViewById(R.id.wallet_balance_deposits)).setText("\u20B9 " + depositbal);
        ((TextView) findViewById(R.id.wallet_balance_winnings)).setText("\u20B9 " + winBal);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == txnRequestCode && data != null) {
           dialog.dismiss();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();

        documentReference.addSnapshotListener((value, error) -> {
            depositbal = value.getString("deposit_balence");
            winBal = value.getString("winning_balence");
            refreshWalletUI();
        });
    }

    private void addAdminIncomeValue(String money) {
        int total = Integer.parseInt(spinIncome) + Integer.parseInt(money);
        adminRef.update("amount", String.valueOf(total));
    }

    private void updateUserDetail(ProgressDialog dialog) {

        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();

        int bal = Integer.parseInt(depositbal) + Integer.parseInt(amunt);

        documentReference.update("deposit_balence", String.valueOf(bal))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialog.dismiss();
                            finish();
                            Toast.makeText(AddMoneyActivity.this, "Successful Updated", Toast.LENGTH_SHORT).show();
                        }
                    }
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
