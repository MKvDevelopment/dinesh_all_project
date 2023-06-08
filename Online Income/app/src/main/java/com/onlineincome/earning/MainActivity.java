package com.onlineincome.earning;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.onlineincome.earning.OneTimeUtils.NetworkChangeReceiver;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static String TASK1, TASK2, TASK3, TASK4, TASK5, TASK6, COMP1, COMP2, COMP3, COMP4, COMP5, COMP6;
    public static String task1_price, task2_price, task3_price, task4_price, task5_price, task6_price, PAYTM_URL, PAYTM_VERIFY_URL;
    private CardView cardView1, cardView2, cardView3, cardView4, cardView5, cardView6;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private ProgressDialog progressDialog;
    private String user_email, app_uninstall, boost_plan, wallet, image, user_name, appOnOff, appOffReason, app_link, app_version;
    private Button community;
    private ImageView imageView1, imageView2, imageView3, imageView4, imageView5, imageView6, navImage;
    private ActionBarDrawerToggle toggle;
    private TextView task1, task2, task3, task4, task5, task6, navEmail, navName;
    private DocumentReference adminRef, taskRef, userRef, commonRef;
    private NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //for progress bar
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);

        findView();

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        toggle = new ActionBarDrawerToggle(this
                , drawer,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        cardView1.setOnClickListener(this::onClick);
        cardView2.setOnClickListener(this::onClick);
        cardView3.setOnClickListener(this::onClick);
        cardView4.setOnClickListener(this::onClick);
        cardView5.setOnClickListener(this::onClick);
        cardView6.setOnClickListener(this::onClick);
        community.setOnClickListener(this::onClick);

        user_email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        taskRef = FirebaseFirestore.getInstance().collection("Task Details").document("task");
        adminRef = FirebaseFirestore.getInstance().collection("App_utils").document("app_utils");
        userRef = FirebaseFirestore.getInstance().collection("users").document(user_email);
        commonRef = FirebaseFirestore.getInstance().collection("completed_task").document(user_email);

        userRef.addSnapshotListener((value1, error1) -> {
            user_name = value1.getString("name");
            image = value1.getString("image");
            wallet = value1.getString("wallet");
            boost_plan = value1.getString("boost");

            adminRef.addSnapshotListener((value, error) -> {
                appOnOff = value.getString("app_on");
                appOffReason = value.getString("reason");
                app_version = value.getString("app_version");
                app_link = value.getString("link");
                app_uninstall = value.getString("uninstall");
                PAYTM_URL = value.getString("paytm_url");
                PAYTM_VERIFY_URL = value.getString("paytm_verify_url");

                if (appOnOff.equals("OFF")) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("Sorry!");
                    alertDialog.setMessage(appOffReason);
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Exit Now", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    }).show();
                } else {
                    checkForUpdate();
                }
                if (app_uninstall.equals("YES")) {
                    Intent intent = new Intent(Intent. ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:com.onlineincome.earning"));
                    intent.putExtra(Intent.EXTRA_RETURN_RESULT,true);
                    startActivityForResult(intent,1);
                }
            });
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
           //     Toast.makeText(this, "user accepted the (un)install", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Intent intent = new Intent(Intent. ACTION_UNINSTALL_PACKAGE);
                intent.setData(Uri.parse("package:com.onlineincome.earning"));
                intent.putExtra(Intent.EXTRA_RETURN_RESULT,true);
                startActivityForResult(intent,1);
            }
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

    private void checkForUpdate() {

        String app_versio = String.valueOf(getCurrentAppVersionCode());

        if (!app_versio.contains(app_version)) {

            new AlertDialog.Builder(this)
                    .setTitle("Update Available!")
                    .setMessage("New Version is Available. Update now otherwise you can not access our Application")
                    .setCancelable(false)
                    .setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //go to the playstore link
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(app_link));
                            startActivity(intent);
                            finish();
                        }
                    }).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_option_menu, menu);
        menu.getItem(1).setTitle("Rs." + wallet + "/-");
        invalidateOptionsMenu();
        return true;
    }

    private void setData() {
        match(Integer.parseInt(COMP1), Integer.parseInt(TASK1), imageView1);
        match(Integer.parseInt(COMP2), Integer.parseInt(TASK2), imageView2);
        match(Integer.parseInt(COMP3), Integer.parseInt(TASK3), imageView3);
        match(Integer.parseInt(COMP4), Integer.parseInt(TASK4), imageView4);
        match(Integer.parseInt(COMP5), Integer.parseInt(TASK5), imageView5);
        match(Integer.parseInt(COMP6), Integer.parseInt(TASK6), imageView6);

        task1.setText("Impression " + COMP1 + "/" + TASK1);
        task2.setText("Impression " + COMP2 + "/" + TASK2);
        task3.setText("Impression " + COMP3 + "/" + TASK3);
        task4.setText("Impression " + COMP4 + "/" + TASK4);
        task5.setText("Impression " + COMP5 + "/" + TASK5);
        task6.setText("Impression " + COMP6 + "/" + TASK6);

        setHeaderData();

    }

    private void setHeaderData() {

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View view = navigationView.getHeaderView(0);

        navName = view.findViewById(R.id.nav_name);
        navEmail = view.findViewById(R.id.nave_email);
        navImage = view.findViewById(R.id.nav_user_image);

        navName.setText(user_name);
        navEmail.setText(user_email);
        Picasso.get().load(image).into(navImage);
    }

    private void match(int parseInt, int parseInt1, ImageView imageView1) {
        if (parseInt == parseInt1) {
            imageView1.setImageResource(R.drawable.ic_baseline_check);
        }
    }

    private void findView() {
        community = findViewById(R.id.community);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        task1 = findViewById(R.id.textView5);
        task2 = findViewById(R.id.textView6);
        task3 = findViewById(R.id.textView13);
        task4 = findViewById(R.id.textView15);
        task5 = findViewById(R.id.textView17);
        task6 = findViewById(R.id.textView19);
        cardView1 = findViewById(R.id.cardView1);
        cardView2 = findViewById(R.id.cardView2);
        cardView3 = findViewById(R.id.cardView3);
        cardView4 = findViewById(R.id.cardView4);
        cardView5 = findViewById(R.id.cardView5);
        cardView6 = findViewById(R.id.cardView6);
        imageView1 = findViewById(R.id.imageView2);
        imageView2 = findViewById(R.id.imageView3);
        imageView3 = findViewById(R.id.imageView4);
        imageView4 = findViewById(R.id.imageView5);
        imageView5 = findViewById(R.id.imageView6);
        imageView6 = findViewById(R.id.imageView7);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.community:
                if (user_email.contains("playstore")) {
                    Toast.makeText(this, "This feature's will come soon.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intnt = new Intent(MainActivity.this, CommunityActivity.class);
                    startActivity(intnt);
                }
                break;
            case R.id.cardView1:
                Intent intent = new Intent(MainActivity.this, TaskActivity.class);
                intent.putExtra("task", "Task-1");
                intent.putExtra("task1", "task1");
                intent.putExtra("price", task1_price);
                startActivity(intent);
                break;
            case R.id.cardView2:
                Intent intent2 = new Intent(MainActivity.this, TaskActivity.class);
                intent2.putExtra("task", "Task-2");
                intent2.putExtra("task1", "task2");
                intent2.putExtra("price", task2_price);
                startActivity(intent2);
                break;
            case R.id.cardView3:
                Intent intent3 = new Intent(MainActivity.this, TaskActivity.class);
                intent3.putExtra("task", "Task-3");
                intent3.putExtra("task1", "task3");
                intent3.putExtra("price", task3_price);
                startActivity(intent3);
                break;
            case R.id.cardView4:
                Intent intent4 = new Intent(MainActivity.this, TaskActivity.class);
                intent4.putExtra("task", "Task-4");
                intent4.putExtra("task1", "task4");
                intent4.putExtra("price", task4_price);
                startActivity(intent4);
                break;
            case R.id.cardView5:
                Intent intent5 = new Intent(MainActivity.this, TaskActivity.class);
                intent5.putExtra("task", "Task-5");
                intent5.putExtra("task1", "task5");
                intent5.putExtra("price", task5_price);
                startActivity(intent5);
                break;
            case R.id.cardView6:
                Intent intent6 = new Intent(MainActivity.this, TaskActivity.class);
                intent6.putExtra("task", "Task-6");
                intent6.putExtra("task1", "task6");
                intent6.putExtra("price", task6_price);
                startActivity(intent6);
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_widrow_money:
                startActivity(new Intent(MainActivity.this, WithdrawActivity.class));
                return true;
            case R.id.nav_terms:
                Intent intent1 = new Intent(MainActivity.this, ContentActivity.class);
                intent1.putExtra("name", "Terms and Conditions");
                intent1.putExtra("file_name", "terms");
                startActivity(intent1);
                return true;
            case R.id.nav_privacy:
                Intent intent2 = new Intent(MainActivity.this, ContentActivity.class);
                intent2.putExtra("name", "Privacy Policy");
                intent2.putExtra("file_name", "privacy");
                startActivity(intent2);
                return true;
            case R.id.nav_exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.price:
                startActivity(new Intent(MainActivity.this, WithdrawActivity.class));
                return true;
            case R.id.wallet:
                startActivity(new Intent(MainActivity.this, WithdrawActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressDialog.show();
        taskRef.addSnapshotListener((value, error) -> {
            TASK1 = value.getString("task1");
            TASK2 = value.getString("task2");
            TASK3 = value.getString("task3");
            TASK4 = value.getString("task4");
            TASK5 = value.getString("task5");
            TASK6 = value.getString("task6");
            task1_price = value.getString("price_task1");
            task2_price = value.getString("price_task2");
            task3_price = value.getString("price_task3");
            task4_price = value.getString("price_task4");
            task5_price = value.getString("price_task5");
            task6_price = value.getString("price_task6");

            commonRef.addSnapshotListener((value1, error1) -> {
                if (value1.exists()) {
                    COMP1 = value1.getString("task1");
                    COMP2 = value1.getString("task2");
                    COMP3 = value1.getString("task3");
                    COMP4 = value1.getString("task4");
                    COMP5 = value1.getString("task5");
                    COMP6 = value1.getString("task6");
                    setData();
                    progressDialog.dismiss();
                } else {
                    Map map = new HashMap();
                    map.put("task1", "0");
                    map.put("task2", "0");
                    map.put("task3", "0");
                    map.put("task4", "0");
                    map.put("task5", "0");
                    map.put("task6", "0");

                    commonRef.set(map)
                            .addOnCompleteListener(task -> {
                                progressDialog.dismiss();
                            });
                }
            });
        });


    }
}