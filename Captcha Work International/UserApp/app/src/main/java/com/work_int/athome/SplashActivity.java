package com.work_int.athome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


      /*  StartAppAd.showSplash(this, savedInstanceState,
                new SplashConfig()
                        .setTheme(SplashConfig.Theme.GLOOMY)
                        .setAppName("Captcha Work")
                        .setLogo(R.drawable.ic_paytm_logo)   // resource ID
                        .setOrientation(SplashConfig.Orientation.AUTO)
                .setMaxAdDisplayTime(SplashConfig.MaxAdDisplayTime.SHORT)
                .setLoadingType(SplashConfig.getDefaultSplashConfig().getLoadingType())
                .setCustomScreen(R.layout.activity_splash)
        );*/

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getUid() == null) {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            finish();
        }, 5000);
    }
}