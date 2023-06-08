package com.skincarestudio.skincarestudioadmin.Activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.skincarestudio.skincarestudioadmin.R;
import com.squareup.picasso.Picasso;

public class ProductDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ImageView imageView = findViewById(R.id.imageView5);
        TextView title = findViewById(R.id.textView6);
        TextView sbtitle = findViewById(R.id.textView7);
        TextView price = findViewById(R.id.textView8);
        TextView link = findViewById(R.id.textView9);
        TextView des = findViewById(R.id.textView5);

        Picasso.get().load(getIntent().getStringExtra("img")).into(imageView);
        title.setText(getIntent().getStringExtra("title"));
        sbtitle.setText(getIntent().getStringExtra("sbtitle"));
        price.setText(getIntent().getStringExtra("price"));
        link.setText(getIntent().getStringExtra("link"));
        des.setText(getIntent().getStringExtra("des"));


    }
}