package com.typingwork.jobathome.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.typingwork.jobathome.Model.UserDataModel;
import com.typingwork.jobathome.R;
import com.typingwork.jobathome.Utils.Constant;
import com.typingwork.jobathome.Utils.NetworkChangeReceiver;
import com.typingwork.jobathome.databinding.ActivityWithdrawBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WithdrawActivity extends AppCompatActivity {

    private ActivityWithdrawBinding binding;
    private UserDataModel model = MainActivity.userDataModel;
    private ProgressDialog progressDialog;
    private DocumentReference userRef, withdrawRef;
    private CollectionReference upiColRef;
    private RadioButton button1,button2,button3;
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWithdrawBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        setSupportActionBar(binding.toolbarrWithdraw);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        upiColRef = FirebaseFirestore.getInstance().collection("UPI_list");
        userRef = FirebaseFirestore.getInstance().collection("Users_list").document(uid);
        userRef.addSnapshotListener((value, error) -> {
            binding.textView27.setText("Wallet Balance :- ₹" + value.getString("wallet") + "/-");
        });
        withdrawRef = FirebaseFirestore.getInstance().collection("Users_list").document(uid).collection("withdraw").document(uid);
        progressDialog = new ProgressDialog(this);
        Constant.showProgressDialog(progressDialog, "Please Wait", "Fetching Data...");
        progressDialog.show();

        withdrawRef.addSnapshotListener((value, error) -> {
            if (value.exists()) {

                if (value.getString("status").contains("Pending")) {
                    binding.textView26.setText(value.getString("status"));
                    binding.textView26.setTextColor(getResources().getColor(R.color.red));
                } else if (value.getString("status").contains("Success")) {
                    binding.textView26.setTextColor(getResources().getColor(R.color.green));
                    binding.textView26.setText(value.getString("status"));
                }
                binding.textView15.setText(value.getString("time"));

                binding.textView19.setText("UPI id :- " + value.getString("upi"));
                binding.textView20.setText("₹ 12.0/-");
                binding.textView21.setText("₹  2.0/-");
                binding.textView22.setText("₹ 10.0/-");

                binding.cardViewTransaction.setVisibility(View.VISIBLE);
                binding.tvNoTransaction.setVisibility(View.GONE);
                binding.tvNotice1.setVisibility(View.VISIBLE);
                binding.tvNotice2.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
            } else {
                binding.cardViewTransaction.setVisibility(View.GONE);
                binding.tvNoTransaction.setVisibility(View.VISIBLE);
                binding.tvNotice1.setVisibility(View.GONE);
                binding.tvNotice2.setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
        binding.btn2submit.setOnClickListener(view -> {
            String upi = binding.appCompatEditText.getText().toString().trim();
            if (upi.isEmpty()) {
                Toast.makeText(this, "Enter UPI Id", Toast.LENGTH_SHORT).show();
            } else {

                if (model.getActivation().equals("No") && model.getPlan().equals("Free Plan")) {
                    if (Float.parseFloat(model.getWallet()) < 12.0) {
                        Toast.makeText(this, "Minimum Rs 12 Required Amount in your Wallet!", Toast.LENGTH_SHORT).show();
                    } else {
                        //hide keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(WithdrawActivity.this, R.style.MyDialogTheme);
                        alertDialog.setPositiveButton("Withdraw Now", (dialog, which) -> {
                            dialog.dismiss();
                            checkMultipleAccount(upi);
                        }).setNegativeButton("Go Back", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        });
                        Constant.showAlertdialog(WithdrawActivity.this, alertDialog, "Confirm!", "Are you sure, entered  UPI id is correct? Because the wrong UPI id will cause of failed payment.");
                    }
                }
                else  if (Float.parseFloat(model.getWallet()) >= Float.parseFloat(MainActivity.Second_Withdraw_Limit)) {
                    if (Float.parseFloat(model.getWallet())>=10000){
                        Toast.makeText(this, "typingworkhelp@gmail.com", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(this, "Technical Error! Contact Us.", Toast.LENGTH_SHORT).show();
                    }

                }else if ( model.getPlan().equals("Yes") ) {
                    Toast.makeText(this, "Minimum Balance Rs." + MainActivity.Third_Withdraw_Limit + " Required!", Toast.LENGTH_SHORT).show();
                }else if (model.getActivation().equals("Yes") ) {
                    Toast.makeText(this, "Minimum Balance Rs." + MainActivity.Second_Withdraw_Limit + " Required!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        button1=findViewById(R.id.radio_first);
        button2=findViewById(R.id.radio_second);
        button3=findViewById(R.id.radio_third);

        button1.setOnClickListener(view -> {
            binding.appCompatEditText.setHint("Enter Google Pay No.");
        });
        button2.setOnClickListener(view -> {
            binding.appCompatEditText.setHint("Enter Paytm No.");
        });
        button3.setOnClickListener(view -> {
            binding.appCompatEditText.setHint("Enter UPI ID.");
        });

    }

    private void checkMultipleAccount(String upi) {
        progressDialog.show();
        upiColRef.whereEqualTo("upi", upi)
                .get().addOnCompleteListener(task -> {
                    if (task.getResult().isEmpty()) {
                        sendDataToServer(upi);
                    } else {
                        Map<String, Object> map5 = new HashMap<>();
                        map5.put("block", "Block");
                        map5.put("reason", "We blocked your account due to a policy violation of multiple accounts.");
                        userRef.update(map5).addOnSuccessListener(unused -> {
                            Toast.makeText(this, "You are Blocked by Admin", Toast.LENGTH_SHORT).show();
                           progressDialog.dismiss();
                            finish();
                        }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                });


    }

    private void sendDataToServer(String upi) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        final String date = dateFormat.format(new Date());
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MMM-yyyy hh:mm aa");
        final String date2 = dateFormat2.format(new Date());
        float remain = Float.parseFloat(model.getWallet()) - 12;

        double bale = Double.parseDouble(String.valueOf(remain));
        bale = Math.round(bale * 100.00) / 100.00;

        Map<String, Object> map4 = new HashMap<>();
        map4.put("upi", upi);


        Map<String, Object> map2 = new HashMap<>();
        map2.put("upi", upi);
        map2.put("status", "Pending");
        map2.put("time", date2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("wallet", String.valueOf(bale));
        map3.put("withdraw", "Pending");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Withdraw").child(date).child(uid);
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", uid);
        map1.put("upi", upi);
        map1.put("status", "Pay");
        map1.put("amount", "10");
        map1.put("name", model.getName());
        map1.put("email", model.getEmail());
        map1.put("mobile", model.getPhone());

        withdrawRef.set(map2).addOnSuccessListener(unused -> {
            userRef.update(map3).addOnSuccessListener(unused1 -> {
                databaseReference
                        .setValue(map1)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                upiColRef.document().set(map4);
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(WithdrawActivity.this, R.style.MyDialogTheme);
                                alertDialog.setPositiveButton("Ok", (dialog, which) -> {
                                    binding.appCompatEditText.setText("");
                                    dialog.dismiss();
                                });
                                Constant.showAlertdialog(WithdrawActivity.this, alertDialog, "Thanks Dear!", "We will Deposit the Amount in your Bank Account within 24 Hours of the Request.");
                                progressDialog.dismiss();
                                Toast.makeText(WithdrawActivity.this, "Request sent successfully.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(e ->
                        {
                            progressDialog.dismiss();
                            Toast.makeText(WithdrawActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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