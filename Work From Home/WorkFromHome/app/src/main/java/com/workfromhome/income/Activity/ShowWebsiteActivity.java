package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.R;

import java.util.HashMap;
import java.util.Map;

public class ShowWebsiteActivity extends AppCompatActivity {

    //timer setting
    private long timeLeftMilliSeconds = 600000;
    private CountDownTimer countDownTimer;
    private boolean timeRunning = false;
    private boolean isDone = false;
    //views
    private TextView timer;
    private WebView webView;
    private String webid, weblink, wallet, email, plan_name, withdraw_request_count;
    private ProgressDialog dialog;
    private ProgressBar progressBar;

    //firebase
    private FirebaseFirestore firestore;
    private DocumentReference dc;

    private NetworkChangeReceiver broadcastReceiver;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_website);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);

        //store onPause/onResume time Value
        pref = getApplicationContext().getSharedPreferences("mypref", 0);
        editor = pref.edit();
        editor.clear();
        editor.apply();

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        //firebase
        firestore = FirebaseFirestore.getInstance();

        //progress dialog
        dialog = new ProgressDialog(this);
        dialog.setMessage("Loading....");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);


        //getting intent form webAdapter
        String userTime = getIntent().getStringExtra("time");
        webid = getIntent().getStringExtra("id");
        weblink = getIntent().getStringExtra("url");
        timeLeftMilliSeconds = Long.parseLong(userTime + "000");


        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        timer = (TextView) findViewById(R.id.timer);
        webView = (WebView) findViewById(R.id.webView);


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(weblink);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                if (!isDone && !timeRunning) {
                    countDownTimer.start();
                    System.out.println("Time start correct");
                    timeRunning = true;
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.animate();
            }
        });


        dc = firestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());
        dc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    wallet = documentSnapshot.getString("wallet");
                    plan_name = documentSnapshot.getString("plan");
                    ;
                    email = documentSnapshot.getString("email");
                    checkwithdrawLimit();
                }
            }
        });

    }

    private void checkwithdrawLimit() {

        if (plan_name.equals("Free Plan")) {

            withdraw_request_count = getIntent().getStringExtra("count");
            if (withdraw_request_count.equals("0")) {
                boolean bal = Float.parseFloat(wallet) >= 7;
                if (bal) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ShowWebsiteActivity.this);
                    alertDialog.setTitle("Alert!");
                    alertDialog.setMessage("You are eligible to withdraw your money.");
                    alertDialog.setCancelable(false);
                    alertDialog.setPositiveButton("Withdraw Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ShowWebsiteActivity.this, WithdrawActivity.class);
                            intent.putExtra("email", email);
                            intent.putExtra("plan", plan_name);
                            startActivity(intent);
                            dialog.dismiss();
                        }
                    }).show();
                }
            }
        }


    }

    private void updateTimer() {

        int min = (int) timeLeftMilliSeconds / 60000;

        int second = (int) timeLeftMilliSeconds % 60000 / 1000;
        String timeLeftText;

        timeLeftText = " " + min;
        if (min < 10)
            timeLeftText = "0" + min;

        timeLeftText += ":";

        if (second < 10)
            timeLeftText += "0";
        timeLeftText += second;

        if (timeLeftText.equals("00:00")) {
            timer.setVisibility(View.GONE);
            countDownTimer.onFinish();
            countDownTimer.cancel();
        } else {
            timer.setText(timeLeftText + " remaining...");
        }

    }

    @Override
    public void onBackPressed() {

        if (isDone) {

            countDownTimer.cancel();

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Alert!");
            alertDialog.setMessage("Do you want to add money in wallet!");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    updateFirebase(dialog, "add");
                    dialog.dismiss();
                }
            }).show();

        } else {
            Toast.makeText(this, "You have not yet complete time limit", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateFirebase(DialogInterface dialog, final String string) {

        switch (string) {
            case "cancel":
                dialog.dismiss();
                finish();
                break;
            case "add":
                String web_amount = getIntent().getStringExtra("wallet");
                Float total_bal = Float.parseFloat(wallet) + Float.parseFloat(web_amount);
                Map map = new HashMap<>();
                map.put("wallet", String.valueOf(total_bal));
                dc.update(map);
                Toast.makeText(this, "Amount Added in wallet", Toast.LENGTH_SHORT).show();
                setResult(101);

                finish();
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.web_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.previous:
                onBackPressed();
                break;
            case R.id.next:
                if (webView.canGoForward()) {
                    webView.goForward();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (timeRunning) {
            editor.putLong("timeLeft", timeLeftMilliSeconds).apply();
            editor.putBoolean("isPause", true).apply();
            countDownTimer.cancel();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isPause = pref.getBoolean("isPause", false);
        if (isPause) {
            timeLeftMilliSeconds = pref.getLong("timeLeft", timeLeftMilliSeconds);
        }
        countDownTimer = new CountDownTimer(timeLeftMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMilliSeconds = millisUntilFinished;
                updateTimer();
            }

            @Override
            public void onFinish() {

                Map map = new HashMap();
                map.put("isDone", true);

                DocumentReference documentReference = firestore.collection("common_data").document(FirebaseAuth.getInstance().getCurrentUser().getEmail()).collection("user_visited_links").document(webid);
                documentReference.set(map);
                isDone = true;
                timeRunning = false;

                timer.setText("Times up!");
                Toast.makeText(ShowWebsiteActivity.this, "Times up! You can go back", Toast.LENGTH_SHORT).show();
                countDownTimer.cancel();

            }
        };
        if (isPause) {
            countDownTimer.start();
            System.out.println("Time start incorrect");
        }
    }

}
