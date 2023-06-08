package com.typingwork.admintypingwork.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.typingwork.admintypingwork.R;
import com.typingwork.admintypingwork.Utils.Constant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    //refer code generate code
    private String userName,userUid,userFamilyName;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient signInClient;
    private SignInButton signInButton;
    private FirebaseAuth firebaseAuth;
    private String email;
    public static FirebaseFirestore db;
    private DocumentReference documentReference;
    private ProgressDialog progressDialog;
    private TextInputLayout number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait");
        Constant.showProgressDialog(progressDialog,"Please wait!","Chacking Details...");

        //for sign in button
        signInButton = findViewById(R.id.google);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);


        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //requesting for email
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);

        //event
        signInButton.setOnClickListener(v -> {
            progressDialog.show();
            signInButton.setEnabled(false);

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
                signInButton.setEnabled(true);
                System.out.println("ApiERROR" + e.toString());

                //Toast shows
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();

            }
        }

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {         //for google sing in
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        email = account.getEmail();
        userName = account.getDisplayName();
        userUid=account.getId();
        userFamilyName=account.getFamilyName();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        String uid =FirebaseAuth.getInstance().getCurrentUser().getUid();
                        // Sign in success, update UI with the signed-in user's information

                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task2 -> {
                            String deviceid = task2.getResult();

                            CollectionReference previusReference = FirebaseFirestore.getInstance().collection("Users_list");
                            previusReference
                                    .whereEqualTo("uid", uid)
                                    .get()
                                    .addOnCompleteListener(task1 -> {

                                        if (!task1.getResult().isEmpty()) {
                                            progressDialog.dismiss();
                                            signInButton.setEnabled(true);
                                            //show Toast
                                            Toast.makeText(LoginActivity.this, "SignIn Successful", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            final Map<String, Object> map = new HashMap<>();
                                            map.put("email", email);
                                            map.put("phone", "");
                                            map.put("photo", "");
                                            map.put("name", "");
                                            map.put("device_id", deviceid);
                                            map.put("form_price", "0.20");
                                            map.put("uid", uid);

                                            documentReference = db.collection("Users_list").document(uid);

                                            documentReference
                                                    .set(map)
                                                    .addOnCompleteListener(task11 -> {
                                                        progressDialog.dismiss();
                                                        signInButton.setEnabled(true);
                                                        if (task11.isSuccessful()) {

                                                            Toast.makeText(LoginActivity.this, "Register Successfully", Toast.LENGTH_SHORT).show();

                                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                            finish();
                                                        }
                                                    }).addOnFailureListener(e -> {
                                                        progressDialog.dismiss();
                                                        signInButton.setEnabled(true);

                                                        Toast.makeText(LoginActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();

                                                    });
                                        }
                                    });

                        }) ;


                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        signInButton.setEnabled(true);
                        //show Toast
                        Toast.makeText(LoginActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private String generateCode() {
        String code = "";
        code += (userName.replaceAll("\\s", "").substring(0, 2));
        code += (userUid.substring(0, 2));
        code += (userFamilyName.replaceAll("\\s", "").substring(0, 1));
        code += (userUid.substring(15, 17));
        code += (userName.replaceAll("\\s", "").substring(1, 3));
        return code;
    }


}