package com.socialmediasaver.status.activity;

import static com.socialmediasaver.status.util.Utils.Subscription;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.model.InstagramSearch.InstaSearchUserDetailsModel;
import com.socialmediasaver.status.retrofit.Api;
import com.socialmediasaver.status.retrofit.ApiInterface;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;
import com.squareup.picasso.Transformation;
import com.startapp.sdk.ads.banner.Banner;
import com.startapp.sdk.adsbase.StartAppAd;

import io.reactivex.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstaSearchUserDetailsActivity extends AppCompatActivity {
    ImageView story_icon, story_icon1, story_icon2, story_icon3;
    TextView Follow, name, Followers;
    Toolbar insta_toolbar;
    String userName, Name;
    Call<InstaSearchUserDetailsModel> call;
    LinearLayout mainLayout;
    CommonClassForAPI commonClassForAPI;
    FloatingActionButton floatingActionButton;
    LinearLayout layout, download_image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insta_user_search_detail_activity);

        insta_toolbar = findViewById(R.id.insta_toolbar);
        setSupportActionBar(insta_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        name = findViewById(R.id.name);
        Followers = findViewById(R.id.Followers);
        layout = findViewById(R.id.layout);
        download_image = findViewById(R.id.download_image);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        Follow = findViewById(R.id.Follow);
        story_icon3 = findViewById(R.id.story_icon3);
        story_icon2 = findViewById(R.id.story_icon2);
        story_icon1 = findViewById(R.id.story_icon1);
        story_icon = findViewById(R.id.story_icon);
        mainLayout = findViewById(R.id.mainLayout);
        userName = getIntent().getStringExtra("userName");
        Name = getIntent().getStringExtra("Name");
        commonClassForAPI = CommonClassForAPI.getInstance(this);
        checksubscription();

        fetchUsers(userName);
        //callStoriesDetailApi(userName);
    }

    private void checksubscription() {
        if (Subscription.equals("NO")) {
            Banner banner = (Banner) findViewById(R.id.startAppinstaprofileDetail);
            banner.setVisibility(View.VISIBLE);
            banner.showBanner();
        }

    }

    private void callStoriesDetailApi(String UserId) {
        try {
            Utils utils = new Utils(this);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {

                    commonClassForAPI.getDeatil(storyObserver, "__a=1", UserId);


                }
            } else {
                Utils.setToast(this, this
                        .getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<InstaSearchUserDetailsModel> storyObserver = new DisposableObserver<InstaSearchUserDetailsModel>() {
        @Override
        public void onNext(InstaSearchUserDetailsModel response) {

            try {

                name.setText(response.getGraphql().getUser().getUsername());
                Glide.with(InstaSearchUserDetailsActivity.this).load(response.getGraphql().getUser().getProfile_pic_url_hd())
                        .thumbnail(0.2f).into(story_icon);
                Glide.with(InstaSearchUserDetailsActivity.this).load(response.getGraphql().getUser().getProfile_pic_url_hd())
                        .thumbnail(0.2f).into(story_icon3);
                Glide.with(InstaSearchUserDetailsActivity.this).load(response.getGraphql().getUser().getProfile_pic_url())
                        .thumbnail(0.2f).into(story_icon);
                Glide.with(InstaSearchUserDetailsActivity.this).load(response.getGraphql().getUser().getProfile_pic_url())
                        .thumbnail(0.2f).into(story_icon);
                Followers.setText(response.getGraphql().getUser().getEdge_followed_by().getCount() + "");
                Follow.setText(response.getGraphql().getUser().getEdge_follow().getCount() + "");
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

    private void loadInterstial() {
        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.VIDEO);
        startAppAd.showAd();
    }

    private void fetchUsers(String username) {

        ApiInterface apiInterface;
        apiInterface = (new Api().getClient("https://www.instagram.com/" + username + "/", true).create(ApiInterface.class));
        call = apiInterface.getusersDetails("ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                // commonClassForAPI.getFullDetailFeed_(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(this).getString(SharePrefs.USERID)
                + "; sessionid=" + SharePrefs.getInstance(this).getString(SharePrefs.SESSIONID), "1");
        call.enqueue(new Callback<InstaSearchUserDetailsModel>() {
            @Override
            public void onResponse(Call<InstaSearchUserDetailsModel> call, Response<InstaSearchUserDetailsModel> response) {


                if (response.code() == 200) {

                    mainLayout.setVisibility(View.VISIBLE);
                    name.setText(response.body().getGraphql().getUser().getUsername());


                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
                            .override(15, 15)
                            .thumbnail(0.2f).into(story_icon3);
                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                            .override(15, 15)
                            .thumbnail(0.2f).into(story_icon2);
                    //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);

                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                            //.override(15,15)
                            .thumbnail(0.2f).into(story_icon1);
                    Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                            .thumbnail(0.2f).into(story_icon);
                    // Picasso.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url()).transform(new Blur(InstaSearchUserDetailsActivity.this, 60)).into(story_icon1);
                    layout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                            intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
                            startActivity(intent);
                        }
                    });
                    floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                            intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
                            startActivity(intent);
                        }
                    });

                    Followers.setText(response.body().getGraphql().getUser().getEdge_followed_by().getCount() + "");
                    Follow.setText(response.body().getGraphql().getUser().getEdge_follow().getCount() + "");
                    story_icon3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Subscription.equals("NO")) {
                                loadInterstial();
                            }
                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
                                    //.override(15,15)
                                    .thumbnail(0.2f).into(story_icon3);
                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon2);
                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon1);
                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
                                    .thumbnail(0.2f).into(story_icon);
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url_hd());
                                    startActivity(intent);
                                }
                            });
                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url_hd());
                                    startActivity(intent);
                                }
                            });


                        }
                    });
                    story_icon2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //loadAd();

                            if (Subscription.equals("NO")) {
                                loadInterstial();
                            }
                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon3);
                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                                    // .override(15,15)
                                    .thumbnail(0.2f).into(story_icon2);
                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon1);
                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                                    .thumbnail(0.2f).into(story_icon);
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
                                    startActivity(intent);
                                }
                            });
                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
                                    startActivity(intent);
                                }
                            });


                        }
                    });
                    story_icon1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // loadAd();
                            if (Subscription.equals("NO")) {
                              loadInterstial();
                            }
                            story_icon3.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));
                            story_icon1.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.bordercopy));
                            story_icon2.setBackgroundDrawable(InstaSearchUserDetailsActivity.this.getResources().getDrawable(R.drawable.border));


                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url_hd())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon3);

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                                    .override(15, 15)
                                    .thumbnail(0.2f).into(story_icon2);
                            //Blurry.with(InstaSearchUserDetailsActivity.this).capture(story_icon1).into(story_icon1);

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                                    //.override(15,15)
                                    .thumbnail(0.2f).into(story_icon1);

                            Glide.with(InstaSearchUserDetailsActivity.this).load(response.body().getGraphql().getUser().getProfile_pic_url())
                                    .thumbnail(0.2f).into(story_icon);
                            layout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
                                    startActivity(intent);
                                }
                            });
                            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(InstaSearchUserDetailsActivity.this, FullinstaprofilePicActivity.class);
                                    intent.putExtra("url", response.body().getGraphql().getUser().getProfile_pic_url());
                                    startActivity(intent);
                                }
                            });

                        }
                    });


                } else {
                    mainLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<InstaSearchUserDetailsModel> call, Throwable t) {
                // Log error here since request failed

                call.cancel();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O_MR1)
    public class Blur implements Transformation {
        protected static final int UP_LIMIT = 25;
        protected static final int LOW_LIMIT = 1;
        protected final Context context;
        protected final int blurRadius;


        public Blur(Context context, int radius) {
            this.context = context;

            if (radius < LOW_LIMIT) {
                this.blurRadius = LOW_LIMIT;
            } else if (radius > UP_LIMIT) {
                this.blurRadius = UP_LIMIT;
            } else
                this.blurRadius = radius;
        }

        @Override
        public Bitmap transform(Bitmap source) {
            Bitmap sourceBitmap = source;

            Bitmap blurredBitmap;
            blurredBitmap = Bitmap.createBitmap(sourceBitmap);

            RenderScript renderScript = RenderScript.create(context);

            Allocation input = Allocation.createFromBitmap(renderScript,
                    sourceBitmap,
                    Allocation.MipmapControl.MIPMAP_FULL,
                    Allocation.USAGE_SCRIPT);


            Allocation output = Allocation.createTyped(renderScript, input.getType());

            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript,
                    Element.U8_4(renderScript));

            script.setInput(input);
            script.setRadius(blurRadius);

            script.forEach(output);
            output.copyTo(blurredBitmap);

            source.recycle();
            return blurredBitmap;
        }

        @Override
        public String key() {
            return "blurred";
        }
    }
}
