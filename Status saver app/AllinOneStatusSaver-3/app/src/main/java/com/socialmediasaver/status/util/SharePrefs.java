package com.socialmediasaver.status.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePrefs {
    public static String PREFERENCE = "AllInOneDownloader";

    public static String ISSHOWHOWTOFB = "IsShoHowToFB";
    public static String ISSHOWHOWTOTT = "IsShoHowToTT";
    public static String ISSHOWHOWTOINSTA = "IsShoHowToInsta";
    public static String ISSHOWHOWTOTWITTER = "IsShoHowToTwitter";
    public static String ISSHOWHOWTOSHARECHAT = "IsShoHowToSharechat";
    public static String ISSHOWHOWTOROPOSO = "IsShoHowToRoposo";
    public static String ISSHOWHOWTOSNACK = "IsShoHowToSnack";


    private Context ctx;
    private SharedPreferences sharedPreferences;
    private static SharePrefs instance;

    public static String SESSIONID = "session_id";
    public static String USERID = "user_id";
    public static String COOKIES = "Cookies";
    public static String CSRF = "csrf";
    public static String ISINSTALOGIN = "IsInstaLogin";
    public static String ISSHOWHOWTOLIKEE = "IsShoHowToLikee";
    public static final String SUBSCRIBE_KEY= "subscribe";
    public static final String ANDROID_11= "android";
    public static final String ANDROID_112= "androidBusiness";
    public static final String LOGIN= "login";


    public SharePrefs(Context context) {
        ctx = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCE, 0);
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharePrefs getInstance(Context ctx) {
        if (instance == null) {
            instance = new SharePrefs(ctx);
        }
        return instance;
    }

    public void putString(String key, String val) {
        sharedPreferences.edit().putString(key, val).apply();
    }

    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }

    public void putInt(String key, Integer val) {
        sharedPreferences.edit().putInt(key, val).apply();
    }

    public void putBoolean(String key, Boolean val) {
        sharedPreferences.edit().putBoolean(key, val).apply();
    }

    public Boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }

    public int getInt(String key) {
        return sharedPreferences.getInt(key, 0);
    }

    public void clearSharePrefs() {
        sharedPreferences.edit().clear().apply();
    }

    public boolean getSubscribeValueFromPref(){
        return sharedPreferences.getBoolean(SUBSCRIBE_KEY,false);
    }
    public void saveSubscribeValueToPref(boolean value){
        sharedPreferences.edit().putBoolean(SUBSCRIBE_KEY,value).commit();
    }
    public boolean getAndroid11WhatsappStatus(){
        return sharedPreferences.getBoolean(ANDROID_11,false);
    }
    public void saveAndroid11WhatsappStatus(boolean value){
        sharedPreferences.edit().putBoolean(ANDROID_11,value).commit();
    }
    public boolean getAndroid11WhatsappBusinessStatus(){
        return sharedPreferences.getBoolean(ANDROID_112,false);
    }
    public void saveAndroid11WhatsappBusinessStatus(boolean value){
        sharedPreferences.edit().putBoolean(ANDROID_112,value).commit();
    }
    public boolean getLoginValueFromPref(){
        return sharedPreferences.getBoolean(LOGIN,false);
    }
    public void saveLoginValueToPref(boolean value){
        sharedPreferences.edit().putBoolean(LOGIN,value).commit();
    }
}
