package com.wheel.colorgame.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.wheel.colorgame.R;

import java.util.Objects;

public class SpleshActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splesh);

        new Handler().postDelayed(() -> {
            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
            } else if (Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()).contains("play")
                    || Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).contains("cnx")){
                startActivity(new Intent(SpleshActivity.this, PlayStoreActivity.class));
            }else {
                startActivity(new Intent(SpleshActivity.this, MainActivity.class));
            }
            finish();
        }, 3000);

      /*  SharedPreferences sh = getSharedPreferences("MyPREFERENCES", Context.MODE_PRIVATE);
        String s=sh.getString("id","0");

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            new Handler().postDelayed(() -> {
                if (sh.getString("id", String.valueOf(0)).equals("1")){
                    Toast.makeText(getApplicationContext(), "ss", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SpleshActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(getApplicationContext(), "dd", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
                    finish();
                }
            },3000);

        } else {
            new Handler().postDelayed(() -> {
                if (FirebaseAuth.getInstance().getCurrentUser().getUid() == null) {

                    Toast.makeText(getApplicationContext(), "dd", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SpleshActivity.this, LoginActivity.class));
                } else if (Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()).contains("play")
                        || Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()).contains("cnx")) {
                    startActivity(new Intent(SpleshActivity.this, PlayStoreActivity.class));
                } else {
                    startActivity(new Intent(SpleshActivity.this, MainActivity.class));
                }
                finish();
            }, 3000);
        }*/

    }
}