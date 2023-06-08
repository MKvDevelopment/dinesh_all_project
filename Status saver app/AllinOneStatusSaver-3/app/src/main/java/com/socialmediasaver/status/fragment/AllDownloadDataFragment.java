package com.socialmediasaver.status.fragment;

import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.activity.DownLoadMainActivity;
import com.socialmediasaver.status.activity.InstagramFolderActivity;
import com.socialmediasaver.status.activity.WhatsappFolderActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AllDownloadDataFragment extends Fragment {
    CardView instacardView, whatsappcardView, facebookcardView, twittercardView;
    private AdRequest adRequest;
    AdView adView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.download_main_layout, null);
        instacardView = rootView.findViewById(R.id.instacardView);
        whatsappcardView = rootView.findViewById(R.id.whatsappcardView);
        facebookcardView = rootView.findViewById(R.id.facebookcardView);
        twittercardView = rootView.findViewById(R.id.twittercardView);
        adView = rootView.findViewById(R.id.adView);
        onViewClicked();
        //checksubscription();
        return rootView;
    }


    private void checksubscription() {
        if (Subscription.equals("YES"))
        // if (SharePrefs.getInstance(FullImageActivity.this).getSubscribeValueFromPref())
        {
            adView.setVisibility(View.GONE);
        }else {
            adRequest = new AdRequest.Builder().build();
            bannerInit(getActivity().getApplicationContext());
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(adRequest);
        }

    }

    public void onViewClicked() {

        instacardView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), InstagramFolderActivity.class);
            startActivity(intent);
        });

        whatsappcardView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WhatsappFolderActivity.class);
            startActivity(intent);
        });

        facebookcardView.setOnClickListener(v ->
                ((DownLoadMainActivity) getActivity()).loadFragment(new FBDownloadedFragment(), true));

        twittercardView.setOnClickListener(v ->
                ((DownLoadMainActivity) getActivity()).loadFragment(new TwitterDownloadedFragment(), true));


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            getActivity().finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
