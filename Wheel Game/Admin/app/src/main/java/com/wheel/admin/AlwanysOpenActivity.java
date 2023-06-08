package com.wheel.admin;

import static com.wheel.admin.Utils.Pause_Url;
import static com.wheel.admin.Utils.Start_Url;
import static com.wheel.admin.Utils.Time_Url;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlwanysOpenActivity extends AppCompatActivity {

    public static DocumentReference betAmountRef, betResultRef, timeRef;
    private String timer;
    private String fixtimer;
    private String aBlue;
    private String aRed;
    private String aYellow;
    WebView webView;
    private NetworkChangeReceiver broadcastReceiver;
    private ProgressDialog progressDialog;
    String valuew1=null;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alwanys_open);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Fetching Data");
        progressDialog.show();


        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        timeRef = FirebaseFirestore.getInstance().collection("App_Utils").document("Spin_time");
        betResultRef = FirebaseFirestore.getInstance().collection("TotalBetAmount").document("result");
        betAmountRef = FirebaseFirestore.getInstance().collection("TotalBetAmount").document("color");

        betResultRef.addSnapshotListener((value, error) -> {
           String v=value.getString("current");
            Toast.makeText(getApplicationContext(), v, Toast.LENGTH_SHORT).show();
        });
        betAmountRef.addSnapshotListener((value, error) -> {
            aRed = value.getString("red");
            aYellow = value.getString("yellow");
            aBlue = value.getString("blue");
        });

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setUserAgentString("Chrome/56.0.0.0 Mobile");

       // fetchDataFromAPIOneTime(Utils.Time_Url);


        setCurrentTime();

        kikDataUsingCountDown();
     //   getSnapshotListner();


    }

    private void getSnapshotListner() {
        timeRef.get().addOnSuccessListener(documentSnapshot -> {
            progressDialog.dismiss();

            assert documentSnapshot != null;
            timer = documentSnapshot.getString("time");
            fixtimer = documentSnapshot.getString("r_time");
            documentSnapshot.getString("stop_time");

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

            String time2 = timer;

            Date date1 = null;
            Date date2 = null;
            try {
                date1 = (Date) simpleDateFormat.parse(fixtimer);
                date2 = (Date) simpleDateFormat.parse(time2);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Calculating the difference in milliseconds
            assert date2 != null;
            assert date1 != null;
            long differenceInMilliSeconds
                    = Math.abs(date2.getTime() - date1.getTime());

            // Calculating the difference in Minutes
            long differenceInMinutes
                    = (differenceInMilliSeconds / (60 * 1000)) % 60;

            // Calculating the difference in Seconds
            long differenceInSeconds
                    = (differenceInMilliSeconds / 1000) % 60;

            ((TextView) findViewById(R.id.textview3)).setText("Our Difference is \n" + differenceInMinutes + " minutes "
                    + differenceInSeconds + " Seconds. ");

            long total = differenceInMinutes * 60 + differenceInSeconds;
            long bal = 120 - total;

            countDownForCustomer(bal * 1000);

        }).addOnFailureListener(e -> {

        });

    }

    private void kikDataUsingCountDown() {

        new CountDownTimer(1200000, 1000) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTick(long l) {

                fetchDataFromAPIEverySecond();
            }

            @Override
            public void onFinish() {
             kikDataUsingCountDown();
            }
        }.start();

    }

    private void setCurrentTime() {

        //  Timestamp timestamp = new Timestamp(millisUntilFinished);
        Date resultdate = new Date(System.currentTimeMillis());
        // S is the millisecond
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        // Dates to be parsed
        String time1 = simpleDateFormat.format(resultdate);

        ((TextView) findViewById(R.id.textview1)).setText("Fix Time is:- " + time1);

        Map map=new HashMap();
        map.put("time",time1);
        map.put("stop_time",time1);

        timeRef.update(map).addOnSuccessListener(o -> {
            getSnapshotListner();
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchDataFromAPIOneTime(String link) {
        progressDialog.show();

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, link, response -> {

            try {
                JSONObject object1 = new JSONObject(response);

                long millisUntilFinished = Long.parseLong(object1.getString("unixtime") + "000");

                Date resultdate = new Date(millisUntilFinished);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                String time1 = simpleDateFormat.format(resultdate);

                Map map=new HashMap();
                map.put("time",time1);
                timeRef.update(map);
                ((TextView) findViewById(R.id.textview1)).setText("Regular Time is:- " + time1);


                timeRef.addSnapshotListener((value, error) -> {

                    progressDialog.dismiss();

                    assert value != null;
                    timer = value.getString("time");

                    Toast.makeText(getApplicationContext(), "Firebase time:-"+timer, Toast.LENGTH_SHORT).show();
                    String time2 = timer;

                    Date date1 = null;
                    Date date2 = null;
                    try {
                        date1 = (Date) simpleDateFormat.parse(time1);
                        date2 = (Date) simpleDateFormat.parse(time2);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    // Calculating the difference in milliseconds
                    assert date2 != null;
                    assert date1 != null;
                    long differenceInMilliSeconds
                            = Math.abs(date2.getTime() - date1.getTime());

                    // Calculating the difference in Minutes
                    long differenceInMinutes
                            = (differenceInMilliSeconds / (60 * 1000)) % 60;

                    // Calculating the difference in Seconds
                    long differenceInSeconds
                            = (differenceInMilliSeconds / 1000) % 60;

                    ((TextView) findViewById(R.id.textview3)).setText("Our Difference is \n" + differenceInMinutes + " minutes "
                            + differenceInSeconds + " Seconds. ");

                    long total = differenceInMinutes * 60 + differenceInSeconds;
                    long bal = 20 - total;

                    countDownForCustomer(bal * 1000);
                });


            } catch (JSONException e) {
                progressDialog.dismiss();

                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            requestQueue.stop();
        });
        requestQueue.add(request);
    }

    private void countDownForCustomer(long bal) {
        progressDialog.dismiss();
        new CountDownTimer(bal, 1005) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");

                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;

                if (f.format(min).equals("00") && sec < 10) {
                    webView.loadUrl(Pause_Url);
                } else {
                    webView.loadUrl(Start_Url);
                }
                ((TextView) findViewById(R.id.textview4)).setText("Count Down  :- " + f.format(min) + ":" + f.format(sec));

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFinish() {
                webView.loadUrl(Utils.Reset_Url);

                String result = calcualteUserResult();
                betResultRef.update("current", result);

                timeRef.update("stop_time","00:00:00");

                new Handler().postDelayed(() -> {
                    //result calculate
                    Map map1 = new HashMap();
                    map1.put("current", "waiting...");
                    map1.put("previous", result);
                    betResultRef.update(map1);

                    Map map2 = new HashMap();
                    map2.put("blue", "0.0");
                    map2.put("pink", "0.0");
                    map2.put("red", "0.0");
                    map2.put("yellow", "0.0");
                    betAmountRef.update(map2);

                    setCurrentTime();
                  // getSnapshotListner();

                }, 5000);
            }
        }.start();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fetchDataFromAPIEverySecond() {

        //  Timestamp timestamp = new Timestamp(millisUntilFinished);
        Date resultdate = new Date(System.currentTimeMillis());
        // S is the millisecond
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        // Dates to be parsed
        String time1 = simpleDateFormat.format(resultdate);
      //  String time2 = "14:20:00";

        ((TextView) findViewById(R.id.textview2)).setText("Api/Second Time is:- " + time1);
           timeRef.update("r_time",time1);


    }


    private String calcualteUserResult() {
        float a = Float.parseFloat(aBlue);
        float b = Float.parseFloat(aYellow);
        float c = Float.parseFloat(aRed);

        if (a < b && a < c) {
            return "Blue";
        } else if (b < a && b < c) {
            return "Yellow";
        } else if (c < a && c < b) {
            return "Red";
        } else if (a == b && a == c) {
            return checkColor(Utils.randomResult("abc"));
        } else if (a == b) {
            return checkColor(Utils.randomResult("ab"));
        } else if (b == c) {
            return checkColor(Utils.randomResult("bc"));
        } else if (a == c) {
            return checkColor(Utils.randomResult("ac"));
        }
        return "Blue";
    }

    private String checkColor(String value) {
        if (value.contains("a")) {
            return "Blue";
        } else if (value.contains("b")) {
            return "Yellow";
        } else if (value.contains("c")) {
            return "Red";
        }
        return "Blue";
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}