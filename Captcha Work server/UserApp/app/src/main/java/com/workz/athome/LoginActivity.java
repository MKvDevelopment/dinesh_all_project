package com.workz.athome;

import static com.workz.athome.Utils.Utils.setUserSharedPreference;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.workz.athome.Model.UserData.Root;
import com.workz.athome.Utils.ApiClient;
import com.workz.athome.Utils.ApiService;
import com.workz.athome.Utils.Utils;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient signInClient;
    private FirebaseAuth firebaseAuth;
    public FirebaseFirestore db;
    private ProgressDialog progressDialog;
    private ActivityResultLauncher<Intent> loginResultLauncher;
    private ApiService apiServices;

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
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        //database
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        apiServices = ApiClient.getRetrofit().create(ApiService.class);


        //requesting for email
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("527754066867-iiei4v6g2roh3adsq6hhavbqj038e8qo.apps.googleusercontent.com")
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
                    }else {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Login Error", Toast.LENGTH_SHORT).show();

                    }
                });


        Button btn_getotp = findViewById(R.id.btn_getotp);
        EditText number = findViewById(R.id.ed_phone);
        btn_getotp.setOnClickListener(v -> {
            String snumber = Objects.requireNonNull(number.getText().toString());

            //validations

            if (TextUtils.isEmpty(snumber)) {
                number.setError("Please enter your Phone Number");
            } else {
                //String ads=  sharedPreferences.getString("removeAds",null);

                Utils.hideKeyboard(LoginActivity.this);

                Intent intent = new Intent(LoginActivity.this, OTPMatchActivity.class);
                intent.putExtra("number", "+91" + snumber);
                startActivity(intent);
                finish();

            }
        });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //for google sing in
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        final String email = account.getEmail();

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Call<Root> call = apiServices.loginUser(
                                email,
                                ""
                        );
                        call.enqueue(new Callback<Root>() {
                            @Override
                            public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                                Root root = response.body();
                                if (response.code() == 200) {
                                    progressDialog.dismiss();
                                    setUserSharedPreference(root,LoginActivity.this);
                                    Toast.makeText(getApplicationContext(), "loginUSer :- SignIn successful", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                    //result ok from server
                                } else {
                                    if (response.code() == 400) {
                                            // api error data fetch from firebase
                                            final String uidd = FirebaseAuth.getInstance().getUid();
                                            CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("User_List");
                                            collectionReference
                                                    .whereEqualTo("uid", uidd)
                                                    .get().addOnSuccessListener(result -> {
                                                        String remove_ads = "No";

                                                        if (!result.isEmpty()) {
                                                            //user already registered on firebase, getting data from firebase
                                                            for (DocumentSnapshot documentSnapshot : result) {

                                                                String finalActivation = documentSnapshot.getString("activation");
                                                                String finalCaptcha_price = documentSnapshot.getString("captcha_price");
                                                                String finalMobile = documentSnapshot.getString("mobile");
                                                                String finalUserName = documentSnapshot.getString("userName");
                                                                String finalWallet = documentSnapshot.getString("wallet");
                                                                String finalUserEmail = documentSnapshot.getString("email");

                                                                assert uidd != null;
                                                                FirebaseFirestore.getInstance()
                                                                        .collection("Instant Activation")
                                                                        .document(uidd)
                                                                        .addSnapshotListener((value, error) -> {
                                                                            assert value != null;
                                                                            String instantActivation;
                                                                            if (value.exists()) {
                                                                                //getting instant activation data
                                                                                instantActivation = value.getString("activation");
                                                                                // instantActivation.set(value.getString("activation"));

                                                                            } else {
                                                                                instantActivation = "NO";
                                                                                Toast.makeText(LoginActivity.this, "error.getMessage()", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            FirebaseFirestore.getInstance()
                                                                                    .collection("Promotion_user")
                                                                                    .document(uidd)
                                                                                    .addSnapshotListener((value1, error1) -> {
                                                                                        String install;
                                                                                        assert value1 != null;
                                                                                        if (value1.exists()) {
                                                                                            //getting promotional user data
                                                                                            install = value1.getString("install");

                                                                                        } else {
                                                                                            install = "NO";
                                                                                          //  Toast.makeText(LoginActivity.this, "error.getMessage()2", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                        FirebaseDatabase.getInstance().getReference().child("Spin_User").child(uidd).get()
                                                                                                .addOnSuccessListener(dataSnapshot -> {
                                                                                                    String winning_balence, index;
                                                                                                    if (dataSnapshot.exists()) {
                                                                                                        SpinModel model = dataSnapshot.getValue(SpinModel.class);
                                                                                                        assert model != null;
                                                                                                        winning_balence = model.getWinning_balence();
                                                                                                        index = model.getIndex();
                                                                                                    } else {
                                                                                                        winning_balence = "1";
                                                                                                        index = "0";
                                                                                                    }

                                                                                                    Call<Root> registerCall = apiServices.registerUser(
                                                                                                            finalActivation,
                                                                                                            finalCaptcha_price,
                                                                                                            finalUserEmail,
                                                                                                            finalMobile,
                                                                                                            uidd,
                                                                                                            finalUserName,
                                                                                                            finalWallet,
                                                                                                            instantActivation,
                                                                                                            install,
                                                                                                            index,
                                                                                                            winning_balence,
                                                                                                            remove_ads,
                                                                                                            "NO"
                                                                                                    );
                                                                                                    registerCall.enqueue(new Callback<Root>() {
                                                                                                        @Override
                                                                                                        public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {

                                                                                                            Root root1 = response.body();
                                                                                                            progressDialog.dismiss();

                                                                                                            if (response.code() == 201) {
                                                                                                                setUserSharedPreference(root1,LoginActivity.this);
                                                                                                                Toast.makeText(getApplicationContext(), "SignIn successful", Toast.LENGTH_SHORT).show();
                                                                                                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                                                                finish();
                                                                                                            } else {

                                                                                                                if (!root1.getStatus() && response.code() == 404) {
                                                                                                                    Toast.makeText(LoginActivity.this, root1.getMessage() + "/ Email Already Registered!", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            }

                                                                                                        }

                                                                                                        @Override
                                                                                                        public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                                                                                                            progressDialog.dismiss();
                                                                                                            Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                                                                                        }
                                                                                                    });

                                                                                                })
                                                                                                .addOnFailureListener(e -> {
                                                                                                    progressDialog.dismiss();
                                                                                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                });

                                                                                    });
                                                                        });
                                                            }
                                                        } else {
                                                            //user not registered on firebase, setting new data on server
                                                            Call<Root> registerCall = apiServices.registerUser(
                                                                    "NO",
                                                                    "5",
                                                                    email,
                                                                    "",
                                                                    uidd,
                                                                    account.getDisplayName(),
                                                                    "0.0",
                                                                    "NO",
                                                                    "NO",
                                                                    "0",
                                                                    "1",
                                                                    "NO",
                                                                    "NO"
                                                            );
                                                            registerCall.enqueue(new Callback<Root>() {
                                                                @Override
                                                                public void onResponse(@NonNull Call<Root> call, @NonNull Response<Root> response) {
                                                                    Root root1 = response.body();

                                                                    if (root1.getStatus() && response.code() == 201) {
                                                                        setUserSharedPreference(root1,LoginActivity.this);

                                                                        Toast.makeText(getApplicationContext(), "Register Scucessfully!", Toast.LENGTH_SHORT).show();
                                                                        progressDialog.dismiss();
                                                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                                        finish();
                                                                    } else if (!root1.getStatus()) {
                                                                        Toast.makeText(LoginActivity.this, root1.getMessage(), Toast.LENGTH_SHORT).show();
                                                                    }


                                                                }

                                                                @Override
                                                                public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                                                                    progressDialog.dismiss();
                                                                    Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                                                                }
                                                            });
                                                        }
                                                    });

                                    }
                                }

                            }


                            @Override
                            public void onFailure(@NonNull Call<Root> call, @NonNull Throwable t) {
                                Log.d("aaaaaaaaaaaa", "onResponse: error:-  " + t.getMessage());
                                Toast.makeText(LoginActivity.this, "Api Error", Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                    }
                });

    }



}