package com.perfect.traders.Constant;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.perfect.traders.Activity.MainActivity;
import com.perfect.traders.R;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class FirebaseMessagingServices extends FirebaseMessagingService {
    private static final String CHANNEL_ID = "channel_id";
    public static final String TYPE = "type";
    private static final String NORMAL_TYPE = "normal";
    private static final String URL_TYPE = "url";
    private static final String URL = "link";
    private Intent intent;
    private String type = NORMAL_TYPE;

    private static final String TAG = "NOTIFICATION_SERVICE";
    public FirebaseMessagingServices() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tune);
        //File file = new File(url.toString());
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setContentIntent(pendingIntent)
                .setSound(url)
                .setSmallIcon(R.drawable.ic_noti_icon)
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
        String uid=Objects.requireNonNull(FirebaseAuth.getInstance().getUid());
        DatabaseReference notifToken = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(uid);
        Map<String,Object> map=new HashMap<>();
        map.put("token",s);
        notifToken.updateChildren(map);

        DocumentReference documentReference= FirebaseFirestore.getInstance().collection("User_List").document(uid);
        documentReference.update("device_id",s);
    }

}
