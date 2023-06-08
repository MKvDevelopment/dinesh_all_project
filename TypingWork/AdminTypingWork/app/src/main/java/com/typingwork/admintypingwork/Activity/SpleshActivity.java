package com.typingwork.admintypingwork.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.auth.FirebaseAuth;
import com.typingwork.admintypingwork.R;

public class SpleshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);



        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);

        new Handler().postDelayed(() -> {
            progressDialog.show();

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
                progressDialog.dismiss();
                finish();
            } else {
                startActivity(new Intent(SpleshActivity.this, MainActivity.class));
                progressDialog.dismiss();
                finish();

            }
        }, 1000);
    }
}