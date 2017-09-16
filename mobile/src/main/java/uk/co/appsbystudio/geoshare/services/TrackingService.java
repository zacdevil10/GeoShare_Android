package uk.co.appsbystudio.geoshare.services;

import android.Manifest;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.AndroidException;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.GPSTracking;
import uk.co.appsbystudio.geoshare.utils.DatabaseLocations;

public class TrackingService extends IntentService {

    //TODO: Use values from shared preferences
    private static final long DISTANCE_TO_CHANGE = 5;
    private static final long TIME_TO_UPDATE = 1000 * 10;

    private boolean hasTrue;

    public TrackingService() {
        super("TrackingService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        System.out.println("Tracking service");
        LocationListener locationListener = new LocationListener();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);

        String bestProvider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, TIME_TO_UPDATE, DISTANCE_TO_CHANGE, locationListener);
    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            System.out.println("Location has changed in the service");
            SharedPreferences sharedPreferences = getSharedPreferences("tracking", MODE_PRIVATE);
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
                DatabaseLocations databaseLocations = new DatabaseLocations(location.getLongitude(), location.getLatitude(), System.currentTimeMillis());
                databaseReference.child("current_location").child(userId).child("location").setValue(databaseLocations);
                for (Map.Entry<String, Boolean> id : shares.entrySet()) {
                    if (id.getValue()) databaseReference.child("current_location").child(id.getKey()).child("tracking").child(userId).child("timestamp").setValue(System.currentTimeMillis());
                }
            }
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
}
