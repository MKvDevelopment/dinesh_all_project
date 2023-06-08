package com.socialmediasaver.status.activity;

import static com.android.billingclient.api.BillingClient.SkuType.SUBS;
import static com.socialmediasaver.status.util.Utils.App_version;
import static com.socialmediasaver.status.util.Utils.Fb_video_link;
import static com.socialmediasaver.status.util.Utils.InAppSubscription;
import static com.socialmediasaver.status.util.Utils.Insta_video_link;
import static com.socialmediasaver.status.util.Utils.PrivacyPolicyUrl;
import static com.socialmediasaver.status.util.Utils.Rating_link;
import static com.socialmediasaver.status.util.Utils.Refer_link;
import static com.socialmediasaver.status.util.Utils.Subscription;
import static com.socialmediasaver.status.util.Utils.Twiter_video_link;
import static com.socialmediasaver.status.util.Utils.Whatsp_video_link;
import static com.socialmediasaver.status.util.Utils.createFileFolder;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.socialmediasaver.status.R;
import com.socialmediasaver.status.adapter.SliderAdapter;
import com.socialmediasaver.status.model.SliderModel;
import com.socialmediasaver.status.util.ClipboardListener;
import com.socialmediasaver.status.util.NetworkChangeReceiver;
import com.socialmediasaver.status.util.SharePrefs;
import com.socialmediasaver.status.util.Utils;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderView;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, PurchasesUpdatedListener {
    MainActivity activity;
    private long back_pressed;
    private ClipboardManager clipBoard;
    private NetworkChangeReceiver broadcastReceiver;
    private BillingClient billingClient;
    private SliderView sliderView;
    private SliderAdapter sliderAdapter;

    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    private static final String[] PERMISSIONS_ = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,

    };
    String CopyKey = "";
    String CopyValue = "";
    private String whatspUs;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private String forceOnOff;
    private RelativeLayout rvInsta, rvfacebook, rvwhatsp, rvtwiter, rvabout, rvdownload, rvshare, rvWhatsappDirect, rvInstaProfile;
    private StartAppAd startAppAd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        initViews();

        RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("01C610E3FCA975F245CA7F2D773ECF05")).build();

        //check network connectivity
        broadcastReceiver = new NetworkChangeReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarhome);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this
                , drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        rvInsta.setOnClickListener(this);
        rvfacebook.setOnClickListener(this);
        rvtwiter.setOnClickListener(this);
        rvwhatsp.setOnClickListener(this);
        rvshare.setOnClickListener(this);
        rvdownload.setOnClickListener(this);
        rvabout.setOnClickListener(this);
        rvWhatsappDirect.setOnClickListener(this);
        rvInstaProfile.setOnClickListener(this);

        StartAppSDK.init(this, "207208219",true);
        startAppAd = new StartAppAd(this);
        StartAppAd.disableSplash();
        StartAppAd.enableConsent(this,false);

        checkIfAlreadySubscribed();

    }

    public void checkIfAlreadySubscribed() {
        billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(SUBS);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();

                    if (queryPurchases != null && queryPurchases.size() > 0) {

                        InAppSubscription = SharePrefs.getInstance(MainActivity.this).getSubscribeValueFromPref();
                        // handlePurchases(queryPurchases);


                    } else {
                        SharePrefs.getInstance(MainActivity.this).saveSubscribeValueToPref(false);
                        InAppSubscription = SharePrefs.getInstance(MainActivity.this).getSubscribeValueFromPref();
                    } } }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(getApplicationContext(), "Service Disconnected", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        activity = this;
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        getDataFromFirebase();
    }

    private void getDataFromFirebase() {
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("App_utils").document("app_data");
        CollectionReference sliderRef = FirebaseFirestore.getInstance().collection("Slider_Image");


        documentReference.addSnapshotListener((value, error) -> {

            assert value != null;
            Insta_video_link = value.getString("instause");
            Fb_video_link = value.getString("fbuse");
            Whatsp_video_link = value.getString("whatsappuse");
            Twiter_video_link = value.getString("twitteruse");
            Rating_link = value.getString("rating");
            Refer_link = value.getString("refer");
            App_version = value.getString("version");
            whatspUs = value.getString("whatsp_us");
            forceOnOff = value.getString("force_update");
            if (forceOnOff.equals("ON")) {
                checkForUpdate();
            }
            if (FirebaseAuth.getInstance().getUid() != null) {
                DocumentReference documentReference1 = FirebaseFirestore.getInstance().collection("Users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                documentReference1.addSnapshotListener((value1, error1) -> {
                    assert value1 != null;
                    if (value1.exists()) {
                        Subscription = value1.getString("subscription");

                    }
                });
            } else {
                Subscription = "NO";

            }
        });

        //fetching slider data
        sliderRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            ArrayList<SliderModel> model_list;
            model_list = new ArrayList<>();

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {

                SliderModel model = documentSnapshot.toObject(SliderModel.class);
                model_list.add(model);

            }

            sliderView = findViewById(R.id.imageSlider);
            sliderAdapter = new SliderAdapter(model_list,MainActivity.this);
            sliderView.setSliderAdapter(sliderAdapter);
            sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP);
            sliderView.setAutoCycle(true);
            sliderAdapter.notifyDataSetChanged();
            // progressDialog.dismiss();
        });

    }

    public void initViews() {
        clipBoard = (ClipboardManager) activity.getSystemService(CLIPBOARD_SERVICE);
        if (activity.getIntent().getExtras() != null) {
            for (String key : activity.getIntent().getExtras().keySet()) {
                CopyKey = key;
                String value = activity.getIntent().getExtras().getString(CopyKey);
                if (CopyKey.equals("android.intent.extra.TEXT")) {
                    CopyValue = activity.getIntent().getExtras().getString(CopyKey);
                    callText(value);
                } else {
                    CopyValue = "";
                    callText(value);
                }
            }
        }
        if (clipBoard != null) {
            clipBoard.addPrimaryClipChangedListener(new ClipboardListener() {
                @Override
                public void onPrimaryClipChanged() {
                    try {
                        showNotification(Objects.requireNonNull(clipBoard.getPrimaryClip().getItemAt(0).getText()).toString());
                    } catch (
                            Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions(0);
        }

        rvfacebook = findViewById(R.id.rvFB);
        rvwhatsp = findViewById(R.id.rvWhatsApp);
        rvInsta = findViewById(R.id.rvInsta);
        rvtwiter = findViewById(R.id.rvTwitter);
        rvabout = findViewById(R.id.rvAbout);
        rvdownload = findViewById(R.id.rvGallery);
        rvshare = findViewById(R.id.rvShareApp);
        rvInstaProfile = findViewById(R.id.rvInstaProfile);
        rvWhatsappDirect = findViewById(R.id.rvWhatsappDirect);

        createFileFolder();

    }

    private void callText(String CopiedText) {
        try {
            if (CopiedText.contains("instagram.com")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(101);
                } else {
                    callInstaActivity();
                }
            } else if (CopiedText.contains("facebook.com")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(104);
                } else {
                    callFacebookActivity();
                }
            } else if (CopiedText.contains("twitter.com")) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(106);
                } else {
                    callTwitterActivity();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent i;

        switch (v.getId()) {
            case R.id.rvInsta:
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(101);
                } else {

                    callInstaActivity();
                }
                break;
            case R.id.rvWhatsApp:
                if (Build.VERSION.SDK_INT >= 30) {
//                   if (checkPermissionsAndroid30(102)) {
//                        callWhatsappActivity();
//
//                    } else {
                    //  callWhatsappActivity();
                    //askPermissionsAndroid30();
                    //}
                    if (SharePrefs.getInstance(MainActivity.this).getAndroid11WhatsappStatus() == false) {
                        askPermissionsAndroid30();
                    } else {
                        callWhatsappActivity();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= 23) {
                        checkPermissions(102);
                    } else {
                        callWhatsappActivity();
                    }
                }
                break;
            case R.id.rvFB:
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(104);
                } else {
                    callFacebookActivity();
                }
                break;
            case R.id.rvInstaProfile:
                startActivity(new Intent(MainActivity.this, InstaSearchUserActivity.class));
                break;
            case R.id.rvWhatsappDirect:
                startActivity(new Intent(MainActivity.this, DirectWhatspActivity.class));
                break;

            case R.id.rvGallery:
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(105);
                } else {
                    callGalleryActivity();
                }
                break;
            case R.id.rvTwitter:
                if (Build.VERSION.SDK_INT >= 23) {
                    checkPermissions(106);
                } else {
                    callTwitterActivity();
                }
                break;
            case R.id.rvAbout:
                i = new Intent(activity, AboutUsActivity.class);
                startActivity(i);
                break;
            case R.id.rvShareApp:
                Utils.ShareApp(activity);
                break;
            case R.id.direct:
                startActivity(new Intent(MainActivity.this, DirectWhatspActivity.class));
                break;
        }
    }

    public void callInstaActivity() {
        Intent i = new Intent(activity, InstagramActivity.class);
        i.putExtra("CopyIntent", CopyValue);
        startActivity(i);
        finish();
    }

    public void callWhatsappActivity() {
        Intent i = new Intent(activity, WhatsappActivity.class);
        startActivity(i);

    }

    public void callFacebookActivity() {
        Intent i = new Intent(activity, FacebookActivity2.class);
        i.putExtra("CopyIntent", CopyValue);
        startActivity(i);

    }

    public void callTwitterActivity() {
        Intent i = new Intent(activity, TwitterActivity.class);
        i.putExtra("CopyIntent", CopyValue);
        startActivity(i);
    }

    public void callGalleryActivity() {
        Intent i = new Intent(activity, DownLoadMainActivity.class);
        startActivity(i);
    }

    public void showNotification(String Text) {
        if (Text.contains("instagram.com") || Text.contains("facebook.com") || Text.contains("twitter.com")) {
            Intent intent = new Intent(activity, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Notification", Text);
            PendingIntent pendingIntent = PendingIntent.getActivity(activity, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManager notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel mChannel = new NotificationChannel(getResources().getString(R.string.app_name),
                        getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
                mChannel.enableLights(true);
                mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
                notificationManager.createNotificationChannel(mChannel);
            }
            NotificationCompat.Builder notificationBuilder;
            notificationBuilder = new NotificationCompat.Builder(activity, getResources().getString(R.string.app_name))
                    .setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setColor(getResources().getColor(R.color.black))
                    .setLargeIcon(BitmapFactory.decodeResource(activity.getResources(),
                            R.mipmap.ic_launcher_round))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentTitle("Copied text")
                    .setContentText(Text)
                    .setChannelId(getResources().getString(R.string.app_name))
                    .setFullScreenIntent(pendingIntent, true);
            notificationManager.notify(1, notificationBuilder.build());
        }
    }

    private boolean checkPermissions(int type) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(activity, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        // }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) (activity),
                    listPermissionsNeeded.toArray(new
                            String[listPermissionsNeeded.size()]), type);
            return false;
        } else {
            if (type == 101) {
                callInstaActivity();
            } else if (type == 102) {
                callWhatsappActivity();
            } else if (type == 104) {
                callFacebookActivity();
            } else if (type == 105) {
                callGalleryActivity();
            } else if (type == 106) {
                callTwitterActivity();
            }
        }

        return true;
    }

    public void askPermissionsAndroid30() {
        try {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            DocumentFile f = DocumentFile.fromFile(getExternalFilesDir("/media"));
            //DocumentFile f = DocumentFile.fromFile(getExternalFilesDir("Android/media/com.whatsapp/WhatsApp/Media/.Statuses"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, f.getUri());
            Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses");
            //Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia");
            DocumentFile file = DocumentFile.fromTreeUri(MainActivity.this, uri);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, file.getUri());
            //intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uri);
            //Log.d(TAG, "uri: " + uri.toString());
            startActivityForResult(intent, 2296);

        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            DocumentFile f = DocumentFile.fromFile(getExternalFilesDir("/media"));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri uri = Uri.parse("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses");
            DocumentFile file = DocumentFile.fromTreeUri(MainActivity.this, uri);
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, file.getUri());

            startActivityForResult(intent, 2296);

