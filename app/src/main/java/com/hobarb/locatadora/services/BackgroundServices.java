package com.hobarb.locatadora.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.hobarb.locatadora.R;
import com.hobarb.locatadora.activities.TrackUserActivity;
import com.hobarb.locatadora.activities.UserActivity;
import com.hobarb.locatadora.utilities.CONSTANTS;
import com.hobarb.locatadora.utilities.LocationUpdates;

public class BackgroundServices extends Service {

    String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
    String channelName = "My Background Service";
    Handler handler = new Handler();
    TrackUserActivity tua = new TrackUserActivity();
    int count =0;
    FusedLocationProviderClient mFusedLocationClient;
    Context context;

    public void startRepeating(){
        handler.postDelayed(runnable, 1000);

    }

    public void stopRepeating() {
        handler.removeCallbacks(runnable);

    }



    private  Runnable runnable = new Runnable() {
        @Override
        public void run() {

            count++;
            LocationUpdates.requestNewLocationData(mFusedLocationClient, context);

            Toast.makeText(context, "Service"+ count + CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE + CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE, Toast.LENGTH_SHORT).show();

            if(count > 5)
            {
                stopRepeating();
            }
            else{
                handler.postDelayed(runnable, 1000);

            }

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


         mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
            context  = this;

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
