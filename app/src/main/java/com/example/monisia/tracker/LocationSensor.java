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
import android.widget.TextView;

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
    private int sensorTime = 101;

    public LocationSensor(Context context, Activity activity) {
        mGPS = new GPSTracker(context, activity);
        mContext = context;
        mActivity = activity;
        sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, 3);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (mContext instanceof ChildViewActivity)
            {
                ((ChildViewActivity) mContext).textView.setText("need location permission");
                ((ChildViewActivity) mContext).setLocation("i am", "here");
            }
            return;
        }

        if (sensorTime < 100)
        {
            sensorTime++;
            return;
        }
        sensorTime =0;

        if (mGPS.CanGetLocation()) {
            mGPS.getLocation();
            if (mContext instanceof ChildViewActivity) {
                ((ChildViewActivity) mContext).setLocation(String.valueOf(mGPS.getLatitude()), String.valueOf(mGPS.getLongitude()));
                ((ChildViewActivity) mContext).textView.setText("You are being TRACKED");
            }
        } else {
            if (mContext instanceof ChildViewActivity)
            {
                ((ChildViewActivity) mContext).textView.setText("can't get location");
                ((ChildViewActivity) mContext).setLocation(String.valueOf(mGPS.getLatitude()), String.valueOf(mGPS.getLongitude()));
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}

