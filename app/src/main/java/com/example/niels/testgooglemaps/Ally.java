package com.example.niels.testgooglemaps;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class Ally {
    private Marker marker;
    private String playerId;
    private GoogleMap mMap;
    private LatLng location;

    Ally(LatLng location, String playerId, GoogleMap Map) {
        this.playerId = playerId;
        this.mMap = Map;
        this.location = location;

        this.addMarker();
    }

    private void addMarker() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable(){

            @Override
            public void run() {
                marker = mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.red))
                        .title("Ally"));
            }
        });
    }

    public void updatePosition(final LatLng location) {
        this.location = new LatLng(location.latitude, location.longitude);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable(){

            @Override
            public void run() {
                marker.setPosition(location);
            }
        });
    }

    public Marker getMarker() {
        return this.marker;
    }

    public String getPlayerId() {
        return this.playerId;
    }
}
