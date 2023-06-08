package com.socialmediasaver.status.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.model.story.ItemModel;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import static com.socialmediasaver.status.adapter.StoriesListAdapter.storyItemModelList;
import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaStories;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;
import static com.socialmediasaver.status.util.Utils.startDownload;

public class DownloadActivity extends AppCompatActivity {
    private VideoView simpleVideoView;
    private ItemModel itemModel;
    private AdView adView;
    private AdRequest adRequest;
    private MediaController mediaController;
    private InterstitialAd mInterstitialAd;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageView imageView = findViewById(R.id.img);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        mediaController = new MediaController(this);
        simpleVideoView = (VideoView) findViewById(R.id.videovieww); // initiate a video view
        mediaController.setAnchorView(simpleVideoView);
        simpleVideoView.setMediaController(mediaController);

        String url = getIntent().getStringExtra("url");
        String position = getIntent().getStringExtra("type");

        itemModel = storyItemModelList.get(Integer.parseInt(position));

        if (itemModel.getMedia_type() == 2) {
            simpleVideoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
            Uri uri = Uri.parse(url);
            simpleVideoView.setVideoURI(uri);
            simpleVideoView.start();
        } else {
            simpleVideoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext())
                    .load(url)
                    .into(imageView);
        }

        Glide.with(getApplicationContext())
                .load(url)
                .into(imageView);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnClickListener(v -> {
            download();
        });

        adView = findViewById(R.id.download_adView);
        checksubscription();
        new Handler().postDelayed(() ->
        {
            InAppReview();
        }, 3000);
    }

    private void InAppReview() {
        ReviewManager manager = ReviewManagerFactory.create(this);

        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(DownloadActivity.this, reviewInfo);
                flow.addOnSuccessListener(result -> {

                });
            }
        });
    }


    private void checksubscription() {
        if (Subscription.equals("YES"))
       // if (SharePrefs.getInstance(DownloadActivity.this).getSubscribeValueFromPref())
        {
            adView.setVisibility(View.GONE);

        }else {

            adRequest = new AdRequest.Builder().build();
            bannerInit(getApplicationContext());
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(adRequest);

        }

    }

    private void loadInterstial() {
        InterstitialAd.load(this, getResources().getString(R.string.Interstial_Ad_Id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        showInterstial();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });
    }


    private void download() {
        if (Subscription.equals("NO"))
        {
            loadInterstial();
        }
        if (itemModel.getMedia_type() == 2) {
            simpleVideoView.pause();
            startDownload(itemModel.getVideo_versions().get(0).getUrl(),
                    RootDirectoryInstaStories, getApplicationContext(), "story_" + itemModel.getId() + ".mp4");
        } else {
            startDownload(itemModel.getImage_versions2().getCandidates().get(0).getUrl(),
                    RootDirectoryInstaStories, getApplicationContext(), "story_" + itemModel.getId() + ".png");
        }

    }

    private void showInterstial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(DownloadActivity.this);
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
            download();
        }
        return super.onOptionsItemSelected(item);
    }

}
