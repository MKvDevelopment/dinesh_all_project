package com.typingwork.jobathome.Activity;

import static com.typingwork.jobathome.Activity.MainActivity.paytm_mid;
import static com.typingwork.jobathome.Activity.MainActivity.paytm_url;
import static com.typingwork.jobathome.Activity.MainActivity.razorpay_server_link;
import static com.typingwork.jobathome.Activity.MainActivity.razorpay_upi;
import static com.typingwork.jobathome.Activity.MainActivity.userDataModel;
import static com.typingwork.jobathome.Utils.Constant.isConnectionAvailable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;
import com.typingwork.jobathome.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class SpinActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private MediaPlayer spinSound;
    private String amount;
    private int plan;
    private MediaPlayer coinSound;
    private Button spinButton;
    private LuckyWheelView wheelView;
    private String depositbal, remainSpin, previousIndex, uid,activation;
    private DocumentReference userReference;
    private ProgressDialog progressDialog;
    private AlertDialog custome_document_dialog;
    private final int txnRequestCode = 110;
    private ActivityResultLauncher<Intent> upiPaymentResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Spin to Earn");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Fetching Data...");

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        userReference = FirebaseFirestore.getInstance().collection("Users_list").document(uid);

        getDataFromFirebase();

        ((Button) findViewById(R.id.btn_byMoreSpin)).setOnClickListener(v -> {
            showPopUp();
        });

        wheelView = findViewById(R.id.wheel);
        spinSound = MediaPlayer.create(this, R.raw.spinsoundeffect);
        coinSound = MediaPlayer.create(this, R.raw.coin);

        int[] items = {70,85,55,105,45,130,30,150,65,25,180,200,90,50,300,40};
        int[] colors = {0xffff6698, 0xffffb366, 0xffffff66, 0xff98ff66, 0xff6698ff, 0xffff00ff};
        int[] targetIndex = {0,1,8,5,9,15,8,4,9,13};


        List<LuckyItem> data = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            LuckyItem luckyItem = new LuckyItem();
            luckyItem.topText = "" + items[i];
            luckyItem.secondaryText = "₹";
            luckyItem.color = colors[i % 6];
            data.add(luckyItem);
        }
        wheelView.setData(data);
        // wheelView.setRound(10);
        wheelView.setTouchEnabled(false);

        wheelView.setLuckyRoundItemSelectedListener(index -> {
            spinButton.setEnabled(true);
            spinSound.stop();

            progressDialog.setMessage("Updating Balance...");
            progressDialog.show();

            new Handler().postDelayed(() -> {
                progressDialog.dismiss();
                showWinningCustomDialog(items[index]);

            }, 3000);

        });
        spinButton = findViewById(R.id.spin_button);
        spinButton.setOnClickListener(v -> {


            if (Integer.parseInt(remainSpin) <= 0) {
                Toast.makeText(getApplicationContext(), "Sorry! No Spin Available!", Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(previousIndex) >= 10) {
                Toast.makeText(getApplicationContext(), "Sorry! No Spin Available!", Toast.LENGTH_SHORT).show();
            } else if (activation.equals("No")&&Double.parseDouble(depositbal) >= 2800.0){
                Toast.makeText(this, "To Spin More Upgrade In Premium", Toast.LENGTH_LONG).show();
            }else if (activation.equals("Yes")&&Double.parseDouble(depositbal) >= 3600.0){
                Toast.makeText(this, "For Now, You cann't Spin!, Try Again after some Day's.", Toast.LENGTH_SHORT).show();
            }else {
                int indexx = Integer.parseInt(previousIndex);
                wheelView.startLuckyWheelWithTargetIndex(targetIndex[indexx]);
                spinButton.setEnabled(false);
                spinSound = MediaPlayer.create(SpinActivity.this, R.raw.spinsoundeffect);
                spinSound.start();
            }
        });

        upiPaymentResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent dataa = result.getData();
                        if (dataa != null) {
                            String txt = dataa.getStringExtra("response");
                            ArrayList<String> datalist = new ArrayList<>();
                            datalist.add(txt);
                            upiAdsPaymentDataOperation(datalist);
                        } else {
                            ArrayList<String> list = new ArrayList<>();
                            list.add("nothing");
                            upiAdsPaymentDataOperation(list);
                        }
                    } else {
                        ArrayList<String> list = new ArrayList<>();
                        list.add("nothing");
                        upiAdsPaymentDataOperation(list);
                    }
                });

    }

    private void upiAdsPaymentDataOperation(ArrayList<String> datalist) {
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
                Toast.makeText(getApplicationContext(), "Spin Purchase Successfully", Toast.LENGTH_SHORT).show();
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    updateData(plan);
                }, 2000);
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

    private void showWinningCustomDialog(int item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.winning_layout, null, false);

        Button submit = view.findViewById(R.id.btn_cancl);
        Button cancel = view.findViewById(R.id.btn_submit);

        ((TextView) view.findViewById(R.id.con_tv)).setText("Congrats!! \uD83E\uDD73 You win = ₹" + item);

        builder.setView(view);
        custome_document_dialog = builder.create();
        custome_document_dialog.setCancelable(false);
        custome_document_dialog.setCanceledOnTouchOutside(false);
        custome_document_dialog.show();

        submit.setOnClickListener(view1 -> {
            custome_document_dialog.dismiss();
            updateUserBal(item);
        });
        cancel.setOnClickListener(view1 ->
        {
            custome_document_dialog.dismiss();
            updateUserBal(item);
        });


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

    private void showPopUp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.offer_plan_layout, null, false);

        Button submit = view.findViewById(R.id.button3);
        Button cancel = view.findViewById(R.id.button2);

        //  ((TextView) view.findViewById(R.id.editText2)).setText("! Buy Spin !");
        ((RadioButton) view.findViewById(R.id.radio_first)).setText("₹ 100 = 3 Spin");
        ((RadioButton) view.findViewById(R.id.radio_second)).setText("₹ 275 = 10 Spin");

        builder.setView(view);
        custome_document_dialog = builder.create();
        custome_document_dialog.setCanceledOnTouchOutside(false);
        custome_document_dialog.show();

        submit.setOnClickListener(view1 -> {
            custome_document_dialog.dismiss();
            //check button position
            RadioGroup radioGroup = view.findViewById(R.id.radio_group);
            int id = radioGroup.getCheckedRadioButtonId();
            View dd = radioGroup.findViewById(id);
            int position = radioGroup.indexOfChild(dd);

            switch (position) {
                case 0:
                    choosePaymentPopUpLayout("100",0);
                    break;
                case 1:
                    choosePaymentPopUpLayout("275",1);
                    break;

            }

        });
        cancel.setOnClickListener(view1 -> custome_document_dialog.dismiss());

    }
    private void choosePaymentPopUpLayout(String amount,int plan) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.update_plan_layout, null, false);

        Button submit = view.findViewById(R.id.button3);
        Button cancel = view.findViewById(R.id.button2);
        if (razorpay_upi.equals("")){
            view.findViewById(R.id.radio_second).setVisibility(View.GONE);
            ((RadioButton)view.findViewById(R.id.radio_first)).setText("Pay with Paytm Wallet/UPI/Card");
        }
        builder.setView(view);
        custome_document_dialog = builder.create();
        custome_document_dialog.setCanceledOnTouchOutside(false);
        custome_document_dialog.show();

        submit.setOnClickListener(view1 -> {
            custome_document_dialog.dismiss();
            //check button position
            RadioGroup radioGroup = view.findViewById(R.id.radio_group);
            int id = radioGroup.getCheckedRadioButtonId();
            View dd = radioGroup.findViewById(id);
            int position = radioGroup.indexOfChild(dd);

            switch (position) {
                case 0:
                    // other gateway
                    goForPayment(amount,plan);

                    break;
                case 1:
                    this.amount = amount;
                    this.plan = plan;
                   // goForUPI(amount);
                    startPayment(amount);
                    break;
            }

        });
        cancel.setOnClickListener(view1 -> custome_document_dialog.dismiss());

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
            upiPaymentResultLauncher.launch(chooser);
        } else {
            Toast.makeText(getApplicationContext(), "This Is Not Working", Toast.LENGTH_SHORT).show();
        }
    }

    private void goForPayment(String price,int plan) {
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

        RequestQueue queue = Volley.newRequestQueue(SpinActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, price);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, "jjjv"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
        {
            progressDialog.dismiss();
            Toast.makeText(SpinActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
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

                    updateData(plan);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(SpinActivity.this, "Payment Cancelled by you !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, "You Cancelled this Transaction", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        int txnRequestCode = 110;
        transactionManager.startTransaction(SpinActivity.this, txnRequestCode);
    }

    private void updateData(int plan) {

        progressDialog.setMessage("Updating Balance");
        progressDialog.show();
        new Handler(Looper.myLooper()).postDelayed(() -> {
            Toast.makeText(this, "Spin Purchased Successfully", Toast.LENGTH_SHORT).show();

            if (plan == 0) {
                userReference.update("field1",String.valueOf(3));
            } else if (plan == 1) {
                userReference.update("field1",String.valueOf(10));
            }
        }, 2000);
    }


    private void getDataFromFirebase() {
        progressDialog.show();
        userReference.addSnapshotListener((value, error) -> {
            depositbal = value.getString("wallet");
            remainSpin = value.getString("field1");
            previousIndex = value.getString("field2");
            activation = value.getString("activation");
            ((TextView) findViewById(R.id.avl_bal)).setText("Wallet Balance = ₹ " + depositbal);
            ((TextView) findViewById(R.id.win_bal)).setText("Remain Spin = " + remainSpin);
            progressDialog.dismiss();
        });

    }

    private void updateUserBal(Object selectedItem) {

        int indexx = Integer.parseInt(previousIndex);

        int i = ++indexx;

        double remain = Double.parseDouble(depositbal) + Long.parseLong(String.valueOf(selectedItem));

        int remainn = Integer.parseInt(remainSpin) - 1;

        Map map = new HashMap();
        map.put("field1", String.valueOf(remainn));
        map.put("field2", String.valueOf(i));
        map.put("wallet", String.valueOf(remain));

        userReference.update(map);


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
        Toast.makeText(SpinActivity.this, "Payment Failed!", Toast.LENGTH_SHORT).show();

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
                                    updateData(plan);
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