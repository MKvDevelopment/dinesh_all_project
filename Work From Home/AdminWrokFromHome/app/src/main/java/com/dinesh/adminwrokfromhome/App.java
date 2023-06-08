package com.dinesh.adminwrokfromhome;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.security.identity.NoAuthenticationKeyAvailableException;

import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    public static final String CHANNEL_ONE="channel_one";
    public static final String CHANNEL_TWO="channel_two";
    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {

            NotificationChannel channel1=new NotificationChannel(CHANNEL_ONE,
                    "Channel One",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is channel one for video notifications");

            NotificationChannel channel2=new NotificationChannel(CHANNEL_TWO,
                    "Channel two",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is channel two for audio notifications");

            NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            List<NotificationChannel> list=new ArrayList<>();
            list.add(channel1);
            list.add(channel2);

            manager.createNotificationChannels(list);


        }


    }
}
