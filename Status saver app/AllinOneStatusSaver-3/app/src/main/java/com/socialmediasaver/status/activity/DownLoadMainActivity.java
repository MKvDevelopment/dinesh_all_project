package com.socialmediasaver.status.activity;

import static com.socialmediasaver.status.util.Utils.Subscription;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.fragment.AllDownloadDataFragment;
import com.startapp.sdk.ads.banner.Banner;

public class DownLoadMainActivity extends AppCompatActivity {
    public static Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_main_activity);

        toolbar = findViewById(R.id.my_downloads_toolbar);
       // toolbar.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadFragment(new AllDownloadDataFragment(), false);


        if (Subscription.equals("NO")) {
            Banner banner = (Banner) findViewById(R.id.startAppmydownladdd);
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
        }
    }

    public void loadFragment(Fragment fragment, Boolean bool) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (!bool) {
            transaction.add(R.id.frame_container, fragment);

        } else {
            transaction.replace(R.id.frame_container, fragment);
        }
        transaction.addToBackStack(null);
        transaction.commit();
        // checkTopStackfrag();

    }
    public void removeCurrentFragmentAndMoveBack() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        /*List<Fragment> fragmentList = fragmentManager.getFragments();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.remove(fragmentList.get(fragmentList.size()-1));
        trans.commit();*/

       /* int count=fragmentManager.getBackStackEntryCount();

        if(fragmentList.size()>1) {
            //Fragment topStackFrag= (Fragment) fragmentManager.getBackStackEntryAt(1);
            fragmentManager.beginTransaction().remove(fragmentList.get(fragmentList.size()-1)).commit();

        }else
            fragmentManager.popBackStack();
        fragmentManager.popBackStack();*/
        fragmentManager.popBackStack();
        fragmentManager.getBackStackEntryCount();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
           // finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (toolbar.getTitle().toString().equalsIgnoreCase("Facebook")){
            removeCurrentFragmentAndMoveBack();
            toolbar.setTitle("");
            return;
        }else if (toolbar.getTitle().toString().equalsIgnoreCase("Twitter")){
            removeCurrentFragmentAndMoveBack();
            toolbar.setTitle("");
            return;

        }else {
            super.onBackPressed();
            finish();
        }


    }
}