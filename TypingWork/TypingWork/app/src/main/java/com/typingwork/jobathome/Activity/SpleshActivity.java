package com.typingwork.jobathome.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.typingwork.jobathome.R;


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
        }, 4000);



    }
}