package com.socialmediasaver.status.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.databinding.ActivityWebviewBinding;
import com.socialmediasaver.status.util.NetworkChangeReceiver;


public class WebviewAcitivity extends AppCompatActivity {

    ActivityWebviewBinding binding;
    String IntentURL, IntentTitle="";
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_webview);

        Toolbar toolbar = findViewById(R.id.toolbar_webview);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        IntentURL = getIntent().getStringExtra("URL");
        IntentTitle = getIntent().getStringExtra("Title");

        binding.swipeRefreshLayout.post(() -> {
            binding.swipeRefreshLayout.setRefreshing(true);
            LoadPage(IntentURL);
        }
        );
        binding.swipeRefreshLayout.setOnRefreshListener(() ->
                LoadPage(IntentURL));
    }

    public void LoadPage(String Url){
        binding.webView1.setWebViewClient(new MyBrowser());
        binding.webView1.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                if (progress == 100) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                } else {
                    binding.swipeRefreshLayout.setRefreshing(true);
                }
            }
        });
        binding.webView1.getSettings().setLoadsImagesAutomatically(true);
        binding.webView1.getSettings().setJavaScriptEnabled(true);
        binding.webView1.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webView1.loadUrl(Url);
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
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
