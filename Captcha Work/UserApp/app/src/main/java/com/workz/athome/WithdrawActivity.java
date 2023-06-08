package com.workz.athome;

import static com.workz.athome.MainActivity.generateRandomEmail;
import static com.workz.athome.MainActivity.randomMobileNoGenerate;
import static com.workz.athome.MainActivity.razorpay_key;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class WithdrawActivity extends AppCompatActivity implements PaymentResultListener {
    private TextInputEditText editText;
    private TextInputLayout ed_layout;
    private ProgressDialog progressDialog;
    private DocumentReference adminRef, userRef, instantActivation;
    private String wallet;
    private String activation;
    private String activation_fee;
    private String withdraw_limit;
    private String watch_video;
    private String uid;
    private final int txnRequestCode = 110;
    private ActivityResultLauncher<Intent> promotionResultLauncher;
    String tag="aaaaaaaaaaaaaaaa  :- ";

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        withdraw_limit = getIntent().getStringExtra("limit");

        RadioGroup radioGroup = findViewById(R.id.radio_group1);
        editText = findViewById(R.id.eed_edittext);
        ed_layout = findViewById(R.id.ed_editext);
        Button btn_submit = findViewById(R.id.btn_submit_request);

        Toolbar toolbar = findViewById(R.id.withdraw_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        activation_fee = getIntent().getStringExtra("fee");
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        radioGroup.setOnCheckedChangeListener((group, id) -> {
            int radioButtonID = group.getCheckedRadioButtonId();
            View radioButton = group.findViewById(radioButtonID);
            int position = group.indexOfChild(radioButton);

            if (position == 0) {
                ed_layout.setHint("Enter your Paytm No.");
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
            } else {
                ed_layout.setHint("Enter your UPI Id.");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(uid);
        instantActivation = FirebaseFirestore.getInstance().collection("Instant Activation").document(uid);

        adminRef = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");
        //event
        promotionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    // updateUserPromationWallet();
                });

        adminRef.get().addOnSuccessListener(documentSnapshot -> {
            watch_video = documentSnapshot.getString("groww_video");
            Log.d(tag, "Spin Activity watch video :- "+watch_video);
        });


        userRef.addSnapshotListener((value, error) -> {
            assert value != null;
            activation = value.getString("activation");
            wallet = value.getString("wallet");

            Log.d(tag, "Spin Activity activation :- "+activation);
            Log.d(tag, "Spin Activity wallet :- "+wallet);

            ((TextView) findViewById(R.id.textView7)).setText("Wallet Balance :- \u20B9" + wallet + "/-");
            if (activation.contains("YES")) {
                instantActivation.addSnapshotListener((value1, error1) -> {
                    Log.d(tag, "Spin Activity instant activation :- ----------------");
                    if (value1.exists()) {
                        progressDialog.dismiss();
                        //show grow dialog
                        findViewById(R.id.textView24).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.textView24)).setText("Final Step ");
                        findViewById(R.id.grow_card).setVisibility(View.VISIBLE);
                        findViewById(R.id.callUs_btn).setVisibility(View.VISIBLE);
                        findViewById(R.id.vdeo_btn).setVisibility(View.VISIBLE);
                        findViewById(R.id.imageView3).setVisibility(View.GONE);

                        findViewById(R.id.btn_instActivation).setVisibility(View.GONE);
                    } else {
                        progressDialog.dismiss();
                        //hide grow dialog
                        findViewById(R.id.callUs_btn).setVisibility(View.GONE);
                        findViewById(R.id.grow_card).setVisibility(View.GONE);
                        findViewById(R.id.vdeo_btn).setVisibility(View.GONE);
                        findViewById(R.id.imageView3).setVisibility(View.VISIBLE);
                        findViewById(R.id.textView24).setVisibility(View.VISIBLE);
                        findViewById(R.id.btn_instActivation).setVisibility(View.VISIBLE);
                    }
                });

            } else {
                progressDialog.dismiss();
                btn_submit.setVisibility(View.VISIBLE);
                findViewById(R.id.textView24).setVisibility(View.GONE);
                findViewById(R.id.btn_instActivation).setVisibility(View.GONE);
            }


        });


        findViewById(R.id.btn_instActivation).setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle("Instant Activate!")
                    .setMessage("If you want to activate your account instant on High Priority, then you have to pay Extra Charges \u20B9 90. Are you want to Activate it?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                        if (razorpay_key.isEmpty()){
                            //pay with Paytm
                            goForPayment("90");
                        }else {
                            //pay with Razorpay
                            startPayment("90");
                        }
                    }).setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.dismiss();
                    });

            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));

        });
        findViewById(R.id.btn_growLink).setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://app.groww.in/v3cO/6k33k382"));
            promotionResultLauncher.launch(intent);
        });
        findViewById(R.id.vdeo_btn).setOnClickListener(view -> {
            Intent intent = new Intent(WithdrawActivity.this, VideoActivity.class);
            intent.putExtra("link", watch_video);
            startActivity(intent);
        });
        findViewById(R.id.callUs_btn).setOnClickListener(v -> {
            AlertDialog custome_dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            View view = LayoutInflater.from(this).inflate(R.layout.option_icon_layout, null, false);

            EditText name = view.findViewById(R.id.textView16);
            EditText desc = view.findViewById(R.id.textView18);
            EditText mobile = view.findViewById(R.id.textView17);
            Button submit = view.findViewById(R.id.btn1_submit);
            Button cancel = view.findViewById(R.id.btn1_cancel);

            builder.setView(view);
            custome_dialog = builder.create();
            custome_dialog.setCancelable(false);
            custome_dialog.setCanceledOnTouchOutside(false);
            custome_dialog.show();

            submit.setOnClickListener(v1 -> {
                if (name.getText().toString().isEmpty() || desc.getText().toString().isEmpty() || mobile.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "All Field's Compulsory!", Toast.LENGTH_SHORT).show();
                } else {
                    custome_dialog.dismiss();
                    progressDialog.setTitle("Please Wait!");
                    progressDialog.setMessage("Checking Data...");
                    progressDialog.show();
                    new Handler().postDelayed(() -> {
                        progressDialog.dismiss();
                        showMinPaymentDialog();
                    }, 3000);

                }
            });
            cancel.setOnClickListener(v1 -> {
                custome_dialog.dismiss();
            });

        });

        btn_submit.setOnClickListener(view -> {

            if (Objects.requireNonNull(editText.getText()).toString().isEmpty()) {
                ed_layout.setError("Field Cann't be Empty");
            } else {

                if (Float.parseFloat(wallet) >= Float.parseFloat(withdraw_limit)) {
                    progressDialog.setMessage("Checking Account Activation!");
                    progressDialog.show();
                    new Handler().postDelayed(() -> {
                        if (activation.contains("NO")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setCancelable(false)
                                    .setTitle("Activate Account!")
                                    .setMessage("Your Account is not Activated Yet. Activation fees is \u20B9" + activation_fee + ". Are you want to Activate it?. You can withdraw after account activation.")
                                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        goForPayment(activation_fee);
                                    }).setNegativeButton("No", (dialogInterface, i) -> {
                                        progressDialog.dismiss();
                                        dialogInterface.dismiss();
                                    });

                            AlertDialog alert = builder.create();
                            alert.show();
                            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                            nbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));
                            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
                            pbutton.setTextColor(ContextCompat.getColor(this, R.color.text_color_black));
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Sorry! Activation Pending. It can take several days.", Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);
                } else {
                    Toast.makeText(this, "Required Minimum balance  \u20B9" + withdraw_limit + "/-", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void startPayment(String v) {

        float price = Float.parseFloat(v) * 100;
        final Checkout co = new Checkout();
        co.setKeyID(razorpay_key);
        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", "Total payable amount");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("send_sms_hash",true);
            options.put("allow_rotation", true);
            options.put("amount", price);

            JSONObject preFill = new JSONObject();
            // preFill.put("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            preFill.put("email", generateRandomEmail()+"@gmail.com");
            preFill.put("contact","98"+randomMobileNoGenerate() );
            options.put("prefill", preFill);
            co.open(this, options);
        } catch (Exception e) {
            Toast.makeText(this, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void showMinPaymentDialog() {
        AlertDialog custome_dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        View view = LayoutInflater.from(this).inflate(R.layout.option_radio_minut_dialog, null, false);

        RadioButton radio1 = view.findViewById(R.id.radio1);
        RadioButton radio2 = view.findViewById(R.id.radio2);
        RadioButton radio3 = view.findViewById(R.id.radio3);
        RadioButton radio4 = view.findViewById(R.id.radio4);
        Button submit = view.findViewById(R.id.btn1_submitt);
        Button cancel = view.findViewById(R.id.btn1_cancell);
        RadioGroup radioGroup = view.findViewById(R.id.radio_group3);

        builder.setView(view);
        custome_dialog = builder.create();
        custome_dialog.setCancelable(false);
        custome_dialog.setCanceledOnTouchOutside(false);
        custome_dialog.show();

        AtomicInteger amount= new AtomicInteger();
        amount.set(55);
        radioGroup.setOnCheckedChangeListener((group, id) -> {

            int radioButtonID = group.getCheckedRadioButtonId();
            View radioButton = group.findViewById(radioButtonID);
            int position = group.indexOfChild(radioButton);

           switch (position){
               case 0:
                   amount.set(55);
                   break;
               case 1:
                   amount.set(85);
                   break;
               case 2:
                   amount.set(130);
                   break;
               case 3:
                   amount.set(210);
                   break;
           }
        });

        submit.setOnClickListener(v1 -> {
            custome_dialog.dismiss();
          goForPayment(String.valueOf(amount));
        });
        cancel.setOnClickListener(v1 -> {
            custome_dialog.dismiss();
        });
    }

    private void goForPayment(String price) {
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

        RequestQueue queue = Volley.newRequestQueue(WithdrawActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, price);
            } catch (JSONException e) {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
        {
            progressDialog.dismiss();
            Toast.makeText(WithdrawActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show();
        }
        );
        queue.add(request);
    }

    private void processTxn(String orderid, String txnToken, String amount) {
        // String mid = "Dinesh18399201056005";

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, MainActivity.paytm_mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                assert bundle != null;
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {
                    Toast.makeText(WithdrawActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();

                    if (Objects.equals(amount, "55"))
                    {
                        Toast.makeText(WithdrawActivity.this, "Call request received. Our Executive Contact you soon", Toast.LENGTH_LONG).show();
                    }else if (Objects.equals(amount, "85")){
                        Toast.makeText(WithdrawActivity.this, "Call request received. Our Executive Contact you soon", Toast.LENGTH_LONG).show();
                    }else if (Objects.equals(amount, "130")){
                        Toast.makeText(WithdrawActivity.this, "Call request received. Our Executive Contact you soon", Toast.LENGTH_LONG).show();
                    }else if (Objects.equals(amount, "210")){
                        Toast.makeText(WithdrawActivity.this, "Call request received. Our Executive Contact you soon", Toast.LENGTH_LONG).show();
                    }else {
                        if (amount.equals("90")) {
                            Map<String,String> map = new HashMap<>();
                            map.put("activation", "Yes");
                            instantActivation.set(map).addOnSuccessListener(unused -> {
                                Toast.makeText(WithdrawActivity.this, "Request Sent Successfully.", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            userRef.update("activation", "YES").addOnSuccessListener(unused -> {
                                showActivationDialog();
                            });
                        }
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(WithdrawActivity.this, "Payment Cancelled by you !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, "You Cancelled this Transaction", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(WithdrawActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.startTransaction(WithdrawActivity.this, txnRequestCode);
    }

    private void showActivationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setTitle("Request Sent")
                .setMessage("Request sent to our Team. Please wait till the account is not Activated.")
                .setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.primary_dark));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPaymentSuccess(String s) {
        Map<String,String> map = new HashMap<>();
        map.put("activation", "Yes");
        instantActivation.set(map).addOnSuccessListener(unused -> {
            Toast.makeText(WithdrawActivity.this, "Request Sent Successfully.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed!", Toast.LENGTH_SHORT).show();
    }
}