package com.socialmediasaver.status.activity;

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;
import static com.socialmediasaver.status.util.Utils.InAppSubscription;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.adapter.SubscriptionListAdapter;
import com.socialmediasaver.status.interfaces.OnSubscriptionUpdated;
import com.socialmediasaver.status.util.Security;
import com.socialmediasaver.status.util.SharePrefs;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InAppPurchaseExampleActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    public static final String PREF_FILE = "MyPref";
    public static final String SUBSCRIBE_KEY = "subscribe";
    public static final String ITEM_SKU_SUBSCRIBE = "socialmediasaveradfree6_month";
    private TextView premiumContent, subscriptionStatus;
    private Button subscribe;
    private BillingClient billingClient;
    private RecyclerView rv;
    private BillingFlowParams flowParams;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient signInClient;
    private SignInButton signInButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore db;
    private DocumentReference documentReference;
    private String email;
    private boolean mobile;
    private GoogleSignInAccount account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.in_app_purchase_example);

        Toolbar toolbar = findViewById(R.id.toolbar_remove_ads);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        signInButton = findViewById(R.id.google);
        premiumContent = (TextView) findViewById(R.id.premium_content);
        subscriptionStatus = (TextView) findViewById(R.id.subscription_status);
        subscribe = (Button) findViewById(R.id.subscribe);
        rv = (RecyclerView) findViewById(R.id.rv);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        if (FirebaseAuth.getInstance().getUid() != null) {
            email = firebaseAuth.getCurrentUser().getEmail();
            CollectionReference uplodRef = FirebaseFirestore.getInstance().collection("User_upgrade_list");
            uplodRef.whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (!task.getResult().isEmpty()) {
                            mobile = true;
                            Toast.makeText(this, "Your request has been already Uploaded!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
//sign in
        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("613933141195-1n5sbpegp2hrkheb031e8263gh1ht4jn.apps.googleusercontent.com")
                .requestEmail()
                .build();
        signInClient = GoogleSignIn.getClient(this, signInOptions);

        //event
        signInButton.setOnClickListener(v -> {
            progressDialog.show();
            Intent intent = signInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });

        // Establish connection to billing client
        //check subscription status from google play store cache
        //to check if item is already Subscribed or subscription is not renewed and cancelled
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(SUBS);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if (queryPurchases != null && queryPurchases.size() > 0) {
                        handlePurchases(queryPurchases);
                    }
                    //if no item in purchase list means subscription is not subscribed
                    //Or subscription is cancelled and not renewed for next month
                    // so update pref in both cases
                    // so next time on app launch our premium content will be locked again
                    else {
                        SharePrefs.getInstance(InAppPurchaseExampleActivity.this).saveSubscribeValueToPref(false);


                        InAppSubscription = SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getSubscribeValueFromPref();

                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
        initialize();
        //item subscribed
//        if(SharePrefs.getInstance(this).getSubscribeValueFromPref()){
//            subscribe.setVisibility(View.GONE);
//            premiumContent.setVisibility(View.VISIBLE);
//            subscriptionStatus.setText("Subscription Status : Subscribed");
//        }
////item not subscribed
//        else{
//            premiumContent.setVisibility(View.GONE);
//            subscribe.setVisibility(View.VISIBLE);
//            subscriptionStatus.setText("Subscription Status : Not Subscribed");
//        }

        if (SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getLoginValueFromPref()) {

            //subscribe.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            if (SharePrefs.getInstance(this).getSubscribeValueFromPref()) {
                subscribe.setVisibility(View.GONE);
                premiumContent.setVisibility(View.VISIBLE);
                subscriptionStatus.setText("Subscription Status : Subscribed");
            }
//item not subscribed
            else {
                premiumContent.setVisibility(View.GONE);
                subscribe.setVisibility(View.VISIBLE);
                subscriptionStatus.setText("Subscription Status : Not Subscribed");
            }

        } else {
            //subscribe.setVisibility(View.VISIBLE);
            subscribe.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
            //signInButton.setVisibility(View.GONE);
        }
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
                account = task.getResult(ApiException.class);
                //Toast.makeText(InAppPurchaseExampleActivity.this, account.getEmail()+"", Toast.LENGTH_SHORT).show();
                SharePrefs.getInstance(InAppPurchaseExampleActivity.this).saveLoginValueToPref(true);
                progressDialog.dismiss();
                signInButton.setVisibility(View.GONE);
                subscribe.setVisibility(View.VISIBLE);

                //  firebaseAuthWithGoogle(account);
                // Google Sign In failed, update UI appropriately

            } catch (ApiException e) {
                progressDialog.dismiss();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

//        public static void main(String... args) throws Exception {
//            // TODO(developer): Replace these variables before running the sample.
//            String projectId = "your-project-id";
//
//            listSubscriptionInProjectExample(projectId);
//        }
//
//        public static void listSubscriptionInProjectExample(String projectId) throws IOException {
//            try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
//                ProjectName projectName = ProjectName.of(projectId);
//                for (Subscription subscription :
//                        subscriptionAdminClient.listSubscriptions(projectName).iterateAll()) {
//                    System.out.println(subscription.getName());
//                }
//                System.out.println("Listed all the subscriptions in the project.");
//            }
//        }

    //    private SharedPreferences getPreferenceObject() {
//        return getApplicationContext().getSharedPreferences(PREF_FILE, 0);
//    }
//    private SharedPreferences.Editor getPreferenceEditObject() {
//        SharedPreferences pref = getApplicationContext().getSharedPreferences(PREF_FILE, 0);
//        return pref.edit();
//    }
//    private boolean getSubscribeValueFromPref(){
//        return getPreferenceObject().getBoolean( SUBSCRIBE_KEY,false);
//    }
//    private void saveSubscribeValueToPref(boolean value){
//        getPreferenceEditObject().putBoolean(SUBSCRIBE_KEY,value).commit();
//    }
    //initiate purchase on button click
    //  public void subscribe(View view) {
    public void initialize() {

//check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase();
        }
//else reconnect service
        else {
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBillingServiceDisconnected() {
                    Toast.makeText(getApplicationContext(), "Service Disconnected ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void initiatePurchase() {
        List<String> skuList = new ArrayList<>();
        skuList.add("socialmediasaveradfree1_month");
        skuList.add("socialmediasaveradfree3_month");
        skuList.add("socialmediasaveradfree");
        skuList.add(ITEM_SKU_SUBSCRIBE);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(SUBS);

        BillingResult billingResult = billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS);
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
            billingClient.querySkuDetailsAsync(params.build(),
                    new SkuDetailsResponseListener() {
                        @Override
                        public void onSkuDetailsResponse(BillingResult billingResult,
                                                         List<SkuDetails> skuDetailsList) {
                            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                if (skuDetailsList != null && skuDetailsList.size() > 0) {

//                                    BillingFlowParams flowParams = BillingFlowParams.newBuilder()
//                                            .setSkuDetails(skuDetailsList.get(0))
//                                            .build();
                                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(InAppPurchaseExampleActivity.this, 1);
                                    rv.setLayoutManager(layoutManager);
                                    SubscriptionListAdapter adapter = new SubscriptionListAdapter(InAppPurchaseExampleActivity.this, skuDetailsList, new OnSubscriptionUpdated() {
                                        @Override
                                        public void onSubscribe(int position) {
                                            flowParams = BillingFlowParams.newBuilder()
                                                    .setSkuDetails(skuDetailsList.get(position))
                                                    .build();
                                            //  Toast.makeText(InAppPurchaseExampleActivity.this, skuDetailsList.get(position).getPrice()+"", Toast.LENGTH_SHORT).show();
                                            subscribe.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Log.i("SUBSCRIPTION", skuDetailsList.get(position).getPrice());
                                                    billingClient.launchBillingFlow(InAppPurchaseExampleActivity.this, flowParams);

                                                }
                                            });
                                        }
                                    });
                                    rv.setAdapter(adapter);
                                    // billingClient.launchBillingFlow(InAppPurchaseExampleActivity.this, flowParams);
                                } else {
//try to add subscription item "sub_example" in google play console
                                    Toast.makeText(getApplicationContext(), "Item not Found", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        " Error " + billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry Subscription not Supported. Please Update Play Store", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
//if item subscribed
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
//if item already subscribed then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(SUBS);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if (alreadyPurchases != null) {
                handlePurchases(alreadyPurchases);
            }
        }
//if Purchase canceled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(getApplicationContext(), "Purchase Canceled", Toast.LENGTH_SHORT).show();
        }
// Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(), "Error " + billingResult.getResponseCode(), Toast.LENGTH_SHORT).show();
        }
    }

    void handlePurchases(List<Purchase> purchases) {
        for (Purchase purchase : purchases) {
//if item is purchased
            if (ITEM_SKU_SUBSCRIBE.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
// Invalid purchase
// show error to user
                    Toast.makeText(getApplicationContext(), "Error : invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
// else purchase is valid
//if item is purchased and not acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
                }
//else item is purchased and also acknowledged
                else {
// Grant entitlement to the user on item purchase
// restart activity
                    if (!SharePrefs.getInstance(this).getSubscribeValueFromPref()) {
                        SharePrefs.getInstance(this).saveSubscribeValueToPref(true);
                        firebaseAuthWithGoogle(account);
                        InAppSubscription = SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getSubscribeValueFromPref();
                        Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                        this.recreate();
                    }
                }
            }
//if purchase is pending
            else if (ITEM_SKU_SUBSCRIBE.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                Toast.makeText(getApplicationContext(),
                        "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            }
//if purchase is unknown mark false
            else if (ITEM_SKU_SUBSCRIBE.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE) {
                SharePrefs.getInstance(this).saveSubscribeValueToPref(false);
                InAppSubscription = SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getSubscribeValueFromPref();

                premiumContent.setVisibility(View.GONE);
                subscribe.setVisibility(View.VISIBLE);
                subscriptionStatus.setText("Subscription Status : Not Subscribed");
                Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }

    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
//if purchase is acknowledged
// Grant entitlement to the user. and restart activity
                SharePrefs.getInstance(InAppPurchaseExampleActivity.this).saveSubscribeValueToPref(true);
                firebaseAuthWithGoogle(account);
                InAppSubscription = SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getSubscribeValueFromPref();

                InAppPurchaseExampleActivity.this.recreate();
            }
        }
    };

    private boolean verifyValidSignature(String signedData, String signature) {
        try {
// To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAi7xPLTYO4eaOwqNxHm9qH1aXKFd+AnZLNgmWembEfoTuRaT1jx/7ek5O0sAXrYQgHS/aYzs6PfCEBTtX7Nz1nGTgItmU3+XJZLAwpkdm6ODAcghNq5+Of1koLtdlUQyvhqY0bJYv9N0Tz+877E7JjCjL45SxNla3UmKKRxKxSqhBknivwXJHGG3EzZxMmslPPQYNCW1UEeKbMIJ/uEukvxCcMRN67P+qlsxglm/SCvuJ4rT6m6zV8l1Lj5bMp0fVF6PAn4H9RsMyEVy+SitdL/TuBz0AUNSF4pJeAmpHbDUOahVMr/aMvSndgd0hTz17/owioUeoUgbXTf4gpT/NXwIDAQAB";
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingClient != null) {
            billingClient.endConnection();
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //for google sing in
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        email = account.getEmail();
        final Map<String, Object> map;
        if (SharePrefs.getInstance(InAppPurchaseExampleActivity.this).getSubscribeValueFromPref()) {
            map = new HashMap<>();
            map.put("email", email);
            map.put("subscription", "YES");
        } else {
            map = new HashMap<>();
            map.put("email", email);
            map.put("subscription", "NO");
        }

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        documentReference = db.collection("Users").document(firebaseAuth.getCurrentUser().getEmail());
                        documentReference
                                .set(map)
                                .addOnCompleteListener(task1 -> {
                                    progressDialog.dismiss();
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(InAppPurchaseExampleActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }).addOnFailureListener(e -> {
                            progressDialog.dismiss();
                            Toast.makeText(InAppPurchaseExampleActivity.this, "SignInnn Failed", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        // If sign in fails, display a message to the user.
                        progressDialog.dismiss();
                        Toast.makeText(InAppPurchaseExampleActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
