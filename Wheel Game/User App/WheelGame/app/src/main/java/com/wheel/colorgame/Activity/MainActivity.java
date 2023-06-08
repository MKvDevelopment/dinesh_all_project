package com.wheel.colorgame.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wheel.colorgame.NetworkChangeReceiver;
import com.wheel.colorgame.R;
import com.wheel.colorgame.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    public static String withdrawLimit;
    private String r_time, fix_time, stop_time;
    private String selectedColor;
    private String selectedAmount;
    private String userBlockReason;
    private String userStatus;
    private String selectedColor2;
    private String selectedColor3;
    private String selectedColor4;
    private String wallet, bettedBlueAmount, bettedPinkAmount, bettedYellowAmount, bettedRedAmount, ccolor, how_to_use;
    private TextView tv_countDown, tv_wallett;
    private DocumentReference userRef;
    private DocumentReference betAmountRef;
    private boolean viewEnable = true;
    private ImageView image;
    private TextView result, result2;
    private ConstraintLayout winLoseLayout;
    private DocumentReference timerRef, dailyTask;
    private ProgressDialog progressDialog;
    private String GAME_ID = "4595794";
    // private String GAME_ID = "3709843";


    @SuppressLint({"ResourceAsColor", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        initViews();

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        timerRef = FirebaseFirestore.getInstance().collection("App_Utils").document("Spin_time");
        dailyTask = FirebaseFirestore.getInstance().collection("Today_Task").document(userUid);
        DocumentReference utilsRef = FirebaseFirestore.getInstance().collection("App_Utils").document("Utils");
        betAmountRef = FirebaseFirestore.getInstance().collection("TotalBetAmount").document("color");
        DocumentReference resultColorRef = FirebaseFirestore.getInstance().collection("TotalBetAmount").document("result");
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(userUid);

        utilsRef.addSnapshotListener((value, error) -> {
            String onOff = value.getString("appOnOff");
            String reason = value.getString("reason");
            String play_app_version = value.getString("playstore_app_version");
            String web_app_version = value.getString("website_app_version");
            String app_link = value.getString("app_link");
            how_to_use = value.getString("how_to_use");
            withdrawLimit = value.getString("withdrawLimit");

            assert onOff != null;
            checkOnOff(onOff, reason);

            checkForUpdate(web_app_version,play_app_version, app_link);

        });
        userRef.addSnapshotListener((value, error) -> {
            if (value.exists()) {
                wallet = value.getString("wallet");
                selectedAmount = value.getString("selectedAmount");
                selectedColor = value.getString("selectedColor");
                selectedColor2 = value.getString("selectedColor2");
                selectedColor3 = value.getString("selectedColor3");
                selectedColor4 = value.getString("selectedColor4");
                userStatus = value.getString("user_status");
                userBlockReason = value.getString("block_reason");
                setMainView();
                checkUserBlock();
            } else {
                Toast.makeText(getApplicationContext(), "sdf", Toast.LENGTH_SHORT).show();
            }
        });
        getTimerFromFirebase();
        betAmountRef.addSnapshotListener((value, error) -> {
            bettedBlueAmount = value.getString("blue");
            bettedPinkAmount = value.getString("pink");
            bettedRedAmount = value.getString("red");
            bettedYellowAmount = value.getString("yellow");
        });

        resultColorRef.addSnapshotListener((value, error) -> {
            assert value != null;
            ccolor = value.getString("current");
            String pcolor = value.getString("previous");

            if (ccolor.contains("Red")) {
                ((TextView) findViewById(R.id.textView33)).setText("Red");
                ((TextView) findViewById(R.id.textView33)).setTextColor(Color.parseColor("#F44336"));
            } else if (ccolor.contains("Yellow")) {
                ((TextView) findViewById(R.id.textView33)).setText("Yellow");
                ((TextView) findViewById(R.id.textView33)).setTextColor(Color.parseColor("#FFDEAB"));
            } else if (ccolor.contains("Blue")) {
                ((TextView) findViewById(R.id.textView33)).setText("Blue");
                ((TextView) findViewById(R.id.textView33)).setTextColor(Color.parseColor("#93BCDD"));
            } else if (ccolor.contains("pink")) {
                ((TextView) findViewById(R.id.textView33)).setText("Pink");
                ((TextView) findViewById(R.id.textView33)).setTextColor(Color.parseColor("#F59BFF"));
            } else if (ccolor.contains("waiting...")) {
                ((TextView) findViewById(R.id.textView33)).setText("waiting...");
                ((TextView) findViewById(R.id.textView33)).setTextColor(Color.parseColor("#000000"));
            }

            if (pcolor.contains("Red")) {
                ((TextView) findViewById(R.id.textView34)).setText("Red");
                ((TextView) findViewById(R.id.textView34)).setTextColor(Color.parseColor("#F44336"));
            } else if (pcolor.contains("Blue")) {
                ((TextView) findViewById(R.id.textView34)).setText("Blue");
                ((TextView) findViewById(R.id.textView34)).setTextColor(Color.parseColor("#93BCDD"));
            } else if (pcolor.contains("Yellow")) {
                ((TextView) findViewById(R.id.textView34)).setText("Yellow");
                ((TextView) findViewById(R.id.textView34)).setTextColor(Color.parseColor("#FFDEAB"));
            } else if (pcolor.contains("Pink")) {
                ((TextView) findViewById(R.id.textView34)).setText("Pink");
                ((TextView) findViewById(R.id.textView34)).setTextColor(Color.parseColor("#F59BFF"));
            }


            if (!((selectedColor.equals("") && selectedColor2.equals("")) && (selectedColor3.equals("") && selectedColor4.equals("")))) {
                if (!ccolor.contains("waiting...")) {

                    if (ccolor.equals(selectedColor)) {
                        updateData(2);
                        Toast.makeText(getApplicationContext(), "12" + selectedColor, Toast.LENGTH_SHORT).show();
                    } else if (ccolor.equals(selectedColor2)) {
                        updateData(2);
                        Toast.makeText(getApplicationContext(), "23" + selectedColor2, Toast.LENGTH_SHORT).show();
                    } else if (ccolor.equals(selectedColor3)) {
                        updateData(9);
                        Toast.makeText(getApplicationContext(), "34" + selectedColor3, Toast.LENGTH_SHORT).show();
                    } else if (ccolor.equals(selectedColor4)) {
                        updateData(18);
                        Toast.makeText(getApplicationContext(), "55" + selectedColor4, Toast.LENGTH_SHORT).show();
                    } else {
                        sadGifView();
                    }
                }
            }

        });
        allClickRef();
       // startCountDownForGraph();
        //showWinningDialog();
    }

    private void getTimerFromFirebase() {
        timerRef.get().addOnSuccessListener(documentSnapshot -> {
            fix_time = documentSnapshot.getString("time");
            r_time = documentSnapshot.getString("r_time");
            stop_time = documentSnapshot.getString("stop_time");

            if (stop_time.equals("00:00:00")) {
                getTimerFromFirebase();
            } else {
                countDownTimer(fix_time, r_time);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @SuppressLint("SetTextI18n")
    private void sadGifView() {

        Glide.with(this).load(R.drawable.sad).into(image);
        winLoseLayout.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textView35)).setText("Result Declared (" + ccolor + ")");
        result.setText("Ooo! You Lose");
        result2.setText("Bust Luck, Try Next Round");

        Map<String, Object> map = new HashMap<>();
        map.put("selectedColor", "");
        map.put("selectedColor2", "");
        map.put("selectedColor3", "");
        map.put("selectedColor4", "");

        userRef.update(map);
    }



    private void fetchDataFromAPI() {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.POST, Utils.Base_Url, response -> {
            try {
                JSONObject object1 = new JSONObject(response);
                JSONObject object = object1.getJSONObject("Users");
                String blue = object.getString("blue");
                String yellow = object.getString("yellow");
                String red = object.getString("red");
                String pink = object.getString("pink");
                setMainGraphData(blue, yellow, red, pink);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> requestQueue.stop());

        requestQueue.add(request);
    }

    private void setMainGraphData(String blue, String yellow, String red, String pink) {
        ((TextView) findViewById(R.id.textView6)).setText(blue);
        ((TextView) findViewById(R.id.textView3)).setText(yellow);
        ((TextView) findViewById(R.id.textView9)).setText(red);
        ((TextView) findViewById(R.id.textView12)).setText(pink);

    }

    private void checkOnOff(String onOff, String reason) {

        if (onOff.contains("OFF")) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            alertDialog.setTitle("Alert!");
            alertDialog.setMessage(reason);
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Exit Now", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });
            Utils.showAlertdialog(MainActivity.this, alertDialog, "Alert!", reason);
        }
    }


    @SuppressLint("SetTextI18n")
    private void updateData(int i) {

        Glide.with(this).load(R.drawable.win).into(image);
        result.setText("Congurets! You Win");
        result2.setText("Amount Added Successfully");
        winLoseLayout.setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.textView35)).setText("Result Declared (" + ccolor + ")");

        float total = Float.parseFloat(selectedAmount) * i;
        float totalbal = Float.parseFloat(wallet) + total;
        Map<String, Object> map = new HashMap<>();
        map.put("selectedColor", "");
        map.put("selectedColor2", "");
        map.put("selectedColor3", "");
        map.put("selectedColor4", "");
        map.put("wallet", String.valueOf(totalbal));

        userRef.update(map).addOnSuccessListener(o ->
                Toast.makeText(getApplicationContext(), "Wallet Updated Successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void allClickRef() {
        findViewById(R.id.ll_wallet).setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, PaymentActivity.class)));

        findViewById(R.id.img_ads).setOnClickListener(view ->
                checkSpinDataBase());


        findViewById(R.id.img_3line).setOnClickListener(vieww -> {
            AlertDialog custome_dialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            View view = LayoutInflater.from(this).inflate(R.layout.option_icon_layout, null, false);

            TextView tv_privacy = view.findViewById(R.id.textView17);
            TextView tv_terms = view.findViewById(R.id.textView18);
            TextView tv_email = view.findViewById(R.id.textView16);

            builder.setView(view);
            custome_dialog = builder.create();
            custome_dialog.show();

            tv_email.setOnClickListener(v -> {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"colorgamebethelp@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Support Webet Color Game");
                email.putExtra(Intent.EXTRA_TEXT, "");

                email.setType("message/rfc822");

                startActivity(email);
            });
            tv_privacy.setOnClickListener(v -> {
                Intent intent2 = new Intent(MainActivity.this, WebActivity.class);
                intent2.putExtra("name", "Privacy Policy");
                intent2.putExtra("file_name", "privacy");
                startActivity(intent2);
                custome_dialog.dismiss();
            });
            tv_terms.setOnClickListener(v -> {
                Intent intent3 = new Intent(MainActivity.this, WebActivity.class);
                intent3.putExtra("name", "Terms and Conditions");
                intent3.putExtra("file_name", "terms");
                startActivity(intent3);
                custome_dialog.dismiss();
            });
        });

        //all card view click listner
        findViewById(R.id.blue_cardView).setOnClickListener(view -> {
            if (selectedColor.equals("")) {
                showColorConfirAlert("Blue", "2");
            } else {
                Toast.makeText(getApplicationContext(), "Color is Already Selected", Toast.LENGTH_SHORT).show();
            }

        });
        findViewById(R.id.yellow_cardView).setOnClickListener(view -> {
            if (selectedColor2.equals("")) {
                showColorConfirAlert("Yellow", "2");
            } else {
                Toast.makeText(getApplicationContext(), "Color is Already Selected", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.red_cardView).setOnClickListener(view -> {

            if (selectedColor3.equals("")) {
                showColorConfirAlert("Red", "9");
            } else {
                Toast.makeText(getApplicationContext(), "Color is Already Selected", Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.pink_cardView).setOnClickListener(view -> {
            if (selectedColor4.equals("")) {
                showColorConfirAlert("Pink", "18");
            } else {
                Toast.makeText(getApplicationContext(), "Color is Already Selected", Toast.LENGTH_SHORT).show();
            }
        });

        //all amount button's click listner
        findViewById(R.id.tv_50).setOnClickListener(view -> showConfirAlert("50"));
        findViewById(R.id.tv_100).setOnClickListener(view -> showConfirAlert("100"));
        findViewById(R.id.tv_200).setOnClickListener(view -> showConfirAlert("200"));
        findViewById(R.id.tv_300).setOnClickListener(view -> showConfirAlert("300"));
        findViewById(R.id.tv_500).setOnClickListener(view -> showConfirAlert("500"));
        findViewById(R.id.tv_800).setOnClickListener(view -> showConfirAlert("800"));
        findViewById(R.id.tv_1000).setOnClickListener(view -> showConfirAlert("1000"));
        findViewById(R.id.tv_1500).setOnClickListener(view -> showConfirAlert("1500"));
        findViewById(R.id.tv_2000).setOnClickListener(view -> showConfirAlert("2000"));
        findViewById(R.id.tv_3000).setOnClickListener(view -> showConfirAlert("3000"));
        findViewById(R.id.tv_5000).setOnClickListener(view -> showConfirAlert("5000"));

    }

    private void checkUserDailyTask() {
        progressDialog.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
        String subStartDate = simpleDateFormat1.format(calendar.getTime());
        String subEndDate = null;
        try {
            Date date = simpleDateFormat1.parse(subStartDate);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            subEndDate = simpleDateFormat1.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String finalSubEndDate = subEndDate;


        Map map = new HashMap();
        map.put("task1", "");
        map.put("task2", "");
        map.put("task3", "");
        map.put("task4", "");
        map.put("task5", "");
        map.put("task6", "");
        map.put("task7", "");
        map.put("task8", "");
        map.put("task9", "");
        map.put("task10", "");
        map.put("time", finalSubEndDate);

        dailyTask.get().addOnSuccessListener((value) -> {
            if (value.exists()) {
                //check task refresh data
                String time = value.getString("time");
                try {
                    Date date1 = simpleDateFormat1.parse(subStartDate);
                    Date date2 = simpleDateFormat1.parse(time);

                    if (date1.compareTo(date2) < 0) {
                        //tomarrow is last date
                        progressDialog.dismiss();
                        startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                    } else if (date1.compareTo(date2) > 0) {
                        //date is expired yesterday
                        //Toast.makeText(getApplicationContext(), "expired", Toast.LENGTH_SHORT).show();
                        dailyTask.set(map).addOnSuccessListener(unused -> {
                            startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        });


                    } else {
                        //date is same
                        progressDialog.dismiss();
                        dailyTask.set(map).addOnSuccessListener(unused -> {
                            startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                        }).addOnFailureListener(e -> {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                }
            } else {
                //set daily task data of user on firebase
                dailyTask.set(map).addOnSuccessListener(unused -> {
                    progressDialog.dismiss();
                    startActivity(new Intent(MainActivity.this, AdsTaskActivity.class));
                }).addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                });


            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void countDownTimer(String fix_time, String r_time) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        Date date1 = null;
        Date date2 = null;
        try {
            date1 = (Date) simpleDateFormat.parse(fix_time);
            date2 = (Date) simpleDateFormat.parse(r_time);
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

        long total = differenceInMinutes * 60 + differenceInSeconds;
        long bal = 120 - total;

        countDownForCustomer(bal * 1000);
    }

    private void countDownForCustomer(long bal) {
         progressDialog.dismiss();
        new CountDownTimer(bal, 1010) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onTick(long millisUntilFinished) {


                NumberFormat f = new DecimalFormat("00");

                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;

                if (String.valueOf(min).contains("01") && String.valueOf(sec).contains("00")) {
                    getTimerFromFirebase();
                }
                tv_countDown.setText("Count Down  :- " + f.format(min) + ":" + f.format(sec));
                fetchDataFromAPI();
                if (String.valueOf(min).contains("00") && String.valueOf(sec).contains("00")) {
                    winLoseLayout.setVisibility(View.VISIBLE);
                    ((TextView) findViewById(R.id.textView35)).setText("Result Declared (" + ccolor + ")");
                }
                if (min == 00 && sec <= 10) {
                    ((TextView) findViewById(R.id.textView30)).setText("It's Result Time");
                    viewEnable = false;
                } else {
                    ((TextView) findViewById(R.id.textView30)).setText("It's Bet Time");
                    viewEnable = true;
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onFinish() {
                new Handler().postDelayed(() -> {
                    getTimerFromFirebase();
                }, 5000);
            }
        }.start();

    }

    private void showColorConfirAlert(String tempSelectedColor, String multiplyAmount) {

        switch (tempSelectedColor) {
            case "Pink":
                checkUserOtherData(tempSelectedColor, "selectedColor4", multiplyAmount);
                break;

            case "Red":
                checkUserOtherData(tempSelectedColor, "selectedColor3", multiplyAmount);
                break;
            case "Yellow":
                checkUserOtherData(tempSelectedColor, "selectedColor2", multiplyAmount);
                break;
            case "Blue":
                checkUserOtherData(tempSelectedColor, "selectedColor", multiplyAmount);
                break;
        }


    }

    private void checkUserOtherData(String tempSelectedColor, String firebaseField, String multiplyAmount) {
        if (selectedAmount.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Select Amount!", Toast.LENGTH_SHORT).show();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setPositiveButton("Yes", (dialog, which) -> {
                dialog.dismiss();
                if (viewEnable) {
                    if (Float.parseFloat(wallet) < Float.parseFloat(selectedAmount)) {
                        Toast.makeText(getApplicationContext(), "Insufficient Amount in Wallet", Toast.LENGTH_SHORT).show();
                    } else {
                        float bal = Float.parseFloat(wallet) - Float.parseFloat(selectedAmount);
                        Map<String, Object> map = new HashMap<>();
                        map.put(firebaseField, tempSelectedColor);
                        map.put("wallet", String.valueOf(bal));
                        userRef.update(map);

                        switch (tempSelectedColor) {
                            case "Blue": {

                                float totalAmount = Float.parseFloat(multiplyAmount) * Float.parseFloat(selectedAmount);
                                float totalBal = Float.parseFloat(bettedBlueAmount) + totalAmount;
                                betAmountRef.update("blue", String.valueOf(totalBal));


                                break;
                            }
                            case "Red": {
                                float totalAmount = Float.parseFloat(multiplyAmount) * Float.parseFloat(selectedAmount);
                                float totalBal = Float.parseFloat(bettedRedAmount) + totalAmount;
                                betAmountRef.update("red", String.valueOf(totalBal));

                                break;
                            }
                            case "Yellow": {
                                float totalAmount = Float.parseFloat(multiplyAmount) * Float.parseFloat(selectedAmount);
                                float totalBal = Float.parseFloat(bettedYellowAmount) + totalAmount;
                                betAmountRef.update("yellow", String.valueOf(totalBal));

                                break;
                            }
                            case "Pink": {
                                float totalAmount = Float.parseFloat(multiplyAmount) * Float.parseFloat(selectedAmount);
                                float totalBal = Float.parseFloat(bettedPinkAmount) + totalAmount;
                                betAmountRef.update("pink", String.valueOf(totalBal));
                                break;
                            }
                        }
                        Toast.makeText(getApplicationContext(), "Now Wait for Result!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "It's Result Time. Try Next Round !", Toast.LENGTH_SHORT).show();
                }

            }).setNegativeButton("No", (dialog, which) ->
                    dialog.dismiss());

            Utils.showAlertdialog(MainActivity.this, builder, "Go To Bet", "Are you Sure?, You want to " + selectedAmount + " Rs. to bet on " + tempSelectedColor + " color.");


        }

    }

    private void showConfirAlert(String amount) {

        if (selectedAmount.equals(amount)) {
            Toast.makeText(getApplicationContext(), "Amount already selected!", Toast.LENGTH_SHORT).show();
        } else {
            if (selectedColor.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
                builder.setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    //update selected amount on user details firebase
                    userRef.update("selectedAmount", amount);
                    Toast.makeText(getApplicationContext(), "Now Select Color To Bet", Toast.LENGTH_LONG).show();
                });

                Utils.showAlertdialog(MainActivity.this, builder, "Continue", amount + " Rs. Selected. Now Select Color to Bet.");
            } else {
                Toast.makeText(getApplicationContext(), "Cann't change on Bet Time! For MultiBet Go with Same Amount. ", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void initViews() {
        tv_wallett = findViewById(R.id.tv_wallet);
        tv_countDown = findViewById(R.id.tv_timer);
        image = findViewById(R.id.imageview);
        result = findViewById(R.id.tv_result);
        result2 = findViewById(R.id.tv_result2);
        winLoseLayout = findViewById(R.id.img_winn_layout);
    }

    @SuppressLint("SetTextI18n")
    private void setMainView() {

        tv_wallett.setText("\u20B9 " + wallet);
        findViewById(R.id.btn_ok).setOnClickListener(v -> winLoseLayout.setVisibility(View.GONE));
        if (selectedColor.equals("") && selectedColor2.equals("") && selectedColor3.equals("") && selectedColor4.equals("")) {
            ((TextView) findViewById(R.id.tv_mybet)).setText("My Bet on :- No Bet");
        } else {
            showTextOnMyBet(selectedColor + " " + selectedColor2 + " " + selectedColor3 + " " + selectedColor4);
        }
    }

    @SuppressLint("SetTextI18n")
    private void showTextOnMyBet(String s) {
        ((TextView) findViewById(R.id.tv_mybet)).setText("My Bet on :- " + s + "/\u20B9" + selectedAmount + "/Color");
    }


    private void checkForUpdate(String web_app_version, String firebase_version, String app_link) {

        String app_version = String.valueOf(getCurrentAppVersionCode());

        if (!app_version.equals(firebase_version)) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            alertDialog.setPositiveButton("Update Now", (dialogInterface, i) -> {
                //go to the playstore link
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(app_link));
                startActivity(intent);
                finish();
            });
            Utils.showAlertdialog(MainActivity.this, alertDialog, "Update Available!", "New Version is Available. Update now otherwise you can not access our Application");
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

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialog.setPositiveButton("Yes", (dialog, which) -> {
            dialog.dismiss();
            MainActivity.super.onBackPressed();
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        Utils.showAlertdialog(MainActivity.this, alertDialog, "Exit", "Are you sure! You want to Exit?");

    }

    private void checkUserBlock() {
        if (userStatus.equals("Block")) {
            androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            alertDialog.setCancelable(false)
                    .setPositiveButton("Exit Now", (dialog, which) -> {
                dialog.dismiss();
                finish();
            });
            Utils.showAlertdialog(MainActivity.this, alertDialog, "Account Disabled!", userBlockReason);

        }

    }

    public void How_to_play(View view) {

        Uri uri = Uri.parse(how_to_use);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }

    public void ReferPage(View view) {
        startActivity(new Intent(MainActivity.this, ReferActivity.class));
    }


    private void checkSpinDataBase() {
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference firebaseDatabase=FirebaseDatabase.getInstance().getReference("Spin_User").child(uid);

        firebaseDatabase.get().addOnSuccessListener(dataSnapshot -> {

            if (   dataSnapshot.exists()){
                Intent intent=new Intent(MainActivity.this,SpinActivity.class);
                startActivity(intent);
                progressDialog.dismiss();
            }else {

                Map map=new HashMap();
                map.put("uid", uid);
                map.put("winning_balence", "0");
                map.put("index", "1");

                firebaseDatabase.setValue(map).addOnSuccessListener(unused -> {
                    Intent intent=new Intent(MainActivity.this,SpinActivity.class);
                    startActivity(intent);
                    progressDialog.dismiss();
                }).addOnFailureListener(e ->{
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                  progressDialog.dismiss();
                });
            }

        }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show());

    }
}
