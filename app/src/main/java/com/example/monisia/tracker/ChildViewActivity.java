package com.example.monisia.tracker;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;

public class ChildViewActivity extends Activity {
    LocationSensor lSensor;
    CoordinateDto res;
    public static boolean isTracking = true;
    public TextView textView;
    public TextView keyView;
    public TextView childIdView;
    private String firstName;
    private String lastName;
    private Long childId;
    ImageView trackingImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_child_view);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("TrackerPref", 0);

        lSensor = new LocationSensor(this, this);
        trackingImage = findViewById(R.id.imageView5);
        textView = findViewById(R.id.textView9);
        keyView = findViewById(R.id.textView13);
        childIdView = findViewById(R.id.textView2);
        keyView.setText(pref.getString("keygen", "unknown"));
        firstName = pref.getString("FirstName", "unknown");
        lastName = pref.getString("LastName", "unknown");
        childId = pref.getLong("childId", 0);
        childIdView.setText(String.valueOf(childId));
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    protected void onResume()
    {
        super.onResume();
        this.changeTrackingImage();
    }

    protected void onPause()
    {
        super.onPause();
        this.changeTrackingImage();
    }

    private void changeTrackingImage()
    {
        if (isTracking)
        {
            //Change tracking image, if to work on older versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                trackingImage.setImageDrawable(getResources().getDrawable(R.mipmap.istracking_image, getApplicationContext().getTheme()));
            } else {
                trackingImage.setImageDrawable(getResources().getDrawable(R.mipmap.istracking_image));
            }
        }
        else
        {
            //Change tracking image, if to work on older versions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                trackingImage.setImageDrawable(getResources().getDrawable(R.mipmap.nottracking_image, getApplicationContext().getTheme()));
            } else {
                trackingImage.setImageDrawable(getResources().getDrawable(R.mipmap.nottracking_image));
            }
        }
    }

    public void setLocation(String lat, String lon) {
        if(lat == "404" || lat == "foo" || lat== "here" || lon=="here")
        {
            isTracking=false;
        }
        this.changeTrackingImage();

        if (isTracking)
            this.sendCoordinates(lon, lat);
    }

    public void sendCoordinates(final String longitude, final String latitude)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = getString(R.string.DBUrl) + "coordinates/save";
                RestTemplate restTemplate = new RestTemplate();
                CoordinateDto postCor = new CoordinateDto();
                postCor.longitude = longitude;
                postCor.latitude = latitude;
                Calendar date = Calendar.getInstance();
                postCor.date = getDate(date);
                postCor.time = getTime(date);
                postCor.childId = String.valueOf(childId);

                try {
                    restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                    res = restTemplate.postForObject(url, postCor, CoordinateDto.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }});

        thread.start();
    }

    public String getDate(Calendar date)
    {
        return String.format("%d-%s%d-%s%d",
                date.get(Calendar.YEAR), date.get(Calendar.MONTH) +1<10 ? 0 : "", date.get(Calendar.MONTH) +1,
                date.get(Calendar.DAY_OF_MONTH)<10 ? "0" : "", date.get(Calendar.DAY_OF_MONTH));
    }

    public String getTime(Calendar date){
        return String.format("%s%d:%s%d",
                date.get(Calendar.HOUR_OF_DAY) <10 ? "0" : "", date.get(Calendar.HOUR_OF_DAY),
                date.get(Calendar.MINUTE) <10 ? "0" : "", date.get(Calendar.MINUTE));
    }
}
