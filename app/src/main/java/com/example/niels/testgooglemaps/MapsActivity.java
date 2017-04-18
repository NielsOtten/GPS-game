package com.example.niels.testgooglemaps;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback {

    private GoogleMap mMap;
    private Player Player;
    private UiSettings UiSettings;
    private Projection mProjection;
    private LatLng leftUpperCorner;
    private LatLng rightBottomCorner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52.06109, 4.818502), 16.0f));

        this.Player = new Player(this, mMap);
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case PERMISSION_LOCATION_CODE:
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    pushToast("Permission granted");
//                } else {
//                    pushToast("Permission denied :(");
//                }
//                break;
//        }
//    }

//    public void pushToast(String text) {
//        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onMapLoaded() {
        this.mProjection = mMap.getProjection();
        VisibleRegion region = mProjection.getVisibleRegion();
        this.leftUpperCorner = region.farLeft;
        this.rightBottomCorner = region.nearRight;
    }
}
