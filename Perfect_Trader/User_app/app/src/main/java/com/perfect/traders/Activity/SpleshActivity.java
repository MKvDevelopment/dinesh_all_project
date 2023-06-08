package com.perfect.traders.Activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.perfect.traders.R;

public class SpleshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splesh);

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getUid() == null) {
                startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
            } else {
                startActivity(new Intent(SpleshActivity.this, MainActivity.class));
            }
            finish();
        }, 3000);
    }
}