//            Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//            DocumentFile f = DocumentFile.fromFile(getExternalFilesDir("/media"));
//            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, f.getUri());
//            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//            startActivityForResult(intent, 2296);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                //Toast.makeText(MainActivity.this, data.getData()+"", Toast.LENGTH_SHORT).show();
                Log.d("TAG1", data.getData() + "");
                Uri treeUri = data.getData();
                // String targetPath = Environment.getDataDirectory().getAbsolutePath();
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                this.getContentResolver().takePersistableUriPermission(treeUri, takeFlags);
                Log.d("TAG2", takeFlags + "");
                if (data.getData() != null)
                    SharePrefs.getInstance(MainActivity.this).saveAndroid11WhatsappStatus(true);
                callWhatsappActivity();
                // updateDirectoryEntries(data.getData());
                Uri docUriTree = DocumentsContract.buildDocumentUriUsingTree(treeUri, DocumentsContract.getTreeDocumentId(data.getData()));

                //DocumentFile documentFile = DocumentFile.fromTreeUri(this, data.getData());
                Uri myUri = Uri.parse(data.getData() + "%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses");
                DocumentFile documentFile = DocumentFile.fromTreeUri(this, myUri);

                for (DocumentFile file : documentFile.listFiles()) {
                    if (file.isDirectory()) { // if it is sub directory
                        // Do stuff with sub directory
                        Log.d("yo", file.getUri() + "\n");
                    } else {
                        // Do stuff with normal file
                    }
                    Log.d("Uri", file.getUri() + "\n");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions,
                                           @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callInstaActivity();
            }
        } else if (requestCode == 102) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callWhatsappActivity();
            }
        } else if (requestCode == 104) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callFacebookActivity();
            }
        } else if (requestCode == 105) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callGalleryActivity();
            }
        } else if (requestCode == 106) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callTwitterActivity();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 2000 > System.currentTimeMillis()) {
            //finish();
            finishAffinity();
            moveTaskToBack(true);
        } else {
            Toast.makeText(activity, "Press Again to Exit", Toast.LENGTH_SHORT).show();
            back_pressed = System.currentTimeMillis();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_whatsp:
                drawerLayout.closeDrawer(GravityCompat.START);
                checkSubscription();
                break;

            case R.id.menu_aboutus:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                break;


            case R.id.menu_promotion:
                drawerLayout.closeDrawer(GravityCompat.START);
                getUserData();
                break;



            case R.id.menu_policy:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent i = new Intent(MainActivity.this, WebviewAcitivity.class);
                i.putExtra("URL", PrivacyPolicyUrl);
                i.putExtra("Title", getResources().getString(R.string.prv_policy));
                startActivity(i);
                break;

            case R.id.menu_rate:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Rating_link));
                startActivity(browserIntent);
                break;

            case R.id.menu_feedback:
                drawerLayout.closeDrawer(GravityCompat.START);
                showFeedbackForm();
                break;

            case R.id.menu_restore:
                drawerLayout.closeDrawer(GravityCompat.START);
                checkLogin();
                break;

            case R.id.subscription:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(MainActivity.this, InAppPurchaseExampleActivity.class));
                break;

        }
        return false;
    }

    private void getUserData(){
        AlertDialog custome_dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.promotion_form, null, false);

        Button allow = view.findViewById(R.id.button8);
        Button deny = view.findViewById(R.id.button7);

        EditText name = view.findViewById(R.id.ed_name);
        EditText mobile = view.findViewById(R.id.ed_mobile);
        EditText email = view.findViewById(R.id.ed_email);
        EditText budget = view.findViewById(R.id.ed_budget);
        EditText total_budget = view.findViewById(R.id.ed_total_budget);
        EditText link = view.findViewById(R.id.ed_link);

        builder.setView(view);

        custome_dialog = builder.create();
        custome_dialog.setCanceledOnTouchOutside(false);
        custome_dialog.show();

        allow.setOnClickListener(v -> {
            String nm = name.getText().toString();
            String mob = mobile.getText().toString();
            String emal = email.getText().toString();
            String amount = budget.getText().toString();
            String t_amount = total_budget.getText().toString();
            String app_link = link.getText().toString();

            if (nm.length() == 0 ||mob.length() == 0 ||emal.length() == 0 ||amount.length() == 0 ||t_amount.length() == 0 ||app_link.length() == 0  ) {
                Toast.makeText(activity, "All Field Reqiuired!", Toast.LENGTH_SHORT).show();
            } else {
                custome_dialog.dismiss();
                Map map = new HashMap();
                map.put("name", nm);
                map.put("mobile", mob);
                map.put("email", emal);
                map.put("budget", amount);
                map.put("total_budget", t_amount);
                map.put("app_link", app_link);
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Promotion").document();
                documentReference.set(map).addOnSuccessListener(command -> {
                    Toast.makeText(activity, "Detail's Submitted, Our Executive Contact You. :)", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

        });
        deny.setOnClickListener(v -> {
            custome_dialog.dismiss();
        });
    }

    private void checkSubscription(){
        if (Subscription.equals("NO")){
            android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(activity);
            ab.setPositiveButton("Get Premium", (dialog, id) -> {
                dialog.cancel();
                //   startActivity(new Intent(MainActivity.this, RemoveAdsActivity.class));
                startActivity(new Intent(MainActivity.this, InAppPurchaseExampleActivity.class));
            });
            ab.setNegativeButton("Later", (dialog, id) -> dialog.cancel());
            android.app.AlertDialog alert = ab.create();
            alert.setTitle("Alert!");
            alert.setMessage("Whatsapp Support Only available for Premium Users.");
            alert.show();
        }else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(whatspUs));
            intent.setPackage("com.whatsapp");
            startActivity(intent);
        }
    }
    
    private void checkLogin() {
        if (FirebaseAuth.getInstance().getUid() == null) {
            android.app.AlertDialog.Builder ab = new android.app.AlertDialog.Builder(activity);
            ab.setPositiveButton("Login Now", (dialog, id) -> {
                dialog.cancel();
                //   startActivity(new Intent(MainActivity.this, RemoveAdsActivity.class));
                startActivity(new Intent(MainActivity.this, InAppPurchaseExampleActivity.class));
            });
            ab.setNegativeButton("Later", (dialog, id) -> dialog.cancel());
            android.app.AlertDialog alert = ab.create();
            alert.setTitle("Alert!");
            alert.setMessage("Please login with your Previous Account!");
            alert.show();
        } else {
            if (Subscription.equals("NO")) {
                Toast.makeText(activity, "You have no Pre-Activated Subscription!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(activity, "You have already Subscription!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void showFeedbackForm() {

        AlertDialog custome_dialog;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.feedback_form, null, false);

        Button allow = view.findViewById(R.id.button4);
        Button deny = view.findViewById(R.id.button5);
        EditText name = view.findViewById(R.id.editText);
        EditText title = view.findViewById(R.id.editText2);
        EditText desc = view.findViewById(R.id.editText3);
        EditText email = view.findViewById(R.id.edittext4);

        builder.setView(view);

        custome_dialog = builder.create();
        custome_dialog.setCanceledOnTouchOutside(false);
        custome_dialog.show();

        allow.setOnClickListener(v -> {
            String nm = name.getText().toString();
            String titl = title.getText().toString();
            String des = desc.getText().toString();
            String eml = email.getText().toString();

            if (nm.length() == 0 || titl.length() == 0 || des.length() == 0|| eml.length() == 0) {
                Toast.makeText(activity, "All Field Reqiuired!", Toast.LENGTH_SHORT).show();
            } else {
                custome_dialog.dismiss();
                Map map = new HashMap();
                map.put("name", nm);
                map.put("email", eml);
                map.put("title", titl);
                map.put("desc", des);
                DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users_Feedback").document();
                documentReference.set(map).addOnSuccessListener(command -> {
                    Toast.makeText(activity, "Thanks for your Precious Feedback :)", Toast.LENGTH_SHORT).show();
                }).addOnFailureListener(e -> {
                    Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

        });
        deny.setOnClickListener(v -> {
            custome_dialog.dismiss();
        });

    }

    private void checkForUpdate() {

        String app_version = String.valueOf(getCurrentAppVersionCode());

        if (!app_version.contains(App_version)) {

            new AlertDialog.Builder(this).setPositiveButton("Update Now", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    //go to the playstore link
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(Rating_link));
                    startActivity(intent);
                    finish();
                }
            }).setTitle("Update Available!")
                    .setMessage("New Version is Available. Update now otherwise you can not access our Application")
                    .setCancelable(false).show();
        }

    }

    private int getCurrentAppVersionCode() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public String getRealPath(Uri uri) {
        String docId = DocumentsContract.getDocumentId(uri);
        String[] split = docId.split(":");
        String type = split[0];
        Uri contentUri;
        switch (type) {
            case "image":
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                break;
            case "video":
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                break;
            case "audio":
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                break;
            default:
                contentUri = MediaStore.Files.getContentUri("external");
        }
        String selection = "_id=?";
        String[] selectionArgs = new String[]{
                split[1]
        };

        return getDataColumn(this, contentUri, selection, selectionArgs);
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                String value = cursor.getString(column_index);
                if (value.startsWith("content://") || !value.startsWith("/") && !value.startsWith("file://")) {
                    return null;
                }
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    private String copyFileToInternalStorage(Uri uri, String newDirName) {
        Uri returnUri = uri;

        Cursor returnCursor = this.getContentResolver().query(returnUri, new String[]{
                OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE
        }, null, null, null);

        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));

        File output;
        if (!newDirName.equals("")) {
            File dir = new File(this.getFilesDir() + "/" + newDirName);
            if (!dir.exists()) {
                dir.mkdir();
            }
            output = new File(this.getFilesDir() + "/" + newDirName + "/" + name);
        } else {
            output = new File(this.getFilesDir() + "/" + name);
        }
        try {
            InputStream inputStream = this.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(output);
            int read = 0;
            int bufferSize = 1024;
            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }

            inputStream.close();
            outputStream.close();

        } catch (Exception e) {

            Log.e("Exception", e.getMessage());
        }
        return output.getPath();
    }

    public void updateDirectoryEntries(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                DocumentsContract.getTreeDocumentId(uri));
        Uri childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri,
                DocumentsContract.getTreeDocumentId(uri));
        Cursor docCursor = contentResolver.query(docUri, new String[]{
                DocumentsContract.Document.COLUMN_DISPLAY_NAME, DocumentsContract.Document.COLUMN_MIME_TYPE}, null, null, null);
        try {
            while (docCursor.moveToNext()) {
                Log.d("TAG", "found doc =" + docCursor.getString(0) + ", mime=" + docCursor
                        .getString(1));

            }
        } finally {
            // close cursor
        }


    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

    }
}
