package com.socialmediasaver.status.activity;

import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.carlosmuvi.segmentedprogressbar.SegmentedProgressBar;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.adapter.InstagramStoriesPagerAdapter;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.model.story.FullDetailModel;
import com.socialmediasaver.status.model.story.ItemModel;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.observers.DisposableObserver;
import jp.shts.android.storiesprogressview.StoriesProgressView;

import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.OnStoryChangedCallback;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;


public class InstagramStoriesActivity extends AppCompatActivity {
    ViewPager instagram_stories_pager;
    String user_id,user_name,profile_pic;
    SegmentedProgressBar segmented_progressbar;
    CommonClassForAPI commonClassForAPI;
    public int currentPage = 0;
    public int currentPage1 = 0;
    Handler handler = null;
    Runnable runnable = null;
    StoriesProgressView stories;
    final long DELAY_MS = 6000;//delay in milliseconds before task is to be executed
    private int counter = 0;
    private ArrayList<String> mStoriesList = new ArrayList<>();
    private ArrayList<View> mediaPlayerArrayList = new ArrayList<>();
    private VideoView simpleVideoView;
    ProgressBar pr_loading_bar;
    LinearLayout progress_layout;
    private MediaController mediaController;
    ImageView imageView;
    RelativeLayout layout;
    VideoView videoview2;
    InstagramStoriesPagerAdapter mAdapter;
    long timeInMillisec=0;
    String extension="";
    int  position_increment=-1;
    TextView profile_user_name;
    ImageView story_icon;
    int  positionSelected=0;
    int tempPositionOffset = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.instagram_stories_activity);
        ///  imageView = findViewById(R.id.img);
        //  pr_loading_bar = findViewById(R.id.pr_loading_bar);
        progress_layout = findViewById(R.id.progress_layout);
        profile_user_name = findViewById(R.id.user_name);
        story_icon = findViewById(R.id.story_icon);

        // mediaController = new MediaController(InstagramStoriesActivity.this);
        // simpleVideoView = (VideoView) findViewById(R.id.videovieww); // initiate a video view
        // mediaController.setAnchorView(simpleVideoView);
        //simpleVideoView.setMediaController(mediaController);
