package com.workz.athome.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.workz.athome.Model.DailyTask.TaskRoot;
import com.workz.athome.Model.UserData.Root;

public class Utils {

    public static String sharedPrefrenceName="myAppPrefrence";
    public static String adminSharedPrefrenceName="myAdminAppPrefrence";
    public static String userTaskSharedPrefrenceName="userTaskData";

    public static void setUserSharedPreference(Root root1,Activity activity) {
        // Toast.makeText(this, "register User", Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = activity.getSharedPreferences(Utils.sharedPrefrenceName,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("activation",root1.data.getActivation());
        editor.putString("captcha_price",root1.data.getCaptcha_price());
        editor.putString("email",root1.data.getEmail());
        editor.putString("mobile",root1.data.getMobile());
        editor.putString("uid",root1.data.getUid());
        editor.putString("userName",root1.data.getUserName());
        editor.putString("wallet",root1.data.getWallet());
        editor.putString("instant_activation",root1.data.getInstant_activation());
        editor.putString("install",root1.data.getInstall());
        editor.putString("index",root1.data.getIndex());
        editor.putString("winning_balence",root1.data.getWinning_balence());
        editor.putString("remove_ads",root1.data.getRemove_ads());
        editor.putString("token",root1.data.getToken());
        editor.putString("is_button_pressed",root1.data.getIs_button_pressed());
        editor.apply();
    }
    public static void updateUserSharedPreference(Root root1,Activity activity) {
        // Toast.makeText(this, "register User", Toast.LENGTH_SHORT).show();
        SharedPreferences preferences = activity.getSharedPreferences(Utils.sharedPrefrenceName,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("activation",root1.data.getActivation());
        editor.putString("captcha_price",root1.data.getCaptcha_price());
        editor.putString("email",root1.data.getEmail());
        editor.putString("mobile",root1.data.getMobile());
       // editor.putString("uid",root1.data.getUid());
        editor.putString("userName",root1.data.getUserName());
        editor.putString("wallet",root1.data.getWallet());
        editor.putString("instant_activation",root1.data.getInstant_activation());
        editor.putString("install",root1.data.getInstall());
        editor.putString("index",root1.data.getIndex());
        editor.putString("winning_balence",root1.data.getWinning_balence());
        editor.putString("remove_ads",root1.data.getRemove_ads());
        editor.putString("is_button_pressed",root1.data.getIs_button_pressed());
        editor.apply();
    }

    public static void setUserTaskSharedPreference(TaskRoot root, Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(Utils.userTaskSharedPrefrenceName,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("task_1",root.getData().task_1);
        editor.putString("task_2",root.getData().task_2);
        editor.putString("task_3",root.getData().task_3);
        editor.putString("task_4",root.getData().task_4);
        editor.putString("task_5",root.getData().task_5);
        editor.putString("task_6",root.getData().task_6);
        editor.putString("task_7",root.getData().task_7);
        editor.putString("task_8",root.getData().task_8);
        editor.putString("task_9",root.getData().task_9);
        editor.putString("task_10",root.getData().task_10);

        editor.apply();
    }
    public static void updateUserTaskSharedPreference(TaskRoot root,Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(Utils.sharedPrefrenceName,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("task_1",root.getData().task_1);
        editor.putString("task_2",root.getData().task_2);
        editor.putString("task_3",root.getData().task_3);
        editor.putString("task_4",root.getData().task_4);
        editor.putString("task_5",root.getData().task_5);
        editor.putString("task_6",root.getData().task_6);
        editor.putString("task_7",root.getData().task_7);
        editor.putString("task_8",root.getData().task_8);
        editor.putString("task_9",root.getData().task_9);
        editor.putString("task_10",root.getData().task_10);
        editor.apply();
    }



    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
