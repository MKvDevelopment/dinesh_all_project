package com.wheel.colorgame.Activity;

import static com.wheel.colorgame.Activity.MainActivity.withdrawLimit;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wheel.colorgame.Adapter.TransactionHistoryAdapter;
import com.wheel.colorgame.Model.HistoryModel;
import com.wheel.colorgame.NetworkChangeReceiver;
import com.wheel.colorgame.R;
import com.wheel.colorgame.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaymentActivity extends AppCompatActivity {

    private TextView tv_wallet;
    private Button btn_addBal, btn_withdraw, btn_balHis, btn_withdrawHis;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private DocumentReference userRef;
    private DocumentReference totalBalRef;
    private DocumentReference adminAmountRef,friendReference;
    private CollectionReference addBalanceRef, withdrawRef;
    private String userUid;
    private String wallet;
    private String upi;
    private String amount;
    private String totalAdminAmount;
    private String totalAdminAmount2;
    private String totalPayout;
    private String totalPayout2;
    private String name, friendUid;
    private String email;
    private final ArrayList<HistoryModel> list = new ArrayList<>();
    private TransactionHistoryAdapter adapter;
    private ActivityResultLauncher<Intent> someActivityResultLauncher;
    private double totalFriendAmount;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        Utils.showProgressDialog(this, progressDialog);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Please wait");

        initView();


        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yy");
        String withdrawTime = simpleDateFormat1.format(calendar.getTime());


        userUid = FirebaseAuth.getInstance().getUid();
        adminAmountRef = FirebaseFirestore.getInstance().collection("Today_Income").document(withdrawTime);
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(userUid);
        DocumentReference upiRef = FirebaseFirestore.getInstance().collection("App_Utils").document("upi_id");
        withdrawRef = FirebaseFirestore.getInstance().collection("User_List").document(userUid).collection("Withdraw history");
        addBalanceRef = FirebaseFirestore.getInstance().collection("User_List").document(userUid).collection("Add balance History");
        totalBalRef = FirebaseFirestore.getInstance().collection("App_Utils").document("totalAmount");

        userRef.addSnapshotListener((value, error) -> {
            assert value != null;
            wallet = value.getString("wallet");
            friendUid = value.getString("friend_uid");
            name = value.getString("name");
            email = value.getString("email");
            tv_wallet.setText("Wallet Balance :- \u20B9 " + wallet);
        });
        upiRef.addSnapshotListener((value, error) -> {
            assert value != null;
            upi = value.getString("upi");
        });
        totalBalRef.addSnapshotListener((value, error) -> {
            assert value != null;
            totalAdminAmount = value.getString("totalIncome");
            totalPayout = value.getString("totalPayout");
        });

        adminAmountRef.addSnapshotListener((value, error) -> {
            assert value != null;
            if (value.exists()) {
                totalAdminAmount2 = value.getString("totalIncome");
                totalPayout2 = value.getString("totalPayout");
            } else {
                Map<String, Object> map = new HashMap<>();
                map.put("totalIncome", "0.0");
                map.put("totalPayout", "0.0");
                adminAmountRef.set(map);
            }
        });

        btn_withdraw.setOnClickListener(v1 -> {

            AlertDialog custome_dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.withdraw_pop_layout, null, false);

            EditText ed_mobile = view.findViewById(R.id.editText);
            EditText ed_amount = view.findViewById(R.id.editText1);
            Button btn_withdraw = view.findViewById(R.id.button1);

            builder.setView(view);
            custome_dialog = builder.create();
            custome_dialog.show();

            btn_withdraw.setOnClickListener(v -> {
                String mob = ed_mobile.getText().toString().trim();
                String amount = ed_amount.getText().toString().trim();

                if (ed_mobile.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Mobile No./UPI ID", Toast.LENGTH_SHORT).show();
                } else if (ed_amount.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Amount", Toast.LENGTH_SHORT).show();
                } else if (Float.parseFloat(amount) > Float.parseFloat(wallet)) {
                    Toast.makeText(getApplicationContext(), "Insufficient Balance !", Toast.LENGTH_SHORT).show();
                } else if (Float.parseFloat(amount) < Float.parseFloat(withdrawLimit)) {
                    Toast.makeText(getApplicationContext(), "Minimum Withdraw Limit Rs." + withdrawLimit, Toast.LENGTH_SHORT).show();
                } else {
                    uploadUserData(mob, amount);
                    custome_dialog.dismiss();
                }
            });
        });

        btn_addBal.setOnClickListener(v1 -> {
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

            if (upi.isEmpty()) {
                btn_upi.setVisibility(View.GONE);
                btn_card.setText("Proceed to Pay");
            } else {
                btn_upi.setVisibility(View.VISIBLE);
            }

            btn_100.setOnClickListener(v -> ed_amount.setText("100"));
            btn_300.setOnClickListener(v -> ed_amount.setText("300"));
            btn_500.setOnClickListener(v -> ed_amount.setText("500"));
            btn_upi.setOnClickListener(v -> {
                String amount = ed_amount.getText().toString();
                if (amount.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Valid Amount!", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(amount) < 50) {
                    Toast.makeText(getApplicationContext(), "Please Add Minimum 50 Rs.", Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmAlert("1", amount);
                    custome_dialog.dismiss();
                }
            });

            btn_card.setOnClickListener(v -> {
                String amount = ed_amount.getText().toString();
                if (amount.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter Valid Amount!", Toast.LENGTH_SHORT).show();
                } else if (Integer.parseInt(amount) < 50) {
                    Toast.makeText(getApplicationContext(), "Please Add Minimum 50 Rs.", Toast.LENGTH_SHORT).show();
                } else {
                    showConfirmAlert("2", amount);
                    custome_dialog.dismiss();
                }
            });

        });

        btn_balHis.setOnClickListener(v -> getHistoryData("1", addBalanceRef));

        btn_withdrawHis.setOnClickListener(v -> getHistoryData("2", withdrawRef));

        getHistoryData("1", addBalanceRef);

        //event
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

    private void uploadUserData(String mob, String amount) {
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        final int finalBal;
        final int charge;
        if (Float.parseFloat(amount) < 5000.0) {
            double amountt = Double.parseDouble(amount);
            double res = (amountt / 100.0f) * 10;  //calculate 10 %

            charge = 10;
            double percentAmount = Math.round(res * 100.00) / 100.00;
            finalBal = (int) (amountt - percentAmount);
            //   Toast.makeText(getApplicationContext(), finalBal + "", Toast.LENGTH_SHORT).show();
        } else {
            double amountt = Double.parseDouble(amount);
            double res = (amountt / 100.0f) * 5; //calculate 5 %

            charge = 5;
            double percentAmount = Math.round(res * 100.00) / 100.00;
            finalBal = (int) (amountt - percentAmount);

        }

        // progressDialog.show();
        float total = Float.parseFloat(wallet) - Float.parseFloat(amount);

        Map<String, Object> map = new HashMap<>();
        map.put("wallet", String.valueOf(total));

        Map<String, Object> map2 = new HashMap<>();
        map2.put("amount", amount);
        map2.put("charge", String.valueOf(charge));
        map2.put("method", mob);
        map2.put("uid", uuid);
        map2.put("status", "Pending");
        map2.put("time", Timestamp.now().getSeconds() + "000");

        Map<String, Object> map3 = new HashMap<>();
        map3.put("amount", String.valueOf(finalBal));
        map3.put("method", mob);
        map3.put("uid", userUid);
        map3.put("email", email);
        map3.put("name", name);
        map3.put("item_id", uuid);
        map3.put("status", "Pending");
        map3.put("time", Timestamp.now().getSeconds() + "000");

        userRef.update(map).addOnSuccessListener(command -> {

            withdrawRef.document(uuid).set(map2).addOnSuccessListener(documentReference -> {
                progressDialog.dismiss();
                getHistoryData("2", withdrawRef);
                Toast.makeText(getApplicationContext(), "Request Submit Successfully.", Toast.LENGTH_SHORT).show();

            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            });

            //today income day wise for admin
            float totall = Float.parseFloat(totalPayout) + Float.parseFloat(amount);
            totalBalRef.update("totalPayout", String.valueOf(totall));

            //today income day wise for admin
            float totalll = Float.parseFloat(totalPayout2) + Float.parseFloat(amount);
            adminAmountRef.update("totalPayout", String.valueOf(totalll));

            if (mob.length() == 10) {
                map3.put("type", "mobile");
                sendDataToAdmin("Paytm Request", map3, uuid);
            } else {
                map3.put("type", "upi");
                sendDataToAdmin("UPI Request", map3, uuid);

            }
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    private void sendDataToAdmin(String type, Map map, String uuid) {

        Calendar calendar = Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd-MM-yy");
        String withdrawTime = simpleDateFormat1.format(calendar.getTime());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(type).child(withdrawTime).child(uuid);

        databaseReference.setValue(map)
                .addOnCompleteListener(task -> progressDialog.dismiss()).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(PaymentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    @SuppressLint("NotifyDataSetChanged")
    private void getHistoryData(String s, CollectionReference reference) {
        recyclerView = findViewById(R.id.rec_history);
        reference
                .orderBy("time").addSnapshotListener((value, error) -> {

            list.clear();
            assert value != null;
            for (DocumentSnapshot documentSnapshot : value) {
                HistoryModel model = new HistoryModel();
                model.setAmount(documentSnapshot.getString("amount"));
                model.setTime(documentSnapshot.getString("time"));
                model.setStatus(documentSnapshot.getString("status"));
                model.setMethod(documentSnapshot.getString("method"));
                model.setCharge(documentSnapshot.getString("charge"));
                list.add(model);
            }

            if (list.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                if (s.contains("1")){
                    Toast.makeText(getApplicationContext(), "No Balance History Found!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "No Withdraw History Found!", Toast.LENGTH_SHORT).show(); }

            }else {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(RecyclerView.VERTICAL);
                layoutManager.setReverseLayout(true);
                layoutManager.setStackFromEnd(true);

                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.setLayoutManager(layoutManager);

                adapter = new TransactionHistoryAdapter(s, list);
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();
            }
        });

    }

    private void initView() {
        tv_wallet = findViewById(R.id.textView19);
        btn_addBal = findViewById(R.id.btn_addBal);
        btn_withdraw = findViewById(R.id.btn_withdraw);
        btn_balHis = findViewById(R.id.btn_addBalHis);
        btn_withdrawHis = findViewById(R.id.btn_withdrawHis);
        recyclerView = findViewById(R.id.rec_history);
    }

    private void showConfirmAlert(String type, String amount) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            dialog.dismiss();
            if (type.contains("1")) {
                this.amount = amount;
                payment(amount);

            } else {
                // other gateway
               // goForPayment(amount);
                Intent intent=new Intent(PaymentActivity.this,PayuMoneyActivity.class);
                intent.putExtra("money",amount);
                intent.putExtra("wallet",wallet);
                intent.putExtra("id",friendUid);
                intent.putExtra("name",name);
                intent.putExtra("email",email);
                startActivity(intent);
            }
        }).setNegativeButton("Cancel", (dialog, which) ->
                dialog.dismiss());

        Utils.showAlertdialog(PaymentActivity.this, builder, "Confirm", "Are you sure, You want to add this amount");

    }

    @SuppressLint("QueryPermissionsNeeded")
    private void payment(String amount) {
        Uri uri = new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", upi)
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
                progressDialog.setMessage("Updating Balance");
                progressDialog.show();
                new Handler(Looper.myLooper()).postDelayed(() -> updateUserDetail(amount), 3000);
                Toast.makeText(getApplicationContext(), "Transaction successful.", Toast.LENGTH_SHORT).show();

            } else if ("Payment cancelled.".equals(paymentCancel)) {
                Toast.makeText(getApplicationContext(), "Payment cancelled.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Transaction failed.Please try com_new", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Internet connection is not available. Please check and try com_new", Toast.LENGTH_SHORT).show();
        }

    }

    private void updateUserDetail(String amount) {

        if (!friendUid.isEmpty()) {
            friendReference = FirebaseFirestore.getInstance().collection("User_List").document(friendUid);
            friendReference.get().addOnSuccessListener(documentSnapshot -> {
                String total = documentSnapshot.getString("wallet");


                double amountt = Double.parseDouble(amount);
                double res = (amountt / 100.0f) * 10;  //calculate 10 %

                double percentAmount = Math.round(res * 100.00) / 100.00;  //finding round off value in 2 decimal point
                totalFriendAmount=Double.parseDouble(total)+percentAmount;

                friendReference.update("wallet",String.valueOf(totalFriendAmount));

            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "Friend Adding amount failed", Toast.LENGTH_SHORT).show();
            });
        }


        float totall = Float.parseFloat(totalAdminAmount) + Float.parseFloat(amount);
        totalBalRef.update("totalIncome", String.valueOf(totall));

        //today income day wise for admin
        float totalll = Float.parseFloat(totalAdminAmount2) + Float.parseFloat(amount);
        adminAmountRef.update("totalIncome", String.valueOf(totalll));

        final String uuid = UUID.randomUUID().toString().replace("-", "");
        progressDialog.show();
        float total = Float.parseFloat(wallet) + Float.parseFloat(amount);

        Map<String, Object> map = new HashMap<>();
        map.put("wallet", String.valueOf(total));

        Map<String, Object> map2 = new HashMap<>();
        map2.put("amount", amount);
        map2.put("charge", "");
        map2.put("method", "");
        map2.put("uid", uuid);
        map2.put("status", "Successfull");
        map2.put("time", Timestamp.now().getSeconds() + "000");

        userRef.update(map).addOnSuccessListener(command ->
                addBalanceRef.document(uuid).set(map2).addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    getHistoryData("1", addBalanceRef);
                    Toast.makeText(getApplicationContext(), "Add Balance Successfully", Toast.LENGTH_SHORT).show();

                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
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
