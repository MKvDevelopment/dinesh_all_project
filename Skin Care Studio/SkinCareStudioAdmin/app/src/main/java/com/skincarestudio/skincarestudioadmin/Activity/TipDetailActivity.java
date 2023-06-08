package com.skincarestudio.skincarestudioadmin.Activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.skincarestudio.skincarestudioadmin.R;
import com.squareup.picasso.Picasso;

public class TipDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_detail);


        ImageView imageView = findViewById(R.id.imageView2);
        TextView tv_title = findViewById(R.id.textView3);
        WebView webView = findViewById(R.id.textView4);


        Bundle bundle = getIntent().getExtras();
        String img = bundle.getString("img");
        String title = bundle.getString("title");
        String des = bundle.getString("des");

        Picasso.get().load(img).into(imageView);
        tv_title.setText(title);


        webView.loadData(des, "text/html", null);

    }
}