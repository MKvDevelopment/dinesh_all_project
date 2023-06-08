package com.socialmediasaver.status.util;


import android.app.Activity;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.socialmediasaver.status.R;

public class AdsManager {

    private static AdView mAdView;
    private static InterstitialAd mInterstitialAd;

    public static void showBannerAd(Activity activity, LinearLayout banner) {

        mAdView = new AdView(activity);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(activity.getResources().getString(R.string.BannerId));
        banner.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
    public static void showLargeBannerAd(Activity activity, LinearLayout banner) {

        mAdView = new AdView(activity);
        mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        mAdView.setAdUnitId(activity.getResources().getString(R.string.BannerId));
        banner.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    public static void loadInterstitialAd(final Activity activity) {

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, activity.getResources().getString(R.string.Interstial_Ad_Id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("Mediation", "onAdLoaded");

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("Mediation", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }

    public static void showIntersAd(Activity activity) {

        if (mInterstitialAd != null) {
            mInterstitialAd.show(activity);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    mInterstitialAd = null;
                    Log.d("TAG", "The ad was shown.");
                }
            });
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    public static void load_and_Show_interstitialAD(Activity activity) {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(activity, activity.getResources().getString(R.string.Interstial_Ad_Id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("Mediation", "onAdLoaded");
                        if (mInterstitialAd != null) {
                            mInterstitialAd.show(activity);
                            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    // Called when fullscreen content is dismissed.
                                    Log.d("TAG", "The ad was dismissed.");
                                }

                                @Override
                                public void onAdFailedToShowFullScreenContent(AdError adError) {
                                    // Called when fullscreen content failed to show.
                                    Log.d("TAG", "The ad failed to show.");
                                }

                                @Override
                                public void onAdShowedFullScreenContent() {
                                    // Called when fullscreen content is shown.
                                    // Make sure to set your reference to null so you don't
                                    // show it a second time.
                                    mInterstitialAd = null;
                                    Log.d("TAG", "The ad was shown.");
                                }
                            });
                        } else {
                            Log.d("TAG", "The interstitial ad wasn't ready yet.");
                        }

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("Mediation", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });

    }

    public static void dismissAds() {
        if (mAdView != null) {
            mAdView.destroy();

        }

        if (mInterstitialAd != null) {
            mInterstitialAd = null;

        }

    }

}

