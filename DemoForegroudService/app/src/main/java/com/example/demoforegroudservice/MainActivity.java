package com.example.demoforegroudservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Button btnstart,btnstop;
    private RelativeLayout relativeLayout;
    private ImageView imgSong,imgPlay,imgClear;
    private TextView tvSingleSong,tvTitleSong;
    private Song msong;
    private boolean isPlaying;
    private int actionMusic;

    private MyService myService;
    private boolean isServiceConnected;
    public ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MyService.MyBinder binder=(MyService.MyBinder) iBinder;
            myService = binder.getMyService();
            isServiceConnected= true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            myService=null;
            isServiceConnected=false;
        }
    };

    // nhan du lieu
    private BroadcastReceiver broadcastReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle=intent.getExtras();
            if(bundle==null){
                return;
            }
            msong=(Song)bundle.get("object_song");
            isPlaying=bundle.getBoolean("status_player");
            actionMusic=bundle.getInt("action_music");

            handleLayoutMusic(actionMusic);
        }
    };

    //xu ly cac nut
    private void handleLayoutMusic(int actionMusic) {
        switch (actionMusic){
            case MyService.ACTION_START:
                relativeLayout.setVisibility(View.VISIBLE);
                showInforSong();
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_PAUSE:
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_RESUME:
                setStatusButtonPlayOrPause();
                break;
            case MyService.ACTION_CLEAR:
                relativeLayout.setVisibility(View.GONE);
                break;
        }
    }
    private void showInforSong(){
        if(msong==null){
            return;
        }
        imgSong.setImageResource(msong.getImage());
        tvTitleSong.setText(msong.getTitile());
        tvSingleSong.setText(msong.getSingle());

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlaying){
                    sendActiontoService(MyService.ACTION_PAUSE);
                }else{
                    sendActiontoService(MyService.ACTION_RESUME);
                }
            }
        });
        imgClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendActiontoService(MyService.ACTION_CLEAR);
            }
        });
    }
    private void setStatusButtonPlayOrPause(){
        if(isPlaying){
            imgPlay.setImageResource(R.drawable.ic_baseline_pause_24);
        }else{
            imgPlay.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }
    }
    private void sendActiontoService(int actionMusic){
        Intent intent=new Intent(this,MyService.class);
        intent.putExtra("action_service",actionMusic);
        startService(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //lang nghe broadcast
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,new IntentFilter("send_data_to_activity"));
        btnstart=findViewById(R.id.btnStartService);
        btnstop=findViewById(R.id.btnStopService);
        relativeLayout=findViewById(R.id.rvBottom);
        imgClear=findViewById(R.id.imgStopSong1);
        imgPlay=findViewById(R.id.imgPlay1);
        imgSong=findViewById(R.id.imgSong1);
        tvSingleSong=findViewById(R.id.tvSingleSong1);
        tvTitleSong=findViewById(R.id.tvTitileSong1);

        btnstart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStartService();
            }
        });
        btnstop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStopService();
            }
        });

    }

    private void clickStartService() {
        Song song=new Song("Music1","Single1",R.drawable.music_1,R.raw.music_1);
        Intent intent=new Intent(this,MyService.class);
        Bundle bundle=new Bundle();
        bundle.putSerializable("key_data",song);
        intent.putExtras(bundle);

        startService(intent);
        bindService(intent,mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void clickStopService() {
        if(isServiceConnected){
            unbindService(mServiceConnection);
            isServiceConnected=false;
        }

        Intent intent=new Intent(this,MyService.class);
        stopService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //huy dang ky
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
