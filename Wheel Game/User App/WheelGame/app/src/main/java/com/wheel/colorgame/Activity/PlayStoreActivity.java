package com.wheel.colorgame.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wheel.colorgame.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import rubikstudio.library.LuckyWheelView;
import rubikstudio.library.model.LuckyItem;

public class PlayStoreActivity extends AppCompatActivity {

    List<LuckyItem> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_store);

        final LuckyWheelView luckyWheelView = findViewById(R.id.wheel);
        luckyWheelView.setTouchEnabled(false);

        LuckyItem luckyItem1 = new LuckyItem();
        luckyItem1.topText = "20";
        luckyItem1.color = 0xffFFF3E0;
        data.add(luckyItem1);

        LuckyItem luckyItem2 = new LuckyItem();
        luckyItem2.topText = "80";
        luckyItem2.color = 0xffFFE0B2;
        data.add(luckyItem2);

        LuckyItem luckyItem3 = new LuckyItem();
        luckyItem3.topText = "100";
        luckyItem3.color = 0xffFFCC80;
        data.add(luckyItem3);

        //////////////////
        LuckyItem luckyItem4 = new LuckyItem();
        luckyItem4.topText = "150";
        luckyItem4.color = 0xffFFF3E0;
        data.add(luckyItem4);

        LuckyItem luckyItem5 = new LuckyItem();
        luckyItem5.topText = "200";
        luckyItem5.color = 0xffFFE0B2;
        data.add(luckyItem5);

        LuckyItem luckyItem6 = new LuckyItem();
        luckyItem6.topText = "300";
        luckyItem6.color = 0xffFFCC80;
        data.add(luckyItem6);

        LuckyItem luckyItem7 = new LuckyItem();
        luckyItem7.topText = "500";
        luckyItem7.color = 0xffFFF3E0;
        data.add(luckyItem7);

        LuckyItem luckyItem8 = new LuckyItem();
        luckyItem8.topText = "700";
        luckyItem8.color = 0xffFFE0B2;
        data.add(luckyItem8);


        LuckyItem luckyItem9 = new LuckyItem();
        luckyItem9.topText = "900";
        luckyItem9.color = 0xffFFCC80;
        data.add(luckyItem9);
        ////////////////////////

        LuckyItem luckyItem10 = new LuckyItem();
        luckyItem10.topText = "1000";
        luckyItem10.color = 0xffFFE0B2;
        data.add(luckyItem10);

        /////////////////////

        luckyWheelView.setData(data);
        luckyWheelView.setRound(5);


        findViewById(R.id.btn_withdraww).setOnClickListener(view -> {

            String previous = ((TextView) findViewById(R.id.wallet_balance_winnings)).getText().toString();

            if (Integer.parseInt(previous) <= 100) {
                Toast.makeText(getApplicationContext(), "Insufficient Coins in Wallet!", Toast.LENGTH_SHORT).show();
            } else {
                int total = Integer.parseInt(previous) - 100;
                ((TextView) findViewById(R.id.wallet_balance_winnings)).setText(String.valueOf(total));

                @SuppressLint("CutPasteId") String previouss = ((TextView) findViewById(R.id.wallet_balance_deposits)).getText().toString();
                int totall = Integer.parseInt(previouss) + 1;
                ((TextView) findViewById(R.id.wallet_balance_deposits)).setText(String.valueOf(totall));
            }
        });

        findViewById(R.id.spin_button).setOnClickListener(view -> {
            @SuppressLint("CutPasteId") String previous = ((TextView) findViewById(R.id.wallet_balance_deposits)).getText().toString();

            if (Integer.parseInt(previous) <= 0) {
                Toast.makeText(getApplicationContext(), "Insufficient Remaining Spin", Toast.LENGTH_SHORT).show();
            } else {
                findViewById(R.id.spin_button).setEnabled(false);
                int total = Integer.parseInt(previous) - 1;
                ((TextView) findViewById(R.id.wallet_balance_deposits)).setText(String.valueOf(total));

                int index = getRandomIndex();
                luckyWheelView.startLuckyWheelWithTargetIndex(index);
            }
        });

        luckyWheelView.setLuckyRoundItemSelectedListener(index -> {

                    String previous = ((TextView) findViewById(R.id.wallet_balance_winnings)).getText().toString();
                    int total = Integer.parseInt(previous) + Integer.parseInt(data.get(index).topText);
                    ((TextView) findViewById(R.id.wallet_balance_winnings)).setText(String.valueOf(total));
                    findViewById(R.id.spin_button).setEnabled(true);
                }
        );


    }

    private int getRandomIndex() {
        Random rand = new Random();
        return rand.nextInt(data.size() - 1);
    }

    public void termsCondition(View view) {

        String url = "https://happyrakhisabko.blogspot.com/p/color-game-term-and-conditions.html";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    public void privacy(View view) {

        String url = "https://happyrakhisabko.blogspot.com/p/color-game-privacy-policy.html";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }
}