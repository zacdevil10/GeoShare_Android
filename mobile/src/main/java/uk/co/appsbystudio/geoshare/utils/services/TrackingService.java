package uk.co.appsbystudio.geoshare.utils.services;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

import uk.co.appsbystudio.geoshare.base.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations;
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper;
import uk.co.appsbystudio.geoshare.utils.ui.notifications.TrackingServiceNotification;

public class TrackingService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final long DISTANCE_TO_CHANGE = 0;
    private static long TIME_TO_UPDATE;

    private LocationListener locationListener;
    private LocationManager locationManager;
    private String bestProvider;

    private SharedPreferences sharedPreferences;

    private FirebaseUser user;

    private boolean hasTrue = false;

    private Intent receiver;
    private PendingIntent stopServiceIntent;

    public static boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        user = FirebaseAuth.getInstance().getCurrentUser();

        isRunning = false;

        TrackingServiceNotification.notify(this, 1);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        receiver = new Intent(this, StopTrackingService.class);
        stopServiceIntent = PendingIntent.getBroadcast(this, 1, receiver, PendingIntent.FLAG_CANCEL_CURRENT);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO: Check for permissions before starting service
            return;
        }

        setupLocationListener();
    }

    @SuppressLint("MissingPermission")
    private void setupLocationListener() {
        locationListener = new LocationListener();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        TIME_TO_UPDATE = Integer.parseInt(sharedPreferences.getString("sync_frequency", "60")) * 1000;

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);

        bestProvider = locationManager.getBestProvider(criteria, false);

        locationManager.requestLocationUpdates(bestProvider, TIME_TO_UPDATE, DISTANCE_TO_CHANGE, locationListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        TrackingServiceNotification.cancel(this);

        isRunning = false;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("sync_frequency")) {
            TIME_TO_UPDATE = Integer.parseInt(sharedPreferences.getString("sync_frequency", "DEFAULT")) * 1000;

            if (locationManager != null) {
                locationManager.removeUpdates(locationListener);
                locationManager.requestLocationUpdates(bestProvider, TIME_TO_UPDATE, DISTANCE_TO_CHANGE, locationListener);
            }
        }
    }

    private void stopService() {
        this.stopSelf();
    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            SharedPreferences sharedPreferences = getSharedPreferences("tracking", MODE_PRIVATE);
            Map<String, Boolean> shares = (Map<String, Boolean>) sharedPreferences.getAll();

            //Notifications
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "tracking_channel")
                    .setSmallIcon(R.drawable.icon_white)
                    .setContentTitle("Tracking service is running")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setTicker("Tracking service is running")
                    .setNumber(1)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setLocalOnly(true)
                    .addAction(R.drawable.ic_close_white_48px,"Stop tracking", stopServiceIntent)
                    .setPriority(Notification.PRIORITY_LOW);
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            String userId = null;
            if (user != null) {
                userId = user.getUid();
            }

            if (!shares.entrySet().isEmpty()) {
                for (Map.Entry<String, Boolean> hasShared : shares.entrySet()) {
                    if (hasShared.getValue()) {
                        hasTrue = true;
                        break;
                    } else {
                        hasTrue = false;
                        if (userId != null) {
                            databaseReference.child(FirebaseHelper.TRACKING).child(userId).child("location").removeValue();
                        }
                    }
                }

                if (hasTrue) {
                    DatabaseLocations databaseLocations = new DatabaseLocations(location.getLongitude(), location.getLatitude(), System.currentTimeMillis());
                    if (userId != null) {
                        databaseReference.child(FirebaseHelper.TRACKING).child(userId).child("location").setValue(databaseLocations);
                        for (Map.Entry<String, Boolean> id : shares.entrySet()) {
                            if (id.getValue()) {
                                if (MainActivity.Companion.getFriendNames().containsKey(id.getKey())) inboxStyle.addLine(MainActivity.Companion.getFriendNames().get(id.getKey()));
                                databaseReference.child(FirebaseHelper.TRACKING).child(id.getKey()).child("tracking").child(userId).child("timestamp").setValue(System.currentTimeMillis());
                            }
                        }
                    }
                } else {
                    try {
                        stopServiceIntent.send(TrackingService.this, 1, receiver);
                    } catch (PendingIntent.CanceledException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    stopServiceIntent.send(TrackingService.this, 1, receiver);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }

            builder.setStyle(inboxStyle);

            TrackingServiceNotification.notify(getApplicationContext(), builder.build());
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