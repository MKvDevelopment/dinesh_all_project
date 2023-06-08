package com.socialmediasaver.status.activity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.socialmediasaver.status.util.Utils.RootDirectoryTwitter;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.Twiter_video_link;
import static com.socialmediasaver.status.util.Utils.createFileFolder;
import static com.socialmediasaver.status.util.Utils.startDownload;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.databinding.ActivityTwitterBinding;
import com.socialmediasaver.status.model.TwitterResponse;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;
import com.startapp.sdk.ads.banner.Mrec;
import com.startapp.sdk.adsbase.StartAppAd;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import io.reactivex.observers.DisposableObserver;

public class TwitterActivity extends AppCompatActivity {
    private ActivityTwitterBinding binding;
    TwitterActivity activity;
    CommonClassForAPI commonClassForAPI;
    private String VideoUrl;
    private ClipboardManager clipBoard;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_twitter);
        activity = this;
        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();

        Toolbar toolbar = findViewById(R.id.toolbartwitter);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
        initViews();

    }

    private void loadInterstial() {
        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.FULLPAGE);
        startAppAd.showAd();
    }

    private void loadbanner() {
        if (Subscription.equals("NO")) {
            Mrec mrec = (Mrec) findViewById(R.id.startAppMrectwitter);
            mrec.setVisibility(View.VISIBLE);
            mrec.showBanner();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        PasteText();
        loadbanner();
    }

    private void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        binding.loginBtn1.setOnClickListener(v -> {
            String LL = binding.etText.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            } else {
                if (Subscription.equals("NO")) {
                   loadInterstial();
                }
                Utils.showProgressDialog(activity);
                GetTwitterData();
            }
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });

    }

    private void GetTwitterData() {
        try {
            createFileFolder();
            URL url = new URL(binding.etText.getText().toString());
            String host = url.getHost();
            if (host.contains("twitter.com")) {
                Long id = getTweetId(binding.etText.getText().toString());
                if (id != null) {
                    callGetTwitterData(String.valueOf(id));
                }
            } else {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Long getTweetId(String s) {
        try {
            String[] split = s.split("\\/");
            String id = split[5].split("\\?")[0];
            return Long.parseLong(id);
        } catch (Exception e) {
            Log.d("TAG", "getTweetId: " + e.getLocalizedMessage());
            return null;
        }
    }

    private void PasteText() {
        try {
            binding.etText.setText("");
            String CopyIntent = getIntent().getStringExtra("CopyIntent");
            if (CopyIntent.equals("")) {

                if (!(clipBoard.hasPrimaryClip())) {

                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("twitter.com")) {
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("twitter.com")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("twitter.com")) {
                    binding.etText.setText(CopyIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGetTwitterData(String id) {
        String URL = "https://twittervideodownloaderpro.com/twittervideodownloadv2/index.php";
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    Utils.showProgressDialog(activity);
                    commonClassForAPI.callTwitterApi(observer, URL, id);
                }
            } else {
                Utils.setToast(activity, getResources().getString(R.string.no_net_conn));
            }


        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<TwitterResponse> observer = new DisposableObserver<TwitterResponse>() {
        @Override
        public void onNext(TwitterResponse twitterResponse) {
            Utils.hideProgressDialog(activity);
            try {
                VideoUrl = twitterResponse.getVideos().get(0).getUrl();
                if (twitterResponse.getVideos().get(0).getType().equals("image")) {
                    startDownload(VideoUrl, RootDirectoryTwitter, activity, getFilenameFromURL(VideoUrl, "image"));
                    binding.etText.setText("");
                } else {
                    VideoUrl = twitterResponse.getVideos().get(twitterResponse.getVideos().size() - 1).getUrl();
                    startDownload(VideoUrl, RootDirectoryTwitter, activity, getFilenameFromURL(VideoUrl, "mp4"));
                    binding.etText.setText("");

                }

            } catch (Exception e) {
                e.printStackTrace();
                Utils.setToast(activity, getResources().getString(R.string.no_media_on_tweet));
            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog(activity);
            e.printStackTrace();

        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog(activity);
        }
    };

    public String getFilenameFromURL(String url, String type) {
        if (type.equals("image")) {
            try {
                return new File(new URL(url).getPath()).getName() + "";
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return System.currentTimeMillis() + ".jpg";
            }
        } else {
            try {
                return new File(new URL(url).getPath()).getName() + "";
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return System.currentTimeMillis() + ".mp4";
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.how_to_use) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Twiter_video_link));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }


}