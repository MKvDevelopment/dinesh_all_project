package com.socialmediasaver.status.activity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.databinding.ActivityWhatsappBinding;
import com.socialmediasaver.status.fragment.WhatsappBusinessVideoFragment;
import com.socialmediasaver.status.fragment.WhatsappBussinessImageFragment;
import com.socialmediasaver.status.fragment.WhatsappImageFragment;
import com.socialmediasaver.status.fragment.WhatsappVideoFragment;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.SharePrefs;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.ads.banner.banner3d.Banner3D;

import java.util.ArrayList;
import java.util.List;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.Whatsp_video_link;
import static com.socialmediasaver.status.util.Utils.bannerInit;
import static com.socialmediasaver.status.util.Utils.createFileFolder;

public class WhatsappActivity extends AppCompatActivity {
    private ActivityWhatsappBinding binding;
    private WhatsappActivity activity;
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //Manifest.permission.MANAGE_EXTERNAL_STORAGE
    };

    private NetworkChangeReceiver broadcastReceiver;
    public boolean whatsapp, whatappBusiness=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_whatsapp);

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        Toolbar toolbar = findViewById(R.id.toolbarwhatsp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        activity = this;
        createFileFolder();
        PackageManager pm = WhatsappActivity.this.getPackageManager();
        boolean isInstalled1 = isPackageInstalled("com.whatsapp", pm);
        boolean isInstalled2 = isPackageInstalled("com.whatsapp.w4b", pm);

        if (isInstalled1 && isInstalled2) {
            binding.cbWhatsapp.setVisibility(View.VISIBLE);
            binding.cbWhatsappbusiness.setVisibility(View.VISIBLE);
            binding.cbWhatsapp.setChecked(true);
        } else if (isInstalled1) {
            binding.cbWhatsapp.setVisibility(View.VISIBLE);
            binding.cbWhatsappbusiness.setVisibility(View.VISIBLE);
            binding.cbWhatsappbusiness.setClickable(false);
            binding.cbWhatsappbusiness.setAlpha(0.5f);
            binding.cbWhatsapp.setChecked(true);
            binding.cbWhatsappbusiness.setChecked(false);

        } else if (isInstalled2) {
            binding.cbWhatsappbusiness.setVisibility(View.VISIBLE);
            binding.cbWhatsapp.setVisibility(View.VISIBLE);
            binding.cbWhatsapp.setClickable(false);
            binding.cbWhatsapp.setAlpha(0.5f);
            binding.cbWhatsappbusiness.setChecked(true);
            binding.cbWhatsapp.setChecked(false);
        }
        initViews();


    }

    private void checksubscription() {
        if (Subscription.equals("NO")) {
            Banner banner = (Banner) findViewById(R.id.startAppWhatspstatus);
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        checksubscription();
    }

    private void initViews() {

        binding.group.setOnCheckedChangeListener((group, checkedId) -> {
            if (((RadioButton) binding.group.getChildAt(0)).isChecked()) {
                whatsapp = true;
                whatappBusiness = false;
                initViews();
                binding.tabs.setupWithViewPager(binding.viewpager);
                binding.RLTab.setVisibility(View.VISIBLE);
                binding.RLTab1.setVisibility(View.GONE);
                setupViewPager(binding.viewpager);
                binding.viewpager1.setVisibility(View.GONE);
                binding.viewpager.setVisibility(View.VISIBLE);
               // Toast.makeText(WhatsappActivity.this, "whatsapp", Toast.LENGTH_SHORT).show();
                for (int i = 0; i < binding.tabs.getTabCount(); i++) {
                    TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
                    binding.tabs.getTabAt(i).setCustomView(tv);
                }

            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (!SharePrefs.getInstance(WhatsappActivity.this).getAndroid11WhatsappBusinessStatus()) {
                    askPermissionsAndroid30();
                    }
                    else {
                        whatsapp = false;
                        whatappBusiness = true;
                        initViews();
                        binding.tabs1.setupWithViewPager(binding.viewpager1);
                        binding.RLTab.setVisibility(View.GONE);
                        binding.RLTab1.setVisibility(View.VISIBLE);
                        binding.viewpager1.setVisibility(View.VISIBLE);
                        binding.viewpager.setVisibility(View.GONE);

                        setupViewPager2(binding.viewpager1);
                        for (int i = 0; i < binding.tabs1.getTabCount(); i++) {
                            TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
                            binding.tabs1.getTabAt(i).setCustomView(tv);
                        }
                    }
                } else {
                    whatsapp = false;
                    whatappBusiness = true;
                    initViews();
                    binding.tabs1.setupWithViewPager(binding.viewpager1);
                    binding.RLTab.setVisibility(View.GONE);
                    binding.RLTab1.setVisibility(View.VISIBLE);
                    binding.viewpager1.setVisibility(View.VISIBLE);
                    binding.viewpager.setVisibility(View.GONE);

                    setupViewPager2(binding.viewpager1);
                    for (int i = 0; i < binding.tabs1.getTabCount(); i++) {
                        TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
                        binding.tabs1.getTabAt(i).setCustomView(tv);
                    }
                }
            }
        });
        if (binding.cbWhatsapp.isChecked()){
            binding.tabs.setupWithViewPager(binding.viewpager);
            binding.RLTab.setVisibility(View.VISIBLE);
            binding.RLTab1.setVisibility(View.GONE);
            binding.viewpager1.setVisibility(View.GONE);
            binding.viewpager.setVisibility(View.VISIBLE);
            setupViewPager(binding.viewpager);

            for (int i = 0; i < binding.tabs.getTabCount(); i++) {
                TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
                binding.tabs.getTabAt(i).setCustomView(tv);
            }


        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!SharePrefs.getInstance(WhatsappActivity.this).getAndroid11WhatsappBusinessStatus()) {
                    askPermissionsAndroid30();
                } else {
                    binding.tabs1.setupWithViewPager(binding.viewpager1);
                    binding.RLTab.setVisibility(View.GONE);
                    binding.RLTab1.setVisibility(View.VISIBLE);
                    binding.viewpager1.setVisibility(View.VISIBLE);
                    binding.viewpager.setVisibility(View.GONE);
                    setupViewPager2(binding.viewpager1);
                    for (int i = 0; i < binding.tabs1.getTabCount(); i++) {
                        TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
                        binding.tabs1.getTabAt(i).setCustomView(tv);
                    }
                }
            }else {
                binding.tabs1.setupWithViewPager(binding.viewpager1);
                binding.RLTab.setVisibility(View.GONE);
                binding.RLTab1.setVisibility(View.VISIBLE);
                binding.viewpager1.setVisibility(View.VISIBLE);
                binding.viewpager.setVisibility(View.GONE);
                setupViewPager2(binding.viewpager1);
                for (int i = 0; i < binding.tabs1.getTabCount(); i++) {
                    TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
                    binding.tabs1.getTabAt(i).setCustomView(tv);
                }
            }

        }
    }

    private void setupViewPager2(ViewPager viewPager) {
        ViewPagerAdapter1 adapter = new ViewPagerAdapter1(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new WhatsappBussinessImageFragment(), getResources().getString(R.string.images));
        adapter.addFragment(new WhatsappBusinessVideoFragment(), getResources().getString(R.string.videos));
        viewPager.setAdapter(adapter);
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(activity.getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

        adapter.addFragment(new WhatsappImageFragment(), getResources().getString(R.string.images));
        adapter.addFragment(new WhatsappVideoFragment(), getResources().getString(R.string.videos));
        viewPager.setAdapter(adapter);
        //viewPager.setOffscreenPageLimit(1);

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
    class ViewPagerAdapter1 extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter1(FragmentManager fm, int behavior) {
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

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, packageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    public void askPermissionsAndroid30() {
        try {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            DocumentFile f = DocumentFile.fromFile(getExternalFilesDir("/media"));
            //DocumentFile f = DocumentFile.fromFile(getExternalFilesDir("Android/media/com.whatsapp/WhatsApp/Media/.Statuses"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, f.getUri());
            Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses");
            //Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia");
            DocumentFile file = DocumentFile.fromTreeUri(WhatsappActivity.this, uri);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, file.getUri());
            //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
            //Log.d(TAG, "uri: " + uri.toString());
            startActivityForResult(intent, 2296);

        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            DocumentFile f = DocumentFile.fromFile(getExternalFilesDir("/media"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp.w4b%2FWhatsApp Business%2FMedia%2F.Statuses");
            DocumentFile file = DocumentFile.fromTreeUri(WhatsappActivity.this, uri);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, file.getUri());

            startActivityForResult(intent, 2296);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (data.getData()!=null){
                Uri docUriTree = DocumentsContract.buildDocumentUriUsingTree(data.getData(), DocumentsContract.getTreeDocumentId(data.getData()));

                //DocumentFile documentFile = DocumentFile.fromTreeUri(this, data.getData());
                Uri myUri = Uri.parse(data.getData().toString());
                DocumentFile documentFile = DocumentFile.fromTreeUri(this, myUri);

                for (DocumentFile file : documentFile.listFiles()) {
                    if(file.isDirectory()){ // if it is sub directory
                        // Do stuff with sub directory
                        Log.d("yo",file.getUri() + "\n");
                    } else {
                        // Do stuff with normal file
                    }
                    Log.d("Uri",file.getUri() + "\n");

                }

                binding.tabs1.setupWithViewPager(binding.viewpager1);
                binding.RLTab.setVisibility(View.GONE);
                binding.RLTab1.setVisibility(View.VISIBLE);
                binding.viewpager1.setVisibility(View.VISIBLE);
                binding.viewpager.setVisibility(View.GONE);
                setupViewPager2(binding.viewpager1);
                for (int i = 0; i < binding.tabs1.getTabCount(); i++) {
                    TextView tv = (TextView) LayoutInflater.from(activity).inflate(R.layout.custom_tab, null);
                    binding.tabs1.getTabAt(i).setCustomView(tv);
                }
                SharePrefs.getInstance(WhatsappActivity.this).saveAndroid11WhatsappBusinessStatus(false);
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
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Whatsp_video_link));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
