package com.socialmediasaver.status.activity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.socialmediasaver.status.R;
import com.socialmediasaver.status.adapter.StoriesListAdapter;
import com.socialmediasaver.status.adapter.UserListAdapter;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.databinding.ActivityInstagramBinding;
import com.socialmediasaver.status.interfaces.UserListInterface;
import com.socialmediasaver.status.model.Edge;
import com.socialmediasaver.status.model.EdgeSidecarToChildren;
import com.socialmediasaver.status.model.InstagramUrlSearchModel;
import com.socialmediasaver.status.model.ResponseModel;
import com.socialmediasaver.status.model.story.FullDetailModel;
import com.socialmediasaver.status.model.story.StoryModel;
import com.socialmediasaver.status.model.story.TrayModel;
import com.socialmediasaver.status.retrofit.Api;
import com.socialmediasaver.status.retrofit.ApiConstant;
import com.socialmediasaver.status.retrofit.ApiInterface;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.startapp.sdk.ads.banner.Cover;
import com.startapp.sdk.ads.banner.Mrec;
import com.startapp.sdk.adsbase.StartAppAd;

import java.io.File;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Objects;

import io.reactivex.observers.DisposableObserver;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static com.socialmediasaver.status.util.Utils.Insta_video_link;
import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaImages;
import static com.socialmediasaver.status.util.Utils.RootDirectoryInstaVideos;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.createFileFolder;
import static com.socialmediasaver.status.util.Utils.startDownload;

