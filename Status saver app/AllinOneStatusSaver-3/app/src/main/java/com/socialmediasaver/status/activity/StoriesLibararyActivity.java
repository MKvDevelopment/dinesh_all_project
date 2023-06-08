package com.socialmediasaver.status.activity;

import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaStories;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;
import static com.socialmediasaver.status.util.Utils.startDownload;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.model.story.FullDetailModel;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;

import java.util.HashMap;

import io.reactivex.observers.DisposableObserver;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoriesLibararyActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    private StoriesProgressView storiesProgressView;
    private ImageView image;
    private VideoView videoview;
    private long pressTime = 0L;
    private long limit = 500L;
    private TextView profile_user_name;
    private ImageView story_icon;
    private String user_id, user_name, profile_pic;
    private CommonClassForAPI commonClassForAPI;
    private LinearLayout progress_layout;
    private int counter = 0;
    private String[] str;
    private String[] id;
    private LinearLayout RLStoryLayout;
    private FloatingActionButton settings;


    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_stories_libarary);

        Toolbar toolbar = findViewById(R.id.insta_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = (ImageView) findViewById(R.id.image);
        RLStoryLayout = (LinearLayout) findViewById(R.id.RLStoryLayout);
        settings = (FloatingActionButton) findViewById(R.id.settings);
        videoview = (VideoView) findViewById(R.id.videoview);
        storiesProgressView = (StoriesProgressView) findViewById(R.id.stories);
        profile_user_name = findViewById(R.id.user_name);
        story_icon = findViewById(R.id.story_icon);
        progress_layout = findViewById(R.id.progress_layout);
        user_id = getIntent().getStringExtra("user_id");
        user_name = getIntent().getStringExtra("user_name");
        profile_pic = getIntent().getStringExtra("image");
        Glide.with(StoriesLibararyActivity.this.getApplicationContext())
                .load(profile_pic)
                .into(story_icon);
        profile_user_name.setText(user_name);
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        checksubscription();

        callStoriesDetailApi(user_id);

    }

    private void checksubscription() {
        if (Subscription.equals("NO")) {
            Banner banner = (Banner) findViewById(R.id.startAppstorydownload);
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
        }

    }


    private void callStoriesDetailApi(String UserId) {
        try {
            Utils utils = new Utils(this);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    progress_layout.setVisibility(View.VISIBLE);
                    RLStoryLayout.setVisibility(View.GONE);
                    settings.setVisibility(View.GONE);

                    commonClassForAPI.getFullDetailFeed(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                            // commonClassForAPI.getFullDetailFeed_(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                            + "; sessionid=" + SharePrefs.getInstance(this).getString(SharePrefs.SESSIONID));

                    Log.d("USERID", user_id);
                }
            } else {
                Utils.setToast(this, this
                        .getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoview.start();
    }

    private DisposableObserver<FullDetailModel> storyDetailObserver = new DisposableObserver<FullDetailModel>() {
        @Override
        public void onNext(FullDetailModel response) {

            try {
                progress_layout.setVisibility(View.GONE);
                RLStoryLayout.setVisibility(View.VISIBLE);
                settings.setVisibility(View.VISIBLE);
                storiesProgressView.setStoriesCount(response.getReel_feed().getItems().size());
                counter = 0;

                str = new String[response.getReel_feed().getItems().size()];
                id = new String[response.getReel_feed().getItems().size()];

                // ArrayList to Array Conversion
                for (int j = 0; j < response.getReel_feed().getItems().size(); j++) {
                    id[j] = response.getReel_feed().getItems().get(j).getId();

                    // Assign each value to String array
                    if (response.getReel_feed().getItems().get(j).getMedia_type() == 2) {
                        str[j] = response.getReel_feed().getItems().get(j).getVideo_versions().get(0).getUrl();


                    } else {
                        str[j] = response.getReel_feed().getItems().get(j).getImage_versions2().getCandidates().get(0).getUrl();

                    }

                    if (str[counter].contains("mp4")) {
                        videoview.setVisibility(View.VISIBLE);
                        image.setVisibility(View.GONE);
                        //storiesProgressView.setStoryDuration(10000L);
                        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                        retriever.setDataSource(str[counter], new HashMap<String, String>());
                        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                        long timeInMillisec = Long.parseLong(time);
                        retriever.release();
                        String durations = convertMillieToHMmSs(timeInMillisec * 1000); //use this duration
                        Log.d("durationns", durations);

                        retriever.release();
                        storiesProgressView.setStoryDuration(timeInMillisec);
                        // storiesProgressView.setStoryDuration(15000L);
                        Uri myUri = Uri.parse(str[counter]);
                        videoview.setVideoPath(str[counter]);
                        videoview.start();
                        settings.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                download(str[counter], id[counter]);
                            }
                        });

                    } else {
                        videoview.setVisibility(View.GONE);
                        image.setVisibility(View.VISIBLE);
                        storiesProgressView.setStoryDuration(6000L);
                        Glide.with(StoriesLibararyActivity.this.getApplicationContext())
                                .load(str[counter])
                                .into(image);
                        settings.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                download(str[counter], id[counter]);
                            }
                        });
                        // image.setImageResource(resources[counter]);
                    }

                }
                storiesProgressView.setStoriesListener(StoriesLibararyActivity.this);

                storiesProgressView.startStories(counter);


                View reverse = findViewById(R.id.reverse);
                reverse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        storiesProgressView.reverse();
                    }
                });
                reverse.setOnTouchListener(onTouchListener);

                // bind skip view
                View skip = findViewById(R.id.skip);
                skip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        storiesProgressView.skip();
                    }
                });
                skip.setOnTouchListener(onTouchListener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(@NonNull Throwable e) {

        }

        @Override
        public void onComplete() {

        }

    };

    @Override
    public void onNext() {
        if (str.length > counter) {
            if (str[++counter].contains("mp4")) {
                Log.i("CHECK", videoview.getDuration() + "");
                videoview.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(str[counter], new HashMap<String, String>());
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time);
                retriever.release();
                String durations = convertMillieToHMmSs(timeInMillisec); //use this duration
                Log.d("durationns", durations);

                retriever.release();
                storiesProgressView.setStoryDuration(timeInMillisec);
                // storiesProgressView.setStoryDuration(10000L);
                Uri myUri = Uri.parse("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4");
                // videoview.setVideoURI(myUri);
                //videoview.setVideoPath("https://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4");
                videoview.setVideoPath(str[counter]);


                videoview.start();
                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        download(str[counter], id[counter]);
                    }
                });


            } else {
                videoview.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                storiesProgressView.setStoryDuration(6000L);
                Glide.with(StoriesLibararyActivity.this.getApplicationContext())
                        .load(str[counter])
                        .into(image);
                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        download(str[counter], id[counter]);
                    }
                });
                //  image.setImageResource(resources[++counter]);
            }
        }
    }

    @Override
    public void onPrev() {
        if ((counter - 1) < 0)
            return;
        if (str.length > counter) {
            if (str[--counter].contains("mp4")) {
                videoview.setVisibility(View.VISIBLE);
                image.setVisibility(View.GONE);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(str[counter], new HashMap<String, String>());
                String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                long timeInMillisec = Long.parseLong(time);
                retriever.release();
                String durations = convertMillieToHMmSs(timeInMillisec); //use this duration
                Log.d("durationns", durations);

                retriever.release();
                storiesProgressView.setStoryDuration(timeInMillisec);
                Uri myUri = Uri.parse(str[counter] + "");
                videoview.setVideoURI(myUri);
                videoview.start();
                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        download(str[counter], id[counter]);
                    }
                });

            } else {
                storiesProgressView.setStoryDuration(6000L);
                videoview.setVisibility(View.GONE);
                image.setVisibility(View.VISIBLE);
                Glide.with(StoriesLibararyActivity.this.getApplicationContext())
                        .load(str[counter])
                        .into(image);
                settings.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        download(str[counter], id[counter]);
                    }
                });
                //image.setImageResource(resources[--counter]);
            }
        }
    }

    @Override
    public void onComplete() {

    }

    private void loadInterstial() {
        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.FULLPAGE);
        startAppAd.showAd();
    }

    private void download(String url, String s) {
        if (Subscription.equals("NO")) {
            loadInterstial();
        }

        if (url.contains("mp4")) {
            //videoview.pause();
            //storiesProgressView.
            startDownload(url,
                    //  RootDirectoryInsta, getActivity().getApplicationContext(), "story_" + itemModel.getId() + ".mp4");
                    RootDirectoryInstaStories, StoriesLibararyActivity.this.getApplicationContext(), "story_" + s + ".mp4");
        } else {
            startDownload(url,
                    RootDirectoryInstaStories, StoriesLibararyActivity.this.getApplicationContext().getApplicationContext(), "story_" + s + ".png");
        }

    }


   /* public void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(
                this,
                getResources().getString(R.string.Interstial_Ad_Id),
                adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        StoriesLibararyActivity.this.interstitialAd = interstitialAd;
                        //Log.i(TAG, "onAdLoaded");
                        // Toast.makeText(InstaSearchUserDetailsActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
                        interstitialAd.setFullScreenContentCallback(
                                new FullScreenContentCallback() {
                                    @Override
                                    public void onAdDismissedFullScreenContent() {
                                        // Called when fullscreen content is dismissed.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        //InstaSearchUserDetailsActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad was dismissed.");
                                    }

                                    @Override
                                    public void onAdFailedToShowFullScreenContent(AdError adError) {
                                        // Called when fullscreen content failed to show.
                                        // Make sure to set your reference to null so you don't
                                        // show it a second time.
                                        StoriesLibararyActivity.this.interstitialAd = null;
                                        Log.d("TAG", "The ad failed to show.");
                                    }

                                    @Override
                                    public void onAdShowedFullScreenContent() {
                                        // Called when fullscreen content is shown.
                                        Log.d("TAG", "The ad was shown.");
                                    }
                                });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        //Log.i(TAG, loadAdError.getMessage());
                        interstitialAd = null;

                        String error =
                                String.format(
                                        "domain: %s, code: %d, message: %s",
                                        loadAdError.getDomain(), loadAdError.getCode(), loadAdError.getMessage());
                        Toast.makeText(
                                StoriesLibararyActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }*/

    @Override
    protected void onDestroy() {
        // Very important !
        storiesProgressView.destroy();
        super.onDestroy();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.option_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
//        } else if (item.getItemId() == R.id.download) {
//            //download();
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format("%02d:%02d", minute, second);
        }

    }
}