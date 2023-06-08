package com.skincarestudio.solution.Activty;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.skincarestudio.solution.R;
import com.skincarestudio.solution.Utils.NetworkChangeReceiver;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class TipDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_detail);

        Toolbar toolbar = findViewById(R.id.tipDetail_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Detail :-");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //check network connectivity
        NetworkChangeReceiver broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        WebView webView = findViewById(R.id.webView2);
        ImageView imageView = findViewById(R.id.imageView4);
        TextView tv_title = findViewById(R.id.textView8);

        Bundle bundle = getIntent().getExtras();
        String img = bundle.getString("img");
        String title = bundle.getString("title");
        String des = bundle.getString("des");

        Picasso.get().load(img).into(imageView);
        tv_title.setText(title);

        webView.loadData(des, "text/html", null);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }
}