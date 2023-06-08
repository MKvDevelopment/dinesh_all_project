package com.skincarestudio.solution.Activty;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skincarestudio.solution.R;
import com.skincarestudio.solution.Utils.NetworkChangeReceiver;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ImageView course, skinCare, hairFall;
    private ProgressDialog progressDialog;
    private EditText us_name, us_mob, us_purpose;
    private AlertDialog custom_dialog;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private DocumentReference documentReference1;
    private String user_name, email, image, wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check app on/Off condition
        CollectionReference appBlockReference = FirebaseFirestore.getInstance().collection("App_Utils");
        final DocumentReference documentReference = appBlockReference.document("App_status");
        documentReference.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot != null) {
                String stauts = documentSnapshot.getString("App_is");
                String reason = documentSnapshot.getString("reason");

                assert stauts != null;
                if (stauts.contains("OFF")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Alert!");
                    alertDialog.setMessage(reason);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Exit Now", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    }).show();
                }

            }
        });

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        findView();

        setSupportActionBar(toolbar);

        //waiting dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);

        //firebase database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollMain = db.collection("users");
        documentReference1 = userCollMain.document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()));


        getDataFromServer();
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);


        course.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, ProudctActivity.class)));


        hairFall.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, HairTipActivity.class)));

        skinCare.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, SkinTipActivity.class)));

    }

    private void getDataFromServer() {
        progressDialog.setMessage("Loading....");
        progressDialog.show();

        documentReference1
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (documentSnapshot != null) {
                        user_name = documentSnapshot.getString("name");
                        email = documentSnapshot.getString("email");
                        image = documentSnapshot.getString("image");
                        wallet = documentSnapshot.getString("wallet");

                        setHeaderData(email, image, user_name);

                    } else {
                        progressDialog.dismiss();
                        assert e != null;
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @SuppressLint("SetTextI18n")
    private void setHeaderData(String email, String image, String user_name) {
        ImageView headerImage;
        TextView headerEmail, headerName;

        NavigationView navigationView = findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);

        headerName = view.findViewById(R.id.name);
        headerEmail = view.findViewById(R.id.textView);
        headerImage = view.findViewById(R.id.imageView);

        headerName.setText("Hi " + user_name + "!");
        headerEmail.setText(email);
        Picasso.get().load(image).into(headerImage);
        progressDialog.dismiss();
    }

    private void findView() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        course = findViewById(R.id.product);
        skinCare = findViewById(R.id.skin);
        hairFall = findViewById(R.id.hair_fall);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_affiliate:
            case R.id.nav_premium:
                startActivity(new Intent(MainActivity.this, ReferActivity.class));
                navigationView.getMenu().getItem(0).setChecked(false);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_my_purchage:
                startActivity(new Intent(MainActivity.this, PurchageActivity.class));
                navigationView.getMenu().getItem(0).setChecked(false);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_consult:
                setAertForConsult();
                navigationView.getMenu().getItem(0).setChecked(false);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_about_us:
                Intent intent = new Intent(MainActivity.this, AppCommonActivity.class);
                intent.putExtra("about", "About us");
                intent.putExtra("field", "about");
                startActivity(intent);
                navigationView.getMenu().getItem(1).setChecked(false);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_contect_us:
                Intent intent1 = new Intent(MainActivity.this, AppCommonActivity.class);
                intent1.putExtra("about", "Contact us");
                intent1.putExtra("field", "contact");
                startActivity(intent1);
                navigationView.getMenu().getItem(2).setChecked(false);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_our_service:
                Intent intent2 = new Intent(MainActivity.this, AppCommonActivity.class);
                intent2.putExtra("about", "Our service");
                intent2.putExtra("field", "service");
                startActivity(intent2);
                navigationView.getMenu().getItem(3).setChecked(false);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_privacy:
                Intent intent3 = new Intent(MainActivity.this, AppCommonActivity.class);
                intent3.putExtra("about", "Privacy Policy");
                intent3.putExtra("field", "privacy");
                startActivity(intent3);
                navigationView.getMenu().getItem(4).setChecked(false);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_refund:
                Intent intent4 = new Intent(MainActivity.this, AppCommonActivity.class);
                intent4.putExtra("about", "Refund and Cancellation Policy");
                intent4.putExtra("field", "refund");
                startActivity(intent4);
                navigationView.getMenu().getItem(5).setChecked(false);
                drawer.closeDrawer(GravityCompat.START);
                break;

            case R.id.nav_terms:
                Intent intent5 = new Intent(MainActivity.this, AppCommonActivity.class);
                intent5.putExtra("about", "Terms and Conditions");
                intent5.putExtra("field", "terms");
                startActivity(intent5);
                navigationView.getMenu().getItem(6).setChecked(false);
                drawer.closeDrawer(GravityCompat.START);
                break;

        }
        return true;
    }

    private void setAertForConsult() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Book appointment");
        alertDialog.setMessage("For book telephonic appointment and consulting with us pay 200 Rs and submit your details. After book appointments as soon as possible our executive contact you!");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("Continue", (dialog, which) -> {
            dialog.dismiss();

            if (Float.parseFloat(wallet) < 200.0) {
                Toast.makeText(getApplicationContext(), "Insufficient Balance in Wallet", Toast.LENGTH_SHORT).show();
            } else {
                float total = Float.parseFloat(wallet) - 200;
                documentReference1.update("wallet", String.valueOf(total));

                Toast.makeText(MainActivity.this, "Tranction successful", Toast.LENGTH_SHORT).show();
                progressDialog.setMessage("Updating your Request...");
                progressDialog.show();
                new Handler().postDelayed(() -> {
                    progressDialog.dismiss();
                    AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                    alertDialog1.setTitle("Thanks Dear!");
                    alertDialog1.setMessage("Payment  successful. Now you have to fill some details to contect your executive.");
                    alertDialog1.setCancelable(false);
                    alertDialog1.setPositiveButton("Fill Now", (dialog1, which1) -> getUserDetail()).show();
                }, 3000);

            }


        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss()).show();

    }

    private void getUserDetail() {

        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.consult_dialog, null);

        us_name = view.findViewById(R.id.toast_name);
        us_mob = view.findViewById(R.id.toast_mob);
        us_purpose = view.findViewById(R.id.toast_purpose);

        Button btn_cancel = view.findViewById(R.id.toast_cancl);
        Button btn_submit = view.findViewById(R.id.toast_sbmit);
        alert.setView(view);

        custom_dialog = alert.create();
        custom_dialog.setCanceledOnTouchOutside(false);
        custom_dialog.show();

        btn_cancel.setOnClickListener(v -> custom_dialog.dismiss());

        btn_submit.setOnClickListener(v -> {
            if (us_name.getText().toString().isEmpty()) {
                us_name.setError("Please Enter Your Name");
            } else if (us_mob.getText().toString().isEmpty()) {
                us_mob.setError("Please Enter Your Mobile Number");
            } else if (us_purpose.getText().toString().isEmpty()) {
                us_purpose.setError("Please enter your calling purpose");
            } else {
                progressDialog.setTitle("Please Wait!");
                progressDialog.setMessage("Upload your Request");
                progressDialog.show();
                custom_dialog.dismiss();
                settingDataBase();

            }
        });

    }

    private void settingDataBase() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        final String date = dateFormat.format(new Date());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("appointment_list").child(date);

        Map<String, Object> map1 = new HashMap<>();
        map1.put("mobile", us_mob.getText().toString());
        map1.put("time", date);
        map1.put("name", user_name);
        map1.put("user_time", us_purpose.getText().toString());

        databaseReference
                .push()
                .setValue(map1)
                .addOnCompleteListener(task -> {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setTitle("Thanks Dear!");
                        alertDialog.setMessage("We received your request Successfully.We will be contact you as per your given time.");
                        alertDialog.setCancelable(false);
                        alertDialog.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss()).show();
                        Toast.makeText(MainActivity.this, "Request sent successfully.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

}