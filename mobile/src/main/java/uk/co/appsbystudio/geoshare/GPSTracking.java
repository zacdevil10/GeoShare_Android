package uk.co.appsbystudio.geoshare;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import uk.co.appsbystudio.geoshare.utils.DatabaseLocations;

public class GPSTracking implements LocationListener {
    private static final String TAG = "GPSTracking";
    private static final boolean LOCAL_LOGV = true;

    private final Context context;

    private Location location;
    private double latitude;
    private double longitude;

    //TODO: Use values from shared preferences
    private static final long DISTANCE_TO_CHANGE = 1;
    private static final long TIME_TO_UPDATE = 50;

    public GPSTracking(Context context) {
        this.context = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!networkEnabled && !gpsEnabled) {
                System.out.println("No network or gps");
            } else {
                if (networkEnabled) {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_TO_UPDATE, DISTANCE_TO_CHANGE, this);
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
                if (gpsEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_TO_UPDATE, DISTANCE_TO_CHANGE, this);
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

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

    /*
    public boolean getLocation() {
        return this.getLocation;
    }
    //*/

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        /*
        if (LOCAL_LOGV) Log.v(TAG, "Location has changed");
        SharedPreferences sharedPreferences = Application.getAppContext().getSharedPreferences("tracking", MODE_PRIVATE);
        Map<String, Boolean> shares = (Map<String, Boolean>) sharedPreferences.getAll();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        for (Map.Entry<String, Boolean> hasShared : shares.entrySet()) {
            if (hasShared.getValue()) {
                hasTrue = true;
                //DatabaseLocations databaseLocations = new DatabaseLocations(location.getLongitude(), location.getLatitude(), System.currentTimeMillis());
                //databaseReference.child("current_location").child(userId).child("location").setValue(databaseLocations);
                break;
            } else {
                hasTrue = false;
                databaseReference.child("current_location").child(userId).child("location").removeValue();
            }
        }

        if (hasTrue) {
            //DatabaseLocations databaseLocations = new DatabaseLocations(location.getLongitude(), location.getLatitude(), System.currentTimeMillis());
            //databaseReference.child("current_location").child(userId).child("location").setValue(databaseLocations);
            for (Map.Entry<String, Boolean> id : shares.entrySet()) {
                if (LOCAL_LOGV) Log.v(TAG, "Updating timestamp for " + id.getKey());
                if (LOCAL_LOGV) Log.v(TAG, "Id status: " + id.getValue());
                if (id.getValue()) databaseReference.child("current_location").child(id.getKey()).child("tracking").child(userId).child("timestamp").setValue(System.currentTimeMillis());
            }
        }
        */


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
