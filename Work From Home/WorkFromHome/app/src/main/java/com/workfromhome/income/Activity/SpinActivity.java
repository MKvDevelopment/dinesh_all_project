package com.workfromhome.income.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;
import com.workfromhome.income.OneTimeActivity.NetworkChangeReceiver;
import com.workfromhome.income.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class SpinActivity extends AppCompatActivity {

    private MediaPlayer spinSound;
    private Button spinButton;
    private TextView totalSpin, WinningBal;
    private LuckyWheelView wheelView;
    private String spin_remain, winBal, previousIndex, spin_index_row;
    private DocumentReference documentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //broadcast receiver
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, filter);
        //toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Earn by Fun");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        documentReference = FirebaseFirestore.getInstance().collection("Spin_users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        findViewById(R.id.btn_add_money).setOnClickListener(v -> {
            Intent intent = new Intent(SpinActivity.this, SpinPaymentActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_withdraw).setOnClickListener(v -> {
            if (winBal.equals("0")) {
                Toast.makeText(SpinActivity.this, "Insufficient wallet amount.", Toast.LENGTH_LONG).show();
            } else if (Integer.parseInt(winBal) > 0 && Integer.parseInt(winBal) < 1000) {
                Toast.makeText(SpinActivity.this, "Minimum withdraw amount is Rs. 1000/-", Toast.LENGTH_LONG).show();
            } else {
                startActivity(new Intent(SpinActivity.this, SpinWithdrawActivity.class));
                finish();
            }
        });


        wheelView = findViewById(R.id.wheel);
        spinSound = MediaPlayer.create(this, R.raw.spinsoundeffect);

        int[] items = {35, 120, 180, 200, -280, 45, 230, 400, 325, 250, 65, 375, 100, 150, 350, 300};
        int[] colors = {
                Color.parseColor("#33ccff"),
                Color.parseColor("#44c8fb"),
                Color.parseColor("#55c4f7"),
                Color.parseColor("#66bff2"),
                Color.parseColor("#77bbee"),
                Color.parseColor("#88b7ea"),
                Color.parseColor("#A9C0ED"),
                Color.parseColor("#adc2eb"),
                Color.parseColor("#bdc0e8"),
                Color.parseColor("#aaaee1"),
                Color.parseColor("#bbaadd"),
                Color.parseColor("#d6b8e1"),
                Color.parseColor("#cca6d9"),
                Color.parseColor("#dda1d4"),
                Color.parseColor("#ee9dd0"),
                Color.parseColor("#FA9AD6")};

        int[] targetIndex1 = {13, 12, 10, 2, 5, 1, 0, 12, 4, 13, 10, 1, 0, 12, 13};
        int[] targetIndex2 = {3, 1, 0, 13, 10, 0, 12, 5, 1, 4, 13, 10, 12, 5, 1};
        int[] targetIndex3 = {6, 12, 10, 1, 0, 13, 5, 2, 4, 10, 12, 0, 5, 10, 5};
        int[] targetIndex4 = {2, 1, 5, 12, 0, 13, 10, 1, 12, 4, 0, 13, 5, 12, 5};
        int[] targetIndex5 = {1, 12, 10, 13, 0, 5, 2, 1, 10, 4, 13, 5, 12, 0, 1};

        List<LuckyItem> data = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            LuckyItem luckyItem = new LuckyItem();
            luckyItem.topText = "\u20B9 " + items[i];
            luckyItem.color = colors[i];
            data.add(luckyItem);
        }
        wheelView.setData(data);
        wheelView.setRotation(10);
        wheelView.setTouchEnabled(false);

        wheelView.setLuckyRoundItemSelectedListener(index -> {
            // indexx = index;
            spinButton.setEnabled(true);
            spinSound.stop();
            //addWinning(items[index]);
            final ProgressDialog progress = new ProgressDialog(SpinActivity.this);
            progress.setMessage("Updating Balance");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setCancelable(false);
            progress.show();

            new Handler().postDelayed(() -> {
                progress.dismiss();

                checkWinningLosing(items[index]);
            }, 4000);

        });

        spinButton = findViewById(R.id.spin_button);
        spinButton.setOnClickListener(v -> {
            int indexx = Integer.parseInt(previousIndex);

            if (spin_remain.equals("0")) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SpinActivity.this);
                alertDialogBuilder.setTitle("Buy Spin");
                alertDialogBuilder
                        .setMessage("Your remaining spin is 0. Please buy spin to Play")
                        .setCancelable(false)
                        .setPositiveButton("Buy Spin", (dialog, id) -> startActivity(new Intent(SpinActivity.this, SpinPaymentActivity.class)))
                        .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else if (Integer.parseInt(winBal) >= 1000) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SpinActivity.this);
                alertDialogBuilder.setTitle("Withdraw");
                alertDialogBuilder
                        .setMessage("Now you are eligible to withdraw money.")
                        .setCancelable(false)
                        .setPositiveButton("Go withdraw", (dialog, id) -> {
                            startActivity(new Intent(SpinActivity.this, SpinWithdrawActivity.class));
                            finish();
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } else {

                switch (Integer.parseInt(spin_index_row)) {
                    case 1:
                        wheelView.startLuckyWheelWithTargetIndex(targetIndex1[indexx]);
                        break;

                    case 2:
                        wheelView.startLuckyWheelWithTargetIndex(targetIndex2[indexx]);
                        break;

                    case 3:
                        wheelView.startLuckyWheelWithTargetIndex(targetIndex3[indexx]);
                        break;

                    case 4:
                        wheelView.startLuckyWheelWithTargetIndex(targetIndex4[indexx]);
                        break;

                    case 5:
                        wheelView.startLuckyWheelWithTargetIndex(targetIndex5[indexx]);
                        break;

                }
                //incrIndex();
                int i = ++indexx;
                documentReference.update("index", String.valueOf(i));
                spinButton.setEnabled(false);
                spinSound = MediaPlayer.create(SpinActivity.this, R.raw.spinsoundeffect);
                spinSound.start();

            }

        });


    }

    private void checkWinningLosing(int item) {


        if (item == -280) {
            showLosingDialog(item);
           // Toast.makeText(this, "sdddd", Toast.LENGTH_SHORT).show();

        } else {
            showWinningDialog(item);
            //Toast.makeText(this, "ggg", Toast.LENGTH_SHORT).show();
        }


    }

    private void showLosingDialog(final Object selectedItem) {

        final ProgressDialog progress = new ProgressDialog(SpinActivity.this);
        progress.setMessage("Updating Balance");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);


        FancyGifDialog build = new FancyGifDialog.Builder(this)
                .setTitle("Bad luck!!, You have lost \u20B9" + (int) selectedItem * -1 + ".")
                .setMessage("Amount will be deducted from your wallet.")
                .setGifResource(R.drawable.depressed)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        progress.show();
                        updateUserBal(selectedItem, progress);
                    }
                })
                .setPositiveBtnText("Spin More")
                .isCancellable(false)
                .setNegativeBtnText("Cancel").OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        updateUserBal(selectedItem, progress);
                    }
                }).build();

    }

    @Override
    protected void onStart() {
        super.onStart();

        documentReference.addSnapshotListener((documentSnapshot, e) -> {
            winBal = documentSnapshot.getString("winning_bal");
            spin_remain = documentSnapshot.getString("spin_bal");
            previousIndex = documentSnapshot.getString("index");
            spin_index_row = documentSnapshot.getString("spin_row");

            updateUI();

        });


    }

    private void updateUI() {

        totalSpin = findViewById(R.id.wallet_balance_deposits);
        WinningBal = findViewById(R.id.wallet_balance_winnings);

        totalSpin.setText(spin_remain);
        WinningBal.setText(winBal);
    }

    private void showWinningDialog(final Object selectedItem) {

        final ProgressDialog progress = new ProgressDialog(SpinActivity.this);
        progress.setMessage("Updating Balance");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.setCancelable(false);

        FancyGifDialog build = new FancyGifDialog.Builder(this)
                .setTitle("Congrats!!  \uD83E\uDD70, You win \u20B9" + selectedItem + ".")
                .setMessage("Amount will be added to your wallet.")
                .setGifResource(R.drawable.trophy)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        updateUserBal(selectedItem, progress);
                    }
                })
                .setPositiveBtnText("Spin More")
                .isCancellable(false)
                .setNegativeBtnText("Cancel").OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        updateUserBal(selectedItem, progress);
                    }
                }).build();
    }

    private void updateUserBal(Object selectedItem, ProgressDialog progressDialog) {

        int remain = Integer.parseInt(spin_remain) - 1;
        long hh = Long.parseLong(winBal);
        int winbale = (int) (hh + Long.parseLong(String.valueOf(selectedItem)));

        Map map = new HashMap();
        map.put("spin_bal", String.valueOf(remain));
        map.put("winning_bal", String.valueOf(winbale));

        documentReference.update(map).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}