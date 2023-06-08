package com.workz.athome;

import static com.workz.athome.Utils.Utils.setUserSharedPreference;

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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.workz.athome.Model.UserData.Root;
import com.workz.athome.Utils.ApiClient;
import com.workz.athome.Utils.ApiService;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPMatchActivity extends AppCompatActivity {
    private ApiService apiServices;
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

        apiServices = ApiClient.getRetrofit().create(ApiService.class);

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
                        mobile = mobile_number;
                        checkUserExistence(mobile_number);
                    } else {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(R.id.view), Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()), Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void checkUserExistence(String mobile_number) {

        Call<Root> call = apiServices.loginUser(
                "",
                mobile_number
        );
        call.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                Root root = response.body();

                if (response.code() == 200&& Objects.requireNonNull(root).getStatus()) {
                    //result ok from server
                    progressDialog.dismiss();
                    setUserSharedPreference(root,OTPMatchActivity.this);
                    Toast.makeText(getApplicationContext(), "SignIn Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(OTPMatchActivity.this, MainActivity.class));
                    finish();

                } else {
                    // api error data fetch from firebase
                    String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                    progressDialog.setMessage("Checking Data...");
                    progressDialog.show();
                    CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("User_List");
                    collectionReference
                            .whereEqualTo("uid", uid)
                            .get().addOnSuccessListener(result -> {
                                String remove_ads = "No";

                                if (!result.isEmpty()) {
                                    //user already registered on firebase, getting data from firebase
                                    for (DocumentSnapshot documentSnapshot : result) {

                                        String finalActivation = documentSnapshot.getString("activation");
                                        String finalCaptcha_price = documentSnapshot.getString("captcha_price");
                                        String finalMobile = documentSnapshot.getString("mobile");
                                        String finalUserName = documentSnapshot.getString("userName");
                                        String finalWallet = documentSnapshot.getString("wallet");
                                        String finalUserEmail = documentSnapshot.getString("email");

                                        FirebaseFirestore.getInstance()
                                                .collection("Instant Activation")
                                                .document(uid)
                                                .addSnapshotListener((value, error) -> {
                                                    assert value != null;
                                                    String instantActivation;
                                                    if (value.exists()) {
                                                        //getting instant activation data
                                                        instantActivation=value.getString("activation");

                                                    } else {
                                                        instantActivation="NO";
                                                    }
                                                    FirebaseFirestore.getInstance()
                                                            .collection("Promotion_user")
                                                            .document(uid)
                                                            .addSnapshotListener((value1, error1) -> {
                                                                String install;
                                                                assert value1 != null;
                                                                if (value1.exists()) {
                                                                    //getting promotional user data
                                                                    install=value1.getString("install");

                                                                } else {
                                                                    install="NO";
                                                            }
                                                                FirebaseDatabase.getInstance().getReference().child("Spin_User").child(uid).get()
                                                                        .addOnSuccessListener(dataSnapshot -> {
                                                                            String winning_balence,index;
                                                                            if (dataSnapshot.exists()){
                                                                                SpinModel model = dataSnapshot.getValue(SpinModel.class);
                                                                                assert model != null;
                                                                                winning_balence=model.getWinning_balence();
                                                                                index=model.getIndex();
                                                                            }else {
                                                                                winning_balence="1";
                                                                                index="0";
                                                                            }

                                                                            Call<Root> registerCall = apiServices.registerUser(
                                                                                    finalActivation,
                                                                                    finalCaptcha_price,
                                                                                    finalUserEmail,
                                                                                    finalMobile,
                                                                                    uid,
                                                                                    finalUserName,
                                                                                    finalWallet,
                                                                                    instantActivation,
                                                                                    install,
                                                                                    index,
                                                                                    winning_balence,
                                                                                    remove_ads,
                                                                                    "NO"
                                                                            );
                                                                            registerCall.enqueue(new Callback<Root>() {
                                                                                @Override
                                                                                public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                                                                                    Root root1=response.body();
                                                                                    progressDialog.dismiss();

                                                                                    if (response.code() == 201) {
                                                                                        setUserSharedPreference(root1,OTPMatchActivity.this);
                                                                                        Toast.makeText(getApplicationContext(), "SignIn successful", Toast.LENGTH_SHORT).show();
                                                                                        startActivity(new Intent(OTPMatchActivity.this, MainActivity.class));
                                                                                        finish();
                                                                                    } else if (response.code()==404){
                                                                                        Toast.makeText(OTPMatchActivity.this, root1.getMessage()+"/ Mobile Already Registered!", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }

                                                                                @Override
                                                                                public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                                                                                    progressDialog.dismiss();
                                                                                    Toast.makeText(OTPMatchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            });

                                                                        })
                                                                        .addOnFailureListener(e -> {
                                                                            progressDialog.dismiss();
                                                                            Toast.makeText(OTPMatchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        });

                                                            });
                                                });
                                    }
                                } else {
                                    //user not registered on firebase, setting new data on server
                                   setNewDataOnSever(uid);
                                }
                            });
                }

            }

            @Override
            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OTPMatchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }


        });
    }

    private void setNewDataOnSever(String uid) {

        Call<Root> registerCall = apiServices.registerUser(
                "NO",
                "5",
                "",
                mobile,
                uid,
                "",
                "0.0",
                "NO",
                "NO",
                "0",
                "1",
                "NO",
                "NO"
        );
        registerCall.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                Root root1=response.body();

                if (root1.getStatus()&&response.code()==201){
                    setUserSharedPreference(root1,OTPMatchActivity.this);

                    Toast.makeText(getApplicationContext(), "Register Scucessfully!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(OTPMatchActivity.this, MainActivity.class));
                    finish();
                }else if (!root1.getStatus()){
                    Toast.makeText(OTPMatchActivity.this, root1.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(OTPMatchActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }

}