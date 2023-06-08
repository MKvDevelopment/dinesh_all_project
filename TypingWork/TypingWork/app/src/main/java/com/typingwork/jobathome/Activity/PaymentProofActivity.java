package com.typingwork.jobathome.Activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.typingwork.jobathome.databinding.ActivityPaymentProofBinding;

import java.util.Objects;

import im.delight.android.webview.AdvancedWebView;

public class PaymentProofActivity extends AppCompatActivity implements AdvancedWebView.Listener {

    private ActivityPaymentProofBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityPaymentProofBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        setSupportActionBar(binding.toolbarrWithdraw);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

       // mWebView = findViewById(R.id.web);
        binding.web.setListener(this, this);
        binding.web.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                AdvancedWebView newWebView = new AdvancedWebView(PaymentProofActivity.this);
                // myParentLayout.addView(newWebView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                return true;
            }

        });

        // disable third-party cookies only
        binding.web.setThirdPartyCookiesEnabled(false);
        binding.web.setDesktopMode(false);
        // or disable cookies in general
        binding.web.setCookiesEnabled(false);

        binding.web.setMixedContentAllowed(false);
        binding.web.loadUrl(MainActivity.paymentProofLink);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}