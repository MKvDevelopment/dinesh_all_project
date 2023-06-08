package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.startapp.sdk.adsbase.StartAppAd;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.R;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    //refer code generate code
    private String userName, userUid, userFamilyName, count;

    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient signInClient;
    private SignInButton signInButton;
    private FirebaseAuth firebaseAuth;
    public static FirebaseFirestore db;
    private DocumentReference documentReference;
    public ProgressDialog progressDialog;
    private NetworkChangeReceiver broadcastReceiver;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppAd.disableSplash();
        setContentView(R.layout.activity_login);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //for progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        //for sign in button
        signInButton = findViewById(R.id.google);
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

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                progressDialog.dismiss();
                System.out.println("hiiiii   " + e.getMessage());
                Toast.makeText(this, e+"", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {         //for google sing in


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        final String email = account.getEmail();
        userName = account.getDisplayName();
        userFamilyName = account.getFamilyName();
        userUid = account.getId();
        final String image = account.getPhotoUrl().toString();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        DocumentReference document = FirebaseFirestore.getInstance().collection("App_utils").document("App_status");
                        document.addSnapshotListener((documentSnapshot, e) -> {
                            String ca = documentSnapshot.getString("withdraw_count");
                            if (ca.equals("1")) {
                                count = ca;
                                loginuser(email, image);
                            } else {
                                count = ca;
                                loginuser(email, image);
                            }
                        });


                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void loginuser(String email, String image) {

        // Sign in success, update UI with the signed-in user's information

        final String previusEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        CollectionReference previusReference = FirebaseFirestore.getInstance().collection("users");
        previusReference
                .whereEqualTo("email", previusEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (!task.getResult().isEmpty()) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "SignIn successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        } else {

                            final Map<String, Object> map = new HashMap<>();
                            map.put("email", email);
                            map.put("name", userName);
                            map.put("image", image);
                            map.put("plan", "Free Plan");
                            map.put("wallet", "0.0");
                            map.put("withdraw_count", count);
                            map.put("refer_code", generateCode());
                            map.put("refered_by", "empty");
                            map.put("user_status", "Unblock");
                            map.put("block_reason", "You are blocked by admin");
                            map.put("time", FieldValue.serverTimestamp());
                            map.put("total_refer_count", "0");
                            map.put("user_rate_count", "0");

                            documentReference = db.collection("users").document(firebaseAuth.getCurrentUser().getEmail());
                            documentReference
                                    .set(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Account created successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                finish();
                                            }
                                        }

                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });

    }

    private String generateCode() {
        String code = "";
        code += (userName.replaceAll("\\s", "").substring(0, 2));
        code += (userUid.substring(0, 2));
        code += (userFamilyName.replaceAll("\\s", "").substring(0, 1));
        code += (userUid.substring(19, 20));
        code += (userName.replaceAll("\\s", "").substring(1, 3));
        return code;
    }

}
