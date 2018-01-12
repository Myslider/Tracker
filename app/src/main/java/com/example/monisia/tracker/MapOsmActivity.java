package com.example.monisia.tracker;

import android.Manifest;
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
import java.util.ArrayList;
import java.util.List;

public class MapOsmActivity extends AppCompatActivity implements SensorEventListener,SelectionSpinner.OnMultipleItemsSelectedListener {

    final private int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private static final String TAG = "PG";
    private MapView osm;
    private MapController mc;
    private List<GeoPoint> list = new ArrayList<>(), list1 = new ArrayList<>();
    private List<List<GeoPoint>> storeChildrenMovement = new ArrayList<>();
    private List<Person> ListofChildren = new ArrayList<>();
    private SensorManager SM;
    private Sensor mySensor;
    private static int CurrColor = 0;
    private SelectionSpinner multiSelectionSpinner;
    private CoordinateDto cord, cord1;
    private CoordinateDto[] coordinateDtos;
    private List<String> select_qualification = new ArrayList<>();

    public Handler handler;
    public Thread thread;
    public volatile List<String> SelectedChildren = new ArrayList<>();

    private static final int COLOR_CHOICES[] = {
            Color.MAGENTA,
            Color.RED,
            Color.YELLOW,
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
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

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);
        childTestlist();

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
        if (!listPermissionsNeeded.isEmpty()) {
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

    public void childTestlist() {
        cord = new CoordinateDto();
        cord.childFirstName = "Jacek";
        cord.childLastName = "Kuś";
        cord.id = "1";
        cord.latitude = "-20.1619";
        cord.longitude = "-40.2500";
        cord.time = "12";
        cord.childId = "1";
        cord.date = "12345";

        cord1 = new CoordinateDto();
        cord1.childFirstName = "Jacek";
        cord1.childLastName = "Kuś";
        cord1.id = "1";
        cord1.latitude = "-20.1589";
        cord1.longitude = "-40.24870";
        cord1.time = "13";
        cord1.childId = "1";
        cord1.date = "123545";

        GeoPoint g1 = new GeoPoint(-20.1618, -40.2490);
        GeoPoint g2 = new GeoPoint(-20.1690, -40.2495);
        list.add(g1);
        list.add(g2);
        list.add(new GeoPoint(-20.1555, -40.2450));

        GeoPoint center = new GeoPoint(-20.1698, -40.2487);
        list.add(center);

        list1.add(new GeoPoint(-20.0511, -40.2000));
        list1.add(new GeoPoint(-19.995, -40.1994));
        list1.add(new GeoPoint(-19.985, -40.1888));

        storeChildrenMovement.add(list);
        storeChildrenMovement.add(list1);
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
                coordinateDtos = new CoordinateDto[2];
                coordinateDtos[0] = cord;
                coordinateDtos[1] = cord1;
                try {
                    while (true) {
                        Person child = new Person();
                        Looper.prepare();
                        sleep(1000);
                        for (int i = 0; i < coordinateDtos.length; i++) {

                            child.id = Integer.parseInt(coordinateDtos[i].id);
                            child.FirstName = coordinateDtos[i].childFirstName;
                            child.LastName = coordinateDtos[i].childLastName;

                            if (child.personGeoPoints.size() != coordinateDtos.length || child.personGeoPoints.isEmpty()) {
                                child.personGeoPoints.add(new GeoPoint(Double.parseDouble(coordinateDtos[i].latitude), Double.parseDouble(coordinateDtos[i].longitude)));
                            }

                            if (!select_qualification.contains(coordinateDtos[i].childFirstName + " " + coordinateDtos[i].childLastName) || select_qualification.isEmpty())
                                select_qualification.add(coordinateDtos[i].childFirstName + " " + coordinateDtos[i].childLastName);
                        }
                        if (!ListofChildren.contains(child.id))
                            ListofChildren.add(child);

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

        if (SelectedChildren.size() != 0) {
            mc.animateTo(ListofChildren.get(0).personGeoPoints.get(ListofChildren.get(0).personGeoPoints.size() - 1));
            for (int i = 0; i < SelectedChildren.size(); i++) {
                String childName = SelectedChildren.get(i);
                Person p;
                p = ListofChildren.get(i);
                if (childName.equals(p.FirstName + " " + p.LastName)) {
                    addPolyline(p.personGeoPoints);
                    addMarker(p.personGeoPoints, SelectedChildren.get(i));
                } else {
                    osm.getOverlays().clear();
                    osm.invalidate();
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
}