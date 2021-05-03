package com.hobarb.locatadora.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.hobarb.locatadora.R;
import com.hobarb.locatadora.services.BackgroundServices;
import com.hobarb.locatadora.utilities.CONSTANTS;
import com.hobarb.locatadora.utilities.LocationUpdates;

public class TrackUserActivity extends AppCompatActivity {
    private static final int PERMISSION_ID = 1001;

    String lat, lon;
    int count = 0;
    Handler handler;
    Intent serviceIntent;
    TextView current_tv;
    double dest_lat, dest_lng;
    boolean reached_destination = false;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            double curr_lat = intent.getDoubleExtra(CONSTANTS.BG_STUFF.INTENT_EXTRA_LATITUDE, 0.0);
            double curr_lng = intent.getDoubleExtra(CONSTANTS.BG_STUFF.INTENT_EXTRA_LONGITUDE, 0.0);
            double distance_remaining =  LocationUpdates.calculateDistance(curr_lat, curr_lng, dest_lat, dest_lng);
            CONSTANTS.BG_STUFF.CURRENT_DISTANCE_REMAINING = distance_remaining;
            current_tv.setText("Distance remaining ~ " + distance_remaining + "km");

            reached_destination = intent.getBooleanExtra(CONSTANTS.BG_STUFF.INTENT_EXTRA_REACHED, false);
            if(reached_destination)
            {
                findViewById(R.id.ll_destReached_ac_track).setVisibility(View.VISIBLE);
            }


        //    CONSTANTS.BG_STUFF.DESTINATION_LAT_LNG;

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONSTANTS.BG_STUFF.INTENT_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CONSTANTS.BG_STUFF.INTENT_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_user);

         serviceIntent = new Intent(this, BackgroundServices.class);

         TextView destination_tv = findViewById(R.id.tv_enroute_ac_track);
         String s = CONSTANTS.BG_STUFF.DESTINATION_LAT_LNG;
         String s0 = s.replace("lat/lng: ", "");
        String s1 = s0.replace("(", "");
        String s2 = s1.replace(")", "");

        String[] words=s2.split(",");
         dest_lat = Double.parseDouble(words[0]);
         dest_lng = Double.parseDouble(words[1]);
         CONSTANTS.BG_STUFF.DESTINATION = getIntent().getStringExtra("destination");
        destination_tv.setText("En route " + CONSTANTS.BG_STUFF.DESTINATION);

        current_tv = findViewById(R.id.tv_currentLatLng_ac_track);
            startBackgroundService();

            findViewById(R.id.btn_stopAlarm_ac_track).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(TrackUserActivity.this, "Alarm stopped", Toast.LENGTH_SHORT).show();
                }
            });

    }




    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startBackgroundService();
            }
        }
    }

    private void startBackgroundService() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }

    }
}