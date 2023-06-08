package com.work_int.athome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private GoogleSignInClient signInClient;
    private FirebaseAuth firebaseAuth;
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

        //for progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        SignInButton signInButton = findViewById(R.id.google);
        countryCodePicker = findViewById(R.id.countrycodepicker);

        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //database
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //requesting for email
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("349834706580-3tp70c17bos55to3c2bnbjcvbb1vnbsb.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);

        //event
        signInButton.setOnClickListener(v -> {
            progressDialog.show();
            Intent intent = signInClient.getSignInIntent();
            loginResultLauncher.launch(intent);
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
                            // Google Sign In failed, update UI appropriately

                        } catch (ApiException e) {
                            progressDialog.dismiss();
                            Toast.makeText(this, "Login Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        Button btn_getotp = findViewById(R.id.btn_getotp);
        EditText number = findViewById(R.id.ed_phone);
        btn_getotp.setOnClickListener(v -> {
            countryCodePicker.registerCarrierNumberEditText(number);
            String mobile = countryCodePicker.getFullNumber();
            String snumber = Objects.requireNonNull(number.getText().toString());

            //validations

            if (TextUtils.isEmpty(snumber)) {
                number.setError("Please enter your Phone Number");
            } else {
               // Toast.makeText(this, mobile, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, OTPMatchActivity.class);
                intent.putExtra("number", mobile);
                startActivity(intent);
                finish();
            }
        });


    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //for google sing in
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        final String email = account.getEmail();

        final Map<String, Object> map = new HashMap<>();
        map.put("email", email);
        map.put("userName", account.getDisplayName());
        map.put("wallet", "0.0");
        map.put("mobile", "");
        map.put("activation", "No");

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        DocumentReference admRef = FirebaseFirestore.getInstance().collection("App_Utils").document("App_Utils");
                        admRef.addSnapshotListener((value, error) -> {
                            assert value != null;
                            String price = value.getString("captcha_price");
                            map.put("captcha_price", price);
                            // Sign in success, update UI with the signed-in user's information
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
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

}