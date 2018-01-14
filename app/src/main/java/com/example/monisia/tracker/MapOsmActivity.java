package com.example.monisia.tracker;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.osmdroid.views.overlay.Marker;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class MapOsmActivity extends AppCompatActivity implements SensorEventListener,SelectionSpinner.OnMultipleItemsSelectedListener {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String TAG = "PG";
    private MapView osm;
    private MapController mc;
    private List<Person> ListofChildren = new ArrayList<>();
    private SensorManager SM;
    private Sensor mySensor;
    private static int CurrColor = 0;
    private SelectionSpinner multiSelectionSpinner;
    private List<String> select_qualification = new ArrayList<>();
    private String parentId;
    public Handler handler;
    public Thread thread;
    public volatile List<String> SelectedChildren = new ArrayList<>();
    private ChildDto []  childDto;

    private static final int COLOR_CHOICES[] = {
            Color.MAGENTA,
            Color.RED,
            Color.BLUE,
            Color.CYAN,
            Color.BLACK
    };

    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    static class Person {
        int id;
        String FirstName, LastName;
        List<GeoPoint> personGeoPoints = new ArrayList<>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_map_osm);
        osm = (MapView) findViewById(R.id.mapView);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("TrackerPref", 0);
        parentId = String.valueOf(pref.getLong("Id", 0));
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissions();
        }
        mapOSM();
        multiSelectionSpinner = (SelectionSpinner) findViewById(R.id.spinner1);
        multiSelectionSpinner.setListener(this);
        childFromDB();
    }

    public boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (listPermissionsNeeded.size()!=0) {

            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mapOSM();
            }
            return;
        }
    }

    public void mapOSM() {
        osm.setTileSource(TileSourceFactory.MAPNIK);
        osm.setMultiTouchControls(true);
        osm.setBuiltInZoomControls(true);

        mc = (MapController) osm.getController();
        mc.setZoom(14);

        osm.getOverlays().clear();
    }

    public void spinnerMenu() {
        multiSelectionSpinner.setItems(select_qualification);
        SelectedChildren = multiSelectionSpinner.getSelectedStrings();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void selectedIndices(List<Integer> indices) {
    }

    @Override
    public void selectedStrings(List<String> strings) {
        Toast.makeText(this, strings.toString(), Toast.LENGTH_LONG).show();
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

    public void addMarker(List<GeoPoint> geoPoints, String childName) {
        Marker marker;
        GeoPoint points;
        for (int i = 0; i < geoPoints.size(); i++) {
            marker = new Marker(osm);
            points = geoPoints.get(geoPoints.size() - 1);
            marker.setTitle(childName);
            marker.setPosition(points);
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

            osm.getOverlays().add(marker);
        }
    }

    public void addPolyline(List<GeoPoint> geoPoints) {
        //add your points here
        Polyline polyline;

        for (int i = 0; i < geoPoints.size(); i++) {
            CurrColor = (CurrColor + 1) % COLOR_CHOICES.length;
            final int selectedColor = COLOR_CHOICES[CurrColor];

            polyline = new Polyline(MapOsmActivity.this);
            polyline.setPoints(geoPoints);
            polyline.setColor(selectedColor);
            polyline.setWidth(4);
            polyline.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, osm));
            polyline.setTitle("Polyline tapped!");
            osm.getOverlays().add(polyline);
        }
    }

    public void childFromDB() {
        handler = new Handler();
        thread = new Thread() {
            public void run() {
                try {
                    while (true) {
                        Looper.prepare();
                        sleep(1000);
                        getChildren();
                        for (int k = 0; k < childDto.length; k++) {
                            CoordinateDto[] coordinateDtos = getCoordinates(String.valueOf(childDto[k].id));
                            Person child = new Person();
                            child.id = (int)(childDto[k].id);
                            child.FirstName = childDto[k].FirstName;
                            child.LastName = childDto[k].LastName;
                            if (!select_qualification.contains(child.FirstName + " " + child.LastName) || select_qualification.isEmpty())
                                select_qualification.add(child.FirstName + " " + child.LastName);
                            child.personGeoPoints.clear();
                            for (int i = 0; i < coordinateDtos.length; i++) {
                                //if (child.personGeoPoints.size() != coordinateDtos.length || child.personGeoPoints.isEmpty()) {
                                    child.personGeoPoints.add(new GeoPoint(Double.parseDouble(coordinateDtos[i].latitude), Double.parseDouble(coordinateDtos[i].longitude)));
                                //}
                            }
                            if (!ListofChildren.contains(child.id))
                                ListofChildren.add(child);


                        }
                        spinnerMenu();
                        TrackingSelectedChildren();
                        handler.post(this);
                        Looper.loop();
                    }
                } catch (Exception e) {

                }
            }
        };
        thread.start();
    }

    public void TrackingSelectedChildren() {

        boolean temp = true;
        osm.getOverlays().clear();
        osm.invalidate();
        if (SelectedChildren.size() != 0) {
            for (int i = 0; i < SelectedChildren.size(); i++) {
                for (int j=0; j < ListofChildren.size();j++)
                {
                    if(SelectedChildren.get(i).equals(ListofChildren.get(j).FirstName+ " " + ListofChildren.get(j).LastName))
                    {
                        Person p = ListofChildren.get(j);
                        if (temp) {
                            mc.animateTo(p.personGeoPoints.get(p.personGeoPoints.size() - 1));
                            temp = false;
                        }
                        addPolyline(p.personGeoPoints);
                        addMarker(p.personGeoPoints, SelectedChildren.get(i));
                        break;
                    }
                }
            }
        } else {
            osm.getOverlays().clear();
            osm.invalidate();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void getChildren()
    {
        try  {
            String url = getString(R.string.DBUrl) + "parent/" + parentId;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            childDto = restTemplate.getForObject(url, ChildDto[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CoordinateDto[] getCoordinates(final String childId)
    {
        try  {
            String url = getString(R.string.DBUrl) + "coordinates/child/" + childId;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
            CoordinateDto []  coordinateDtos = restTemplate.getForObject(url, CoordinateDto[].class);
            return coordinateDtos;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}