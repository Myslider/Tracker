package com.example.monisia.tracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class GPSTracker implements LocationListener {
    private static final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MIN_TIME_BW_UPDATES = 0;
    static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 1;

    private final Context mContext;
    private final Activity mActivity;

    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private Location location;
    private LocationManager locationManager;
    private String provider;

    private boolean canGetLocation = false;

    public GPSTracker(Context context, Activity activity) {
        this.mContext = context;
        this.mActivity = activity;
        this.getLocation();
    }

    public void getLocation() {
        try {
            if (ContextCompat.checkSelfPermission(
                    this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this.mActivity,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            }

            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled == false && isNetworkEnabled == false) {
                location.setLatitude(0);
                location.setLongitude(0);
                // no network provider is enabled
            }
            else {
                this.canGetLocation = true;
                if (isGPSEnabled) {
                    this.getLastLocation(LocationManager.GPS_PROVIDER);
                } else if (isNetworkEnabled) {
                    this.getLastLocation(LocationManager.NETWORK_PROVIDER);
                }
            }

            locationManager.requestLocationUpdates(this.provider, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        } catch (Exception e) { }
    }

    private void getLastLocation(String provider) {
        this.provider = provider;
        if (ContextCompat.checkSelfPermission(
                this.mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.mActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        }

        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(provider);
        }
    }

    /**
     * Stop using GPS listener Calling this function will stop using GPS in your
     * app
     */
    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    public double getLatitude() {
        if (location != null) {
            return location.getLatitude();
        }

        return 404;
    }

    public double getLongitude() {
        if (location != null) {
            return location.getLongitude();
        }

        return 404;
    }

    public boolean CanGetLocation() {
        return canGetLocation;
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }
}
