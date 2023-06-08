package com.workz.athome;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPMatchActivity extends AppCompatActivity {

    private EditText otp;
    private String verificationId;
    private TextView resend, dont;
    private String mobile;
    private ProgressDialog progressDialog;

    public OTPMatchActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_otpmatch);

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

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
            String code = Objects.requireNonNull(otp).getText().toString();
            if (TextUtils.isEmpty(code)) {
                otp.setError("Enter OTP Please");
            } else if (code.length() != 6) {
                Snackbar.make(v, "Enter 6 digit valid OTP", Snackbar.LENGTH_LONG).show();
            } else {
                progressDialog.setMessage("OTP Verifying...");
                progressDialog.show();
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

        otp_Send(mobile_number);

    }

    private void otp_Send(final String mobile_number) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mobile_number, 60, TimeUnit.SECONDS, this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        otp_Match(phoneAuthCredential, mobile_number);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        verificationId = s;
                        new CountDownTimer(60000, 1000) {
                            @SuppressLint("SetTextI18n")
                            public void onTick(long millisUntilFinished) {
                                resend.setText(String.valueOf(millisUntilFinished / 1000));
                                dont.setText("Second remaining");
                            }

                            @SuppressLint("SetTextI18n")
                            public void onFinish() {
                                resend.setText("Resend OTP");
                                dont.setText("Don't recive");
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
                        askforEmail();
                    } else {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(R.id.view), Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void askforEmail() {
        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        progressDialog.setMessage("Checking Data...");
        progressDialog.show();
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("User_List");
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
        final Map<String, Object> map = new HashMap<>();

        map.put("email", "");
        map.put("userName", "");
        map.put("wallet", "0.0");
        map.put("mobile", mobile);
        map.put("activation", "NO");
        map.put("uid", uid);

        DocumentReference admRef = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");
        admRef.addSnapshotListener((value, error) -> {
            assert value != null;
            String price = value.getString("captcha_price");
            map.put("captcha_price", price);
            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User_List").document(uid);
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


        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("User_List").document(uid);
        documentReference.set(map).addOnSuccessListener(command -> {

            Toast.makeText(this, "Register Scucessfully!", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
            startActivity(new Intent(OTPMatchActivity.this, MainActivity.class));
            finish();

        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OTPMatchActivity.this, LoginActivity.class));
        finish();
    }


}