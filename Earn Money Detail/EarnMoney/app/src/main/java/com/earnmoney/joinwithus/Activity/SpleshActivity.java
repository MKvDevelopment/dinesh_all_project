package com.earnmoney.joinwithus.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.earnmoney.joinwithus.R;
import com.google.android.datatransport.runtime.scheduling.jobscheduling.SchedulerConfig;
import com.google.firebase.auth.FirebaseAuth;

public class SpleshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);
        try {
            String s=getIntent().getStringExtra("key");
            if(s.equals("1")){
                startActivity(new Intent(getApplicationContext(),ChatActivity.class));
            }
        }catch (Exception ignore){}
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getUid() == null) {

                startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
                finish();
            } else {
                startActivity(new Intent(SpleshActivity.this, MainActivity.class));
                finish();
            }
        }, 2300);
    }
}