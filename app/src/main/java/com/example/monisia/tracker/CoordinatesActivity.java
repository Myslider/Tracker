package com.example.monisia.tracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class CoordinatesActivity extends Activity {

    TextView latitude;
    TextView longitude;
    //ImageView trackingImage;
    LocationSensor lSensor;
    String result;
    CoordinateDto res;
    public static boolean isTracking = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_coordinates);

        lSensor = new LocationSensor(this, this);

        //trackingImage = findViewById(R.id.imageView);
        latitude = findViewById(R.id.textView6);
        longitude = findViewById(R.id.textView4);
    }

    public void setLocation(String lat, String lon) {
        latitude.setText("");
        longitude.setText("");
        latitude.setText(lat);
        longitude.setText(lon);
        //this.sendCoordinates(lon, lat);
        this.getCoordinates();
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
        //if (isTracking)
        //{
            //Change tracking image, if to work on older versions
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                trackingImage.setImageDrawable(getResources().getDrawable(R.mipmap.is_tracking_launcher_round, getApplicationContext().getTheme()));
            } else {
                trackingImage.setImageDrawable(getResources().getDrawable(R.mipmap.is_tracking_launcher_round));
            }*/
            //lSensor.mGPS.getLocation();

        //}
        //else
        //{
            //Change tracking image, if to work on older versions
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                trackingImage.setImageDrawable(getResources().getDrawable(R.mipmap.not_tracking_launcher_round, getApplicationContext().getTheme()));
            } else {
                trackingImage.setImageDrawable(getResources().getDrawable(R.mipmap.not_tracking_launcher_round));
            }*/
            //lSensor.mGPS.stopUsingGPS();
        //}
    }

    protected void onPause()
    {
        super.onPause();
    }

    private void getCoordinates()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    String url = "http://192.168.1.10:8080/coordinates/child/1";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                    CoordinateDto []  coordinateDtos = restTemplate.getForObject(url, CoordinateDto[].class);
                } catch (Exception e) {
                    result = "failed";
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private void sendCoordinates(final String longitude, final String latitude)
    {
        Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
        String url = "http://192.168.1.10:8080/coordinates/save";
        RestTemplate restTemplate = new RestTemplate();
        CoordinateDto postCor = new CoordinateDto();
        postCor.longitude = longitude;
        postCor.latitude = latitude;
        Calendar date = Calendar.getInstance();
        postCor.date = String.format("%d-%d-%d", date.get(Calendar.YEAR), date.get(Calendar.MONTH) +1, date.get(Calendar.DAY_OF_MONTH));
        if (date.get(Calendar.HOUR_OF_DAY)<10)
        postCor.time = String.format("0%d:%d",date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));
        else
            postCor.time = String.format("%d:%d",date.get(Calendar.HOUR_OF_DAY), date.get(Calendar.MINUTE));
        postCor.childFirstName ="Pawel";
        postCor.childLastName ="Kowalski";
        postCor.childId ="1";

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

    public void getChildren()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    String url = "http://192.168.1.10:8080/parent/1";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                    ChildDto []  childDto = restTemplate.getForObject(url, ChildDto[].class);
                } catch (Exception e) {
                    result = "failed";
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
