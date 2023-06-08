package com.socialmediasaver.status.activity;

import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappImageShow;
import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappShow;
import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappVideoShow;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;
import static com.socialmediasaver.status.util.Utils.createFileFolder;

import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.adapter.WhatsappVideoPagerAdapter;
import com.socialmediasaver.status.model.WhatsappStatusModel;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.startapp.sdk.ads.banner.Banner;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VideoActivity extends AppCompatActivity {
    private VideoView simpleVideoView;
    private WhatsappStatusModel fileItem;
    private MediaController mediaController;
    public String SaveFilePath = RootDirectoryWhatsappShow + "/";
    private NetworkChangeReceiver broadcastReceiver;
    ViewPager pager;
    String position;
    ArrayList<WhatsappStatusModel> statusModelArrayList_;
    public String SaveFilePathImage = RootDirectoryWhatsappImageShow + "/";
    public String SaveFilePathVideo = RootDirectoryWhatsappVideoShow + "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Toolbar toolbar = findViewById(R.id.whatsp_video_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        pager = findViewById(R.id.pager);

        String url = getIntent().getStringExtra("url");
        position = getIntent().getStringExtra("type");

        statusModelArrayList_ = getIntent().getParcelableArrayListExtra("list");
        //fileItem = statusModelArrayList.get(Integer.parseInt(position));
        setViewpager();

        checksubscription();
    }


    public void setViewpager() {
        WhatsappVideoPagerAdapter mAdapter = new WhatsappVideoPagerAdapter(getSupportFragmentManager(), statusModelArrayList_);
        pager.setClipToPadding(false);
        pager.setPageMargin(12);
        pager.setAdapter(mAdapter);
        // current = pager.getCurrentItem();
        pager.setCurrentItem(Integer.parseInt(position), false);

        // set current item in the adapter to middle
        //pager.setOnPageChangeListener(new CircularViewPagerHandler(pager));
    }

    private void checksubscription() {
        if (Subscription.equals("NO")) {
            Banner banner = (Banner) findViewById(R.id.startAppWhatspvideostatus);
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
        }
    }
    private void download() {
        File destFile;
        createFileFolder();
        final String path = fileItem.getPath();
        String filename = path.substring(path.lastIndexOf("/") + 1);
        final File file = new File(path);
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            destFile = new File(SaveFilePathVideo);
        } else {
            destFile = new File(SaveFilePathImage);
        }
        try {
            FileUtils.copyFileToDirectory(file, destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileNameChange = filename.substring(12);
        File newFile;
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            newFile = new File(SaveFilePathVideo + fileNameChange);
        } else {
            newFile = new File(SaveFilePathImage + fileNameChange);

        }
        //  File newFile = new File(SaveFilePath + fileNameChange);
        String ContentType = "image/*";
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            ContentType = "video/*";
        } else {
            ContentType = "image/*";
        }
        MediaScannerConnection.scanFile(this.getApplicationContext(), new String[]{newFile.getAbsolutePath()}, new String[]{ContentType},
                new MediaScannerConnection.MediaScannerConnectionClient() {
                    public void onMediaScannerConnected() {
                    }

                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
        File from, to;
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            from = new File(SaveFilePathVideo, filename);

        } else {
            from = new File(SaveFilePathVideo, filename);

        }
        if (fileItem.getUri().toString().endsWith(".mp4")) {
            to = new File(SaveFilePathVideo, fileNameChange);
        } else {
            to = new File(SaveFilePathImage, fileNameChange);
        }

        // File from = new File(SaveFilePath, filename);
        // File to = new File(SaveFilePath, fileNameChange);
        from.renameTo(to);

        Toast.makeText(this.getApplicationContext(), "Saved to My Downloads", Toast.LENGTH_LONG).show();

    }
//    private void download() {
//        simpleVideoView.pause();
//
//        createFileFolder();
//        final String path = fileItem.getPath();
//        String filename = path.substring(path.lastIndexOf("/") + 1);
//        final File file = new File(path);
//        File destFile = new File(SaveFilePath);
//        try {
//            FileUtils.copyFileToDirectory(file, destFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String fileNameChange = filename.substring(12);
//        File newFile = new File(SaveFilePath + fileNameChange);
//        String ContentType = "image/*";
//        if (fileItem.getUri().toString().endsWith(".mp4")) {
//            ContentType = "video/*";
//        } else {
//            ContentType = "image/*";
//        }
//        MediaScannerConnection.scanFile(getApplicationContext(), new String[]{newFile.getAbsolutePath()}, new String[]{ContentType},
//                new MediaScannerConnection.MediaScannerConnectionClient() {
//                    public void onMediaScannerConnected() {
//                    }
//
//                    public void onScanCompleted(String path, Uri uri) {
//                    }
//                });
//
//        File from = new File(SaveFilePath, filename);
//        File to = new File(SaveFilePath, fileNameChange);
//        from.renameTo(to);
//
//        Toast.makeText(getApplicationContext(), "Saved to My Downloads", Toast.LENGTH_LONG).show();
//
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}