package com.socialmediasaver.status.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.databinding.ActivityAboutUsBinding;
import com.socialmediasaver.status.util.NetworkChangeReceiver;

import static com.socialmediasaver.status.util.Utils.PrivacyPolicyUrl;

public class AboutUsActivity extends AppCompatActivity {

    private NetworkChangeReceiver broadcastReceiver;
    private ActivityAboutUsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_about_us);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        Toolbar toolbar = findViewById(R.id.toolbarabout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        binding.RLPrivacyPolicy.setOnClickListener((View.OnClickListener) view -> {
             Intent i  = new Intent(AboutUsActivity.this,WebviewAcitivity.class);
             i.putExtra("URL",PrivacyPolicyUrl);
             i.putExtra("Title", getResources().getString(R.string.prv_policy));
             startActivity(i);
         });

     //   binding.imBack.setOnClickListener((View.OnClickListener) view -> onBackPressed());


        binding.RLWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.website_tag)));
                    startActivity(browserIntent);
                }catch (Exception ex){
                    Log.d("WebsiteTag", "onClick: "+ex);
                }

            }
        });




        binding.RLEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=AboutUsActivity.this.getResources().getString(R.string.email_tag);

                Intent intent=new Intent(Intent.ACTION_SEND);
                String[] recipients={email};
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");
                startActivity(Intent.createChooser(intent, "Send mail"));

            }
        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
