package com.wheel.colorgame.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.wheel.colorgame.NetworkChangeReceiver;
import com.wheel.colorgame.R;
import com.wheel.colorgame.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReferActivity extends AppCompatActivity {

    public static String friend_email, friend_wallet, friend_name, friend_uid;
    private String refercode, user_wallet, referBy;
    private AlertDialog custom_dialog;
    private Button verifyCode, addBal, remove_code;
    private EditText friendReferCode, mobile;
    private TextView tvrefercod, success_refer;
    private String addingAmount;
    private ProgressDialog progress;
    private DocumentReference documentReference;

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
        progress = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progress.setTitle("Please Wait!");
        progress.setMessage("Fetching Info...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);
        progress.show();


        Toolbar toolbar = findViewById(R.id.referToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //initiate view
        friendReferCode = findViewById(R.id.appCompatEditText);
        tvrefercod = findViewById(R.id.textView38);
        verifyCode = findViewById(R.id.appCompatButton);
        remove_code = findViewById(R.id.remove_refer_Button);
        Button referNow = findViewById(R.id.appCompatButton2);
        success_refer = findViewById(R.id.textView39);

        //fetching data
        final String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        assert uid != null;
        documentReference = FirebaseFirestore.getInstance().collection("User_List").document(uid);

        getUserData();
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
            String link = "https://play.google.com/store/apps/details?id="+getPackageName();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Hey!! friends - Today i find this amazing "+R.string.app_name+" application. Earn lot of money from this app at home by smartphone. Download app from here " + link + ". Use my refer code  to get Rs.300/-. My refer code :- " + refercode + " - ");
            intent.putExtra(Intent.EXTRA_SUBJECT, "My Refer Link");
            startActivity(Intent.createChooser(intent, "Share via"));
        });

        remove_code.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            alertDialog.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();

                   Map map=new HashMap();
                   map.put("refered_by","");
                   map.put("friend_uid","");

                   documentReference.update(map).addOnSuccessListener(o -> {
                       getUserData();
                       remove_code.setVisibility(View.GONE);
                   });


            }).setCancelable(false).setNegativeButton("NO", (dialog, which) -> dialog.dismiss());
            Utils.showAlertdialog(ReferActivity.this, alertDialog, "Delete Alert!", "Are you Sure? You want to delete your freind refer code.");

        });
    }

    private void getUserData() {

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null) {

                user_wallet = documentSnapshot.getString("wallet");
                refercode = documentSnapshot.getString("refer_code");

                tvrefercod.setText("Your refer code :- " + refercode);

                referBy = documentSnapshot.getString("refered_by");
                assert referBy != null;
                if (!referBy.isEmpty()) {
                    updateReferedUI(referBy);
                }else {
                    success_refer.setVisibility(View.GONE);
                    remove_code.setVisibility(View.GONE);
                    verifyCode.setVisibility(View.VISIBLE);
                    friendReferCode.setVisibility(View.VISIBLE);
                }
                progress.dismiss();

            }
        }).addOnFailureListener(e -> {
            progress.dismiss();
            assert e != null;
            Toast.makeText(ReferActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }

    @SuppressLint("SetTextI18n")
    private void updateReferedUI(String referBy) {
        success_refer.setVisibility(View.VISIBLE);
        success_refer.setText(Html.fromHtml("You entered " + "<font color='#EE0000'>" + referBy + "</font>" + " Refer Code.\n Your friend will get 10% of each amount you will add to this game."));
        remove_code.setVisibility(View.VISIBLE);
        verifyCode.setVisibility(View.GONE);
        friendReferCode.setVisibility(View.GONE);
    }

    private void verifingCode(final String code) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("User_List");
        reference
                .whereEqualTo("refer_code", code)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.getResult().isEmpty()) {

                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            referBy = snapshot.getString("refer_code");
                            friend_email = snapshot.getString("email");
                            friend_wallet = snapshot.getString("wallet");
                            friend_name = snapshot.getString("name");
                            friend_uid = snapshot.getString("uid");
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
        progress.setMessage("Verifying Refer Code...");
        new Handler().postDelayed(() -> {
            Map map = new HashMap();
            map.put("refered_by", friend_name);
            map.put("friend_uid", friend_uid);
            documentReference.update(map).addOnSuccessListener(o -> {
                getUserData();
                progress.dismiss();
            });

        }, 3000);
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