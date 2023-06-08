package com.earnmoney.joinwithus.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.earnmoney.joinwithus.R;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient signInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private ProgressDialog progressDialog;
    private String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //for progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Please wait!");
        progressDialog.setMessage("Loading...");

        //for sign in button
        SignInButton signInButton = findViewById(R.id.google);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //database
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //sign in
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("138475379352-7c233pp7luecptp2sdsn0pupu1qugkmm.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);

        //event
        signInButton.setOnClickListener(v -> {
            progressDialog.show();
            Intent intent = signInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });
    }


    //get result from sign in button
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {           //for google signin
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                // Google Sign In failed, update UI appropriately

            } catch (ApiException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Login failed ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //for google sing in

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        final String email = account.getEmail();
        String uri = account.getPhotoUrl().toString();
        String name = account.getDisplayName();

        final Map<String, Object> map = new HashMap<>();
        map.put("email", email);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        loginuser(email, uri, name);
                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void loginuser(String email, String photo, String name) {
        // Sign in success, update UI with the signed-in user's information

        final String previusEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        CollectionReference previusReference = FirebaseFirestore.getInstance().collection("users");
        previusReference
                .whereEqualTo("email", previusEmail)
                .get()
                .addOnCompleteListener(task -> {

                    if (!task.getResult().isEmpty()) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "SignIn successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } else {

                        final Map<String, Object> map = new HashMap<>();
                        map.put("email", email);
                        map.put("name", name);
                        map.put("image", photo);
                        map.put("join", "NO");
                        map.put("chat", "unblock");
                        map.put("subscribe", "NO");
                        map.put("whatsapp", "NO");
                        map.put("amount", amount);
                        documentReference = db.collection("users").document(firebaseAuth.getCurrentUser().getEmail());
                        documentReference
                                .set(map)
                                .addOnCompleteListener(task1 -> {
                                    progressDialog.dismiss();
                                    if (task1.isSuccessful()) {

                                        Toast.makeText(getApplicationContext(), "Account created successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();

                                    }
                                }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(LoginActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                });

    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.show();

        DocumentReference amountRef = FirebaseFirestore.getInstance().collection("App_Utils").document("app_utils");
        amountRef.addSnapshotListener((value, error) -> {
         if (value.exists())
         {
             amount = value.getString("join_fee");
             // Toast.makeText(this, amount, Toast.LENGTH_SHORT).show();
             progressDialog.dismiss();
         }else {
             progressDialog.dismiss();
          //   Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
         }
        });
    }
}