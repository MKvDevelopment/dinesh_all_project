package com.earnmoney.joinwithus.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.earnmoney.joinwithus.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private WebView webView, webView2;
    private int txnRequestCode = 110;
    public static String reciverUid, reciverName, reciverStatus, reciverImage, reciverToken;
    private DrawerLayout drawerLayout;
    private FirebaseDatabase reciverRef;
    private NavigationView navigationView;
    private String watspLink, watspfee, appLink, youtubeLink, join_fee, total_income, url, chat_status, appOnOff;
    private String amount, name, email, join_status, photo, subscribe, whatsp, banner_Image, channel_link, mid_image;
    private ImageView image1, image2, mid_img, profileImg;
    private Button join_btn, chat_btn, rs400Dis_btn, direct_join_btn;
    private CollectionReference admin;
    private DocumentReference cardview1, cardview2, appUtils, userRef;
    private ProgressDialog progressDialog;
    private AlertDialog customDialog;
    private EditText ed_name;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        webView = findViewById(R.id.webview);
        webView2 = findViewById(R.id.webview2);
        image1 = findViewById(R.id.image_view2);
        mid_img = findViewById(R.id.image_view3);
        image2 = findViewById(R.id.image_btn);
        join_btn = findViewById(R.id.joinUsbtn);
        chat_btn = findViewById(R.id.chat_btn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please wait!");
        progressDialog.setMessage("Fetching Data...");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_aboutus);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                , drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navigation_view);
        // navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        admin = FirebaseFirestore.getInstance().collection("App_Utils");
        userRef = FirebaseFirestore.getInstance().collection("users")
                .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()));
        reciverRef = FirebaseDatabase.getInstance();

        cardview1 = admin.document("card_view_1_text");
        cardview1.addSnapshotListener((value, error) -> {
            String data = value.getString("txt");
            webView.loadData(data, "text/html", null);
        });
        cardview2 = admin.document("card_view_2_text");
        cardview2.addSnapshotListener((value, error) -> {
            String data = value.getString("txt");
            webView2.loadData(data, "text/html", null);
        });
        image1.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(youtubeLink));
            startActivity(intent);
        });

        image2.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(youtubeLink));
            startActivity(intent);
        });

        chat_btn.setOnClickListener(v -> {

            checkDatabaseForNewCoustomer();
        });
        join_btn.setOnClickListener(v -> {

            String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            if (email.contains("play")) {
                showToast();
            } else if (subscribe.equals("NO") || join_status.equals("NO")) {
                askOfferFromUser();
            } else {
                Toast.makeText(this, "You have already joined. Please be patient", Toast.LENGTH_SHORT).show();
            }
        });

        InAppReview();
    }

    private void checkDatabaseForNewCoustomer() {

        if (chat_status.equals("block")) {
            Toast.makeText(this, "Sorry, You are blocked by our Executive.", Toast.LENGTH_LONG).show();
        } else {
            progressDialog.setMessage("Fetching Data...");
            progressDialog.show();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("Users").child(FirebaseAuth.getInstance().getUid())
                    .get()
                    .addOnSuccessListener(dataSnapshot -> {
                        if (dataSnapshot.exists()) {
                            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                            intent.putExtra("id", reciverUid);
                            intent.putExtra("token", reciverToken);
                            startActivity(intent);
                            progressDialog.dismiss();
                        } else {
                            takeChatUserDetail();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    private void takeChatUserDetail() {

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.chat_user_detail, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Enter Detail");
        profileImg = view.findViewById(R.id.imageView3);
        ed_name = view.findViewById(R.id.ed_name);

        customDialog = builder.create();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        profileImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 101);
        });

        view.findViewById(R.id.btn_cancel).setOnClickListener(v -> {
            customDialog.dismiss();
        });
        view.findViewById(R.id.btn_submit).setOnClickListener(v -> {

            progressDialog.setMessage("Uploading your data...");

            String name = ed_name.getText().toString();
//            Toast.makeText(this, uri.toString(), Toast.LENGTH_SHORT).show();
            //String urii=uri.toString();
            if (name.equals("")) {
                Toast.makeText(this, "Enter your name", Toast.LENGTH_SHORT).show();
            } else if (uri == null) {
                Toast.makeText(this, "Upload your photo", Toast.LENGTH_SHORT).show();
            } else {
                customDialog.dismiss();
                progressDialog.show();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
                StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                        .child("user_image/" + FirebaseAuth.getInstance().getUid());
                storageReference.putFile(uri).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                            Date date = new Date();
                            Map<String, String> map = new HashMap<>();
                            map.put("img", String.valueOf(uri1));
                            map.put("name", ed_name.getText().toString());
                            map.put("token", FirebaseInstanceId.getInstance().getToken());
                            map.put("status", "online");
                            map.put("email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            map.put("seen", "yes");
                            map.put("date", String.valueOf(date.getTime()));
                            map.put("last_msg", "Last Msg");
                            map.put("uid", FirebaseAuth.getInstance().getUid());

                            databaseReference.setValue(map).addOnCompleteListener(task1 -> {
                                progressDialog.dismiss();
                                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                                intent.putExtra("id", reciverUid);
                                startActivity(intent);
                                Toast.makeText(this, "Upload successfully", Toast.LENGTH_SHORT).show();
                            });
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(snapshot -> {
                    double uplods = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setProgress((int) uplods);
                    progressDialog.setMessage("Uploaded: " + uplods + " %");
                    progressDialog.show();
                });
            }
        });

    }

    private void postDataUsingVolley(String amount, String toast_text, int no) {
        progressDialog.setMessage("Fetching Info...");
        progressDialog.show();
        String order_id = UUID.randomUUID().toString(); // It should be unique
        // int amount = 10;
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(url)
                .appendQueryParameter("ORDER_ID", order_id)
                .appendQueryParameter("CUST_ID", FirebaseAuth.getInstance().getUid())
                .appendQueryParameter("TXN_AMOUNT", amount);

        String myUrl = builder.build().toString();

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, myUrl, response -> {
            progressDialog.dismiss();
            try {
                JSONObject res = new JSONObject(response);
                String txnToken = res.getJSONObject("body").getString("txnToken");
                processTxn(order_id, txnToken, amount, toast_text, no);
            } catch (JSONException e) {
                progressDialog.dismiss();

                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error ->
                Toast.makeText(MainActivity.this, "Fail to get response = " + error, Toast.LENGTH_SHORT).show());
        queue.add(request);
    }

    private void processTxn(String orderid, String txnToken, String amount, String toast, int no) {
        String mid = "Dinesh18399201056005";

        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderid; // Production Environment:
        PaytmOrder paytmOrder = new PaytmOrder(orderid, mid, txnToken, amount, callbackurl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback() {
            @Override
            public void onTransactionResponse(@Nullable Bundle bundle) {
                if (bundle.getString("STATUS").compareTo("TXN_SUCCESS") == 0) {
                    Toast.makeText(MainActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                    progressDialog.show();

                    new Handler().postDelayed(() -> {
                        progressDialog.dismiss();
                        addAdminIncomeValue(amount);
                        if (no == 1) {
                            enterDetailsFromUser();
                            addUserData(toast);
                        } else {
                            userRef.update("whatsapp", "YES").addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, toast, Toast.LENGTH_LONG).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Data Updating Error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }, 3000);
                } else {
                    Toast.makeText(MainActivity.this, "Payment Cancelled by you !", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkNotAvailable() {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorProceed(String s) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void clientAuthenticationFailed(String s) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Authentication Failed!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void someUIErrorOccurred(String s) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Ui Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "WebPage Loding Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Check your Internet Connection!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Transaction Cancel!", Toast.LENGTH_SHORT).show();
            }
        });

        transactionManager.setAppInvokeEnabled(false);
        transactionManager.startTransaction(this, txnRequestCode);
    }

    private void enterDetailsFromUser() {

        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.take_data_from_coustomer, null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        builder.setTitle("Submit Form!");

        TextView tv_name, tv_email, tv_mobile, tv_pincode, tv_address, tv_state, tv_city;
        Button btn_submit;
        tv_name = view.findViewById(R.id.editTextTextPersonName);
        tv_email = view.findViewById(R.id.editTextTextPersonName3);
        tv_mobile = view.findViewById(R.id.editTextTextPersonName2);
        tv_pincode = view.findViewById(R.id.editTextTextPersonName7);
        tv_address = view.findViewById(R.id.editTextTextPersonName4);
        tv_state = view.findViewById(R.id.editTextTextPersonName6);
        tv_city = view.findViewById(R.id.editTextTextPersonName5);
        btn_submit = view.findViewById(R.id.button4);


        customDialog = builder.create();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        btn_submit.setOnClickListener(v -> {
            customDialog.dismiss();
            String name = tv_name.getText().toString();
            String email = tv_email.getText().toString();
            String mob = tv_mobile.getText().toString();
            String pin = tv_pincode.getText().toString();
            String add = tv_address.getText().toString();
            String state = tv_state.getText().toString();
            String city = tv_city.getText().toString();

            if (name.isEmpty() || email.isEmpty() || mob.isEmpty() || pin.isEmpty() || add.isEmpty() || state.isEmpty() || city.isEmpty()) {
                Toast.makeText(this, "All Field Required", Toast.LENGTH_LONG).show();
            } else {
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("joined_user").document(email);
                Map map = new HashMap();
                map.put("name", name);
                map.put("email", email);
                map.put("mobile", mob);
                map.put("pincode", pin);
                map.put("date", Calendar.getInstance().getTime());
                map.put("add", add);
                map.put("state", state);
                map.put("city", city);
                documentReference.set(map).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        AlertDialog.Builder builderr = new AlertDialog.Builder(this);
                        builderr.setTitle("Details Submited!")
                                .setMessage("Detail submitted successfully. Our Executive will contact you as soon as possible. You can also contact us directly through Whatsapp Option. Keep Paitent.")
                                .setCancelable(false)
                                .setPositiveButton("Ok", (dialog, which) -> {
                                    dialog.dismiss();
                                })
                                .show();
                    } else {
                        Toast.makeText(this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }


        });

    }

    private void addUserData(String toast) {

        Map map = new HashMap();
        map.put("whatsapp", "YES");
        map.put("join", "YES");

        userRef.update(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                progressDialog.dismiss();
                Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
            } else {
                progressDialog.dismiss();
                Toast.makeText(this, "Data Updating Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == txnRequestCode && data != null) {
            // Toast.makeText(this, data.getStringExtra("nativeSdkForMerchantMessage") + data.getStringExtra("response"), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        } else if (requestCode == 101 && data != null) {
            uri = data.getData();
            Picasso.get().load(uri).into(profileImg);
            //   profileImg.setImageURI(uri);
        }
    }

    private void askOfferFromUser() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.offe_layout, null);

        rs400Dis_btn = view.findViewById(R.id.button2);
        direct_join_btn = view.findViewById(R.id.button3);
        direct_join_btn.setText("Direct join with Rs. " + amount);

        if (subscribe.equals("YES")) {
            rs400Dis_btn.setEnabled(false);
            rs400Dis_btn.setText("Applied discount of Rs 400");
        }

        builder.setView(view);
        AlertDialog customDialog = builder.create();
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.show();

        rs400Dis_btn.setOnClickListener(v -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Subscribe Us!");
            alertDialog.setMessage("To get a 400 Rs discount you have to subscribe our channel on youtube.");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Subscribe now", (dialog, which) -> {
                dialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(channel_link));
                startActivity(intent);
                customDialog.dismiss();
                new Handler().postDelayed(() ->
                                addMoney("400", "subscribe"),
                        3000);
            }).setNegativeButton("Later", (dialog, which) -> {
                dialog.dismiss();
            });
            alertDialog.show();
        });

        direct_join_btn.setOnClickListener(v -> {
            customDialog.dismiss();
            postDataUsingVolley(amount, "Enter Your Details Now!", 1);
        });

    }

    private void addAdminIncomeValue(String money) {
        int total = Integer.parseInt(money) + Integer.parseInt(total_income);
        appUtils.update("total_income", String.valueOf(total));
    }

    private void addMoney(String money, String field) {

        int balance = Integer.parseInt(amount) - Integer.parseInt(money);

        Map map = new HashMap<>();
        map.put("amount", String.valueOf(balance));
        map.put(field, "YES");
        userRef.update(map);

    }

    private void showToast() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Join Us!")
                .setMessage("If you want to join with us, Contact on live chat to connect our team.")
                .setCancelable(false)
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Live Chat", (dialog, which) -> {
                    checkDatabaseForNewCoustomer();
                    dialog.dismiss();
                })
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        progressDialog.show();
        userRef.addSnapshotListener((value, error) -> {
            amount = value.getString("amount");
            name = value.getString("name");
            email = value.getString("email");
            photo = value.getString("image");
            subscribe = value.getString("subscribe");
            whatsp = value.getString("whatsapp");
            join_status = value.getString("join");
            chat_status = value.getString("chat");

            appUtils = admin.document("app_utils");
            appUtils.addSnapshotListener((value1, error1) -> {
                watspfee = value1.getString("whatsp_fee");
                watspLink = value1.getString("whatsp_link");
                appLink = value1.getString("app_link");
                youtubeLink = value1.getString("youtube_link");
                join_fee = value1.getString("join_fee");
                total_income = value1.getString("total_income");
                url = value1.getString("paytm_url");
                mid_image = value1.getString("mid_image");
                banner_Image = value1.getString("banner_image");
                channel_link = value1.getString("Chanel_link");
                appOnOff = value1.getString("appOnOff");
                setHeaderData(email, photo, name);
                //   Picasso.get().load(Uri.parse(mid_image)).fit().placeholder(R.drawable.web_pik).into(mid_img);
                // Picasso.get().load(Uri.parse(banner_Image)).fit().placeholder(R.drawable.banner).into(image1);

                reciverRef.getReference().child("admin")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NotNull DataSnapshot snapshot) {
                                //  reciverImage = snapshot.child("image").getValue().toString();
                                reciverName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                                 reciverUid = snapshot.child("uid").getValue().toString();
                                reciverStatus = Objects.requireNonNull(snapshot.child("online").getValue()).toString();
                                  reciverToken = snapshot.child("token").getValue().toString();
                            }

                            @Override
                            public void onCancelled(@NotNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            });

        });
    }

    private void InAppReview() {
        ReviewManager manager = ReviewManagerFactory.create(this);

        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(MainActivity.this, reviewInfo);
                flow.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void result) {

                    }
                });
            } else {
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_about_us:
                Intent intent2 = new Intent(MainActivity.this, ContentActivity.class);
                intent2.putExtra("name", "About Us");
                intent2.putExtra("file_name", "about");
                startActivity(intent2);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_chat:
                checkDatabaseForNewCoustomer();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_how_to_join:
                Intent intent3 = new Intent(MainActivity.this, ContentActivity.class);
                intent3.putExtra("name", "How to Join");
                intent3.putExtra("file_name", "join");
                startActivity(intent3);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_whatsp:
                whatspdialog();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.menu_terms:
                Intent intent1 = new Intent(MainActivity.this, ContentActivity.class);
                intent1.putExtra("name", "Terms and Conditions");
                intent1.putExtra("file_name", "terms");
                startActivity(intent1);
                break;

            case R.id.menu_privacy:
                Intent intent4 = new Intent(MainActivity.this, ContentActivity.class);
                intent4.putExtra("name", "Privacy Policy");
                intent4.putExtra("file_name", "privacy");
                startActivity(intent4);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;


            case R.id.menu_logout:
                finish();
                break;
        }
        return true;
    }

    private void whatspdialog() {
        if (email.contains("play")) {
            Toast.makeText(this, "This feature will coming soon ...", Toast.LENGTH_SHORT).show();
        } else if (whatsp.equals("NO")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Whatsapp Support Fee!")
                    .setMessage("If till now you do not join with us and if you want to contact us on Whatsapp before joining you need to pay 100 rs. Otherwise, you can join us and contact us on Whatsapp without paying any fees. After joining, our expert will be connecting you as soon as possible.")
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton("Pay Fee", (dialog, which) -> {
                        postDataUsingVolley(watspfee, "Now You Can Chat Our Team.", 2);
                    })
                    .setCancelable(false)
                    .show();
        } else {
            Intent intents = new Intent(Intent.ACTION_VIEW);
            intents.setData(Uri.parse(watspLink));
            startActivity(intents);
        }

    }

    //setting navigation header data
    private void setHeaderData(String eml, String img, String nm) {
        ImageView headerImage;
        TextView headerEmail, headerName;

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View view = navigationView.getHeaderView(0);

        headerName = view.findViewById(R.id.nav_user_name);
        headerEmail = view.findViewById(R.id.nav_user_email);
        headerImage = view.findViewById(R.id.nav_image);

        headerName.setText(nm);
        headerEmail.setText(eml);
        Picasso.get().load(img).placeholder(R.drawable.ic_launcher_foreground).into(headerImage);

        progressDialog.dismiss();

    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Exit");
        builder.setMessage("Are you sure you want to close the application?");
        builder.setPositiveButton("YES", (dialog, id) -> {
            finish();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            //super.onBackPressed();
        });
        builder.setNegativeButton("NO", (dialog, id) ->
                dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
