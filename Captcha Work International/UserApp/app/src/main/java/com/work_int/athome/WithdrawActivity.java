package com.work_int.athome;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class WithdrawActivity extends AppCompatActivity {
    private TextInputEditText editText;
    private TextInputLayout ed_layout;
    private ProgressDialog progressDialog;
    private DocumentReference adminRef, userRef;
    private String wallet;
    private String activation;
    private String activation_fee;
    private String withdraw_limit;
    private String uid;

    @SuppressLint("SetTextI18n")
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
                ed_layout.setHint("Enter your PayPal ID.");
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
            } else {
                ed_layout.setHint("Enter your JazzCash No.");
                editText.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(uid);

        adminRef = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");


        userRef.addSnapshotListener((value, error) -> {
            assert value != null;
            activation = value.getString("activation");
            wallet = value.getString("wallet");

            ((TextView) findViewById(R.id.textView7)).setText("Wallet Balance :- $" + wallet + "/-");
            if (activation.contains("Yes")) {
                showActivationDialog();

            } else {
                progressDialog.dismiss();
                btn_submit.setVisibility(View.VISIBLE);
                findViewById(R.id.textView24).setVisibility(View.GONE);
            }


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
                                    .setMessage("Your Account is not Activated Yet. Activation fees is $" + activation_fee + ". Are you want to Activate it?. You can withdraw after account activation.")
                                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                                        dialogInterface.dismiss();
                                        // goForPayment(activation_fee);
                                        /*if (amount.equals("90")){
                                            Map map=new HashMap();
                                            map.put("activation","Yes");
                                            instantActivation.set(map).addOnSuccessListener(unused -> {
                                                Toast.makeText(WithdrawActivity.this, "Request Sent Successfully.", Toast.LENGTH_SHORT).show();
                                            });
                                        }else {
                                            userRef.update("activation", "Yes").addOnSuccessListener(unused -> {
                                                showActivationDialog();
                                            });
                                        }*/
                                        Toast.makeText(this, "pending", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(this, "Required Minimum balance  $" + withdraw_limit + "/-", Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.btn_bank_transfer).setOnClickListener(view -> {
            if (Float.parseFloat(wallet) >= Float.parseFloat(withdraw_limit)) {
                progressDialog.setMessage("Checking Account Activation!");
                progressDialog.show();
                new Handler().postDelayed(() -> {
                    if (activation.contains("No")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setCancelable(false)
                                .setTitle("Activate Account!")
                                .setMessage("Your Account is not Activated Yet. Activation fees is $" + activation_fee + ". Are you want to Activate it?. You can withdraw after account activation.")
                                .setPositiveButton("Yes", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                    // goForPayment(activation_fee);
                                        /*if (amount.equals("90")){
                                            Map map=new HashMap();
                                            map.put("activation","Yes");
                                            instantActivation.set(map).addOnSuccessListener(unused -> {
                                                Toast.makeText(WithdrawActivity.this, "Request Sent Successfully.", Toast.LENGTH_SHORT).show();
                                            });
                                        }else {
                                            userRef.update("activation", "Yes").addOnSuccessListener(unused -> {
                                                showActivationDialog();
                                            });
                                        }*/
                                    Toast.makeText(this, "pending", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "Required Minimum balance  $" + withdraw_limit + "/-", Toast.LENGTH_SHORT).show();
            }
        });

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


}