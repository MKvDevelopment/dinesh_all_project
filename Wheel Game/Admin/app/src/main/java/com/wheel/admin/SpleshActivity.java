package com.wheel.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;

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