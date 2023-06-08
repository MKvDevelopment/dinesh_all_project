package com.socialmediasaver.status.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;


import com.socialmediasaver.status.R;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
/*
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
*/

import java.io.File;

import static android.os.Environment.DIRECTORY_DOWNLOADS;


public class Utils {
    public static Dialog customDialog;
    private static Context context;

    public Utils(Context _mContext) {
        context = _mContext;
    }

    public static String RootDirectoryFacebook = "/Social_Media_Saver/Facebook/";
    //public static String RootDirectoryInsta = "/Social_Media_Saver/Insta/";
    //public static String RootDirectoryInsta = "/Social_Media_Saver/";
    public static String RootDirectoryInstaVideos = "/Social_Media_Saver/Insta_Videos/";
    public static String RootDirectoryInstaImages = "/Social_Media_Saver/Insta_Images/";
    public static String RootDirectoryInstaStories= "/Social_Media_Saver/Insta_Stories/";
    public static String RootDirectoryInstaProfilePic= "/Social_Media_Saver/Insta_Profile_Picture/";

    public static String RootDirectoryTwitter ="/Social_Media_Saver/Twitter/";

    public static File RootDirectoryFacebookShow = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Facebook");
    public static File RootDirectoryInstaShow = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Insta");
    public static File RootDirectoryInstaVideo = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Insta_Videos");
    public static File RootDirectoryInstaImage = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Insta_Images");
    public static File RootDirectoryDownloadInstaStories = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Insta_Stories");
    public static File RootDirectoryProfilePic = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Insta_Profile_Picture");

    public static File RootDirectoryTwitterShow = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Twitter");
    public static File RootDirectoryWhatsappShow = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Whatsapp");
    public static File RootDirectoryWhatsappImageShow = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Whatsapp_Images");
    public static File RootDirectoryWhatsappBusinessImageShow = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/WhatsappBusiness_Images");
   // public static File RootDirectoryWhatsappImageShow = new File(Environment.getStorageDirectory() + "/Download/Social_Media_Saver/Whatsapp_Images");
    public static File RootDirectoryWhatsappVideoShow = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/Whatsapp_Videos");
    public static File RootDirectoryWhatsappBusinessVideoShow = new File(Environment.getExternalStorageDirectory() + "/Download/Social_Media_Saver/WhatsappBusiness_Videos");

    public static String PrivacyPolicyUrl = "https://socialmediasaver.com/index.php/privacy-policy";

    public static String Insta_video_link;
    public static String Fb_video_link;
    public static String Whatsp_video_link;
    public static String Twiter_video_link;
    public static String Rating_link;
    public static String Refer_link;
    public static String App_version;
    public static String Subscription ;
    public static boolean InAppSubscription ;
    //public static boolean Subscription ;


    public static void setToast(Context _mContext, String str) {
        Toast toast = Toast.makeText(_mContext, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void createFileFolder() {
        if (!RootDirectoryFacebookShow.exists()) {
            RootDirectoryFacebookShow.mkdirs();
        }
//        if (!RootDirectoryInstaShow.exists()) {
//            RootDirectoryInstaShow.mkdirs();
//        }
        if (!RootDirectoryInstaImage.exists()) {
            RootDirectoryInstaImage.mkdirs();
        }
        if (!RootDirectoryInstaVideo.exists()) {
            RootDirectoryInstaVideo.mkdirs();
        }

        if (!RootDirectoryTwitterShow.exists()) {
            RootDirectoryTwitterShow.mkdirs();
        }
//        if (!RootDirectoryWhatsappShow.exists()) {
//            RootDirectoryWhatsappShow.mkdirs();
//        }
        if (!RootDirectoryWhatsappImageShow.exists()) {
            RootDirectoryWhatsappImageShow.mkdirs();
        }
        if (!RootDirectoryWhatsappVideoShow.exists()) {
            RootDirectoryWhatsappVideoShow.mkdirs();
        }

    }

    public static void showProgressDialog(Activity activity) {
        System.out.println("Show");
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
        customDialog = new Dialog(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View mView = inflater.inflate(R.layout.progress_dialog, null);
        customDialog.setCancelable(false);
        customDialog.setContentView(mView);
        if (!customDialog.isShowing() && !activity.isFinishing()) {
            customDialog.show();
        }
    }

    public static void hideProgressDialog(Activity activity) {
        System.out.println("Hide");
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void startDownload(String downloadPath, String destinationPath, Context context, String FileName) {
        setToast(context, context.getResources().getString(R.string.download_started));
        Uri uri = Uri.parse(downloadPath); // Path where you want to DownloadInterface file.
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to DownloadInterface file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle(FileName+""); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS,destinationPath+FileName);  // Storage directory path
        ((DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request); // This will start downloading

        try {
            if (Build.VERSION.SDK_INT >= 19) {
                MediaScannerConnection.scanFile(context, new String[]{new File(DIRECTORY_DOWNLOADS + "/" + destinationPath + FileName).getAbsolutePath()},
                        null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            } else {
                context.sendBroadcast(new Intent("android.intent.action.MEDIA_MOUNTED", Uri.fromFile(new File(DIRECTORY_DOWNLOADS + "/" + destinationPath + FileName))));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void shareImage(Context context, String filePath) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
           // intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_txt));
            String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), filePath, "", null);
            Uri screenshotUri = Uri.parse(path);
            intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            intent.setType("image/*");
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share_image_via)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void shareImageVideoOnWhatsapp(Context context, String filePath, boolean isVideo) {
        Uri imageUri = Uri.parse(filePath);
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage("com.whatsapp");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        if (isVideo) {
            shareIntent.setType("video/*");
        }else {
            shareIntent.setType("image/*");
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(shareIntent);
        } catch (Exception e) {
            Utils.setToast(context,context.getResources().getString(R.string.whatsapp_not_installed));
        }
    }

    public static void shareVideo(Context context, String filePath) {
        Uri mainUri = Uri.parse(filePath);
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("video/mp4");
        sharingIntent.putExtra(Intent.EXTRA_STREAM, mainUri);
        sharingIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            context.startActivity(Intent.createChooser(sharingIntent, "Share Video using"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, context.getResources().getString(R.string.no_app_installed), Toast.LENGTH_LONG).show();
        }
    }

    public static void ShareApp(Context context) {
        final String appLink = Refer_link;
        Intent sendInt = new Intent(Intent.ACTION_SEND);
        sendInt.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.app_name));
        sendInt.putExtra(Intent.EXTRA_TEXT,  appLink);
        sendInt.setType("text/plain");
        context.startActivity(Intent.createChooser(sendInt, "Share"));
    }

    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.length() == 0) || (s.equalsIgnoreCase("null"))||(s.equalsIgnoreCase("0"));
    }


    public static void bannerInit(Context context){
//        MobileAds.initialize(context, initializationStatus -> {
//        });
        MobileAds.initialize(context, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }
}
