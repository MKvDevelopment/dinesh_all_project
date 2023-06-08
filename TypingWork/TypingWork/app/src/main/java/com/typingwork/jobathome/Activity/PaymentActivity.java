package com.typingwork.jobathome.Activity;

import static com.typingwork.jobathome.Activity.MainActivity.razorpay_server_link;
import static com.typingwork.jobathome.Activity.MainActivity.userDataModel;
import static com.typingwork.jobathome.Utils.Constant.isConnectionAvailable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.typingwork.jobathome.Model.UserDataModel;
import com.typingwork.jobathome.R;
import com.typingwork.jobathome.Utils.Constant;
import com.typingwork.jobathome.Utils.NetworkChangeReceiver;
import com.typingwork.jobathome.databinding.ActivityPaymentBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    private UserDataModel model = userDataModel;
    private ActivityPaymentBinding binding;
    private DocumentReference adminRef;
    private CollectionReference userRef;
    private int securityFee, limited_offer, upi_offer;
    private String razorpay_upi, paytm_mid, paytm_url, userUid, friendWallet;
    private String amount;
    private ActivityResultLauncher<Intent> upiActivityResultLauncher;
    private ProgressDialog progressDialog;
    private final int txnRequestCode = 110;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        progressDialog = new ProgressDialog(this);
        Constant.showProgressDialog(progressDialog, "Please Wait", "Fetching Data...");
        progressDialog.show();

        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setSupportActionBar(binding.toolbarrWithdraw);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        adminRef = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");
        userRef = FirebaseFirestore.getInstance().collection("Users_list");
        adminRef.addSnapshotListener((value, error) -> {
            assert value != null;
            securityFee = Integer.parseInt(Objects.requireNonNull(value.getString("security_fee")));
            limited_offer = Integer.parseInt(Objects.requireNonNull(value.getString("limited_offer")));
            upi_offer = Integer.parseInt(Objects.requireNonNull(value.getString("upi_offer")));
            razorpay_upi = value.getString("razorpay_upi");
            paytm_mid = value.getString("paytm_mid");
            paytm_url = value.getString("paytm_url");
            if (razorpay_upi.equals("")){
                binding.textView35.setVisibility(View.GONE);
                binding.appCompatButton1.setVisibility(View.GONE);

            }else {
                binding.textView35.setVisibility(View.VISIBLE);
                binding.appCompatButton1.setVisibility(View.VISIBLE);
                binding.textView35.setText("Additional ₹100 Discount If you Pay with GPay/PhonePay (₹" + upi_offer + ")");
            }

            binding.textView33.setText("" + securityFee);
            binding.textView34.setText("Limited Time Offer : ₹" + limited_offer);

            progressDialog.dismiss();
        });

        if (!model.getFriend_uid().isEmpty()) {
            userRef.document(model.getFriend_uid()).addSnapshotListener((value, error) -> {
                friendWallet = value.getString("wallet");
            });
        }


        //event
        upiActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        if (data != null) {
                            String txt = data.getStringExtra("response");
                            ArrayList<String> datalist = new ArrayList<>();
                            datalist.add(txt);
                            upiPaymentDataOperation(datalist);
                        } else {
                            ArrayList<String> list = new ArrayList<>();
                            list.add("nothing");
                            upiPaymentDataOperation(list);
                        }
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("nothing");
                        upiPaymentDataOperation(list);
                    }
                });
        binding.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                Toast.makeText(PaymentActivity.this, "Discount Applied", Toast.LENGTH_SHORT).show();
                binding.textView33.setText(""+securityFee);
                binding.textView34.setText("Limited Time Offer : ₹" + (limited_offer - 100));
                binding.textView35.setText("Additional ₹100 Discount If you Pay with GPay/PhonePay (₹" + (upi_offer - 100) + ")");
            } else {
                binding.textView33.setText(""+ securityFee);
                binding.textView34.setText("Limited Time Offer : ₹" + limited_offer);
                binding.textView35.setText("Additional ₹100 Discount If you Pay with GPay/PhonePay (₹" + upi_offer + ")");
            }
        });
        //card ,netbanking
        binding.appCompatButton7.setOnClickListener(view -> {
            if (binding.checkBox.isChecked()) {
                int totl = limited_offer - 100;
                showConfirmAlert("2", String.valueOf(totl));
            } else {
                showConfirmAlert("2", String.valueOf(limited_offer));
            }
        });
        //upi payment
        binding.appCompatButton1.setOnClickListener(view -> {
            // updateUserDetail();
            if (binding.checkBox.isChecked()) {
                int totl = upi_offer - 100;
                showConfirmAlert("1", String.valueOf(totl));
            } else {
                showConfirmAlert("1", String.valueOf(upi_offer));

            }
        });

    }

    private void upiPaymentDataOperation(ArrayList<String> datalist) {
        progressDialog.show();
        if (isConnectionAvailable(getApplicationContext())) {
            String str = datalist.get(0);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String[] response = str.split("&");
            for (String s : response) {
                String[] equalStr = s.split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else {
                        if (!equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase())) {
                            equalStr[0].toLowerCase();
                            "txnRef".toLowerCase();
                        }
                    }
                } else {
                    paymentCancel = "Payment cancelled.";
                }
            }

            if (status.equals("success")) {
                progressDialog.show();
                Toast.makeText(getApplicationContext(), "Premium Upgrade Successfully.", Toast.LENGTH_SHORT).show();
                new Handler(Looper.myLooper()).postDelayed(() -> updateUserDetail(), 2000);
            } else if ("Payment cancelled.".equals(paymentCancel)) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Payment cancelled.", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Transaction failed.Please try com_new", Toast.LENGTH_SHORT).show();
            }
        } else {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Internet connection is not available. Please check and try com_new", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUserDetail() {
        Map<String, Object> map1 = new HashMap<>();
        map1.put("activation", "Yes");
        map1.put("plan", "Free Plan");
        map1.put("form_price", "4");

        userRef.document(userUid).update(map1).addOnSuccessListener(unused -> {
            if (!model.getFriend_uid().isEmpty()) {
                userRef.document(model.getFriend_uid()).get().addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot!=null){
                        String  photo=documentSnapshot.getString("photo");
                        String  wallet=documentSnapshot.getString("wallet");

                        if (photo.equals("Yes")){
                            double bale = Double.parseDouble(wallet) + 500;
                            bale = Math.round(bale * 100.00) / 100.00;
                            userRef.document(model.getFriend_uid()).update("wallet", String.valueOf(bale)).addOnSuccessListener(unused1 -> {
                                progressDialog.dismiss();
                                startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                                finish();
                            });
                        }else
                        {
                            double bale = Double.parseDouble(wallet) + 100;
                            bale = Math.round(bale * 100.00) / 100.00;
                            userRef.document(model.getFriend_uid()).update("wallet", String.valueOf(bale)).addOnSuccessListener(unused1 -> {
                                progressDialog.dismiss();
                                startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                                finish();
                            });
                        }


                    }
                });

            } else {
                progressDialog.dismiss();
                finish();
            }
        });

    }

    private void showConfirmAlert(String type, String amount) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            dialog.dismiss();
            if (type.contains("1")) {
                this.amount = amount;
                //goForUPI(amount);
                startPayment(amount);

            } else {
                // other gateway
                goForPayment(amount);
            }
        }).setNegativeButton("Cancel", (dialog, which) ->
                dialog.dismiss());

        if (type.contains("1")) {
            Constant.showAlertdialog(PaymentActivity.this, builder, "Confirm", "Are you sure, You want to Upgrade in Premium with G Pay or PhonePay");
        } else {
            Constant.showAlertdialog(PaymentActivity.this, builder, "Confirm", "Are you sure, You want to Upgrade in Premium with Card or NetBanking");
        }
    }

    public void startPayment(String amount)  {
       progressDialog.show();
        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setKeyID(razorpay_upi);//razorpay key
        try {
            JSONObject postData = new JSONObject();
            try {
                // int amount = 1;//edit amount
                postData.put("action", "create_order");
                postData.put("amount", ""+amount);//amount
                MediaType mediaType = MediaType.parse("application/json");
                RequestBody body = RequestBody.create(mediaType, String.valueOf(postData));
                OkHttpClient client = new OkHttpClient();
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(razorpay_server_link)
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            try {
                                String str = response.body().string();
                                JSONObject object = new JSONObject(str);
                                boolean success = object.getBoolean("success");
                                String result = object.getString("response");
                                if (success) {
                                    JSONObject options = new JSONObject();
                                    options.put("name", getString(R.string.app_name));//merchant name
                                    options.put("description", "Total payable amount");//
                                    options.put("send_sms_hash",true);
                                    options.put("allow_rotation", true);
                                    //You can omit the image option to fetch the image from dashboard
                                    options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
                                    options.put("currency", "INR");
                                    options.put("amount", ""+Integer.parseInt(amount)*100);//edit amount in paise
                                    options.put("order_id",""+result);

                                    JSONObject preFill = new JSONObject();
                                    preFill.put("email",  userDataModel.getEmail());
                                    preFill.put("contact",  userDataModel.getPhone());
                                    options.put("prefill", preFill);

                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                            co.open(activity, options);
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    if (result.isEmpty()) {
                                        result = "Something went wrong";
                                        //  Toast.makeText(activity, result, Toast.LENGTH_SHORT).show();
                                    }
                                    String finalResult = result;
                                    activity.runOnUiThread(() -> Toast.makeText(activity, finalResult, Toast.LENGTH_SHORT).show());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
                            }
                        } else {
                            progressDialog.dismiss();
                            activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
                        }
                    }
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                progressDialog.dismiss();
                activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
            }
        } catch (Exception e) {
            progressDialog.dismiss();
            activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
            e.printStackTrace();
        }
    }

    private void goForUPI(String amount) {
        Uri uri = new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", razorpay_upi)
                .appendQueryParameter("pn", String.valueOf(R.string.app_name))
                .appendQueryParameter("tn", "")
                .appendQueryParameter("tr", "114356789")
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);
        //it will show a dialog to user to choose an app
        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");
        //check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            upiActivityResultLauncher.launch(chooser);
        } else {
            Toast.makeText(getApplicationContext(), "This Is Not Working", Toast.LENGTH_SHORT).show();
        }
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

        RequestQueue queue = Volley.newRequestQueue(PaymentActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, price);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
        {
            progressDialog.dismiss();
            Toast.makeText(PaymentActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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
                    new Handler(Looper.myLooper()).postDelayed(() -> updateUserDetail(), 2000);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PaymentActivity.this, "Payment Cancelled by you !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progressDialog.dismiss();
                Toast.makeText(PaymentActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progressDialog.dismiss();
                Toast.makeText(PaymentActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progressDialog.dismiss();
                Toast.makeText(PaymentActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progressDialog.dismiss();
                Toast.makeText(PaymentActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progressDialog.dismiss();
                Toast.makeText(PaymentActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progressDialog.dismiss();
                Toast.makeText(PaymentActivity.this, "You Cancelled this Transaction", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(PaymentActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.startTransaction(PaymentActivity.this, txnRequestCode);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        run_api(s);

    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        progressDialog.dismiss();
        Toast.makeText(PaymentActivity.this, "Payment Failed!", Toast.LENGTH_SHORT).show();

    }
    private void run_api(String s) {
        final Activity activity = this;
        JSONObject postData = new JSONObject();
        try {
            postData.put("action", "verify_order");
            postData.put("pay_id", s);
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, String.valueOf(postData));
            OkHttpClient client = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(razorpay_server_link)//api URL
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        try {
                            String str = response.body().string();
                            JSONObject object = new JSONObject(str);
                            boolean success = object.getBoolean("success");
                            String result = object.getString("response");
                            if (success) {
                                activity.runOnUiThread(() -> {
                                    Toast.makeText(getApplicationContext(), "Upgrade to Premium Successfully.", Toast.LENGTH_SHORT).show();
                                    new Handler(Looper.myLooper()).postDelayed(() -> updateUserDetail(), 2000);
                                });
                            } else {
                                if (result.isEmpty()) {
                                    result = "Something went wrong";
                                }
                                String finalResult = result;
                                activity.runOnUiThread(() -> Toast.makeText(activity, finalResult, Toast.LENGTH_SHORT).show());

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Payment Failed", Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Payment Failed", Toast.LENGTH_SHORT).show());
                    }
                }
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            activity.runOnUiThread(() -> Toast.makeText(activity, "Sorry! Api Error", Toast.LENGTH_SHORT).show());
        }
    }
}