package com.wheel.colorgame;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

public class Utils {
    public static String googleId="1083915228932-uof315vsr45e4uagchjqqkectq2hdur0.apps.googleusercontent.com";

    public static String Base_Url = "https://haryanviexpress.com/api/graph-api.php";


    public static void showAlertdialog(Activity activity, AlertDialog.Builder builder, String title, String msg) {
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(msg);
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(activity.getResources().getColor(R.color.white));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(activity.getResources().getColor(R.color.white));
    }
    public  static void showProgressDialog(Activity context, ProgressDialog progressDialog){
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.mipmap.ic_logo);
    };

}
