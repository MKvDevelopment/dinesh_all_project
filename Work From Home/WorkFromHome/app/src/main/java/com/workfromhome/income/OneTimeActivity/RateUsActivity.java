package com.workfromhome.income.OneTimeActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.workfromhome.income.R;

public class RateUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_us);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


