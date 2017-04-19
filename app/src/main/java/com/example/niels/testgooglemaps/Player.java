package com.example.niels.testgooglemaps;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.example.niels.testgooglemaps.Weapons.IWeapon;
import com.example.niels.testgooglemaps.Weapons.Sniper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.LOCATION_SERVICE;

public class Player {
    private GoogleMap mMap;
    private Activity mainActivity;
    private LocationManager mLocationManager;
    private Marker marker;

    private IWeapon Weapon;

    public Player(Activity mainActivity, GoogleMap mMap) {
        // Construct singleton.
        GameSocket.getInstance();

        // Check for Location permissions.
        // Built check in when permission is not granted.
        Util.getPermission(mainActivity);

        this.mMap = mMap;
        this.mainActivity = mainActivity;

        setupLocationManager();

        this.Weapon = new Sniper();
    }

    /*
     * http://stackoverflow.com/questions/17591147/how-to-get-current-location-in-android
     */
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            changeLocation(location);

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void setupLocationManager() {
        mLocationManager = (LocationManager) this.mainActivity.getSystemService(LOCATION_SERVICE);

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                2, mLocationListener);
    }

    private void changeLocation(Location location) {
        if (this.marker == null) {
            this.marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("User"));
        }

        this.marker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16.0f));

        GameSocket.getInstance().emit("changeLocation", location.getLatitude(), location.getLongitude(), location.getAccuracy());
    }

    public String getWeapon() {
        return Weapon.getType();
    }
}
