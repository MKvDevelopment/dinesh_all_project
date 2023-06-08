package com.workfromhome.income.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.workfromhome.income.R;

import dmax.dialog.SpotsDialog;

public class SplashActivity extends AppCompatActivity {
    private AlertDialog alertDialog;
    private LottieAnimationView animationView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAppAd.disableSplash();
        StartAppSDK.setTestAdsEnabled(false);
        setContentView(R.layout.activity_splash);

        alertDialog = new SpotsDialog.Builder().setContext(SplashActivity.this)
                .setTheme(R.style.Custom)
                .setCancelable(false)
                .build();
        alertDialog.setCanceledOnTouchOutside(false);
        animationView = (LottieAnimationView) findViewById(R.id.animation_view);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                animationView.cancelAnimation();
                alertDialog.show();

                if (FirebaseAuth.getInstance().getUid() == null) {

                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    alertDialog.dismiss();
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    alertDialog.dismiss();
                    finish();
                }

            }
        }, 4000);


    }


    @Override
    protected void onStart() {
        super.onStart();
        animationView.playAnimation();

    }
}

