package com.socialmediasaver.status.activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.adapter.DownloadedPagerAdapter;
import com.socialmediasaver.status.databinding.ActivityFullViewBinding;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;

import java.io.File;
import java.util.ArrayList;

import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;
import static com.socialmediasaver.status.util.Utils.shareImage;
import static com.socialmediasaver.status.util.Utils.shareImageVideoOnWhatsapp;
import static com.socialmediasaver.status.util.Utils.shareVideo;

public class FullViewActivity extends AppCompatActivity {
    private ActivityFullViewBinding binding;
    private FullViewActivity activity;
    private ArrayList<File> fileArrayList;
    private int Position = -1;
    //ShowImagesAdapter showImagesAdapter;
    DownloadedPagerAdapter showImagesAdapter;
    private NetworkChangeReceiver broadcastReceiver;
    int  position_increment=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_full_view);

        activity = this;

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fileArrayList = (ArrayList<File>) getIntent().getSerializableExtra("ImageDataFile");
            Position = getIntent().getIntExtra("Position", 0);
        }
        initViews();

    }

    private void checksubscription() {
        if (Subscription.equals("NO")) {
            Banner banner = (Banner) findViewById(R.id.startAppfullView);
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
        }

    }

    public void initViews() {
        showImagesAdapter = new DownloadedPagerAdapter(getSupportFragmentManager(), fileArrayList);
        binding.vpView.setAdapter(showImagesAdapter);
        binding.vpView.setCurrentItem(Position);

        binding.vpView.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (position_increment<arg0)
                    position_increment=arg0;
                else return;
                if (Position!=arg0)
                Position = arg0;
                System.out.println("Current position==" + Position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int num) {
            }
        });

        binding.imDelete.setOnClickListener(view -> {
            if (Subscription.equals("NO"))
            {
                loadInterstial();
            }
            AlertDialog.Builder ab = new AlertDialog.Builder(activity);
            ab.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                boolean b = fileArrayList.get(Position).delete();
                if (b) {

                    deleteFileAA(Position);
                }
            });
            ab.setNegativeButton(getResources().getString(R.string.no), (dialog, id) ->
                    dialog.cancel());
            AlertDialog alert = ab.create();
            alert.setTitle(getResources().getString(R.string.do_u_want_to_dlt));
            alert.show();
        });

        binding.imShare.setOnClickListener(view -> {
            if (fileArrayList.get(Position).getName().contains(".mp4")) {
                shareVideo(activity, fileArrayList.get(Position).getPath());
            } else {
                shareImage(activity, fileArrayList.get(Position).getPath());
            }
        });
        binding.imWhatsappShare.setOnClickListener(view -> {
            if (fileArrayList.get(Position).getName().contains(".mp4")) {
                shareImageVideoOnWhatsapp(activity, fileArrayList.get(Position).getPath(), true);
            } else {
                shareImageVideoOnWhatsapp(activity, fileArrayList.get(Position).getPath(), false);
            }
        });

    }

    private void loadInterstial() {
        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.FULLPAGE);
        startAppAd.showAd();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        checksubscription();
    }

    public void deleteFileAA(int position) {
        fileArrayList.remove(position);
        if (fileArrayList.size()!=0) {
            if (fileArrayList.size() > Position) {
                Position = Position;
            } else {
                Position = 0;
            }
        }

        if (fileArrayList.size()!=0)
        initViews();

       // showImagesAdapter.notifyDataSetChanged();
        Utils.setToast(activity, getResources().getString(R.string.file_deleted));
        if (fileArrayList.size() == 0) {
            onBackPressed();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

}
