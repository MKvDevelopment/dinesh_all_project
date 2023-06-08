package com.socialmediasaver.status.activity;

import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaImages;
import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaProfilePic;
import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaVideos;
import static com.socialmediasaver.status.util.Utils.RootDirectoryWhatsappShow;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.createFileFolder;
import static com.socialmediasaver.status.util.Utils.startDownload;

import android.content.IntentFilter;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.model.WhatsappStatusModel;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FullinstaprofilePicActivity extends AppCompatActivity {

    private WhatsappStatusModel fileItem;
    public String SaveFilePath = RootDirectoryWhatsappShow + "/";
    public String SaveFilePathImage = RootDirectoryInstaImages + "/";
    public String SaveFilePathVideo = RootDirectoryInstaVideos + "/";
    private String url;
    private ImageView imageView;
    private NetworkChangeReceiver broadcastReceiver;
    ArrayList<WhatsappStatusModel> statusModelArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instapic_image);

        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toolbar toolbar = findViewById(R.id.insta_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        url = getIntent().getStringExtra("url");

        imageView = findViewById(R.id.fullImag);

        Glide.with(getApplicationContext())
                .load(url)
                .into(imageView);

        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(v -> {
            download1(url);
        });


        checksubscription();
    }

    private void checksubscription() {
        if (Subscription.equals("NO")) {
            Banner banner = (Banner) findViewById(R.id.startAppprofileDownload);
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
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

    private void loadInterstial() {
        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.FULLPAGE);
        startAppAd.showAd();
    }

    private void download1(String PhotoUrl) {
        if (Subscription.equals("NO")) {
            loadInterstial();
        }
        startDownload(PhotoUrl, RootDirectoryInstaProfilePic, FullinstaprofilePicActivity.this, getImageFilenameFromURL(PhotoUrl));
    }

    public String getImageFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath().toString()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".png";
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
//        if (Subscription.equals("NO"))
//        {
//            initAdmobAds();
//        }
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


}