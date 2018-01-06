package com.example.monisia.tracker;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;

public class ChildViewActivity extends Activity {
    LocationSensor lSensor;
    String result;
    CoordinateDto res;
    public static boolean isTracking = true;
    public TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_child_view);
        lSensor = new LocationSensor(this, this);
        textView = findViewById(R.id.textView9);
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
    }

    protected void onPause()
    {
        super.onPause();
    }

    public void sendCoordinates(final String longitude, final String latitude)
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
                date.get(Calendar.YEAR);
                postCor.date = String.format("%d-%s%d-%s%d",
                        date.get(Calendar.YEAR), date.get(Calendar.MONTH) +1<10 ? 0 : "", date.get(Calendar.MONTH) +1,
                        date.get(Calendar.DAY_OF_MONTH)<10 ? "0" : "", date.get(Calendar.DAY_OF_MONTH));
                postCor.time = String.format("%s%d:%s%d",
                        date.get(Calendar.HOUR_OF_DAY) <10 ? "0" : "", date.get(Calendar.HOUR_OF_DAY),
                        date.get(Calendar.MINUTE) <10 ? "0" : "", date.get(Calendar.MINUTE));
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

}
