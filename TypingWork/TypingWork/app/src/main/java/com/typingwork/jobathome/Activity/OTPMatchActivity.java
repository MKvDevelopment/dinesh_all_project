package com.typingwork.jobathome.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.typingwork.jobathome.R;
import com.typingwork.jobathome.Utils.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class OTPMatchActivity extends AppCompatActivity {

    private TextInputLayout otp;
    private String verificationId;
    private TextView resend, dont;
    private String deviceid, mobile;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpmatch);

        // FOR FULLSCREEN
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressDialog = new ProgressDialog(OTPMatchActivity.this);
        Constant.showProgressDialog(progressDialog,"Please Wait!","Checking Data...");

        //get intent
        Intent intent = getIntent();
        final String mobile_number = intent.getStringExtra("number");

        // findViewById
        TextView number = findViewById(R.id.number);
        resend = findViewById(R.id.resend);
        dont = findViewById(R.id.dont);

        Button next = findViewById(R.id.next);
        otp = findViewById(R.id.otp);

        // set number
        number.setText(mobile_number);

        next.setOnClickListener(v -> {
            String code = Objects.requireNonNull(otp.getEditText()).getText().toString();
            if (TextUtils.isEmpty(code)) {
                otp.setError("Enter OTP Please");
            } else {
                progressDialog.setMessage("OTP Verifying...");
                progressDialog.show();
                otp.setCounterEnabled(false);
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                otp_Match(credential, mobile_number);
            }
        });

        resend.setOnClickListener(v -> {
            if (resend.getText().equals("Resend OTP")) {
                Snackbar.make(v, "Code has been Re-Sent", Snackbar.LENGTH_LONG).show();
                otp_Send(mobile_number);
            }
        });

        Objects.requireNonNull(otp.getEditText()).setOnFocusChangeListener((v, hasFocus) ->
                otp.setCounterEnabled(false));

        otp_Send(mobile_number);


    }
    private void otp_Send(final String mobile_number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobile_number, 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        //  otp_Match(phoneAuthCredential, mobile_number);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        verificationId = s;
                        new CountDownTimer(60000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                resend.setText(String.valueOf(millisUntilFinished / 1000));
                                dont.setText(R.string.second_remaining);
                            }

                            public void onFinish() {
                                resend.setText(R.string.resend_otp);
                                dont.setText(R.string.dont_recive);
                            }
                        }.start();
                    }

                    @SuppressLint("NewApi")
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Snackbar.make(findViewById(R.id.view), Objects.requireNonNull(e.getMessage()), Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void otp_Match(final PhoneAuthCredential credential, final String mobile_number) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(OTPMatchActivity.this, task -> {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        mobile = mobile_number;
                        checkPreviousRegister();
                    } else {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(R.id.view), Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void checkPreviousRegister() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        progressDialog.setMessage("Checking Data...");
        progressDialog.show();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("Users_list");
        collectionReference
                .whereEqualTo("uid", uid)
                .get()
                .addOnCompleteListener(task -> {
                    if (!task.getResult().isEmpty()) {
                        progressDialog.dismiss();
                        //already customer
                        Intent intent = new Intent(OTPMatchActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(getApplicationContext(), "SignIn successful", Toast.LENGTH_SHORT).show();

                    } else {
                        progressDialog.setMessage("Uploading Data...");
                        //new customer
                        setDataOnSever(uid);
                    }
                });


    }

    private void setDataOnSever(String uid) {
        //event
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task100 -> {
            deviceid = task100.getResult();

            final Map<String, Object> map = new HashMap<>();
            map.put("email", "");
            map.put("phone", mobile);
            map.put("name", "");
            map.put("photo", "No");
            map.put("field1", "");
            map.put("field2", "");
            map.put("block", "UnBlock");
            map.put("reason", "We Block your account due to policy violation. We found suspicious activity on your account. Read our term and conditions and follow them.");
            map.put("friend_uid", "");
            map.put("wrong_entry", "0");
            map.put("ads_activation", "No");
            map.put("right_entry", "0");
            map.put("rating", "No");
            map.put("instal1", "No");
            map.put("refer_code", randomGererate());
            map.put("refered_by", "");
            map.put("plan", "Yes");
            map.put("wallet", "0");
            map.put("device_id", deviceid);
            map.put("withdraw", "Success");
            map.put("activation", "No");
            map.put("form_price", "1");
            map.put("uid", uid);

            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users_list").document(uid);
            documentReference.set(map).addOnSuccessListener(command -> {

                Toast.makeText(this, "Register Scucessfully!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                startActivity(new Intent(OTPMatchActivity.this, MainActivity.class));
                finish();

            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OTPMatchActivity.this, LoginActivity.class));
        finish();
    }

    private String randomGererate() {

        String LETTERS = "abcdefghijkmnopqrstuvwxy";
        char[] Free_Plan_CODE = (("0123456789") + LETTERS.toUpperCase()+LETTERS.toLowerCase()).toCharArray();

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 9; i++) {
            result.append(Free_Plan_CODE[new Random().nextInt(Free_Plan_CODE.length)]);
        }
        return result.toString();
    }
}