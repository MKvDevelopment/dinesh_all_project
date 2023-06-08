package com.spin.wheelgame.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.spin.wheelgame.R;
import com.spin.wheelgame.utils.NetworkChangeReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthActivity extends AppCompatActivity {

    //refer code generate code
    private String userName, userUid, userFamilyName;

    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient signInClient;
    private SignInButton signInButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    public ProgressDialog progressDialog;
    private String email;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // FOR FULLSCREEN
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        //for progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        //for sign in button
        signInButton = findViewById(R.id.google);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //database
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //requesting for email
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
                System.out.println("ApiERROR" + e.toString());
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {         //for google sing in


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        email = account.getEmail();
        userUid = account.getId();
        userFamilyName = account.getFamilyName();
        userName = account.getDisplayName();
        final String image = account.getPhotoUrl().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String newDate = sdf.format(new Date());

        final Map<String, Object> map = new HashMap<>();

        map.put("email", email);
        map.put("name", userName);
        map.put("image", image);
        map.put("deposit_balence", "10");
        map.put("winning_balence", "0");
        map.put("index", "0");
        map.put("row", "1");
        map.put("Activation", "NO");
        map.put("date", newDate);
        map.put("user_rate_count", "0");

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
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
                                                startActivity(new Intent(AuthActivity.this, Spin2Activity.class));
                                                finish();

                                            }  else if (task.getResult().isEmpty()) {

                                                documentReference = db.collection("users").document(firebaseAuth.getCurrentUser().getEmail());
                                                documentReference
                                                        .set(map)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                progressDialog.dismiss();
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(getApplicationContext(), "Account created successful", Toast.LENGTH_SHORT).show();
                                                                    startActivity(new Intent(AuthActivity.this, Spin2Activity.class));
                                                                    finish();
                                                                }
                                                            }

                                                        }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(AuthActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }
                                    });


                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(AuthActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

}
