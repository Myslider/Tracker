package com.example.monisia.tracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        final Context context = this;
        // try login with saved credentials
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("TrackerPref", 0);
        if (pref.getString("username", "") != "")
        {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                if (tryLogin(pref.getString("username", ""), pref.getString("password", ""),
                        pref.getBoolean("isParent", false)))
                {
                    if (pref.getBoolean("isParent", false)) {
                        Intent intent = new Intent(context, MapOsmActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent intent = new Intent(context, ChildViewActivity.class);
                        startActivity(intent);
                    }
                }
                }
            });
            thread.start();
        }
        setContentView(R.layout.activity_main);
    }

    public void goToSignInView(View view)
    {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void goToRegisterView(View view)
    {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
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

    private Boolean tryLogin(final String username, final String password, final Boolean isParent)
    {
        try {
            String url = getString(R.string.DBUrl) + "login/";
            RestTemplate restTemplate = new RestTemplate();
            LoginDto loginDto = new LoginDto();
            loginDto.Username = username;
            loginDto.Password = password;
            loginDto.IsParent = isParent;
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            LoginDto result = restTemplate.postForObject(url, loginDto, LoginDto.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
