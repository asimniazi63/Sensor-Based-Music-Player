package com.example.asimkhanniazi.musicappma;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;

public class MusicService extends Service {

    MediaPlayer mediaPlayer;
    int pos;

    private int NOTIFICATION_ID = 12345;
    NotificationManager nManager;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int pos = intent.getIntExtra("Position",0);

        try {

            mediaPlayer = new MediaPlayer();
            //sets the data source of audio file
            mediaPlayer.setDataSource(MainActivity.absolutePath);
            //prepares the player for playback synchronously
            mediaPlayer.prepare();

            //sets the player for looping
            mediaPlayer.setLooping(true);



            //starts or resumes the playback
            mediaPlayer.start();
            if (intent!=null) {
                mediaPlayer.seekTo(pos);
            } else {
                mediaPlayer.seekTo(MainActivity.currentPositionofMusic);
            }
            showNotification();

        } catch (IOException e) {
            e.printStackTrace();
            Log.i("show","Error: "+e.toString());
        }

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.d("MusicService", "onDestroy: service killed");
        MainActivity.currentPositionofMusic = mediaPlayer.getCurrentPosition();
        mediaPlayer.stop();
        //releases any resource attached with MediaPlayer object
        mediaPlayer.reset();
        mediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void showNotification() {

        String csongName = MainActivity.absolutePath;
        String[] cSong = csongName.split("/");
        String nSonName = cSong[cSong.length-1];


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.app_icon_music)
                        .setContentTitle("Sensor Based Music Player")
                        .setContentText(nSonName)
                        .setOngoing(true)
                        .setColor(getApplicationContext().getResources().getColor(R.color.mwhite));


        Intent targetIntent = new Intent(getApplicationContext(), MainActivity.class);
        targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void cancelNotification() {

        nManager.cancel(NOTIFICATION_ID);
    }

}
