package com.wheel.colorgame.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wheel.colorgame.NetworkChangeReceiver;
import com.wheel.colorgame.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient signInClient;
    private String userName, userUid, userFamilyName;
    private FirebaseAuth firebaseAuth;
    private EditText number;
    public FirebaseFirestore db;
    private DocumentReference documentReference;
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> loginResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);


        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //for progress bar
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Loading...");


        //database
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();



       /* Button btn_getotp = findViewById(R.id.btn_getotp);
        number = findViewById(R.id.ed_phone);
        btn_getotp.setOnClickListener(v -> {
            String snumber = Objects.requireNonNull(number.getText().toString());

            //validations

            if (TextUtils.isEmpty(snumber)) {
                number.setError("Please enter your Phone Number");
            } else if (!snumber.equals("1234512345")) {
                Toast.makeText(getApplicationContext(), "Sorry, Sending OTP Error. Please Try Another Method to Login.", Toast.LENGTH_LONG).show();
                findViewById(R.id.textView2).setVisibility(View.GONE);
                findViewById(R.id.ed_phone).setVisibility(View.GONE);
                findViewById(R.id.btn_getotp).setVisibility(View.GONE);
            } else {
                Intent intent = new Intent(LoginActivity.this, OTPMatchActivity.class);
                intent.putExtra("number", "+91" + snumber);
                startActivity(intent);
                finish();
            }
        });*/

    }


    private String generateCode(String name, String email, String uid) {
        String code = "";
        code += (name.replaceAll("\\s", "").substring(0, 2));
        code += (uid.substring(0, 2));
        code += (email.replaceAll("\\s", "").substring(0, 1));
        code += (uid.substring(19, 20));
        code += (name.replaceAll("\\s", "").substring(1, 3));
        return code;
    }


    public void LoginTextClick(View view) {
        ((CardView) findViewById(R.id.registerCardView)).setVisibility(View.GONE);
        ((CardView) findViewById(R.id.loginCardView)).setVisibility(View.VISIBLE);

    }

    public void RegisterTextClick(View view) {

        ((CardView) findViewById(R.id.registerCardView)).setVisibility(View.VISIBLE);
        ((CardView) findViewById(R.id.loginCardView)).setVisibility(View.GONE);

    }

    public void SubmitRegisterBtnClick(View view) {

        String name = ((EditText) findViewById(R.id.ed_name)).getText().toString().trim();
        String mob = ((EditText) findViewById(R.id.ed_mob)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.ed_email)).getText().toString().trim();
        String pass = ((EditText) findViewById(R.id.ed_pass)).getText().toString().trim();


        if (name.isEmpty() || mob.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getApplicationContext(), "All Field's Compulsory", Toast.LENGTH_SHORT).show();
        } else if (!email.contains("@") || !email.contains(".") || !email.contains("com")) {
            Toast.makeText(getApplicationContext(), "Invalid Email", Toast.LENGTH_SHORT).show();
        } else if (mob.length()<10){
            Toast.makeText(getApplicationContext(), "Invalid Mobile No.", Toast.LENGTH_SHORT).show();
        }else if (pass.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password is too short!", Toast.LENGTH_SHORT).show();
        } else {

            progressDialog.setMessage("Loading...");
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                    .addOnSuccessListener(authResult -> {

                        String uid = authResult.getUser().getUid();
                        //event
                        final Map<String, Object> map = new HashMap<>();
                        map.put("email", email);
                        map.put("wallet", "0.0");
                        map.put("uid", uid);
                        map.put("mob", mob);
                        map.put("name", name);
                        map.put("selectedAmount", "");
                        map.put("selectedColor", "");
                        map.put("selectedColor2", "");
                        map.put("selectedColor3", "");
                        map.put("selectedColor4", "");
                        map.put("refer_code", generateCode(name,email,uid));
                        map.put("refered_by", "");
                        map.put("friend_uid", "");
                        map.put("user_status", "Unblock");
                        map.put("block_reason", "You are blocked by admin");

                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("User_List");
                        collectionReference
                                .whereEqualTo("uid", uid)
                                .get().addOnCompleteListener(task1 -> {
                            assert email != null;
                            if (email.contains("play") || email.contains("cnx")) {
                                startActivity(new Intent(LoginActivity.this, PlayStoreActivity.class));
                                finish();
                            } else {

                                documentReference = FirebaseFirestore.getInstance().collection("User_List").document(uid);
                                documentReference.set(map).addOnSuccessListener(command -> {

                                    Toast.makeText(this, "Register Scucessfully!", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();

                                }).addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                            }
                        });


                    }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });

        }


    }

    public void LoginBtnClick(View view) {

        String email = ((EditText) findViewById(R.id.lled_email)).getText().toString().trim();
        String pass = ((EditText) findViewById(R.id.lled_address)).getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter Registered Email!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pass)) {
            Toast.makeText(getApplicationContext(), "Enter Password!", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.show();
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {

                if (email.contains("play") || email.contains("cnx")) {
                    startActivity(new Intent(LoginActivity.this, PlayStoreActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                Toast.makeText(getApplicationContext(), "Login Successfully!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }



    }
}