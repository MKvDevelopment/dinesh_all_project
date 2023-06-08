package com.dinesh.adminwrokfromhome.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.dinesh.adminwrokfromhome.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.show();

                if (FirebaseAuth.getInstance().getUid() == null) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    progressDialog.dismiss();
                    finish();
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    progressDialog.dismiss();
                    finish();
                }
            }
        }, 4000);


    }
}