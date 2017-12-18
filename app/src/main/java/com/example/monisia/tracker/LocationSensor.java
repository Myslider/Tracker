package com.example.monisia.tracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import static android.content.Context.SENSOR_SERVICE;

/**
 * Created by Monisia on 12/10/2017.
 */

public class LocationSensor implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private Context mContext;
    private Activity mActivity;
    public GPSTracker mGPS;
    private Location location;

    public LocationSensor(Context context, Activity activity) {
        mGPS = new GPSTracker(context, activity);
        mContext = context;
        mActivity = activity;
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ((CoordinatesActivity) mContext).setLocation("i am", "here");
            return;
        }
        if (mGPS.CanGetLocation() && CoordinatesActivity.isTracking) {
            mGPS.getLocation();
            ((CoordinatesActivity) mContext).setLocation(String.valueOf(mGPS.getLatitude()), String.valueOf(mGPS.getLongitude()));
        } else
            ((CoordinatesActivity) mContext).setLocation("foo", "foo");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
