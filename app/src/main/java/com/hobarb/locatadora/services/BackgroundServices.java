package com.hobarb.locatadora.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.hobarb.locatadora.R;
import com.hobarb.locatadora.activities.TrackUserActivity;
import com.hobarb.locatadora.activities.UserActivity;
import com.hobarb.locatadora.utilities.CONSTANTS;
import com.hobarb.locatadora.utilities.LocationUpdates;

import java.util.ArrayList;

public class BackgroundServices extends IntentService {


    public static final String ACTION_LOCATION_BROADCAST = BackgroundServices.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";


    String NOTIFICATION_CHANNEL_ID = "com.example.simpleapp";
    String channelName = "My Background Service";
    Handler handler = new Handler();

    int count =0;
    FusedLocationProviderClient mFusedLocationClient;
    Context context;
    boolean destination_reached = false;

    public BackgroundServices() {
        super(null);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public void stopRepeating() {
        handler.removeCallbacks(runnable);

    }

    private  Runnable runnable = new Runnable() {
        @Override
        public void run() {

            count++;
            LocationUpdates.requestNewLocationData(mFusedLocationClient, context);

            Toast.makeText(context, "Service "+ count, Toast.LENGTH_SHORT).show();

            updateTrackUserActivity();

            Toast.makeText(context, "" + CONSTANTS.BG_STUFF.CURRENT_DISTANCE_REMAINING, Toast.LENGTH_SHORT).show();
            if(CONSTANTS.BG_STUFF.CURRENT_DISTANCE_REMAINING<2)
            {
                destination_reached = true;
                updateTrackUserActivity();
                Toast.makeText(context, "Destination reached", Toast.LENGTH_SHORT).show();
            }

            if(destination_reached || count >6)
            {
                stopRepeating();
                Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
                ringtone.play();
            }
            else{
                if(CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE == 0.0 || CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE == 0.0)
                    handler.postDelayed(runnable, 1000);
                else
                {
                    //sendSMS();
                    handler.postDelayed(runnable, 5000);
                }

            }

        }
    };

    private void sendSMS() {
          final String SMS_SENT_INTENT_FILTER = "com.yourapp.sms_send";
          final String SMS_DELIVERED_INTENT_FILTER = "com.yourapp.sms_delivered";

          String curr_loc = "https://maps.google.com/?q="+ CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE +","+CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE+"";

        String message = "Hello my friend, I am en route " + CONSTANTS.BG_STUFF.DESTINATION + ". Currently I am here -> " + curr_loc;

        ArrayList<String> phnNo = new ArrayList<>(); //preferable use complete international number
        phnNo.add("+919706660771");
        phnNo.add("+919854052673");
        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SMS_SENT_INTENT_FILTER), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(
                SMS_DELIVERED_INTENT_FILTER), 0);

        SmsManager sms = SmsManager.getDefault();
        for(int i = 0; i<2; i++)
        {
            sms.sendTextMessage(phnNo.get(i), null, message, sentPI, deliveredPI);
        }



    }

    private void updateTrackUserActivity() {
        Intent intent1 = new Intent();
        intent1.setAction(CONSTANTS.BG_STUFF.INTENT_ACTION);

        intent1.putExtra(CONSTANTS.BG_STUFF.INTENT_EXTRA_LATITUDE, CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE);
        intent1.putExtra(CONSTANTS.BG_STUFF.INTENT_EXTRA_LONGITUDE, CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE);
        intent1.putExtra(CONSTANTS.BG_STUFF.INTENT_EXTRA_REACHED, destination_reached);

        sendBroadcast(intent1);
    }


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
    protected void onHandleIntent(@Nullable Intent intent) {

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(false);
        stopSelf();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        context = this;
        createNotification();
        Intent intent1 = new Intent(this, UserActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent1, 0);
        Notification notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("" + getApplicationContext().getApplicationInfo().packageName).setContentText("Alarm set for destination").setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();
        startForeground(1, notification);


         mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());


        handler.post(runnable);
        //stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(context, "totototo n" + "otig", Toast.LENGTH_SHORT).show();
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

    }


}
