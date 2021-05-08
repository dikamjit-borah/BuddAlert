package com.hobarb.locatadora.utilities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
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
            CONSTANTS.BG_STUFF.CURRENT_USER_LATITUDE = (mLastLocation.getLatitude());
            CONSTANTS.BG_STUFF.CURRENT_USER_LONGITUDE = (mLastLocation.getLongitude());
            }
    };

    public static double calculateDistance(double userLat, double userLng, double venueLat, double venueLng){
        double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
        double remaining_distance = 0.0;
        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        remaining_distance =  (Math.round(AVERAGE_RADIUS_OF_EARTH_KM * c));
        return remaining_distance;
    }
}
