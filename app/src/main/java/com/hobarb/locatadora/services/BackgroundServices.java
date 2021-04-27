package com.hobarb.locatadora.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.hobarb.locatadora.R;
import com.hobarb.locatadora.activities.UserActivity;
import com.hobarb.locatadora.utilities.CONSTANTS;

public class BackgroundServices extends Service {

    String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
    String channelName = "My Background Service";

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        //runinforeground
        @Override
        public void run() {
            Toast.makeText(BackgroundServices.this, "test", Toast.LENGTH_SHORT).show();
            handler.postDelayed(this, CONSTANTS.ALARM_STUFF.CHECK_LOCATION_INTERVAL);
        }
    };
    
    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(false);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        createNotification();
        Intent intent1 = new Intent(this, UserActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("" + getApplicationContext().getApplicationInfo().packageName).setContentText("Alarm set for destination").setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();
        startForeground(1, notification);
        handler.post(runnable);
        //stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

    }
}
