package com.typingwork.jobathome.Activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.typingwork.jobathome.R;

public class FullScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        final String url = getIntent().getStringExtra("imageUrl");

        final ImageView image = findViewById(R.id.a_fullscreen_image);
        final TextView message = findViewById(R.id.a_fullscreen_message);

        message.setText("Loading Picture...");
        message.setVisibility(View.VISIBLE);

        Picasso.get()
                .load(url)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        message.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Picasso.get()
                                .load(url)
                                .into(image, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        message.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        message.setVisibility(View.VISIBLE);
                                        message.setText("Error: Could not load picture.");
                                    }

                                });
                    }
                });
    }
}