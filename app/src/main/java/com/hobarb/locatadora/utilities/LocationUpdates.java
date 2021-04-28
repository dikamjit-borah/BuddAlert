package com.hobarb.locatadora.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationUpdates {
    static Context contextx;
    @SuppressLint("MissingPermission")
    public static void requestNewLocationData(FusedLocationProviderClient mFusedLocationClient, Context context){
    contextx = context;
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    public static LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE = String.valueOf(mLastLocation.getLatitude());
            CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE = String.valueOf(mLastLocation.getLongitude());
            }
    };
}
