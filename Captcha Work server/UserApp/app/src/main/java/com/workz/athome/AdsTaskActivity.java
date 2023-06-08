package com.workz.athome;

import static com.workz.athome.MainActivity.querka_link;
import static com.workz.athome.MainActivity.wallet;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAdsLoadOptions;
import com.unity3d.ads.UnityAdsShowOptions;
import com.workz.athome.Model.DailyTask.TaskRoot;
import com.workz.athome.Utils.ApiClient;
import com.workz.athome.Utils.ApiService;
import com.workz.athome.Utils.Utils;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdsTaskActivity extends AppCompatActivity implements IUnityAdsInitializationListener {

    private String GAME_ID = "4595794";
    //private String GAME_ID = "3709843";
    private String AdId = "Rewarded_Android";
    //private String AdId = "Android_Rewarded";


    private ProgressDialog progressDialog;

    private IUnityAdsLoadListener loadListener;
    private IUnityAdsShowListener showListner;
    private String price, position, token;
    private ApiService apiServices;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ActivityResultLauncher<Intent> promotionResultLauncher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_task);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Toolbar toolbar = findViewById(R.id.common_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        UnityAds.initialize(getApplicationContext(), GAME_ID, false, this);

        showListner = new IUnityAdsShowListener() {
            @Override
            public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {
                // Toast.makeText(getApplicationContext(), "Unity ads failed Show listner ="+unityAdsShowError, Toast.LENGTH_SHORT).show();
                if (unityAdsShowError.equals(UnityAds.UnityAdsShowError.NOT_READY) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.NO_CONNECTION) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.NOT_INITIALIZED) ||
                        unityAdsShowError.equals(UnityAds.UnityAdsShowError.INTERNAL_ERROR)) {

                    progressDialog.show();
                    Toast.makeText(AdsTaskActivity.this, "Wait Here 2 Minutes.", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(() -> {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(querka_link));
                        startActivity(i);

                        promotionResultLauncher.launch(i);
                    }, 1000);
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
                            updateTask(price, "task_1");
                            break;

                        case "2":
                            updateTask(price, "task_2");
                            break;

                        case "3":
                            updateTask(price, "task_3");
                            break;

                        case "4":
                            updateTask(price, "task_4");
                            break;

                        case "5":
                            updateTask(price, "task_5");
                            break;

                        case "6":
                            updateTask(price, "task_6");
                            break;

                        case "7":
                            updateTask(price, "task_7");
                            break;

                        case "8":
                            updateTask(price, "task_8");
                            break;

                        case "9":
                            updateTask(price, "task_9");
                            break;

                        case "10":
                            updateTask(price, "task_10");
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

        progressDialog = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("Please Wait!");
        progressDialog.setMessage("Fetching Data...");

        apiServices = ApiClient.getRetrofit().create(ApiService.class);
        preferences = getSharedPreferences(Utils.sharedPrefrenceName, MODE_PRIVATE);
        editor=preferences.edit();
        token = preferences.getString("token", null);
        getApiData();

        allClickRef();

        //event
        promotionResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    progressDialog.show();
                    switch (position) {
                        case "1":
                            updateTask(price, "task_1");
                            break;

                        case "2":
                            updateTask(price, "task_2");
                            break;

                        case "3":
                            updateTask(price, "task_3");
                            break;

                        case "4":
                            updateTask(price, "task_4");
                            break;

                        case "5":
                            updateTask(price, "task_5");
                            break;

                        case "6":
                            updateTask(price, "task_6");
                            break;

                        case "7":
                            updateTask(price, "task_7");
                            break;

                        case "8":
                            updateTask(price, "task_8");
                            break;

                        case "9":
                            updateTask(price, "task_9");
                            break;

                        case "10":
                            updateTask(price, "task_10");
                            break;
                    }
                });

    }

    private void getApiData() {
         progressDialog.show();

        Call<TaskRoot> call = apiServices.getTaskList("Bearer " + token);
        call.enqueue(new Callback<TaskRoot>() {
            @Override
            public void onResponse(@NonNull Call<TaskRoot> call, @NonNull Response<TaskRoot> response) {
                if (response.code() == 200) {

                    TaskRoot root = response.body();

                    if (root.getData()!=null){
                        Utils.setUserTaskSharedPreference(root, AdsTaskActivity.this);
                        setDataOnViews(root);
                    }
                    progressDialog.dismiss();
                    Log.d("aaaaaaaaaaaa:-    ",  response.body().getMessage());
                    Log.d("aaaaaaaaaaaaadfaaaaddd :-    ",  token);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AdsTaskActivity.this, "Response Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TaskRoot> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.d("aaaaaaaaaaaa:-    ",  t.toString()+"");
                Toast.makeText(AdsTaskActivity.this, "Api Error!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void setDataOnViews(TaskRoot root) {
        if (root.getData().task_1.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward1)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward1)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward1)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward1)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }
        if (root.getData().task_2.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward2)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward2)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward2)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward2)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }
        if (root.getData().task_3.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward3)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward3)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward3)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward3)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }
        if (root.getData().task_4.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward4)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward4)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward4)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward4)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }
        if (root.getData().task_5.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward5)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward5)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward5)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward5)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }
        if (root.getData().task_6.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward6)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward6)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward6)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward6)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }
        if (root.getData().task_7.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward7)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward7)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward7)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward7)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }
        if (root.getData().task_8.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward8)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward8)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward8)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward8)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }
        if (root.getData().task_9.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward9)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward9)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward9)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward9)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }
        if (root.getData().task_10.isEmpty()) {
            ((TextView) findViewById(R.id.btn_reward10)).setText("Get Reward");
        } else {
            ((TextView) findViewById(R.id.btn_reward10)).setText("Completed");
            ((TextView) findViewById(R.id.btn_reward10)).setEnabled(false);
            ((TextView) findViewById(R.id.btn_reward10)).setBackground(getDrawable(R.drawable.btn_background_borderrr));
        }

    }

    private void allClickRef() {

        findViewById(R.id.btn_reward1).setOnClickListener(v -> {
            showAdToUser("7", "1");
        });
        findViewById(R.id.btn_reward2).setOnClickListener(v -> {
            showAdToUser("5", "2");
        });
        findViewById(R.id.btn_reward3).setOnClickListener(v -> {
            showAdToUser("6", "3");
        });
        findViewById(R.id.btn_reward4).setOnClickListener(v -> {
            showAdToUser("5", "4");
        });
        findViewById(R.id.btn_reward5).setOnClickListener(v -> {
            showAdToUser("4", "5");
        });
        findViewById(R.id.btn_reward6).setOnClickListener(v -> {
            showAdToUser("3", "6");
        });
        findViewById(R.id.btn_reward7).setOnClickListener(v -> {
            showAdToUser("8", "7");
        });
        findViewById(R.id.btn_reward8).setOnClickListener(v -> {
            showAdToUser("5", "8");
        });
        findViewById(R.id.btn_reward9).setOnClickListener(v -> {
            showAdToUser("4", "9");
        });
        findViewById(R.id.btn_reward10).setOnClickListener(v -> {
            showAdToUser("3", "10");
        });

    }

    private void showAdToUser(String price, String position) {
        this.price = price;
        this.position = position;

        DisplayRewardedAd();
     //  updateTask(price,taskNo);
    }

    private void updateTask(String price, String taskNo) {
        Call<TaskRoot> taskRootCall = apiServices.updateTaskData("Bearer " + token,
                taskNo,
                "YES",
                price);
        taskRootCall.enqueue(new Callback<TaskRoot>() {
            @Override
            public void onResponse(@NonNull Call<TaskRoot> call, @NonNull Response<TaskRoot> response) {
                if (response.code() == 200) {
                    TaskRoot root = response.body();
                    Utils.updateUserTaskSharedPreference(root, AdsTaskActivity.this);
                    setDataOnViews(root);

                    float totl=Float.parseFloat(wallet)+Float.parseFloat(price);

                    editor.putString("wallet", String.valueOf(totl));
                    editor.commit();
                    showTimer();
                } else {
                    Toast.makeText(AdsTaskActivity.this, "Response Failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<TaskRoot> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(AdsTaskActivity.this, "Api Error!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showTimer() {
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
                            tv_timer.setText(millisUntilFinished / 1000 + " (Second)");
                        }

                        @SuppressLint("SetTextI18n")
                        public void onFinish() {
                            getApiData();
                            custome_dialog.dismiss();
                        }
                    }.start();
                });
        builder1.show();
    }

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

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

}