package com.perfect.traders.Activity;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.perfect.traders.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient signInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
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

        //for progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        SignInButton signInButton = findViewById(R.id.google);
        SignInButton signInButton2 = findViewById(R.id.google1);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton2.setColorScheme(SignInButton.COLOR_DARK);
        signInButton2.setSize(SignInButton.SIZE_STANDARD);

        //database
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //requesting for email
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1055459891657-vor9q3t1dffarl829ou59fjlr2mn38cn.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);


        //event
        signInButton.setOnClickListener(v -> {
            progressDialog.show();
            Intent intent = signInClient.getSignInIntent();
            loginResultLauncher.launch(intent);
        });
        //event
        signInButton2.setOnClickListener(v -> {
            progressDialog.show();
            Intent intent = signInClient.getSignInIntent();
            loginResultLauncher.launch(intent);
        });

        findViewById(R.id.appCompatButton7).setOnClickListener(view -> {
            findViewById(R.id.appCompatButton7).setVisibility(View.GONE);
            findViewById(R.id.google1).setVisibility(View.GONE);
            findViewById(R.id.registerCardView).setVisibility(View.VISIBLE);
            findViewById(R.id.loginCardView).setVisibility(View.GONE);
        });


        loginResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Login Error", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });


        (findViewById(R.id.textView30)).setOnClickListener(view -> {
            findViewById(R.id.registerCardView).setVisibility(View.GONE);
            findViewById(R.id.loginCardView).setVisibility(View.VISIBLE);
        });

        (findViewById(R.id.textView45)).setOnClickListener(view -> {
            findViewById(R.id.registerCardView).setVisibility(View.VISIBLE);
            findViewById(R.id.loginCardView).setVisibility(View.GONE);
        });

        //register with email and password
        (findViewById(R.id.appCompatButton5)).setOnClickListener(v -> {
            String email = ((EditText) findViewById(R.id.eed_email)).getText().toString();
            String pass = ((EditText) findViewById(R.id.eed_pass)).getText().toString();

            if (TextUtils.isEmpty(email)) {
                Snackbar.make(v, "Enter Email Address", Snackbar.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pass)) {
                Snackbar.make(v, "Enter Password", Snackbar.LENGTH_SHORT).show();
            } else if (pass.length() <= 6) {
                Toast.makeText(this, "Password Too Short!", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.setMessage("Loading...");
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(email, pass)
                        .addOnSuccessListener(authResult -> {
                            final String uidd = FirebaseAuth.getInstance().getUid();

                            FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                                String deviceid = task.getResult();

                                final Map<String, Object> map = new HashMap<>();
                                map.put("email", email);
                                map.put("image", "https://firebasestorage.googleapis.com/v0/b/perfect-traders.appspot.com/o/profile.png?alt=media&token=e98ef89a-f58e-4f95-8dd2-c58d5bfaab53");
                                map.put("wallet", "0.0");
                                map.put("uid", uidd);
                                map.put("chat", "unblock");
                                map.put("course", "NO");
                                map.put("trade_with_us", "NO");
                                map.put("device_id", deviceid);
                                map.put("subscritpion", "NO");
                                map.put("subscritpion_end_date", "NO");
                                map.put("plan", "NO");

                                // Sign in success, update UI with the signed-in com's information
                                documentReference = db.collection("User_List").document(firebaseAuth.getUid());
                                documentReference
                                        .set(map)
                                        .addOnCompleteListener(task1 -> {
                                            progressDialog.dismiss();
                                            if (task1.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Register successfully", Toast.LENGTH_SHORT).show();
                                                (findViewById(R.id.registerCardView)).setVisibility(View.GONE);
                                                (findViewById(R.id.loginCardView)).setVisibility(View.VISIBLE);
                                            }
                                        }).addOnFailureListener(e -> {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Register Failed", Toast.LENGTH_SHORT).show();
                                });
                            });
                        }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

        //login here
        (findViewById(R.id.appCompatButton6)).setOnClickListener(view -> {
            String email = ((EditText) findViewById(R.id.lleed_email)).getText().toString();
            String pass = ((EditText) findViewById(R.id.lled_pass)).getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter Registered Email!", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(pass)) {
                Toast.makeText(getApplicationContext(), "Enter Password!", Toast.LENGTH_SHORT).show();
            } else {
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Login Scucessfully!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //for google sing in
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        final String email = account.getEmail();
        final Uri img = account.getPhotoUrl();

        final Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("image", img.toString());
        map.put("wallet", "0.0");
        map.put("chat", "unblock");
        map.put("device_id", "deviceid");
        map.put("course", "NO");
        map.put("trade_with_us", "NO");
        map.put("subscritpion", "NO");
        map.put("subscritpion_end_date", "NO");
        map.put("plan", "NO");

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        final String uidd = FirebaseAuth.getInstance().getUid();

                        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("User_List");
                        collectionReference
                                .whereEqualTo("uid", uidd)
                                .get().addOnCompleteListener(task1 -> {
                            if (!task1.getResult().isEmpty()) {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "SignIn successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                map.put("uid", uidd);
                                assert uidd != null;
                                documentReference = FirebaseFirestore.getInstance().collection("User_List").document(uidd);
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

                    } else {
                        // If sign in fails, display a message to the com.
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}