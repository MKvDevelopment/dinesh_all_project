package com.earnmoney.adminearnmoney.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.earnmoney.adminearnmoney.R;
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (FirebaseAuth.getInstance().getUid() == null) {
                    Intent intent = new Intent(SpleshActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    startActivity(new Intent(SpleshActivity.this, MainActivity.class));
                    finish();
                }

            }
        }, 2000);
    }
}