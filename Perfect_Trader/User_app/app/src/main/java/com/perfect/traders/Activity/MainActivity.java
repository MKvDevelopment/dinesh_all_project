package com.perfect.traders.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.perfect.traders.Adatper.SliderAdapter;
import com.perfect.traders.Constant.NetworkChangeReceiver;
import com.perfect.traders.Model.LinkModel;
import com.perfect.traders.R;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DocumentReference userRef;
    private SliderView sliderView;
    private SliderAdapter sliderAdapter;
    private ProgressDialog progressDialog;
    private NetworkChangeReceiver broadcastReceiver;
    private DocumentReference adminReference;

    public static String device_id, uidd;
    private DrawerLayout drawerLayout;
    public static String USER_IMAGE, SUBSCRIPTION, SUBSCRIPTION_END, USER_PLAN, USER_CHAT, USER_COURSE, USER_TRADE_WITH_US;
    private CardView btnIntraday, btnGlobal, btnPositonal, btnUserCommunity, btnAddPromotion;
    private Uri uri;
    private String link,appOnOff,off_reason,app_version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        initViews();

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                , drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        uidd = FirebaseAuth.getInstance().getUid();

        CollectionReference adminRef = FirebaseFirestore.getInstance().collection("Main_slider_banner");
        assert uidd != null;
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(uidd);

        adminReference = FirebaseFirestore.getInstance().collection("App_Utils").document("utils");
        adminReference.addSnapshotListener((value, error) -> {
            if (value.exists()){
                link=value.getString("course_link");
                appOnOff = value.getString("appOnOff");
                off_reason = value.getString("off_reason");
                app_version = value.getString("app_version");

               // Toast.makeText(getApplicationContext(), app_version, Toast.LENGTH_SHORT).show();
                showAppOffDialog();
                checkForUpdate();
            }
        });

        userRef.addSnapshotListener((value, error) -> {
            assert value != null;
            USER_IMAGE = value.getString("image");
            SUBSCRIPTION = value.getString("subscritpion");
            SUBSCRIPTION_END = value.getString("subscritpion_end_date");
            USER_PLAN = value.getString("plan");
            USER_CHAT = value.getString("chat");
            USER_TRADE_WITH_US = value.getString("trade_with_us");
            USER_COURSE = value.getString("course");
            device_id = value.getString("device_id");
            checkuserToken(device_id);
        });

        adminRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<LinkModel> model_list = new ArrayList<>();

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                LinkModel model = documentSnapshot.toObject(LinkModel.class);
                model_list.add(model);

            }

            sliderView = findViewById(R.id.imageSlider);
            sliderAdapter = new SliderAdapter(model_list);
            sliderView.setSliderAdapter(sliderAdapter);
            sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP);
            sliderView.setAutoCycle(true);
            sliderAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        });


        btnUserCommunity.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, UserCommunity.class));
        });
        btnPositonal.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, PositionalActivity.class));
        });
        btnIntraday.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, IntradayCallActivity.class));
        });
        btnGlobal.setOnClickListener(view -> {
            Toast.makeText(this, "Comming Soon", Toast.LENGTH_SHORT).show();
        });

        btnAddPromotion.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, IntradayCallActivity.class));
        });

    }

    private void checkForUpdate() {

        String app_versionn = String.valueOf(getCurrentAppVersionCode());
        if (!app_versionn.contains(app_version)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setTitle("Update Available!")
                    .setMessage("New Version is Available. Update now otherwise you can not access our Application")
                    .setCancelable(false)
                    .setPositiveButton("Update Now", (dialogInterface, i) -> {
                        //go to the playstore link
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName()));
                        startActivity(intent);
                    });
            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this,R.color.black));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this,R.color.black));

        }

    }

    private int getCurrentAppVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void showAppOffDialog() {
        if (appOnOff.contains("OFF")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false)
                    .setTitle("Sorry!")
                    .setMessage(off_reason)
                    .setPositiveButton("Exit", (dialogInterface, i) -> {
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    });
            AlertDialog alert = builder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this,R.color.black));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this,R.color.black));

        }
    }


    private void checkuserToken(String deviceId) {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(newToken -> {
            if (!deviceId.contains(newToken)) {
                userRef.update("device_id", newToken);
            }
        });
    }

    private void initViews() {
        btnGlobal = findViewById(R.id.btn_global);
        btnIntraday = findViewById(R.id.btn_intraday);
        btnPositonal = findViewById(R.id.btn_positional);
        btnUserCommunity = findViewById(R.id.btn_user_comm);
        btnAddPromotion = findViewById(R.id.btn_add_promotion);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_course:
                drawerLayout.closeDrawer(GravityCompat.START);
                checkCourseDetail();
                break;

            case R.id.menu_today_call:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, IntradayCallActivity.class));
                break;

            case R.id.menu_contact_us:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent4 = new Intent(MainActivity.this, ContentActivity.class);
                intent4.putExtra("name", "Contact Us");
                intent4.putExtra("file_name", "contact");
                startActivity(intent4);
                break;


            case R.id.menu_about_us:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent5 = new Intent(MainActivity.this, ContentActivity.class);
                intent5.putExtra("name", "About Us");
                intent5.putExtra("file_name", "about");
                startActivity(intent5);
                break;


            case R.id.menu_refund:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent2 = new Intent(MainActivity.this, ContentActivity.class);
                intent2.putExtra("name", "Refund Policy");
                intent2.putExtra("file_name", "refund");
                startActivity(intent2);
                break;

            case R.id.menu_terms:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent3 = new Intent(MainActivity.this, ContentActivity.class);
                intent3.putExtra("name", "Terms and Conditions");
                intent3.putExtra("file_name", "terms");
                startActivity(intent3);
                break;
            case R.id.menu_privacy:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent1 = new Intent(MainActivity.this, ContentActivity.class);
                intent1.putExtra("name", "Privacy Policy");
                intent1.putExtra("file_name", "privacy");
                startActivity(intent1);
                break;
            case R.id.menu_exit:
                drawerLayout.closeDrawer(GravityCompat.START);
                exitDialog();
                break;

        }

        return true;
    }

    private void checkCourseDetail() {
        if (USER_COURSE.contains("YES")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setTitle("Download Alert!");
            alertDialogBuilder
                    .setMessage("You have already Paid your Course Fees. Do You Want to Download your Course?")
                    .setCancelable(false)
                    .setPositiveButton("Yes",
                            (dialog, id) -> {
                                startDownloadCourse();
                            })
                    .setNegativeButton("No", (dialog, id) -> dialog.cancel());
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();
            Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
            nbutton.setTextColor(ContextCompat.getColor(this, R.color.primary));
            Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
            pbutton.setTextColor(ContextCompat.getColor(this, R.color.primary));

        } else {
            startActivity(new Intent(MainActivity.this, MarketCourseActivity.class));
        }

    }

    private void startDownloadCourse() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        startActivity(i);

    }


    private void exitDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("Exit Confirmation");
        alertDialogBuilder
                .setMessage("Click yes to Exit!")
                .setCancelable(false)
                .setPositiveButton("Yes",
                        (dialog, id) -> {
                            FirebaseAuth.getInstance().signOut();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finishAffinity();
                        })
                .setNegativeButton("No", (dialog, id) -> dialog.cancel());
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(ContextCompat.getColor(this, R.color.primary));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(this, R.color.primary));

    }

}