//        simpleVideoView.setMediaController(null);
        layout = findViewById(R.id.layout);
        videoview2 = findViewById(R.id.videoview2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ActionBar actionBar = this.getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.setDisplayHomeAsUpEnabled(true);
//        }
        instagram_stories_pager = findViewById(R.id.instagram_stories_pager);
        stories = findViewById(R.id.stories);
        segmented_progressbar = findViewById(R.id.segmented_progressbar);
        user_id = getIntent().getStringExtra("user_id");
        user_name = getIntent().getStringExtra("user_name");
        profile_pic = getIntent().getStringExtra("image");
        Glide.with(InstagramStoriesActivity.this.getApplicationContext())
                .load(profile_pic)
                .into(story_icon);
        profile_user_name.setText(user_name);
        commonClassForAPI = CommonClassForAPI.getInstance(this);

        callStoriesDetailApi(user_id);

    }

    private void callStoriesDetailApi(String UserId) {
        try {
            Utils utils = new Utils(this);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    progress_layout.setVisibility(View.VISIBLE);
                    segmented_progressbar.setVisibility(View.GONE);
                    instagram_stories_pager.setVisibility(View.GONE);
                    commonClassForAPI.getFullDetailFeed(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                   // commonClassForAPI.getFullDetailFeed_(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                            + "; sessionid=" + SharePrefs.getInstance(this).getString(SharePrefs.SESSIONID));

                    Log.d("USERID",user_id);
                }
            } else {
                Utils.setToast(this, this
                        .getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<FullDetailModel> storyDetailObserver = new DisposableObserver<FullDetailModel>() {
        @Override
        public void onNext(FullDetailModel response) {

            try {
                progress_layout.setVisibility(View.GONE);
                segmented_progressbar.setVisibility(View.VISIBLE);
                instagram_stories_pager.setVisibility(View.VISIBLE);
                setViewpager(response.getReel_feed().getItems());
                // showStories(response.getReel_feed().getItems());
                // showStories_(response.getReel_feed().getItems());
                // setStoryView(counter,response.getReel_feed().getItems());
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

    public void prepareStoriesList(ArrayList<ItemModel> statusModelArrayList) {
        for (int i = 0; i < statusModelArrayList.size(); i++) {
            mStoriesList.add(statusModelArrayList.get(i).getImage_versions2().getCandidates().get(0).getUrl());
        }
    }



    public void showStories_(ArrayList<ItemModel> statusModelArrayList) {
        stories.setStoriesCount(statusModelArrayList.size());
        for (int i = 0; i < statusModelArrayList.size(); i++) {

            if (statusModelArrayList.get(i).getMedia_type() == 2) {
                simpleVideoView.setVisibility(View.VISIBLE);
                imageView.setVisibility(View.GONE);
                mediaPlayerArrayList.add(getVideoView(i, statusModelArrayList));
                //  Toast.makeText(InstagramStoriesActivity.this, statusModelArrayList.get(i).getVideo_versions().get(0).getUrl()+"", Toast.LENGTH_SHORT).show();

                //setStoryView(i,statusModelArrayList);

                //mediaPlayerArrayList.add(getVideoView(i,statusModelArrayList));
            } else {
                //simpleVideoView.setVisibility(View.GONE);
                // imageView.setVisibility(View.VISIBLE);
                //mediaPlayerArrayList.add(imageView);
                //Toast.makeText(InstagramStoriesActivity.this, statusModelArrayList.get(i).getImage_versions2().getCandidates().get(0).getUrl()+"", Toast.LENGTH_SHORT).show();
                mediaPlayerArrayList.add(getImageView(i, statusModelArrayList));
                //setStoryView(i,statusModelArrayList);
            }
        }
    }

    private View getVideoView(int i, ArrayList<ItemModel> statusModelArrayList) {

        Uri uri = Uri.parse(statusModelArrayList.get(i).getVideo_versions().get(0).getUrl());
        VideoView video = new VideoView(this);
        video.setVideoURI(uri);
        video.setLayoutParams(new FrameLayout.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layout.addView(video);
        return video;
    }

    private View getImageView(int i, ArrayList<ItemModel> statusModelArrayList) {
        ImageView imageView_ = new ImageView(this);
        LinearLayout.LayoutParams vp =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView_.setLayoutParams(vp);
        // imageView.setImageResource(id);
        layout.addView(imageView_);
        return imageView_;

    }

    public void showStories(ArrayList<ItemModel> statusModelArrayList) {
        final ArrayList<MyStory> myStories = new ArrayList<>();
        MyStory story2 = null;
        for (int i = 0; i < statusModelArrayList.size(); i++) {
            // myStories.add(statusModelArrayList.get(i).getImage_versions2().getCandidates().get(0).getUrl())
            if (statusModelArrayList.get(i).getMedia_type() == 2) {
                // getVideoView(i,statusModelArrayList);
                story2 = new MyStory(
                        statusModelArrayList.get(i).getVideo_versions().get(0).getUrl()

                );

//                videoview2.setVisibility(View.VISIBLE);
//                mediaController = new MediaController(InstagramStoriesActivity.this);
//                Uri uri = Uri.parse(statusModelArrayList.get(i).getVideo_versions().get(0).getUrl());
//                videoview2.setVideoURI(uri);
//                mediaController.setAnchorView(videoview2);
//                videoview2.setMediaController(null);
//                videoview2.start();
            } else {
                story2 = new MyStory(
                        statusModelArrayList.get(i).getImage_versions2().getCandidates().get(0).getUrl());
            }

            myStories.add(story2);
        }
        new StoryView.Builder(getSupportFragmentManager())
                .setStoriesList(myStories)
                .setStoryDuration(5000)
                .setTitleText("Hamza Al-Omari")
                .setSubtitleText("Damascus")
                .setStoryClickListeners(new StoryClickListeners() {
                    @Override
                    public void onDescriptionClickListener(int position) {
                        Toast.makeText(InstagramStoriesActivity.this, "Clicked: ", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onTitleIconClickListener(int position) {
                    }


                })
                .setOnStoryChangedCallback(new OnStoryChangedCallback() {
                    @Override
                    public void storyChanged(int position) {
                        //Toast.makeText(InstagramStoriesActivity.this, position + "", Toast.LENGTH_SHORT).show();
                    }
                })

                .setStartingIndex(0)
                .build()
                .show();
    }

//    public void setViewpager(ArrayList<ItemModel> statusModelArrayList) {
//        instagram_stories_pager.setOnTouchListener(new View.OnTouchListener()
//        {
//            @Override
//            public boolean onTouch(View v, MotionEvent event)
//            {
//                instagram_stories_pager.setCurrentItem(instagram_stories_pager.getCurrentItem());
//                return true;
//            }
//        });
//        InstagramStoriesPagerAdapter mAdapter = new InstagramStoriesPagerAdapter(getSupportFragmentManager(), statusModelArrayList);
//
//        instagram_stories_pager.setClipToPadding(false);
//        instagram_stories_pager.setPageMargin(12);
//        instagram_stories_pager.setAdapter(mAdapter);
//        //instagram_stories_pager.setCurrentItem(0, false);
//
//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//                if (currentPage == statusModelArrayList.size() - 1) {
//                    //currentPage = 0;
//                   // segmented_progressbar.setCompletedSegments(0);
//                    finish();
//                } else {
//                    currentPage++;
//                    segmented_progressbar.incrementCompletedSegments();
//
////                        for (int i=0;i<statusModelArrayList.size();i++){
////                            if (statusModelArrayList.get(i).getMedia_type()==2){
////                                double d = statusModelArrayList.get(i).getVideo_duration();
////                                long x = (long) d; // x = 1234
////                                segmented_progressbar.playSegment(x);
////                            }else {
////                                segmented_progressbar.playSegment(DELAY_MS);
////                            }
////
////                        }
//
//                }
//                instagram_stories_pager.setCurrentItem(currentPage, true);
//                handler.postDelayed(this, DELAY_MS);
//            }
//        };
//
//        instagram_stories_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
////                if (statusModelArrayList.get(position).getMedia_type()==2){
////                    double d = statusModelArrayList.get(position).getVideo_duration();
////                    long x = (long) d; // x = 1234
////                    segmented_progressbar.playSegment(x);
////                }else {
////                    segmented_progressbar.playSegment(DELAY_MS);
////                }
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                if(position == 1){  // if you want the second page, for example
//
//                }
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
//
//        handler.postDelayed(runnable, DELAY_MS);
//        segmented_progressbar.playSegment(DELAY_MS);
//
//        // segmented_progressbar.playSegment(DELAY_MS);
//
//
//
//
//
////        if (statusModelArrayList.get(instagram_stories_pager.getCurrentItem()).getMedia_type() == 2) {
////
////            double d = (statusModelArrayList.get(instagram_stories_pager.getCurrentItem()).getVideo_duration());
////            long x = (long) d; // x = 1234
////            handler.postDelayed(runnable, x);
////            segmented_progressbar.playSegment(x);
////        }else {
////            handler.postDelayed(runnable, DELAY_MS);
////            segmented_progressbar.playSegment(DELAY_MS);
////        }
//        //fill the next empty segment without animation
//        segmented_progressbar.setSegmentCount(statusModelArrayList.size()-1);
//
//
//    }


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
            //download();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setStoryView(final int counter, ArrayList<ItemModel> statusModelArrayList) {
        final View view = (View) mediaPlayerArrayList.get(counter);
        layout.addView(view);
        if (view instanceof VideoView) {
            final VideoView video = (VideoView) view;
            Uri uri = Uri.parse(statusModelArrayList.get(counter).getVideo_versions().get(0).getUrl());
            simpleVideoView.setVideoURI(uri);
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                            Log.d("mediaStatus", "onInfo: =============>>>>>>>>>>>>>>>>>>>" + i);
                            switch (i) {
                                case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START: {
                                    // mProgressBar.setVisibility(View.GONE);
                                    stories.resume();
                                    return true;
                                }
                                case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                                    //mProgressBar.setVisibility(View.VISIBLE);
                                    stories.pause();
                                    return true;
                                }
                                case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                                    //mProgressBar.setVisibility(View.VISIBLE);
                                    stories.pause();
                                    return true;

                                }
                                case MediaPlayer.MEDIA_ERROR_TIMED_OUT: {
                                    //  mProgressBar.setVisibility(View.VISIBLE);
                                    stories.pause();
                                    return true;
                                }

                                case MediaPlayer.MEDIA_INFO_AUDIO_NOT_PLAYING: {
                                    // mProgressBar.setVisibility(View.VISIBLE);
                                    stories.pause();
                                    return true;
                                }
                            }
                            return false;
                        }
                    });
                    simpleVideoView.start();
                    //mProgressBar.setVisibility(View.GONE);
                    stories.setStoryDuration(mediaPlayer.getDuration());
                    stories.startStories(0);
                }
            });
        } else if (view instanceof ImageView) {

            final ImageView image = (ImageView) view;
            // mProgressBar.setVisibility(View.GONE);

            Glide.with(this).load(statusModelArrayList.get(counter).getImage_versions2().getCandidates().get(0).getUrl()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    Toast.makeText(InstagramStoriesActivity.this, "Failed to load media...", Toast.LENGTH_SHORT).show();
                    //mProgressBar.setVisibility(View.GONE);
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    //mProgressBar.setVisibility(View.GONE);
                    stories.setStoryDuration(5000);
                    stories.startStories(0);
                    return false;
                }


            }).into(image);
        }
    }


    public void setViewpager(ArrayList<ItemModel> statusModelArrayList) {

        mAdapter = new InstagramStoriesPagerAdapter(getSupportFragmentManager(), statusModelArrayList);

        instagram_stories_pager.setClipToPadding(false);
        //instagram_stories_pager.setPageMargin(12);
        instagram_stories_pager.setAdapter(mAdapter);
        instagram_stories_pager.setOffscreenPageLimit(statusModelArrayList.size());
        stories.setStoriesCount(statusModelArrayList.size()); // <- set stories
        //instagram_stories_pager.setCurrentItem(0, false);

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                if (currentPage == statusModelArrayList.size() - 1) {
                    //currentPage = 0;
                    // segmented_progressbar.setCompletedSegments(0);
                   // finish();

                    //finishAffinity();
                } else {

                    currentPage++;
                    //segmented_progressbar.incrementCompletedSegments();
                   // instagram_stories_pager.setCurrentItem(currentPage, true);

                    // segmented_progressbar.incrementCompletedSegments();
                }
                Log.d("TAG","Called");
                segmented_progressbar.incrementCompletedSegments();
                stories.setStoriesCount(statusModelArrayList.size()); // <- set stories

                instagram_stories_pager.setCurrentItem(currentPage, true);
                //segmented_progressbar.playSegment(DELAY_MS);
           //     for (int i = 0; i < statusModelArrayList.size(); i++) {
//                    if (statusModelArrayList.get(currentPage).getMedia_type() == 2) {
//                        double d = statusModelArrayList.get(currentPage).getVideo_duration();
//
//                        long x = (long) d*1000; // x = 1234
//                        Toast.makeText(InstagramStoriesActivity.this, x+"inside play segment", Toast.LENGTH_SHORT).show();
//                        handler.postDelayed(this, x);
//                    } else {
//                        handler.postDelayed(this, DELAY_MS);
//                    }

               // }
               // handler.postDelayed(this, DELAY_MS);

            }
        };





