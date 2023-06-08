package com.socialmediasaver.status.fragment;

import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaStories;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.bannerInit;
import static com.socialmediasaver.status.util.Utils.startDownload;

import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

import java.util.ArrayList;

public class InstagramPagerFragment extends Fragment {
    private VideoView simpleVideoView;
    private ItemModel itemModel;
    private AdView adView;
    private AdRequest adRequest;
    private MediaController mediaController;
    private InterstitialAd mInterstitialAd;
    private NetworkChangeReceiver broadcastReceiver;
    String Url,Type;
    ArrayList<ItemModel> list;
    boolean isVisible = false;

    public static InstagramPagerFragment newInstance(ItemModel banner, ArrayList<ItemModel> sliderListResponsesData, int position) {

        Bundle args = new Bundle();

        args.putString("url", banner.getImage_versions2().getCandidates().get(0).getUrl());
        args.putString("type", String.valueOf(position));
        args.putParcelableArrayList("list",sliderListResponsesData);

        args.putInt("position",position);

        InstagramPagerFragment fragment = new InstagramPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String url = getArguments().getString("url");
        String position = getArguments().getString("type");
        list = getArguments().getParcelableArrayList("list");
        int layout = R.layout.instagram_stories_pager_data;
        //int layout = R.layout.activity_full_image2;
        View root = inflater.inflate(layout, container, false);
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        getActivity().registerReceiver(broadcastReceiver, intentFilter);
        ImageView imageView = root.findViewById(R.id.img);
        ImageView iv_play = root.findViewById(R.id.iv_play);
        simpleVideoView = (VideoView) root.findViewById(R.id.videovieww); // initiate a video view
        itemModel = list.get(Integer.parseInt(position));

        if (itemModel.getMedia_type() == 2) {
            simpleVideoView.setVisibility(View.VISIBLE);
            imageView.setVisibility(View.GONE);
//            Uri uri = Uri.parse(itemModel.getVideo_versions().get(0).getUrl());
//            simpleVideoView.setVideoURI(uri);
//            if(isVisible)
//                //player.play();
//                simpleVideoView.start();

            mediaController = new MediaController(getActivity());
            Uri uri = Uri.parse(itemModel.getVideo_versions().get(0).getUrl());
//        if(simpleVideoView!=null)
//            simpleVideoView.stopPlayback();
            // Toast.makeText(getActivity(), isVisible+"", Toast.LENGTH_SHORT).show();
            simpleVideoView.setVideoURI(uri);
            mediaController.setAnchorView(simpleVideoView);
            simpleVideoView.setMediaController(null);

           // simpleVideoView.start();
        } else {
            simpleVideoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            Glide.with(getActivity().getApplicationContext())
                    .load(url)
                    .into(imageView);
        }
//        simpleVideoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (simpleVideoView.isPlaying()){
//                    simpleVideoView.pause();
//                    iv_play.setVisibility(View.VISIBLE);
//                }else {
//                    simpleVideoView.start();
//                    iv_play.setVisibility(View.GONE);
//                }
//            }
//        });

        Glide.with(getActivity().getApplicationContext())
                .load(url)
                .into(imageView);

        FloatingActionButton floatingActionButton = root.findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnClickListener(v -> {
            download();
        });
        simpleVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

            }
        });

        simpleVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
               // simpleVideoView.pause();

            }
        });

        adView = root.findViewById(R.id.download_adView);
        checksubscription();
        new Handler().postDelayed(() ->
        {
            //InAppReview();
        }, 3000);


        return root;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {


//        isVisible = isVisibleToUser;
//      //  Toast.makeText(getActivity(), isVisible+"", Toast.LENGTH_SHORT).show();
//        if(simpleVideoView!=null)
//            simpleVideoView.pause();

        super.setUserVisibleHint(isVisibleToUser);

            if (this.isVisible()) {
                if (!isVisibleToUser)   // If we are becoming invisible, then...
                {
                    //pause or stop video
                    simpleVideoView.pause();
                }

                if (isVisibleToUser) // If we are becoming visible, then...
                {
                    //play your video
                    simpleVideoView.start();
                }
            }

    }

    @Override
    public void onResume() {
        //if (itemModel.getMedia_type() == 2) {
            boolean t = getUserVisibleHint();
            setUserVisibleHint(t);
        //}
        super.onResume();

    }

    @Override
    public void onPause() {
       // {
            setUserVisibleHint(false);
      //  }
        super.onPause();
    }





    private void InAppReview() {
        ReviewManager manager = ReviewManagerFactory.create(getActivity());

        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewInfo);
                flow.addOnSuccessListener(result -> {

                });
            }
        });
    }


    private void checksubscription() {
        if (Subscription.equals("YES"))
      //  if (SharePrefs.getInstance(getActivity()).getSubscribeValueFromPref())
        {
            adView.setVisibility(View.GONE);

        }else {

            adRequest = new AdRequest.Builder().build();
            bannerInit(getActivity().getApplicationContext());
            adView.setVisibility(View.VISIBLE);
            adView.loadAd(adRequest);

        }

    }

    private void loadInterstial() {
        InterstitialAd.load(getActivity(), getResources().getString(R.string.Interstial_Ad_Id), adRequest,
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
//        if (Subscription.equals("NO"))
//        {
//            loadInterstial();
//        }
        if (itemModel.getMedia_type() == 2) {
            simpleVideoView.pause();
            startDownload(itemModel.getVideo_versions().get(0).getUrl(),
                  //  RootDirectoryInsta, getActivity().getApplicationContext(), "story_" + itemModel.getId() + ".mp4");
                    RootDirectoryInstaStories, getActivity().getApplicationContext(), "story_" + itemModel.getId() + ".mp4");
        } else {
            startDownload(itemModel.getImage_versions2().getCandidates().get(0).getUrl(),
                    RootDirectoryInstaStories, getActivity().getApplicationContext(), "story_" + itemModel.getId() + ".png");
        }

    }

    private void showInterstial() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(getActivity());
        }

    }



}