public class InstagramActivity extends AppCompatActivity implements UserListInterface {
    private ActivityInstagramBinding binding;
    private InstagramActivity activity;
    Context context;
    private ClipboardManager clipBoard;
    CommonClassForAPI commonClassForAPI;
    private String PhotoUrl;
    private String VideoUrl;
    UserListAdapter userListAdapter;
    StoriesListAdapter storiesListAdapter;
    private NetworkChangeReceiver broadcastReceiver;
    int counter = 0;
    Call<InstagramUrlSearchModel> call;
    ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_instagram);


        getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Toolbar toolbar = findViewById(R.id.toolbarinsta);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        context = activity = this;

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();

        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        context = activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

        binding.etText.setText("");
        PasteText();
        checksubscription();
        if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
            //checksubscription();
            loadbanner();
        }
    }


    private void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

        binding.loginBtn1.setOnClickListener(v -> {

            String LL = binding.etText.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(activity, getResources().getString(R.string.enter_url));
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            } else {
                // checksubscription();
                if (Subscription.equals("NO")) {
                    loadInterstial();
                }
                GetInstagramData();
            }
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });

        GridLayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        binding.RVUserList.setLayoutManager(mLayoutManager);
        binding.RVUserList.setNestedScrollingEnabled(false);
        mLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

        if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
            layoutCondition();
            callStoriesApi();
            binding.SwitchLogin.setChecked(true);

        } else {
            binding.SwitchLogin.setChecked(false);
            checksubscription();
        }

        binding.tvLogin.setOnClickListener(v -> {
            showAlertDialog();
        });

        binding.RLLoginInstagram.setOnClickListener(v -> {
            if (!SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
                Intent intent = new Intent(activity,
                        LoginActivity.class);
                startActivityForResult(intent, 100);
            } else {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setPositiveButton(getResources().getString(R.string.yes), (dialog, id) -> {
                    SharePrefs.getInstance(activity).putBoolean(SharePrefs.ISINSTALOGIN, false);
                    SharePrefs.getInstance(activity).putString(SharePrefs.COOKIES, "");
                    SharePrefs.getInstance(activity).putString(SharePrefs.CSRF, "");
                    SharePrefs.getInstance(activity).putString(SharePrefs.SESSIONID, "");
                    SharePrefs.getInstance(activity).putString(SharePrefs.USERID, "");

                    if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
                        binding.SwitchLogin.setChecked(true);
                    } else {
                        binding.SwitchLogin.setChecked(false);
                        binding.RVUserList.setVisibility(View.GONE);
                        binding.RVStories.setVisibility(View.GONE);
                        binding.tvViewStories.setText(activity.getResources().getText(R.string.view_stories));
                        binding.tvLogin.setVisibility(View.VISIBLE);
                    }
                    dialog.cancel();

                });
                ab.setNegativeButton(getResources().getString(R.string.cancel), (dialog, id) -> dialog.cancel());
                AlertDialog alert = ab.create();
                alert.setTitle("Alert!");
                alert.setMessage("If you logout your account, you can't view story and can't Download Video's and Story's from private account");
                alert.show();
            }

        });

        GridLayoutManager mLayoutManager1 = new GridLayoutManager(getApplicationContext(), 3);
        binding.RVStories.setLayoutManager(mLayoutManager1);
        binding.RVStories.setNestedScrollingEnabled(false);
        mLayoutManager1.setOrientation(RecyclerView.VERTICAL);

    }

    private void showAlertDialog() {
        androidx.appcompat.app.AlertDialog custome_dialog;

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.insta_login_alert_layout, null, false);

        Button allow = view.findViewById(R.id.button4);
        Button deny = view.findViewById(R.id.button5);

        builder.setView(view);

        custome_dialog = builder.create();
        custome_dialog.setCanceledOnTouchOutside(false);
        custome_dialog.show();

        allow.setOnClickListener(v -> {
            custome_dialog.dismiss();
            Intent intent = new Intent(activity,
                    LoginActivity.class);
            startActivityForResult(intent, 100);
        });
        deny.setOnClickListener(v -> {
            custome_dialog.dismiss();
        });
    }

    private void checksubscription() {
        if (FirebaseAuth.getInstance().getUid() != null) {
            DocumentReference documentReference1 = FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(Objects.requireNonNull(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail()));
            documentReference1.addSnapshotListener((value1, error1) -> {
                assert value1 != null;
                if (value1.exists()) {
                    Subscription = value1.getString("subscription");
                    loadbanner();
                } else {
                    Subscription = "NO";
                }
            });
        } else {
            Subscription = "NO";
            loadbanner();
        }
    }

    private void loadbanner() {
        if (Subscription.equals("NO")) {
            Mrec mrec = (Mrec) findViewById(R.id.startAppMrecinsta);
            mrec.setVisibility(View.VISIBLE);
            mrec.showBanner();
        }
    }

    public void layoutCondition() {
        binding.tvViewStories.setText(activity.getResources().getString(R.string.stories));
        binding.tvLogin.setVisibility(View.GONE);

    }

    private void loadInterstial() {
        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.FULLPAGE);
        startAppAd.showAd();
    }

    private void GetInstagramData() {
        try {
            createFileFolder();
            URL url = new URL(binding.etText.getText().toString());
            String host = url.getHost();
            if (host.equals("www.instagram.com")) {
                counter = counter + 1;
                // Toast.makeText(InstagramActivity.this, counter+" CLICKED", Toast.LENGTH_SHORT).show();
                //callDownload(binding.etText.getText().toString());
                fetchUsers(binding.etText.getText().toString());
            } else {
                Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void PasteText() {
        try {
            binding.etText.setText("");
            String CopyIntent = getIntent().getStringExtra("CopyIntent");

            if (CopyIntent.equals("") || !CopyIntent.contains("instagram.com")) {
                Log.e("taran1", CopyIntent);
                if (!(clipBoard.hasPrimaryClip())) {
                    Log.e("taran2", CopyIntent);
                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    Log.e("taran3", CopyIntent);
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("instagram.com")) {
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", " ");
                        clipboard.setPrimaryClip(clip);
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("instagram.com")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                Log.e("taran", CopyIntent);
                if (CopyIntent.contains("instagram.com")) {
                    binding.etText.setText(CopyIntent);
                    getIntent().removeExtra("CopyIntent");
                    getIntent().setData(null);
                    getIntent().setFlags(0);
                    // getIntent().removeExtra("CopyIntent");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            if (!(clipBoard.hasPrimaryClip())) {
            } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("instagram.com")) {
                    binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                }

            } else {
                ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                if (item.getText().toString().contains("instagram.com")) {
                    binding.etText.setText(item.getText().toString());
                }

            }
        }
    }

    private String getUrlWithoutParameters(String url) {
        try {
            URI uri = new URI(url);
            return new URI(uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    null, // Ignore the query part of the input url
                    uri.getFragment()).toString();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.setToast(activity, getResources().getString(R.string.enter_valid_url));
            return "";
        }
    }

    //private void callDownload(String Url) {
    private void callDownload(String Url) {
        String UrlWithoutQP = getUrlWithoutParameters(Url);
        UrlWithoutQP = UrlWithoutQP.replace("https://www.instagram.com/reel/", "");
        UrlWithoutQP = UrlWithoutQP.replace("/", "");

        //UrlWithoutQP = UrlWithoutQP + "?__a=1";
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    Utils.showProgressDialog(activity);
//                    commonClassForAPI.callResult(instaObserver, UrlWithoutQP,
//                            "sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));

//                    commonClassForAPI.callResult(instaObserver, UrlWithoutQP+"ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID),
//                         "");
//                    commonClassForAPI.callResult(instaObserver, UrlWithoutQP,
//                            "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
//                                           + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID)+ "; CSRFToken=" + SharePrefs.getInstance(activity).getString(SharePrefs.CSRF));

//                    commonClassForAPI.callResult(instaObserver, UrlWithoutQP,
//                            "CSRFToken=" + SharePrefs.getInstance(activity).getString(SharePrefs.CSRF)
//                    );

                    /////////
                    commonClassForAPI.callResult(instaObserver, "https://instagram-scraper-2022.p.rapidapi.com/ig/reel/?shortcode=" + UrlWithoutQP,
                            "x-rapidapi-host=" + "instagram-scraper-2022.p.rapidapi.com" + "; x-rapidapi-key=" + "9737316f07msh5fffdb77ac17f8ap157e06jsnc3fbd593a06e"


                            //////////
                    );
//                    commonClassForAPI.callResult(instaObserver, UrlWithoutQP,
//                            "");
//                            commonClassForAPI.callResult(instaObserver, UrlWithoutQP,
//                                    "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
//                                            + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));
//                    commonClassForAPI.callResult(instaObserver, UrlWithoutQP,
//                            "");
                }
            } else {
                Utils.setToast(activity, getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchUsers(String Url) {
        Utils.showProgressDialog(activity);
        String UrlWithoutQP = getUrlWithoutParameters(Url);
        apiInterface = (new Api().getClient(ApiConstant.BASE_URL).create(ApiInterface.class));


        if (Url.contains("/tv")) {
            //UrlWithoutQP = UrlWithoutQP.replace("https://www.instagram.com/tv/", "");
            String s = UrlWithoutQP;
            UrlWithoutQP = UrlWithoutQP.substring(s.indexOf("tv/"));
            UrlWithoutQP.trim();
            UrlWithoutQP = UrlWithoutQP.replace("tv", "");
            UrlWithoutQP = UrlWithoutQP.replace("/", "");
            call = apiInterface.getURLVideoTV(UrlWithoutQP);
        } else if (Url.contains("/p")) {
            String s = UrlWithoutQP;
            UrlWithoutQP = UrlWithoutQP.substring(s.indexOf("p/"));
            UrlWithoutQP.trim();
            UrlWithoutQP = UrlWithoutQP.replace("p/", "");
            UrlWithoutQP = UrlWithoutQP.replace("/", "");
            call = apiInterface.getPVideoUrl(UrlWithoutQP);

        } else {
            //UrlWithoutQP = UrlWithoutQP.replace("https://www.instagram.com/reel/", "");
            String s = UrlWithoutQP;
            UrlWithoutQP = UrlWithoutQP.substring(s.indexOf("reel/"));
            UrlWithoutQP.trim();
            //UrlWithoutQP = UrlWithoutQP.replaceAll("reel/", "");
            UrlWithoutQP = UrlWithoutQP.replace("reel", "");
            UrlWithoutQP = UrlWithoutQP.replace("/", "");

            call = apiInterface.getURLVideo(UrlWithoutQP);
        }

        //call = apiInterface.getURLVideo(UrlWithoutQP);
        call.enqueue(new Callback<InstagramUrlSearchModel>() {
            @Override
            public void onResponse(Call<InstagramUrlSearchModel> call, Response<InstagramUrlSearchModel> response) {
                Utils.hideProgressDialog(activity);
                if (response.body()!=null) {
                    InstagramUrlSearchModel root = response.body();
                    if (response.code() == 200) {

//                    if (Url.contains("/p")) {
//                        if (root.isIs_video()){
//                            VideoUrl = root.getVideo_url();
//                            startDownload(VideoUrl, RootDirectoryInstaVideos, activity, getVideoFilenameFromURL(VideoUrl));
//                            binding.etText.setText("");
//                            VideoUrl = "";
//                        }else {
//                            PhotoUrl = root.getDisplay_url();
//                            startDownload(PhotoUrl, RootDirectoryInstaImages, activity, getImageFilenameFromURL(PhotoUrl));
//                            PhotoUrl = "";
//                            binding.etText.setText("");
//                        }
//
                        if (root.getEdge_sidecar_to_children() != null) {
                            List<Edge> edgeArrayList = root.getEdge_sidecar_to_children().getEdges();
                            for (int i = 0; i < edgeArrayList.size(); i++) {
                                if (edgeArrayList.get(i).getNode().isIs_video()) {
                                    VideoUrl = edgeArrayList.get(i).getNode().getVideo_url();
                                    startDownload(VideoUrl, RootDirectoryInstaVideos, activity, getVideoFilenameFromURL(VideoUrl));
                                    binding.etText.setText("");
                                    VideoUrl = "";

                                } else {
                                    PhotoUrl = edgeArrayList.get(i).getNode().getDisplay_resources().get(edgeArrayList.get(i).getNode().getDisplay_resources().size() - 1).getSrc();
                                    startDownload(PhotoUrl, RootDirectoryInstaImages, activity, getImageFilenameFromURL(PhotoUrl));
                                    PhotoUrl = "";
                                    binding.etText.setText("");
                                }
                            }

                        } else {
                            boolean isVideo = root.isIs_video();
//                        VideoUrl = root.getVideo_url();
//                        startDownload(VideoUrl, RootDirectoryInstaVideos, activity, getVideoFilenameFromURL(VideoUrl));
//                        binding.etText.setText("");
//                        VideoUrl = "";
                            if (isVideo) {
                                VideoUrl = root.getVideo_url();
                                startDownload(VideoUrl, RootDirectoryInstaVideos, activity, getVideoFilenameFromURL(VideoUrl));
                                VideoUrl = "";
                                binding.etText.setText("");
                            } else {
                                PhotoUrl = root.getDisplay_url();

                                startDownload(PhotoUrl, RootDirectoryInstaImages, activity, getImageFilenameFromURL(PhotoUrl));
                                PhotoUrl = "";
                                binding.etText.setText("");
                            }

                        }


                    } else if (response.code() != 200) {
                        Toast.makeText(InstagramActivity.this, getResources().getString(R.string.insta_API_error), Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(InstagramActivity.this, getResources().getString(R.string.insta_API_error), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<InstagramUrlSearchModel> call, Throwable t) {
                // Log error here since request failed
                Utils.hideProgressDialog(activity);
                call.cancel();
            }
        });
    }

    private DisposableObserver<JsonObject> instaObserver = new DisposableObserver<JsonObject>() {
        @Override
        public void onNext(JsonObject versionList) {
            Utils.hideProgressDialog(activity);
            try {
                Type listType = new TypeToken<ResponseModel>() {
                }.getType();
                ResponseModel responseModel = new Gson().fromJson(versionList.toString(), listType);
                EdgeSidecarToChildren edgeSidecarToChildren = responseModel.getGraphql().getShortcode_media().getEdge_sidecar_to_children();
                if (edgeSidecarToChildren != null) {
                    List<Edge> edgeArrayList = edgeSidecarToChildren.getEdges();
                    for (int i = 0; i < edgeArrayList.size(); i++) {
                        if (edgeArrayList.get(i).getNode().isIs_video()) {
                            VideoUrl = edgeArrayList.get(i).getNode().getVideo_url();
                            startDownload(VideoUrl, RootDirectoryInstaVideos, activity, getVideoFilenameFromURL(VideoUrl));
                            binding.etText.setText("");
                            VideoUrl = "";

                        } else {
                            PhotoUrl = edgeArrayList.get(i).getNode().getDisplay_resources().get(edgeArrayList.get(i).getNode().getDisplay_resources().size() - 1).getSrc();
                            startDownload(PhotoUrl, RootDirectoryInstaImages, activity, getImageFilenameFromURL(PhotoUrl));
                            PhotoUrl = "";
                            binding.etText.setText("");
                        }
                    }
                } else {
                    boolean isVideo = responseModel.getGraphql().getShortcode_media().isIs_video();
                    if (isVideo) {
                        VideoUrl = responseModel.getGraphql().getShortcode_media().getVideo_url();
                        startDownload(VideoUrl, RootDirectoryInstaVideos, activity, getVideoFilenameFromURL(VideoUrl));
                        VideoUrl = "";
                        binding.etText.setText("");
                    } else {
                        PhotoUrl = responseModel.getGraphql().getShortcode_media().getDisplay_resources()
                                .get(responseModel.getGraphql().getShortcode_media().getDisplay_resources().size() - 1).getSrc();

                        startDownload(PhotoUrl, RootDirectoryInstaImages, activity, getImageFilenameFromURL(PhotoUrl));
                        PhotoUrl = "";
                        binding.etText.setText("");
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            Utils.hideProgressDialog(activity);
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            Utils.hideProgressDialog(activity);
        }
    };

    public String getImageFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath().toString()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".png";
        }
    }

    public String getVideoFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath().toString()).getName();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        instaObserver.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);

            if (requestCode == 100 && resultCode == RESULT_OK) {
                String requiredValue = data.getStringExtra("key");
                if (SharePrefs.getInstance(activity).getBoolean(SharePrefs.ISINSTALOGIN)) {
                    binding.SwitchLogin.setChecked(true);
                    layoutCondition();
                    callStoriesApi();
                } else {
                    binding.SwitchLogin.setChecked(false);
                }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void callStoriesApi() {
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    binding.prLoadingBar.setVisibility(View.VISIBLE);
                    commonClassForAPI.getStories(storyObserver, "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
                            + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));
                }
            } else {
                Utils.setToast(activity, activity
                        .getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }


    }

    private DisposableObserver<StoryModel> storyObserver = new DisposableObserver<StoryModel>() {
        @Override
        public void onNext(StoryModel response) {
            binding.RVUserList.setVisibility(View.VISIBLE);
            binding.prLoadingBar.setVisibility(View.GONE);
            try {
                userListAdapter = new UserListAdapter(activity, response.getTray(), activity);
                Log.d("DATA", response.getTray().toString());
                binding.RVUserList.setAdapter(userListAdapter);
                userListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            binding.prLoadingBar.setVisibility(View.GONE);
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            binding.prLoadingBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void userListClick(int position, TrayModel trayModel) {
        // callStoriesDetailApi(String.valueOf(trayModel.getUser().getPk()));

        // Intent intent=new Intent(this, InstagramStoriesActivity.class);
        Intent intent = new Intent(this, StoriesLibararyActivity.class);
        intent.putExtra("user_id", String.valueOf(trayModel.getUser().getPk()));
        intent.putExtra("user_name", trayModel.getUser().getFull_name());
        intent.putExtra("image", trayModel.getUser().getProfile_pic_url());
        startActivity(intent);

    }

    private void callStoriesDetailApi(String UserId) {
        try {
            Utils utils = new Utils(activity);
            if (utils.isNetworkAvailable()) {
                if (commonClassForAPI != null) {
                    binding.prLoadingBar.setVisibility(View.VISIBLE);
                    commonClassForAPI.getFullDetailFeed(storyDetailObserver, UserId, "ds_user_id=" + SharePrefs.getInstance(activity).getString(SharePrefs.USERID)
                            + "; sessionid=" + SharePrefs.getInstance(activity).getString(SharePrefs.SESSIONID));
                }
            } else {
                Utils.setToast(activity, activity
                        .getResources().getString(R.string.no_net_conn));
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private DisposableObserver<FullDetailModel> storyDetailObserver = new DisposableObserver<FullDetailModel>() {
        @Override
        public void onNext(FullDetailModel response) {
            binding.RVUserList.setVisibility(View.VISIBLE);
            binding.prLoadingBar.setVisibility(View.GONE);
            try {
                storiesListAdapter = new StoriesListAdapter(activity, response.getReel_feed().getItems());
                binding.RVStories.setAdapter(storiesListAdapter);
                storiesListAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(Throwable e) {
            binding.prLoadingBar.setVisibility(View.GONE);
            e.printStackTrace();
        }

        @Override
        public void onComplete() {
            binding.prLoadingBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        binding.etText.setText("");
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
            finish();
        } else if (item.getItemId() == R.id.how_to_use) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Insta_video_link));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