//        instagram_stories_pager.setOnTouchListener(new View.OnTouchListener() {
//            private boolean moved;
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                view.performClick();
////                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
////                    moved = false;
////                }
////                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
////                    moved = false;
////                }
////                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
////                    if (!moved) {
////                        view.performClick();
////                    }
////                }
//                return true;
//            }
//
//        });
        instagram_stories_pager.setOnTouchListener(new View.OnTouchListener() {
            private boolean moved;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.performClick();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    moved = true;
                }
                if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {

//                    if (positionSelected - 1 < 0||positionSelected + 1 >= statusModelArrayList.size())
//                        moved=true;
//                    else {
//                        segmented_progressbar.setCompletedSegments((positionSelected));
//                        moved = false;
//
//                    }

//                   segmented_progressbar.pause();
//                    segmented_progressbar.setCompletedSegments((positionSelected));
//                        segmented_progressbar.resume();
//
                   Log.d("POSITION",positionSelected+"");
//                    //positionSelected=0;
                    moved=true;


                }
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (!moved) {
                        view.performClick();
                    }
                }
                return moved;
            }

        });
        instagram_stories_pager.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        Log.i("LOG", "Dayum!");
                    }
                }
        );
        instagram_stories_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


                // for (int i = 0; i < statusModelArrayList.size(); i++) {
               // Toast.makeText(InstagramStoriesActivity.this, statusModelArrayList.get(position).getVideo_versions()+"", Toast.LENGTH_SHORT).show();
              //  if (statusModelArrayList.get(position).getVideo_versions()!=null) {
                if (position_increment<position)
                    position_increment=position;
                else return;

                if (statusModelArrayList.get(position).getMedia_type() == 2) {
                     extension = statusModelArrayList.get(position).getVideo_versions().get(0).getUrl();
                    MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                    retriever.setDataSource(extension, new HashMap<String, String>());
                    String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    timeInMillisec = Long.parseLong(time);
                    retriever.release();
                   /// Log.d("POSITION",timeInMillisec+" & "+position+"");
                    //timeInMillisec=0;
                   // String duration=convertMillieToHMmSs(timeInMillisec); //use this duration
                    Toast.makeText(InstagramStoriesActivity.this, timeInMillisec+"", Toast.LENGTH_SHORT).show();

              //  }
               // if (statusModelArrayList.get(position).getMedia_type() == 2) {
               // if (extension.equals(".mp4")) {
                   // double d = statusModelArrayList.get(position).getVideo_duration();
                    segmented_progressbar.playSegment(timeInMillisec);
                    stories.setStoryDuration(timeInMillisec); // <- set a story duration
                    stories.startStories(); // <- start progress



                    // handler.postDelayed(runnable, x);
                    handler.postDelayed(runnable, timeInMillisec);

                    // long x = (long) d; // x = 1234
                    // segmented_progressbar.playSegment(x);
                    // double d = 1234.56;
                  //  long x = (Math.round(d)) * 1000; //1235
                    try {
                       // segmented_progressbar.playSegment(x);
                        //segmented_progressbar.playSegment(timeInMillisec);

                       // handler.postDelayed(runnable, x);
                       // handler.postDelayed(runnable, timeInMillisec);
                        Toast.makeText(InstagramStoriesActivity.this, "EXception"+position, Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        Toast.makeText(InstagramStoriesActivity.this, "EXception occur"+position, Toast.LENGTH_SHORT).show();

                    }

                    //Toast.makeText(InstagramStoriesActivity.this, statusModelArrayList.get(position).getMedia_type() + position+" outside play segment", Toast.LENGTH_SHORT).show();
                } else {
                    segmented_progressbar.playSegment(DELAY_MS);
                    stories.setStoryDuration(DELAY_MS);
                    stories.startStories(); // <- start progress

                    // <- set a story duration

                    handler.postDelayed(runnable, DELAY_MS);
                }

//                if (position > tempPositionOffset) {
//                    instagram_stories_pager.setCurrentItem(tempPositionOffset, false);
//                    segmented_progressbar.incrementCompletedSegments();
//                    segmented_progressbar.playSegment(DELAY_MS);
//
//                } else {
//                    tempPositionOffset = position;
//                }
               //  }

            }

            @Override
            public void onPageSelected(int position) {
               // Log.d("POSITIONSELECTED",position+"");
                positionSelected=position;


//                if(currentPage1 < position) {
//                    segmented_progressbar.incrementCompletedSegments();
//                    Toast.makeText(InstagramStoriesActivity.this, "Left", Toast.LENGTH_SHORT).show();
//
//                    // handle swipe LEFT
//                } else if(currentPage1 > position){
//
//                    //segmented_progressbar.();
//                    // handle swipe RIGHT
//                    Toast.makeText(InstagramStoriesActivity.this, "right", Toast.LENGTH_SHORT).show();
//
//                }
//                currentPage1 = position;
                if (position == 1) {  // if you want the second page, for example

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

       // handler.postDelayed(runnable, DELAY_MS);

        for (int i = 0; i < statusModelArrayList.size(); i++) {
            if (statusModelArrayList.get(i).getMedia_type() == 2) {
                double d = statusModelArrayList.get(i).getVideo_duration();

                long x = (long) d; // x = 1234
                //  Toast.makeText(InstagramStoriesActivity.this, x+"play segment", Toast.LENGTH_SHORT).show();
                // handler.postDelayed(runnable, x);
                // segmented_progressbar.playSegment(x);
                // handler.postDelayed(this, x);
            } else {
                //handler.postDelayed(runnable, DELAY_MS);
                //segmented_progressbar.playSegment(DELAY_MS);
                // handler.postDelayed(this, DELAY_MS);
            }

        }

        //segmented_progressbar.playSegment(DELAY_MS);
        //fill the next empty segment without animation
        if (statusModelArrayList.size() > 1) {
            segmented_progressbar.setSegmentCount(statusModelArrayList.size());
        } else if (statusModelArrayList.size() == 1) {
            segmented_progressbar.setSegmentCount(1);
        }


    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        //  startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }



    public static String convertMillieToHMmSs(long millie) {
        long seconds = (millie / 1000);
        long second = seconds % 60;
        long minute = (seconds / 60) % 60;
        long hour = (seconds / (60 * 60)) % 24;

        String result = "";
        if (hour > 0) {
            return String.format("%02d:%02d:%02d", hour, minute, second);
        }
        else {
            return String.format("%02d:%02d" , minute, second);
        }

    }


}
