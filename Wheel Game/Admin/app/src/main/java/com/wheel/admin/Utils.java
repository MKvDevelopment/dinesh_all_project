package com.wheel.admin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;

import java.util.Random;

public class Utils {

    public static String googleId = "1083915228932-uof315vsr45e4uagchjqqkectq2hdur0.apps.googleusercontent.com";


    public static void showAlertdialog(Activity activity, AlertDialog.Builder builder, String title, String msg) {
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(msg);
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(activity.getResources().getColor(R.color.alert_btn_color));
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(activity.getResources().getColor(R.color.alert_btn_color));
    }

    public static void showProgressDialog(Activity context, ProgressDialog progressDialog) {
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setIcon(R.mipmap.ic_launcher);
    }


    public static String randomResult(String value){
        char[] pink = (value).toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 1; i++) {
            result.append(pink[new Random().nextInt(pink.length)]);
        }
        return result.toString();
    }


    public static String randomGererate(String color) {

        char[] pink = ("01").toCharArray();
        char[] red = ("0123").toCharArray();
        char[] bothPlan = ("0123456").toCharArray();

        StringBuilder result = new StringBuilder();

        if (color.contains("pink")) {

            for (int i = 0; i < 1; i++) {
                result.append(pink[new Random().nextInt(pink.length)]);
            }
            return result.toString();
        } else if (color.contains("red")) {
            for (int i = 0; i < 1; i++) {
                result.append(red[new Random().nextInt(red.length)]);
            }
            return result.toString();
        } else {
            for (int i = 0; i < 1; i++) {
                result.append(bothPlan[new Random().nextInt(bothPlan.length)]);
            }
            return result.toString();
        }


    }

    public static String Time_Url = "https://worldtimeapi.org/api/timezone/Asia/Kolkata";
    public static String Base_Url = "https://haryanviexpress.com/api/graph-api.php";
    public static String Start_Url = "https://haryanviexpress.com/api/index.php?status=start";
    public static String Pause_Url = "https://haryanviexpress.com/api/index.php?status=pause";
    public static String Reset_Url = "https://haryanviexpress.com/api/index.php?status=reset";

}
