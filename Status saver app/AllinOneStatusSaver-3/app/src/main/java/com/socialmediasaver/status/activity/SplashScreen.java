package com.socialmediasaver.status.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.socialmediasaver.status.R;
import com.startapp.sdk.ads.splash.SplashConfig;
import com.startapp.sdk.adsbase.StartAppAd;

public class SplashScreen extends AppCompatActivity {
    SplashScreen activity;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = activity = this;

       /* StartAppAd.showSplash(this, savedInstanceState,
                new SplashConfig()
                        .setTheme(SplashConfig.Theme.GLOOMY)
                        .setAppName("Social Media Saver")
                        .setLogo(R.drawable.logo)   // resource ID
                        .setOrientation(SplashConfig.Orientation.AUTO)
                        .setMaxAdDisplayTime(SplashConfig.MaxAdDisplayTime.SHORT)
                        .setLoadingType(SplashConfig.getDefaultSplashConfig().getLoadingType())
                        .setCustomScreen(R.layout.activity_splash_screen)
        );*/

    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        next();
    }

    private void next() {

        new Handler().postDelayed(() -> {
            Intent i = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(i);
        }, 1500);

    }


}
