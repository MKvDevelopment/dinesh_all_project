package com.socialmediasaver.status.activity;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;
import static com.socialmediasaver.status.util.Utils.createFileFolder;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.databinding.WhatsappFolderActivityBinding;
import com.socialmediasaver.status.fragment.WhatsAppImageDowndlededFragment;
import com.socialmediasaver.status.fragment.WhatsAppVideoDownlodedFragment;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.startapp.sdk.ads.banner.Banner;

import java.util.ArrayList;
import java.util.List;

public class WhatsappFolderActivity extends AppCompatActivity  {
    WhatsappFolderActivity activity;
    WhatsappFolderActivityBinding binding;

    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.whatsapp_folder_activity);
        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbar_whatsp_download);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        initViews();

        checksubscription();
    }
    private void checksubscription() {
        if (Subscription.equals("NO")) {
            Banner banner = (Banner) findViewById(R.id.startAppWhatspdownloadfolder);
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
        }

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
       // startActivity(new Intent(WhatsappFolderActivity.this,GalleryActivity.class));
        finish();
    }

    public void initViews() {
        setupViewPager(binding.viewpager);
        binding.tabs.setupWithViewPager(binding.viewpager);

        for (int i = 0; i < binding.tabs.getTabCount(); i++) {
            TextView tv=(TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab,null);
            binding.tabs.getTabAt(i).setCustomView(tv);
        }

        binding.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        createFileFolder();
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new WhatsAppImageDowndlededFragment(), "WhatsApp Images");
        adapter.addFragment(new WhatsAppVideoDownlodedFragment(), "WhatsApp Videos");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);

    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
