package com.skincarestudio.solution.Activty;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.skincarestudio.solution.R;
import com.skincarestudio.solution.Utils.NetworkChangeReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ReferActivity extends AppCompatActivity {
    public static String friend_email, friend_wallet;
    private String refercode, user_wallet, referBy,UPI_ID;
    private AlertDialog custom_dialog;
    private Button verifyCode;
    private EditText friendReferCode, mobile;
    private TextView tvrefercod, tv_wallet, success_refer;
   private String addingAmount;
    private ProgressDialog progress;
    private DocumentReference documentReference;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //progress dialog
        progress = new ProgressDialog(this);
        progress.setMessage("Fetching Info...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();


        Toolbar toolbar = findViewById(R.id.referToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //initiate view
        tv_wallet = findViewById(R.id.wallet);
        Button withdraw = findViewById(R.id.withdraw);
        friendReferCode = findViewById(R.id.refer_edit_text);
        tvrefercod = findViewById(R.id.user_refer_code);
        verifyCode = findViewById(R.id.verify_code_btn);
        Button referNow = findViewById(R.id.referFriendsBtn);
        success_refer = findViewById(R.id.textView10);
        Button addBal = findViewById(R.id.addball);

        //fetching data
        final String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        assert uid != null;
        documentReference = FirebaseFirestore.getInstance().collection("users").document(uid);
        DocumentReference upiRef = FirebaseFirestore.getInstance().collection("App_Utils").document("upiref");

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            progress.dismiss();
            if (documentSnapshot != null) {

                user_wallet = documentSnapshot.getString("wallet");
                refercode = documentSnapshot.getString("refer_code");

                tv_wallet.setText("Wallet:-   Rs." + user_wallet + "/-");
                tvrefercod.setText("Your refer code :- " + refercode);

                referBy = documentSnapshot.getString("refered_by");
                assert referBy != null;
                if ("empty".compareTo(referBy) != 0) {
                    updateReferedUI(referBy);
                }

            } else {
                assert e != null;
                Toast.makeText(ReferActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        upiRef.addSnapshotListener((value, error) -> {
            assert value != null;
            UPI_ID=value.getString("upi_id");
        });
        //events
        verifyCode.setOnClickListener(v -> {
            String code = friendReferCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(ReferActivity.this, "Enter Valid Code", Toast.LENGTH_LONG).show();
            } else if (code.length() < 8) {
                Toast.makeText(ReferActivity.this, "Enter Valid Code", Toast.LENGTH_LONG).show();
            } else if (refercode.compareTo(code) == 0) {
                Toast.makeText(ReferActivity.this, "Enter your friend's referral code.", Toast.LENGTH_SHORT).show();
            } else {
                progress.show();
                verifingCode(code);
            }
        });

        referNow.setOnClickListener(v -> {
            String link="https://play.google.com/store/apps/details?id=com.skincarestudio.solution";
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Hey!! friends - Today i find this amazing application Skin Care Tips. Earn lot of money from this app at home by smartphone. Download app from here "+link +". Use my refer code  to get Rs.300/-. My refer code :- " + refercode + " - ");
            intent.putExtra(Intent.EXTRA_SUBJECT, "My Refer Link");
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        withdraw.setOnClickListener(view -> getInput());

        addBal.setOnClickListener(v1 -> {
            AlertDialog custome_dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.add_balance_pop_layout, null, false);

            EditText ed_amount = view.findViewById(R.id.editText1);
            Button btn_100 = view.findViewById(R.id.button2);
            Button btn_300 = view.findViewById(R.id.button3);
            Button btn_500 = view.findViewById(R.id.button4);
            Button btn_upi = view.findViewById(R.id.button5);
            Button btn_card = view.findViewById(R.id.button6);

            builder.setView(view);
            custome_dialog = builder.create();
            custome_dialog.show();


            btn_100.setOnClickListener(v -> ed_amount.setText("100"));
            btn_300.setOnClickListener(v -> ed_amount.setText("300"));
            btn_500.setOnClickListener(v -> ed_amount.setText("500"));
            btn_upi.setOnClickListener(v -> {
                String amount = ed_amount.getText().toString();
                if (amount.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Valid Amount!", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(amount) < 10) {
                    Toast.makeText(getApplicationContext(), "Please Add Minimum 50 Rs.", Toast.LENGTH_SHORT).show();
                } else {
                    payment( amount);
                    custome_dialog.dismiss();
                }
            });

            btn_card.setOnClickListener(v -> {
                String amount = ed_amount.getText().toString();
                if (amount.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Valid Amount!", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(amount) < 10) {
                    Toast.makeText(getApplicationContext(), "Please Add Minimum 50 Rs.", Toast.LENGTH_SHORT).show();
                } else {
                    startPaytm( amount);
                    custome_dialog.dismiss();
                }
            });


        });

        someActivityResultLauncher = registerForActivityResult(
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

    }


    private void startPaytm(String price) {

        progress.setMessage("Fetching Info...");
        progress.show();
        String order_id = UUID.randomUUID().toString();
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority("paytm-gateway-server.glitch.me")
                .appendQueryParameter("ORDER_ID", order_id)
                .appendQueryParameter("CUST_ID", FirebaseAuth.getInstance().getUid())
                .appendQueryParameter("TXN_AMOUNT", price);

        String myUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(ReferActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progress.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, price);
            } catch (JSONException e) {
                progress.dismiss();
                Toast.makeText(ReferActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
        {
            progress.dismiss();
            Toast.makeText(ReferActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }
        );
        queue.add(request);
    }

    private void processTxn(String orderid, String txnToken, String amount) {
        String mid = "Dinesh18399201056005";

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                assert bundle != null;
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {
                    Toast.makeText(ReferActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();

                    progress.setCanceledOnTouchOutside(false);
                    progress.setCancelable(false);
                    progress.setMessage("Updating your balance...");
                    progress.show();
                    new Handler(Looper.myLooper()).postDelayed(() -> {
                        float total=Float.parseFloat(user_wallet)+Float.parseFloat(amount);

                        documentReference.update("wallet",String.valueOf(total));


                    }, 3000);

                } else {
                    progress.dismiss();
                    Toast.makeText(ReferActivity.this, "Payment Cancelled by you!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progress.dismiss();
                Toast.makeText(ReferActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progress.dismiss();
                Toast.makeText(ReferActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progress.dismiss();
                Toast.makeText(ReferActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progress.dismiss();
                Toast.makeText(ReferActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progress.dismiss();
                Toast.makeText(ReferActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progress.dismiss();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progress.dismiss();
                Toast.makeText(ReferActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        int txnRequestCode = 110;
        transactionManager.startTransaction(this, txnRequestCode);
    }


    private void payment(String amount) {
        Uri uri = new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", UPI_ID)
                .appendQueryParameter("pn", getString(R.string.app_name))
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
            addingAmount=amount;
            someActivityResultLauncher.launch(chooser);

        } else {
            Toast.makeText(getApplicationContext(), "This Is Not Working", Toast.LENGTH_SHORT).show();
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> datalist) {
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
                progress.setMessage("Updating Balance");
                progress.show();
                new Handler(Looper.myLooper()).postDelayed(() -> {
                    float total=Float.parseFloat(user_wallet)+Float.parseFloat(addingAmount);

                    documentReference.update("wallet",String.valueOf(total));


                }, 3000);
                Toast.makeText(getApplicationContext(), "Transaction successful.", Toast.LENGTH_SHORT).show();

            } else if ("Payment cancelled.".equals(paymentCancel)) {
                Toast.makeText(getApplicationContext(), "Payment cancelled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }

    }

    private void getInput() {
        AlertDialog.Builder alert = new AlertDialog.Builder(ReferActivity.this);
        View view = getLayoutInflater().inflate(R.layout.custom_dialog_weblink, null);

        mobile = (EditText) view.findViewById(R.id.toast_payment);

        Button btn_cancel = (Button) view.findViewById(R.id.toast_cancel);
        Button btn_submit = (Button) view.findViewById(R.id.toast_submit);
        alert.setView(view);

        custom_dialog = alert.create();
        custom_dialog.setCanceledOnTouchOutside(false);
        custom_dialog.show();

        btn_cancel.setOnClickListener(v -> custom_dialog.dismiss());

        btn_submit.setOnClickListener(v -> {
            boolean bal = Float.parseFloat(user_wallet) == 0.0;
            if (mobile.getText().toString().isEmpty()) {
                Toast.makeText(ReferActivity.this, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
            } else if (mobile.getText().length() < 10) {
                Toast.makeText(ReferActivity.this, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
            } else if (bal) {
                Toast.makeText(ReferActivity.this, "You have not sufficient money in wallet!", Toast.LENGTH_SHORT).show();
            } else {
                UploadFile(mobile.getText().toString());
                custom_dialog.dismiss();
            }
        });


    }

    private void UploadFile(String mob) {

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String date = dateFormat.format(new Date());

        progress.setMessage("Uploading your request....");
        progress.show();

        sendDataToServer(mob, date);
        documentReference.update("wallet", "0.0");
        Toast.makeText(ReferActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
    }

    private void sendDataToServer(String mob, String date) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("withdraw_list").child(date);

        Map<String,Object> map1 = new HashMap<>();
        map1.put("mobile", mob);
        map1.put("time", date);
        map1.put("wallet", user_wallet);

        databaseReference
                .push()
                .setValue(map1)
                .addOnCompleteListener(task -> {
                    progress.dismiss();
                    if (task.isSuccessful()) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReferActivity.this);
                        alertDialog.setTitle("Thanks Dear!");
                        alertDialog.setMessage("We get your withdraw request. Money will be send in your Paytm Wallet within 24 hours.");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
                            dialog.dismiss();
                            finish();
                        }).show();
                        Toast.makeText(ReferActivity.this, "Request sent successfully.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> Toast.makeText(ReferActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());

        mobile.setText("");

    }

    @SuppressLint("SetTextI18n")
    private void updateReferedUI(String referBy) {
        success_refer.setVisibility(View.VISIBLE);
        success_refer.setText("You have already been refered by\n" + referBy + ". Now when you Buy any product then your refered friend get 30% commission.");
        verifyCode.setVisibility(View.GONE);
        friendReferCode.setVisibility(View.GONE);

    }

    private void verifingCode(final String code) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("users");
        reference
                .whereEqualTo("refer_code", code)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.getResult().isEmpty()) {
                        progress.dismiss();
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            referBy = snapshot.getString("refer_code");
                            friend_email = snapshot.getString("email");
                            friend_wallet = snapshot.getString("wallet");
                            setData();
                        }
                    } else {
                        progress.dismiss();
                        Toast.makeText(ReferActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    progress.dismiss();
                    Toast.makeText(ReferActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setData() {
        documentReference.update("refered_by", referBy);
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

    private boolean isConnectionAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null) {
            return netInfo.isConnected() || netInfo.isConnectedOrConnecting() || netInfo.isAvailable();
        }
        return false;

    }


}