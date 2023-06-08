package com.dreamyphobic.paymentgateway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private ProgressDialog progress;
    private int txnRequestCode = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.start_button);

        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postDataUsingVolley();
            }
        });


    }
    private void postDataUsingVolley() {
        String order_id = String.valueOf(new Random().nextInt(1000)); // It should be unique
        int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("paytm-gateway-server.glitch.me")
                .appendQueryParameter("ORDER_ID", order_id)
                .appendQueryParameter("CUST_ID", "qwe")
                .appendQueryParameter("TXN_AMOUNT", String.valueOf(amount));
        String myUrl = builder.build().toString();
        //String url = "https://paytm-gateway-server.glitch.me/";
        progress.show();


        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progress.dismiss();
                try {
                    JSONObject res = new JSONObject(response);
                    String txnToken = res.getJSONObject("body").getString("txnToken");
                    Toast.makeText(MainActivity.this, txnToken, Toast.LENGTH_LONG).show();
                    processTxn(order_id,txnToken,String.valueOf(amount));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Txn Token not received", Toast.LENGTH_LONG).show();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);
    }

    private void processTxn(String orderid, String txnToken, String amount){
        String mid = "Dinesh18399201056005";

        //String callbackurl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+orderid; // Staging Environment
        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                Toast.makeText(getApplicationContext(), "Payment Transaction response " + bundle.toString(), Toast.LENGTH_LONG).show();
                progress.dismiss();
            }

            @Override
            public void networkNotAvailable() {

            }

            @Override
            public void onErrorProceed(String s) {

            }

            @Override
            public void clientAuthenticationFailed(String s) {

            }

            @Override
            public void someUIErrorOccurred(String s) {

            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {

            }

            @Override
            public void onBackPressedCancelTransaction() {

            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {

            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.startTransaction(this, txnRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == txnRequestCode && data != null) {
            Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage")
                    + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
            progress.dismiss();
        }
    }
}