package com.example.monisia.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

public class CoordinatesActivity extends Activity {

    TextView latitude;
    TextView longitude;
    //ImageView trackingImage;
    LocationSensor lSensor;
    String result;
    public static boolean isTracking = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            // wylaczyc firewall, sprawdzic antywirus czy nie blokuje, byc polaczonym do tego samego wifi co telefon, odpowiedni adres IP wpisac
            // mozna sprawdzic czy telefon jest w stanie wejsc na ta stronke, jesli tak to raczej zadziala
            // u mnie "Zapora osobista" w eset musi byc wylaczona
            @Override
            public void run() {
                try  {
                    String url = "http://192.168.1.10:8080/coordinates/child/1";   //"https://ajax.googleapis.com/ajax/" + "services/search/web?v=1.0&q={query}";
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
                    result = restTemplate.getForObject(url, String.class);
                    String x = result;
                } catch (Exception e) {
                    result = "failed";
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }
}
