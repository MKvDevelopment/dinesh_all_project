package com.spin.wheelgame.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean isInternetOn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean res = activeNetwork != null && activeNetwork.isConnected();
        if (res) {
            // Toast.makeText(context,"Connected",Toast.LENGTH_SHORT).show();
        } else {
            // Toast.makeText(context,"Not Connected",Toast.LENGTH_SHORT).show();
        }
        return res;
    }

}
