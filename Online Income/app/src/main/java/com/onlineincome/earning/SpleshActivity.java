package com.onlineincome.earning;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

public class SpleshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);

        FirebaseUser uid = FirebaseAuth.getInstance().getCurrentUser();

        StartAppSDK.setTestAdsEnabled(false);
        StartAppAd.disableSplash();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (uid == null) {
                    startActivity(new Intent(SpleshActivity.this, AuthActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SpleshActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, 3000);

    }
}