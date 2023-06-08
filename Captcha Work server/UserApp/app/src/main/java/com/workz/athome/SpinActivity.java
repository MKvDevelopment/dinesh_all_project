package com.workz.athome;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.workz.athome.Model.UserData.Root;
import com.workz.athome.Utils.ApiClient;
import com.workz.athome.Utils.ApiService;
import com.workz.athome.Utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class SpinActivity extends AppCompatActivity {

    private MediaPlayer spinSound;
    private Button spinButton;
    private LuckyWheelView wheelView;
    private String remainSpin,index,token;
    private ProgressDialog progressDialog;
    private AlertDialog custome_document_dialog;
    private SharedPreferences.Editor editor;
    private ApiService apiServices;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Spin to Earn");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Fetching Data...");

        apiServices = ApiClient.getRetrofit().create(ApiService.class);
        preferences = getSharedPreferences(Utils.sharedPrefrenceName, MODE_PRIVATE);
        editor = preferences.edit();

        token = preferences.getString("token", null);

        getDataFromSharedPreference();

        ((Button) findViewById(R.id.btn_byMoreSpin)).setOnClickListener(v -> showPopUp());

        wheelView = findViewById(R.id.wheel);
        spinSound = MediaPlayer.create(this, R.raw.spinsoundeffect);

        int[] items = {50, 30, 80, 110, 130, 70, 150, 10, 120, 60, 90, 170, 115, 35, 85, 45};
        int[] colors = {0xffff6698, 0xffffb366, 0xffffff66, 0xff98ff66, 0xff6698ff, 0xffff00ff};
        int[] targetIndex = {4, 2, 15, 10, 13, 5, 0, 8, 7, 13, 5, 7, 1, 13, 15, 7, 7, 1, 7, 7, 13, 1, 0, 7, 7, 5};


        List<LuckyItem> data = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            LuckyItem luckyItem = new LuckyItem();
            luckyItem.topText = "" + items[i];
            luckyItem.secondaryText = "\uD83D\uDCB0";
            luckyItem.color = colors[i % 6];
            data.add(luckyItem);
        }
        wheelView.setData(data);
        wheelView.setRound(10);
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

            index = preferences.getString("index", null);
            remainSpin = preferences.getString("winning_balence", null);

            if (Integer.parseInt(remainSpin) <= 0) {
                Toast.makeText(getApplicationContext(), "Sorry! No Spin Available!", Toast.LENGTH_SHORT).show();
            } else if (Integer.parseInt(index) >= 26) {
                Toast.makeText(getApplicationContext(), "Sorry! No Spin Available!", Toast.LENGTH_SHORT).show();
            } else {

                int indexx = Integer.parseInt(index);

                wheelView.startLuckyWheelWithTargetIndex(targetIndex[indexx]);

                spinButton.setEnabled(false);
                spinSound = MediaPlayer.create(SpinActivity.this, R.raw.spinsoundeffect);
                spinSound.start();
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void showWinningCustomDialog(int item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.winning_layout, null, false);

        Button submit = view.findViewById(R.id.btn_cancl);
        Button cancel = view.findViewById(R.id.btn_submit);

        ((TextView) view.findViewById(R.id.con_tv)).setText("Congrats!! \uD83E\uDD73 You win = ₹"+item);

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

    @SuppressLint("SetTextI18n")
    private void showPopUp() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.update_plan_layout, null, false);

        Button submit = view.findViewById(R.id.button3);
        Button cancel = view.findViewById(R.id.button2);

        ((TextView) view.findViewById(R.id.editText2)).setText("! Buy Spin !");
        ((RadioButton) view.findViewById(R.id.radio_first)).setText("₹ 20 = 1 Spin");
        ((RadioButton) view.findViewById(R.id.radio_second)).setText("₹ 50 = 3 Spin");
        ((RadioButton) view.findViewById(R.id.radio_third)).setText("₹ 100 = 7 Spin");

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
                   goForPayment("20", 0);
                   // updateData( 0);
                    break;
                case 1:
                    goForPayment("50", 1);
                    //updateData( 1);
                    break;
                case 2:
                    goForPayment("100", 2);
                   // updateData( 2);
                    break;
            }

        });
        cancel.setOnClickListener(view1 -> custome_document_dialog.dismiss());
    }

    private void goForPayment(String price, int plan) {
        progressDialog.setMessage("Fetching Info...");
        progressDialog.show();
        String order_id = UUID.randomUUID().toString(); // It should be unique
        // int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(MainActivity.paytm_callback_url)
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
                processTxn(order_id, txnToken, price, plan);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
        {
            progressDialog.dismiss();
            Toast.makeText(SpinActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }
        );
        queue.add(request);
    }
    private void processTxn(String orderid, String txnToken, String amount, int plan) {
       // String mid = "Dinesh18399201056005";

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, MainActivity.paytm_mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                assert bundle != null;
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {
                    Toast.makeText(SpinActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                    updateData( plan);
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
    private void updateData( int plan) {

        progressDialog.setMessage("Updating Balance");
        progressDialog.show();
        new Handler(Looper.myLooper()).postDelayed(() -> {

            if (plan == 0) {
                updateUserPlan( 1);
            } else if (plan == 1) {
                updateUserPlan( 3);
            } else if (plan == 2) {
                updateUserPlan( 7);
            }
        }, 2000);
    }

    private void updateUserPlan( int totalSpin) {

        Call<Root> call=apiServices.purchaseSpin("Bearer " +token,String.valueOf(totalSpin));
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                progressDialog.dismiss();
                if (response.code()==200){
                    editor.putString("winning_balence",String.valueOf(totalSpin));
                    editor.commit();
                    Toast.makeText(SpinActivity.this, "Update Successfully", Toast.LENGTH_SHORT).show();
                    getDataFromSharedPreference();
                }else {
                    Toast.makeText(SpinActivity.this, "Response Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, "Api Error!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @SuppressLint("SetTextI18n")
    private void getDataFromSharedPreference() {
      String  mainWallet = preferences.getString("wallet", null);
      String  remainSpin = preferences.getString("winning_balence", null);
      String  index = preferences.getString("index", null);

        ((TextView) findViewById(R.id.avl_bal)).setText("Wallet Balance = ₹ " + mainWallet);
        ((TextView) findViewById(R.id.win_bal)).setText("Remaining Spin = " + remainSpin);

        if (Integer.parseInt(index) >= 26) {
            ((TextView) findViewById(R.id.t1)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.t2)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.t3)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.t4)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.t1)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.t2)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.t3)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.t4)).setVisibility(View.GONE);
        }


    }

    private void updateUserBal(int selectedItem) {

        String  mainWallet = preferences.getString("wallet", null);
        String  remainSpin = preferences.getString("winning_balence", null);
        String  index = preferences.getString("index", null);

        progressDialog.show();

        float newWallet = Float.parseFloat(mainWallet) + Float.parseFloat(String.valueOf(selectedItem));
        int remainn = Integer.parseInt(remainSpin) - 1;
        int indexx = Integer.parseInt(index) + 1;

        Call<Root> call = apiServices.updatespin("Bearer " +token,String.valueOf(selectedItem));
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                progressDialog.dismiss();
                if (response.code()==200){

                    editor.putString("winning_balence",String.valueOf(remainn));
                    editor.putString("wallet",String.valueOf(newWallet));
                    editor.putString("index",String.valueOf(indexx));
                    editor.commit();
                     getDataFromSharedPreference();

                }else {
                    Toast.makeText(SpinActivity.this, "Response Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(SpinActivity.this, "Api Error!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}