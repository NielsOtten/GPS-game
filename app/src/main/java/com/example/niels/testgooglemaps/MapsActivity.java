package com.example.niels.testgooglemaps;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.VisibleRegion;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLoadedCallback,
        SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private GoogleMap mMap;
    private Player Player;
    private UiSettings UiSettings;
    private Projection mProjection;
    private LatLng leftUpperCorner;
    private LatLng rightBottomCorner;
    private float[] mGData = new float[3];
    private float[] mMData = new float[3];
    private float[] mR = new float[16];
    private float[] mI = new float[16];
    private float[] mOrientation = new float[3];
    private int mCount;
    private Vibrator vibrator;
    private ArrayList<Ally> allies;

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        this.mPrefs = getSharedPreferences("player", 0);
        this.mPrefsEditor = this.mPrefs.edit();

        final Button button = (Button) findViewById(R.id.shoot_button);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GameSocket.getInstance().emit("shoot", Player.getWeapon());
            }
        });

        this.allies = new ArrayList<Ally>();

        GameSocket.getInstance()
                .on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String playerId = mPrefs.getString("playerId", "");
                        GameSocket.getInstance().emit("loginPlayer", playerId);
                    }
                })
                .on("loggedIn", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        String playerId = args[0].toString();

                        mPrefsEditor.putString("playerId", playerId);
                        mPrefsEditor.apply();
                    }
                })
                .on("hit", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        vibrator.vibrate(500);
                    }
                })
                .on("changeLocation", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        JSONObject obj = (JSONObject)args[0];
                        try {
                            String lat = obj.getString("lat");
                            String lng = obj.getString("long");
                            LatLng location = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
                            String playerId = obj.getString("playerId");
                            String accuracy = obj.getString("accuracy");

                            boolean found = false;
                            for (Ally ally : allies) {
                                if (ally.getPlayerId().equals(playerId)) {
                                    found = true;
                                    ally.updatePosition(location);
                                }
                            }
                            if (!found) allies.add(new Ally(location, playerId, mMap));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Disable al UI and gestures
        this.UiSettings = mMap.getUiSettings();
        this.UiSettings.setAllGesturesEnabled(false);
        this.UiSettings.setCompassEnabled(false);
        this.UiSettings.setZoomControlsEnabled(false);

        mMap.setOnMapLoadedCallback(this);

        // Zoom camera to Wijnhaven
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.9174221, 4.4826467), 16.0f));

        // Locatie Ivoordreef
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.118937, 5.1084433), 16.0f));


        this.Player = new Player(this, mMap);
    }

    @Override
    public void onMapLoaded() {
        this.mProjection = mMap.getProjection();
        VisibleRegion region = mProjection.getVisibleRegion();
        this.leftUpperCorner = region.farLeft;
        this.rightBottomCorner = region.nearRight;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        float[] data;
        if (type == Sensor.TYPE_ACCELEROMETER) {
            data = mGData;
        } else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
            data = mMData;
        } else {
            // Another type that shouldn't be here.
            return;
        }
        for (int i=0 ; i<3 ; i++) {
            data[i] = event.values[i];
        }

        SensorManager.getRotationMatrix(mR, mI, mGData, mMData);
        SensorManager.getOrientation(mR, mOrientation);
        if (mCount++ > 50) {
            final float rad2deg = (float)(180.0f/Math.PI);
            mCount = 0;

            // Degrees from north.
            int yaw = (int)(mOrientation[0]*rad2deg);
            Player.setYaw(yaw);
            GameSocket.getInstance().emit("changeAngle", Player.getYaw());
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    protected void onResume() {
        super.onResume();
        Sensor gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor msensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, gsensor, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, msensor, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
