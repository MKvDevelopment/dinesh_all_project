package com.typingwork.jobathome.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
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
import com.typingwork.jobathome.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ReferActivity extends AppCompatActivity {


    public static String friend_email, friend_wallet, friend_name, friend_uid;
    private String refercode, user_wallet, referBy, activation;
    private AlertDialog custom_dialog;
    private Button verifyCode, referNow;
    private EditText friendReferCode, mobile;
    private TextView tvrefercod, success_refer, tv_referDetail;
    private String addingAmount;
    private ProgressDialog progress;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refer);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


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
        tvrefercod = findViewById(R.id.myReferCode);
        verifyCode = findViewById(R.id.appCompatButton);
        referNow = findViewById(R.id.appCompatButton2);
        success_refer = findViewById(R.id.textView39);
        tv_referDetail = findViewById(R.id.textView42);

        //fetching data
        final String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        assert uid != null;
        documentReference = FirebaseFirestore.getInstance().collection("Users_list").document(uid);

        getUserData();


        //events
        verifyCode.setOnClickListener(v -> {
            String code = friendReferCode.getText().toString().trim();
            if (code.isEmpty()) {
                Toast.makeText(ReferActivity.this, "Enter Valid Code", Toast.LENGTH_LONG).show();
            } else if (code.length() < 9) {
                Toast.makeText(ReferActivity.this, "Enter Valid Code", Toast.LENGTH_LONG).show();
            } else if (refercode.compareTo(code) == 0) {
                Toast.makeText(ReferActivity.this, "Enter your friend's referral code.", Toast.LENGTH_SHORT).show();
            } else {
                progress.show();
                verifingCode(code);
            }
        });

        referNow.setOnClickListener(v -> {
            String link = "https://play.google.com/store/apps/details?id=" + getPackageName();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "My Refer Link");

            if (activation.equals("Yes")) {
                intent.putExtra(Intent.EXTRA_TEXT, "Hey!! friends - Today i find this amazing Typing Work Application. Earn lot of money from this app at home by Typing Data with your Smartphone. Download app from here " + link + ". Use my refer code  to get Rs.100/-. My refer code :- " + refercode + " - ");
                startActivity(Intent.createChooser(intent, "Share via"));
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, "Hey!! friends - Today i find this amazing Typing Work Application. Earn lot of money from this app at home by Typing Data with your Smartphone. Download app from here " + link + ".");
                startActivity(Intent.createChooser(intent, "Share via"));
            }


        });

    }

    private void getUserData() {

        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null) {

                activation = documentSnapshot.getString("activation");
                user_wallet = documentSnapshot.getString("wallet");
                refercode = documentSnapshot.getString("refer_code");

                tvrefercod.setText("Your refer code :- " + refercode);

                referBy = documentSnapshot.getString("refered_by");
                assert referBy != null;

                if (!referBy.isEmpty()) {
                    //refer is not empty
                    success_refer.setVisibility(View.VISIBLE);
                    success_refer.setText(Html.fromHtml("You Refered by " + "<font color='#EE0000'>" + referBy + "</font>"+" You and Your friend get Rs 100 When you Upgrade in Premium." ));

                    verifyCode.setVisibility(View.GONE);
                    friendReferCode.setVisibility(View.GONE);
                }

                if (activation.equals("Yes")) {
                    //activation is Yes
                    tvrefercod.setVisibility(View.VISIBLE);
                    referNow.setText("Refer & Earn ₹ 100");
                    tv_referDetail.setVisibility(View.VISIBLE);
                    success_refer.setVisibility(View.GONE);
                    verifyCode.setVisibility(View.GONE);
                    friendReferCode.setVisibility(View.GONE);


                }
                progress.dismiss();

            }
        }).addOnFailureListener(e -> {
            progress.dismiss();
            assert e != null;
            Toast.makeText(ReferActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });

    }


    private void verifingCode(final String code) {
        CollectionReference reference = FirebaseFirestore.getInstance().collection("Users_list");
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
                Toast.makeText(this, "Your friend get ₹ 100 when you deposit Security Fee.", Toast.LENGTH_SHORT).show();
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