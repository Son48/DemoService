package com.example.demoforegroudservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int actionMusic = intent.getIntExtra("action_music",0);

        //chuyen di de lang nghe nen h chuyen nguoc lai myservice de xu ly
        Intent intent1=new Intent(context,MyService.class);
        intent1.putExtra("action_service",actionMusic);

        context.startService(intent1);
    }
}
