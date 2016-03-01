package com.bsupits.persistentservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by supitsara on 1/3/2559.
 */
public class PersistentService extends Service {

    CountDownTimer countDownTimer;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        countDownTimer.cancel();
        stopForeground(true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        countDownTimer = new CountDownTimer(11000,100) {
            @Override
            public void onTick(long millisUntilFinished) {
                Intent intent = new Intent();
                intent.setAction(MainActivity.mBroadcastStringAction);
                String sec = String.format("%02d", Integer.parseInt(String.valueOf(millisUntilFinished / 1000)));
                intent.putExtra("second", sec);
                sendBroadcast(intent);
                pushNotification(sec);
            }

            @Override
            public void onFinish() {
                countDownTimer.cancel();
                onDestroy();
            }
        }.start();

        return START_STICKY;
    }

    public void pushNotification(String strSecond) {
        Intent intent = new Intent(PersistentService.this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(PersistentService.this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(intent);
        PendingIntent pendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification =
                new NotificationCompat.Builder(PersistentService.this)
                        .setSmallIcon(R.drawable.ic_av_timer_white_48dp)
                        .setContentTitle("Countdown Timer")
                        .setContentIntent(pendingIntent)
                        .setContentText(strSecond + "s remaining")
                        .build();

        notification.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        startForeground(1000,notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
