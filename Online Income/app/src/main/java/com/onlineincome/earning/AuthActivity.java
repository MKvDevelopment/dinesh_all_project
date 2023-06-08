package com.onlineincome.earning;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onlineincome.earning.OneTimeUtils.NetworkChangeReceiver;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    private String userName;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient signInClient;
    private SignInButton signInButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private TextView terms, policy;
    private NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
    private DocumentReference documentReference, adminRef;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        StartAppSDK.setTestAdsEnabled(false);
        StartAppAd.disableSplash();

        //broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);

        //for progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        //for sign in button
        signInButton = findViewById(R.id.google);
        terms = findViewById(R.id.textView2);
        policy = findViewById(R.id.textView3);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //database
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //sign in
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);

        //event
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                Intent intent = signInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

    }

    //get result from sign in button
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {           //for google signin
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                progressDialog.dismiss();
                Toast.makeText(this, "Authentication Failed!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {         //for google sing in

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        final String email = account.getEmail();
        // userUid = account.getId();
        // userFamilyName = account.getFamilyName();
        userName = account.getDisplayName();
        final String image = account.getPhotoUrl().toString();

        final Map<String, Object> map = new HashMap<>();

        map.put("email", email);
        map.put("name", userName);
        map.put("image", image);
        map.put("wallet", "0");
        map.put("user_rate_count", "0");
        map.put("withdraw", "NO");
        map.put("boost", "NO");
        map.put("comunity", "NO");

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        final String previusEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                        CollectionReference previusReference = FirebaseFirestore.getInstance().collection("users");
                        previusReference
                                .whereEqualTo("email", previusEmail)
                                .get()
                                .addOnCompleteListener(task12 -> {

                                    if (!task12.getResult().isEmpty()) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(), "SignIn successful", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                        finish();

                                    } else {

                                        adminRef = FirebaseFirestore.getInstance().collection("App_utils").document("app_utils");
                                        adminRef.addSnapshotListener((value, error) -> {
                                            String new_user = value.getString("new_user");
                                            String reason = value.getString("reason");

                                            if (new_user.equals("OFF")) {
                                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AuthActivity.this);
                                                alertDialog.setTitle("Sorry!");
                                                alertDialog.setMessage(reason);
                                                alertDialog.setCancelable(false);
                                                alertDialog.setPositiveButton("Exit Now", (dialog, which) -> {
                                                    dialog.dismiss();
                                                    finish();
                                                }).show();
                                            } else {
                                                documentReference = db.collection("users").document(firebaseAuth.getCurrentUser().getEmail());
                                                documentReference
                                                        .set(map)
                                                        .addOnCompleteListener(task1 -> {
                                                            progressDialog.dismiss();
                                                            if (task1.isSuccessful()) {
                                                                Toast.makeText(getApplicationContext(), "Account created successful", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(AuthActivity.this, MainActivity.class));
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(AuthActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(AuthActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();

                    }
                });
    }

}