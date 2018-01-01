package com.example.monisia.tracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
    }

    public void goToLocationView(View view)
    {
        Intent intent = new Intent(this, CoordinatesActivity.class);
        startActivity(intent);
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
}
