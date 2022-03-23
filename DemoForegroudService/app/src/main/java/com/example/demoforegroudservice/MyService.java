package com.example.demoforegroudservice;

import static com.example.demoforegroudservice.MyAplication.CHANEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class MyService extends Service {

    public static final int ACTION_PAUSE=1;
    public static final int ACTION_RESUME=2;
    public static final int ACTION_CLEAR=3;
    public static final int ACTION_START=4;

    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private Song msong;

    private MyBinder myBinder=new MyBinder();
    public class MyBinder extends Binder {
        MyService getMyService(){
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Son", "My Service On Create ");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Son", "ibind ");
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song song = (Song) bundle.get("key_data");
            if (song != null) {
                msong=song;
                senNotification(song);
                starMusic(song);

            }
            int actionMusic=intent.getIntExtra("action_service",0);
            handleActionMusic(actionMusic);

        }

        return START_NOT_STICKY;
    }

    private void starMusic(Song song) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), song.getResource());
        }
        mediaPlayer.start();
        isPlaying=true;
        sendActiontoActivity(ACTION_START);
    }
    private void handleActionMusic(int action){
        switch (action){
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case ACTION_CLEAR:
                clearMusic();
                break;
        }
    }

    private void clearMusic() {
        stopSelf();
        sendActiontoActivity(ACTION_CLEAR);
    }

    private void pauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.pause();
            isPlaying = false;
            senNotification(msong);
            sendActiontoActivity(ACTION_PAUSE);
        }
    }

    private void resumeMusic() {
        if(mediaPlayer!=null && !isPlaying){
            mediaPlayer.start();
            isPlaying=true;
            senNotification(msong);
            sendActiontoActivity(ACTION_RESUME);
        }
    }

    private void senNotification(Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), song.getImage());

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_custom_notification);
        remoteViews.setTextViewText(R.id.tvTitileSong, song.getTitile());
        remoteViews.setTextViewText(R.id.tvSingleSong, song.getSingle());
        remoteViews.setImageViewBitmap(R.id.imgSong, bitmap);
        remoteViews.setImageViewResource(R.id.imgPlay, R.drawable.ic_baseline_pause_24);

        if(isPlaying){
            remoteViews.setOnClickPendingIntent(R.id.imgPlay,getPendingIntent(this,ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.imgPlay,R.drawable.ic_baseline_pause_24);
        }else{
            remoteViews.setOnClickPendingIntent(R.id.imgPlay,getPendingIntent(this,ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.imgPlay,R.drawable.ic_baseline_play_arrow_24);
        }
            remoteViews.setOnClickPendingIntent(R.id.imgStopSong,getPendingIntent(this,ACTION_CLEAR));

        Notification notification = new NotificationCompat.Builder(this, CHANEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_clear_24)
                .setContentIntent(pendingIntent)
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();
        startForeground(1, notification);
    }
    private PendingIntent getPendingIntent(Context context,int action){
            Intent intent=new Intent(this,MyReceiver.class);
            intent.putExtra("action_music",action);
            return PendingIntent.getBroadcast(context.getApplicationContext(),action,intent,PendingIntent.FLAG_MUTABLE);
    }
    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("Son", "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Son", "My Service On Destroy");
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    private void sendActiontoActivity(int action){
        Intent intent=new Intent("send_data_to_activity");
        Bundle bundle =new Bundle();
        bundle.putSerializable("object_song",msong);
        bundle.putBoolean("status_player",isPlaying);
        bundle.putInt("action_music",action);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}