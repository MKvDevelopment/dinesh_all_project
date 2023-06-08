package com.socialmediasaver.status.activity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.ContentValues.TAG;
import static com.socialmediasaver.status.util.Utils.Fb_video_link;
import static com.socialmediasaver.status.util.Utils.RootDirectoryFacebook;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.createFileFolder;
import static com.socialmediasaver.status.util.Utils.startDownload;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.ashudevs.facebookurlextractor.FacebookExtractor;
import com.ashudevs.facebookurlextractor.FacebookFile;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.databinding.ActivityFacebookBinding;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.Utils;
import com.facebook.CallbackManager;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.startapp.sdk.adsbase.StartAppAd;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FacebookActivity extends AppCompatActivity {
    ActivityFacebookBinding binding;

    FacebookActivity activity;
    CommonClassForAPI commonClassForAPI;
    private String VideoUrl;
    private ClipboardManager clipBoard;
    private NetworkChangeReceiver broadcastReceiver;
    private static final String EMAIL = "email";
    private static final String PROFILE_PICTURE = "public_profile";
    private static final String ACCESS_TOKEN = "access_token";
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_facebook);
        activity = this;
        getdata();

        Toolbar toolbar = findViewById(R.id.toolbarfb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();
        initViews();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void getdata() {

        //  Matcher matcher = Pattern.compile("src=\"\"").matcher("<iframe allow=\"autoplay; clipboard-write; encrypted-media; picture-in-picture; web-share\" allowfullscreen=\"true\" frameborder=\"0\" height=\"130\" scrolling=\"no\" src=\"https://www.facebook.com/plugins/video.php?width=130&href=https%3A%2F%2Fwww.facebook.com%2F427501935410625%2Fvideos%2F388655972897225\" style=\"border:none;overflow:hidden\" width=\"130\"></iframe>");
        //Matcher matcher = Pattern.compile("#(\\d+)/$#").matcher("https://www.facebook.com/searchbangladeshofficial/videos/1180300868811111/");
        final Pattern pattern = Pattern.compile("#(\\\\d+)/$#", Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher("https://www.facebook.com/searchbangladeshofficial/videos/1180300868811111/");
        matcher.find();
        //String data ="<iframe allow=\\\"autoplay; clipboard-write; encrypted-media; picture-in-picture; web-share\\\" allowfullscreen=\\\"true\\\" frameborder=\\\"0\\\" height=\\\"720\\\" scrolling=\\\"no\\\" src=\\\"https://www.facebook.com/plugins/video.php?width=720&href=https%3A%2F%2Fwww.facebook.com%2F427501935410625%2Fvideos%2F388655972897225\\\" style=\\\"border:none;overflow:hidden\\\" width=\\\"720\\\"></iframe>";
        // boolean match = data.matches("/facebook\\.com\\/([0-9]+)\\//i");

        // String src = matcher.group(1);
    }

    private void checksubscription() {

        if (FirebaseAuth.getInstance().getUid() != null) {
            DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            documentReference1.addSnapshotListener((value1, error1) -> {
                assert value1 != null;
                if (value1.exists()) {
                    Subscription = value1.getString("subscription");
                } else {
                    Subscription = "NO";
                }
            });
        } else {
            Subscription = "NO";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        PasteText();
       // checksubscription();
    }

    private void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);

        binding.loginBtn1.setOnClickListener(v -> {
            String LL = binding.etText.getText().toString();
            if (Subscription.equals("NO")) {
             //   loadInterstial();
            }
            new FacebookExtractor(this, "https://www.facebook.com/VikasKhannaGroup", true) {
                @Override
                protected void onExtractionComplete(FacebookFile facebookFile) {
                    Log.e("TAG", "---------------------------------------");
                    Log.e("TAG", "facebookFile AutherName :: " + facebookFile.getAuthor());
                    Log.e("TAG", "facebookFile FileName :: " + facebookFile.getFilename());
                    Log.e("TAG", "facebookFile Ext :: " + facebookFile.getExt());
                    Log.e("TAG", "facebookFile SD :: " + facebookFile.getSdUrl());
                    Log.e("TAG", "facebookFile HD :: " + facebookFile.getHdUrl());
                    Log.e("TAG", "---------------------------------------");
                    Toast.makeText(FacebookActivity.this, "success", Toast.LENGTH_SHORT).show();
                }

                @Override
                protected void onExtractionFail(Exception error) {
                    Log.e("Error", "Error :: " + error.getMessage());
                    error.printStackTrace();
                    Toast.makeText(FacebookActivity.this, "failure" + error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            };
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });
    }


    private void PasteText() {
        try {
            binding.etText.setText("");
            String CopyIntent = getIntent().getStringExtra("CopyIntent");
            if (CopyIntent.equals("")) {
                if (!(clipBoard.hasPrimaryClip())) {

                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("facebook.com")) {
                        binding.etText.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("facebook.com")) {
                        binding.etText.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("facebook.com")) {
                    binding.etText.setText(CopyIntent);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class callGetFacebookData extends AsyncTask<String, Void, Document> {
        Document facebookDoc;
        Element element;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Document doInBackground(String... urls) {
            try {
                facebookDoc = Jsoup.connect(urls[0]).get();

                Log.d(TAG, Jsoup.connect(urls[0]).get().body() + "doInBackground: Error");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "doInBackground: Error");
            }
            return facebookDoc;
        }

        protected void onPostExecute(Document result) {
            Utils.hideProgressDialog(activity);
            try {

                VideoUrl = result.select("meta[property=\"og:video\"]").last().attr("content");
                Log.e("onPostExecute: ", VideoUrl);
                if (!VideoUrl.equals("")) {
                    try {
                        startDownload(VideoUrl, RootDirectoryFacebook, activity, getFilenameFromURL(VideoUrl));
                        VideoUrl = "";
                        binding.etText.setText("");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public String getFilenameFromURL(String url) {
        try {
            return new File(new URL(url).getPath()).getName() + ".mp4";
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return System.currentTimeMillis() + ".mp4";
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
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Fb_video_link));
            startActivity(browserIntent);
        }
        return super.onOptionsItemSelected(item);
    }

}