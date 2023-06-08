package com.skincarestudio.solution.Activty;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.skincarestudio.solution.R;
import com.skincarestudio.solution.Utils.NetworkChangeReceiver;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ProductDetailActivity extends AppCompatActivity {
    private ProgressDialog dialog;
    private String img;
    private String title;
    private String price;
    private String sbtitle;
    private String link;
    private String referBy, user_wallet;
    private String friend_email;
    private String friend_wallet;
    private String admin_Total_amount;
    private CollectionReference reference;
    private DocumentReference adminRef;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        Toolbar toolbar = findViewById(R.id.productDetail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Detail :-");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        Button buyNow = findViewById(R.id.btn_byNow);
        ImageView productImage = findViewById(R.id.product_image);
        TextView productTitle = findViewById(R.id.product_title);
        TextView productPrice = findViewById(R.id.product_price);
        WebView product_description = findViewById(R.id.webView3);

        adminRef = FirebaseFirestore.getInstance().collection("App_Utils")
                .document("Total_amount");
        adminRef.addSnapshotListener((value, error) -> {
            assert value != null;
            admin_Total_amount = value.getString("Rupees");
        });


        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users")
                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()));
        documentReference.addSnapshotListener((value, error) -> {
            assert value != null;
            referBy = value.getString("refered_by");
            user_wallet = value.getString("wallet");
            assert referBy != null;
            if (referBy.equals("empty")) {
               // Toast.makeText(ProductDetailActivity.this, "You have not Friends Refer. If you want to get disccount please enter friends refer code.", Toast.LENGTH_LONG).show();
            } else {
                reference = FirebaseFirestore.getInstance().collection("users");
                reference
                        .whereEqualTo("refer_code", referBy)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (!task.getResult().isEmpty()) {
                                dialog.dismiss();
                                for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                    friend_email = snapshot.getString("email");
                                    friend_wallet = snapshot.getString("wallet");
                                }
                            }
                        }).addOnFailureListener(e -> {
                    dialog.dismiss();
                    Toast.makeText(ProductDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });

        //progress dialog
        dialog = new ProgressDialog(this);
        dialog.setTitle("Please wait!");
        dialog.setMessage("loading...");

        Bundle bundle = getIntent().getExtras();
        title = bundle.getString("title");
        price = bundle.getString("price");
        String des = bundle.getString("des");
        img = bundle.getString("img");
        sbtitle = bundle.getString("sbtitle");
        link = bundle.getString("link");

        Picasso.get().load(img).into(productImage);
        product_description.loadData(des, "text/html", null);
        productTitle.setText(title);
        productPrice.setText("Price:- " + price + " Rs.");

        buyNow.setOnClickListener(view -> {
            if (Float.parseFloat(user_wallet) < Float.parseFloat(price)) {
                Toast.makeText(getApplicationContext(), "Insufficient Balance in wallet!", Toast.LENGTH_SHORT).show();
            } else {
                float total = Float.parseFloat(user_wallet) - Float.parseFloat(price);
                documentReference.update("wallet", String.valueOf(total));

                dialog.setMessage("Updating your Request...");
                dialog.show();
                new Handler().postDelayed(() -> {
                    dialog.dismiss();

                    sendDetails();
                }, 3000);
            }
        });

    }


    void sendDetails() {
        Toast.makeText(ProductDetailActivity.this, "Tranction successful", Toast.LENGTH_SHORT).show();
        dialog.setMessage("Updating your Product...");
        dialog.show();
        new Handler().postDelayed(() -> {
            dialog.dismiss();

            createPurchedList();
            sendMoneyInFriendWallet(price);

            int total = Integer.parseInt(admin_Total_amount) + Integer.parseInt(price);
            adminRef.update("Rupees", String.valueOf(total));

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductDetailActivity.this);
            alertDialog.setTitle("Thanks Dear!");
            alertDialog.setMessage("Course Buy successful. Now you can download Course from My Purchased Option.");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Download Now", (dialog, which) -> {
                startActivity(new Intent(ProductDetailActivity.this, PurchageActivity.class));
                finish();
            }).show();
        }, 3000);

    }

    private void createPurchedList() {

        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        //upgrade plan details
        assert email != null;
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Common_data")
                .document(email).collection("product").document();
        Map<String, Object> map = new HashMap<>();
        map.put("product_img", img);
        map.put("link", link);
        map.put("sb_title", sbtitle);
        map.put("product_title", title);
        documentReference.set(map);

    }

    private void sendMoneyInFriendWallet(String price) {

        if (!referBy.equals("empty")) {
            int pricee = Integer.parseInt(price);
            double per = (double) pricee / 100 * 30;

            Double money = per + Double.parseDouble(friend_wallet);
            DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("users").document(friend_email);
            documentReference1.update("wallet", String.valueOf(money));

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }
}
