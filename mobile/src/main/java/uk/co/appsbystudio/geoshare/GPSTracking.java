package uk.co.appsbystudio.geoshare;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSTracking implements LocationListener {

    private final Context context;

    private Location location;
    private LocationManager locationManager;
    private double latitude;
    private double longitude;

    private static final long DISTANCE_TO_CHANGE = 0;
    private static final long TIME_TO_UPDATE = 500;

    public GPSTracking(Context context) {
        this.context = context;
        setLocation();
    }

    private void setLocation() {
        try {
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled = false;
            boolean networkEnabled = false;
            if (locationManager != null) {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }

            if (networkEnabled) {
                setLocationManager(LocationManager.NETWORK_PROVIDER);
            }
            if (gpsEnabled) {
                if (location == null) {
                    setLocationManager(LocationManager.GPS_PROVIDER);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingPermission")
    private void setLocationManager(String provider) {
        locationManager.requestLocationUpdates(provider, TIME_TO_UPDATE, DISTANCE_TO_CHANGE, this);
        location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
    }

    public Location getLocation() {
        return location;
    }

    public double getLatitude() {
        if (location != null) latitude = location.getLatitude();
        return latitude;
    }

    public double getLongitude() {
        if (location != null) longitude = location.getLongitude();
        return longitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
