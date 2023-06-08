package com.document.security;

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

import com.document.dbsecurity.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.document.security.utils.NetworkChangeReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthActivity extends AppCompatActivity {

    private final char[] ALPHANUMERIC = ("0123456789" + "abcdefghijklmnopqrstuvwxyz" + "ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();


    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient signInClient;
    private FirebaseAuth firebaseAuth;
    private DocumentReference documentReference;
    public ProgressDialog progressDialog;
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

        //for sign in button
        SignInButton signInButton = findViewById(R.id.google);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //database
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //sign in
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);
        signInClient.revokeAccess();
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
                System.out.println("ApiERROR" + e.toString());
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {         //for google sing in


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        //refer code generate code

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information

                        final String previusEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        FirebaseDatabase.getInstance().getReference("Approved_users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                int i=0;
                                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                                    if(previusEmail.equals(dataSnapshot.getValue().toString())){
                                        ++i;
                                    }
                                }

                                if(i>0){
                                    startActivity(new Intent(AuthActivity.this, PinActivity.class));
                                    // HomeActivity.class is the activity to go after showing the splash screen.
                                    finish();
                                }else {
                                    progressDialog.dismiss();
                                    FirebaseAuth.getInstance().signOut();
                                    Toast.makeText(AuthActivity.this, "This Mail Is Not Approved", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

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
