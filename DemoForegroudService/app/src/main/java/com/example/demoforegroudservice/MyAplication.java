package com.example.demoforegroudservice;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.os.Build;

public class MyAplication extends Application {

    public static final String CHANEL_ID="chanel_service_exmaple";
    @Override
    public void onCreate() {
        super.onCreate();

        createChaneNotification();
    }

    private void createChaneNotification() {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.S){
            NotificationChannel channel=new NotificationChannel(CHANEL_ID,"Chanel Service Example", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager=getSystemService(NotificationManager.class);
            channel.setSound(null,null);
            if(manager!=null){
                manager.createNotificationChannel(channel);
            }
        }
    }
}
