package com.socialmediasaver.status.activity;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.ContentValues.TAG;
import static com.socialmediasaver.status.util.Utils.Fb_video_link;
import static com.socialmediasaver.status.util.Utils.RootDirectoryFacebook;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.createFileFolder;
import static com.socialmediasaver.status.util.Utils.startDownload;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.api.CommonClassForAPI;
import com.socialmediasaver.status.databinding.ActivityFacebookBinding;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.Utils;
import com.startapp.sdk.ads.banner.Mrec;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdDisplayListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class FacebookActivity2 extends AppCompatActivity {
    ActivityFacebookBinding binding;
    FacebookActivity2 activity;
    CommonClassForAPI commonClassForAPI;
    private String VideoUrl;
    private ClipboardManager clipBoard;
    private NetworkChangeReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_facebook);
        activity = this;

        Toolbar toolbar = findViewById(R.id.toolbarfb);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        commonClassForAPI = CommonClassForAPI.getInstance(activity);
        createFileFolder();
        initViews();

        if (binding.cbWhatsapp.isChecked()) {
            binding.cbWhatsappbusiness.setChecked(false);

        } else if (binding.cbWhatsappbusiness.isChecked()) {
            binding.cbWhatsapp.setChecked(false);
        }

    }

    private void checksubscription() {

        if (FirebaseAuth.getInstance().getUid() != null) {
            DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
            documentReference1.addSnapshotListener((value1, error1) -> {
                assert value1 != null;
                if (value1.exists()) {
                    Subscription = value1.getString("subscription");
                    loadbanner();
                    loadInterstial();
                } else {
                    Subscription = "NO";
                }
            });
        } else {
            Subscription = "NO";
            loadbanner();
            loadInterstial();
        }

    }

    private void loadbanner() {
        if (Subscription.equals("NO")) {
            Mrec mrec = (Mrec) findViewById(R.id.startAppMrecfb);
            mrec.setVisibility(View.VISIBLE);
            mrec.showBanner();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        assert activity != null;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        PasteText();
      //  checksubscription();
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
                if (Subscription.equals("NO")) {
                        loadInterstial();
                }
                GetFacebookData();
                // showDialog1();
            }
        });

        binding.tvPaste.setOnClickListener(v -> {
            PasteText();
        });
    }

    private void loadInterstial() {
        StartAppAd startAppAd = new StartAppAd(this);
        startAppAd.loadAd(StartAppAd.AdMode.FULLPAGE);
        startAppAd.showAd();
    }
    private void showDialog1() {

        LinearLayout layout, layout1, layout2;
        TextView ok, textview2, textview1;
        ImageView close;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity, R.style.CustomScreenDialogStyle);
        dialogBuilder.setCancelable(true);

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.dialog_layout, null);
        dialogBuilder.setView(dialogView);
        layout = dialogView.findViewById(R.id.layout);
        layout1 = dialogView.findViewById(R.id.layout1);
        layout2 = dialogView.findViewById(R.id.layout2);
        ok = dialogView.findViewById(R.id.ok);
        textview2 = dialogView.findViewById(R.id.textview2);
        textview1 = dialogView.findViewById(R.id.textview1);
        close = dialogView.findViewById(R.id.close);

        layout1.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape2));
        layout2.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape));
        textview1.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.colorPrimary));
        textview2.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.grey));

        layout1.setOnClickListener(v -> {
            layout1.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape2));
            layout2.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape));
            textview1.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.colorPrimary));
            textview2.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.grey));

        });
        layout2.setOnClickListener(v -> {
            layout2.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape2));
            layout1.setBackground(FacebookActivity2.this.getApplicationContext().getResources().getDrawable(R.drawable.text_view_shape));
            textview1.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.grey));
            textview2.setTextColor(FacebookActivity2.this.getApplicationContext().getResources().getColor(R.color.colorPrimary));

        });
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


        ok.setOnClickListener(v -> {
            alertDialog.dismiss();
            GetFacebookData();
        });
        close.setOnClickListener(v -> alertDialog.dismiss());
        alertDialog.setOnDismissListener(dialogInterface -> {

        });
        alertDialog.setOnCancelListener(dialogInterface -> {

        });

    }

    private void GetFacebookData() {
        try {
            createFileFolder();
            URL url = new URL(binding.etText.getText().toString());
            String host = url.getHost();

            if (host.contains("facebook.com")) {
                Utils.showProgressDialog(activity);
                new FacebookActivity2.callGetFacebookData().execute(binding.etText.getText().toString());
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
            } catch (IOException e) {
                e.printStackTrace();
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
                    binding.etText.setText("");
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

    @Override
    protected void onPause() {
        super.onPause();
      binding.etText.setText("");
    }
}
