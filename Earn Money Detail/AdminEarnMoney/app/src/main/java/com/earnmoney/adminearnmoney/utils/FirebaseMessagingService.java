package com.earnmoney.adminearnmoney.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.earnmoney.adminearnmoney.Activity.ChatActivity;
import com.earnmoney.adminearnmoney.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;


public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String CHANNEL_ID = "channel_id";
    private static final String NORMAL_TYPE = "normal";
    private Intent intent;
    private String type = NORMAL_TYPE;

    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, ChatActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tune);
        //File file = new File(url.toString());
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .setSound(url)
                .setSmallIcon(R.mipmap.ic_logo)
                .setAutoCancel(false);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

    }


    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        sendTokenToServer(s);
    }

    private void sendTokenToServer(String s) {
        DatabaseReference notifToken = FirebaseDatabase.getInstance().getReference()
                .child("admin");
        Map<String,Object> map = new HashMap<>();
        map.put("token", s);
        notifToken.updateChildren(map);
    }

}
