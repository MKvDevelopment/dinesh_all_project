package com.wheel.colorgame.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wheel.colorgame.R;

public class AdsTaskActivity extends AppCompatActivity  {

    private String GAME_ID = "4595794";
    //private String GAME_ID = "3709843";
    private String AdId = "Rewarded_Android";
    //private String AdId = "Android_Rewarded";
    private DocumentReference dailyTask, userRef;
    private ProgressDialog progressDialog;
    private String amount, task1, task2, task3, task4, task5, task6, task7, task8, task9, task10;
   // private IUnityAdsLoadListener loadListener;
   //private IUnityAdsShowListener showListner;
    private String price, position;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_task);


       /* UnityAds.initialize(getApplicationContext(), GAME_ID, false, this);

        showListner = new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {
                // Toast.makeText(getApplicationContext(), "Unity ads failed Show listner ="+unityAdsShowError, Toast.LENGTH_SHORT).show();
                if (unityAdsShowError.equals(UnityAds.UnityAdsShowError.NOT_READY) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.NO_CONNECTION) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.NOT_INITIALIZED) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.INTERNAL_ERROR)) {

                    AlertDialog.Builder dialog = new AlertDialog.Builder(AdsTaskActivity.this, R.style.AppCompatAlertDialogStyle);

                    dialog.setTitle("Sorry!")
                            .setCancelable(false)
                            .setMessage("Reward Not Available. Try After Some Time")
                            .setPositiveButton("Ok", (dialog1, which) -> dialog1.dismiss());
                    dialog.show();
                }

            }

            @Override
            public void onUnityAdsShowStart(String s) {
                Toast.makeText(getApplicationContext(), "Watch Complete to Get Reward", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnityAdsShowClick(String s) {
                //  Toast.makeText(getApplicationContext(), "Show click", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onUnityAdsShowComplete(String adUnitId, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {

                if (unityAdsShowCompletionState.equals(UnityAds.UnityAdsShowCompletionState.COMPLETED)) {
                    //Toast.makeText(getApplicationContext(), "Ads Completed", Toast.LENGTH_SHORT).show();

                    progressDialog.show();
                    switch (position) {
                        case "1":
                            setDataOnFirebase(price, "task1");
                            break;

                        case "2":
                            setDataOnFirebase(price, "task2");
                            break;

                        case "3":
                            setDataOnFirebase(price, "task3");
                            break;

                        case "4":
                            setDataOnFirebase(price, "task4");
                            break;

                        case "5":
                            setDataOnFirebase(price, "task5");
                            break;

                        case "6":
                            setDataOnFirebase(price, "task6");
                            break;

                        case "7":
                            setDataOnFirebase(price, "task7");
                            break;

                        case "8":
                            setDataOnFirebase(price, "task8");
                            break;

                        case "9":
                            setDataOnFirebase(price, "task9");
                            break;

                        case "10":
                            setDataOnFirebase(price, "task10");
                            break;


                    }


                } else {
                    //  Toast.makeText(getApplicationContext(), "skipped by user", Toast.LENGTH_SHORT).show();
                }
            }
        };

        loadListener = new IUnityAdsLoadListener() {
            @Override
            public void onUnityAdsAdLoaded(String s) {
                //  Toast.makeText(getApplicationContext(), "Ad Load listner", Toast.LENGTH_SHORT).show();
                //   UnityAds.show(AdsTaskActivity.this,AdId,new UnityAdsShowOptions(),showListner);
            }

            @Override
            public void onUnityAdsFailedToLoad(String addId, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {
                //  Toast.makeText(getApplicationContext(), "Unity Ads load failed listner  =="+s1, Toast.LENGTH_SHORT).show();
            }
        };
*/
        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Fetching Data...");

        String userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        dailyTask = FirebaseFirestore.getInstance().collection("Today_Task").document(userUid);
        userRef = FirebaseFirestore.getInstance().collection("User_List").document(userUid);

        userRef.addSnapshotListener((value, error) -> {
            amount = value.getString("wallet");
        });
        dailyTask.addSnapshotListener((value, error) -> {
            task1 = value.getString("task1");
            task2 = value.getString("task2");
            task3 = value.getString("task3");
            task4 = value.getString("task4");
            task5 = value.getString("task5");
            task6 = value.getString("task6");
            task7 = value.getString("task7");
            task8 = value.getString("task8");
            task9 = value.getString("task9");
            task10 = value.getString("task10");

            if (task1.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward1)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward1)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward1)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward1)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }
            if (task2.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward2)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward2)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward2)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward2)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }
            if (task3.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward3)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward3)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward3)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward3)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }
            if (task4.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward4)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward4)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward4)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward4)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }
            if (task5.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward5)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward5)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward5)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward5)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }
            if (task6.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward6)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward6)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward6)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward6)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }
            if (task7.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward7)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward7)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward7)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward7)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }
            if (task8.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward8)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward8)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward8)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward8)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }
            if (task9.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward9)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward9)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward9)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward9)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }
            if (task10.isEmpty()) {
                ((TextView) findViewById(R.id.btn_reward10)).setText("Get Reward");
            } else {
                ((TextView) findViewById(R.id.btn_reward10)).setText("Completed");
                ((TextView) findViewById(R.id.btn_reward10)).setEnabled(false);
                ((TextView) findViewById(R.id.btn_reward10)).setBackground(getDrawable(R.drawable.gredient_blue_border));
            }

        });


        allClickRef();
    }

    private void allClickRef() {
        findViewById(R.id.btn_reward1).setOnClickListener(v -> {
            showAdToUser("1", "1");

        });
        findViewById(R.id.btn_reward2).setOnClickListener(v -> {
            showAdToUser("1", "2");
        });
        findViewById(R.id.btn_reward3).setOnClickListener(v -> {
            showAdToUser("1", "3");
        });
        findViewById(R.id.btn_reward4).setOnClickListener(v -> {
            showAdToUser("1", "4");
        });
        findViewById(R.id.btn_reward5).setOnClickListener(v -> {
            showAdToUser("0.90", "5");
        });
        findViewById(R.id.btn_reward6).setOnClickListener(v -> {
            showAdToUser("0.80", "6");
        });
        findViewById(R.id.btn_reward7).setOnClickListener(v -> {
            showAdToUser("0.70", "7");
        });
        findViewById(R.id.btn_reward8).setOnClickListener(v -> {
            showAdToUser("0.60", "8");
        });
        findViewById(R.id.btn_reward9).setOnClickListener(v -> {
            showAdToUser("0.50", "9");
        });
        findViewById(R.id.btn_reward10).setOnClickListener(v -> {
            showAdToUser("0.50", "10");
        });

    }

    private void showAdToUser(String price, String position) {

        this.price = price;
        this.position = position;
      //  DisplayRewardedAd();

    }

    private void setDataOnFirebase(String price, String position) {

        float total = Float.parseFloat(price) + Float.parseFloat(amount);

        dailyTask.update(position, "YES").addOnSuccessListener(unused -> {
            userRef.update("wallet", String.valueOf(total)).addOnSuccessListener(unused1 -> {
                progressDialog.dismiss();
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);

                builder1.setTitle("Congratulation!")
                        .setMessage("Rewarded Amount Successfully Added in Your Wallet.")
                        .setCancelable(false)
                        .setPositiveButton("Got it", (dialog, which) -> {
                            dialog.dismiss();
                            //show timer to hold customer


                            AlertDialog custome_dialog;
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            View view = LayoutInflater.from(this).inflate(R.layout.time_remain_layout, null, false);

                            TextView tv_timer = view.findViewById(R.id.textView41);

                            builder.setView(view);
                            builder.setCancelable(false);
                            custome_dialog = builder.create();
                            custome_dialog.show();

                            //show timer
                            new CountDownTimer(60000, 1000) {
                                @SuppressLint("SetTextI18n")
                                public void onTick(long millisUntilFinished) {
                                    tv_timer.setText(String.valueOf(millisUntilFinished / 1000) + " (Second)");
                                }

                                @SuppressLint("SetTextI18n")
                                public void onFinish() {
                                    custome_dialog.dismiss();
                                }
                            }.start();


                        });
                builder1.show();
            }).addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        });


    }
/*
    @Override
    public void onInitializationComplete() {
        //   Toast.makeText(getApplicationContext(), "Init Complete", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
        //   Toast.makeText(getApplicationContext(), "Init Failed", Toast.LENGTH_SHORT).show();
    }

    public void DisplayRewardedAd() {

        UnityAds.load(AdId, new UnityAdsLoadOptions(), loadListener);

        UnityAds.show(AdsTaskActivity.this, AdId, new UnityAdsShowOptions(), showListner);

    }*/

}