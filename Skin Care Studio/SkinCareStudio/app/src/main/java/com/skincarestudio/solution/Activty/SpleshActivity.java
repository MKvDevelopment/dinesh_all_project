package com.skincarestudio.solution.Activty;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skincarestudio.solution.R;

public class SpleshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splesh);

        //for full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(() -> {
            if (user==null) {
                startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
                finish();
            } else {
                startActivity(new Intent(SpleshActivity.this, MainActivity.class));
                finish();
            }
        }, 2500);

    }
}