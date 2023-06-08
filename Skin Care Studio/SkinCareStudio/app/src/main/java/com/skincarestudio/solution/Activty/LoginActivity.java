package com.skincarestudio.solution.Activty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
import com.skincarestudio.solution.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    //refer code generate code
    private String userName, userUid, userFamilyName;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient signInClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //for progress bar
        progressDialog = new ProgressDialog(this);
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
                .requestIdToken("207100436114-vua4pfq8clbj4junorbnud0fl8urc303.apps.googleusercontent.com")
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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {         //for google sing in


        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        final String email = account.getEmail();
        userName = account.getDisplayName();
        userFamilyName = account.getFamilyName();
        userUid = account.getId();
        final String image = Objects.requireNonNull(account.getPhotoUrl()).toString();

        //  String count = getIntent().getStringExtra("count");


        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {
                        loginuser(email, image);

                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "SignIn Failed", Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void loginuser(final String email, final String image) {

        // Sign in success, update UI with the signed-in user's information

        final String previusEmail = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

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
                        map.put("name", userName);
                        map.put("image", image);
                        map.put("wallet", "0.0");
                        map.put("refer_code", generateCode());
                        map.put("refered_by", "empty");

                        documentReference = db.collection("users").document(Objects.requireNonNull(Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail()));
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

