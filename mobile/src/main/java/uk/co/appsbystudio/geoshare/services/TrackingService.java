package uk.co.appsbystudio.geoshare.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.appsbystudio.geoshare.GPSTracking;
import uk.co.appsbystudio.geoshare.utils.DatabaseLocations;

public class TrackingService extends BroadcastReceiver {

    String friendId;
    String uid;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Broadcasting");

        Bundle extras = intent.getExtras();
        friendId = extras.getString("friendId");
        uid = extras.getString("uid");

        GPSTracking gpsTracking = new GPSTracking(context);
        DatabaseLocations databaseLocations = new DatabaseLocations(gpsTracking.getLongitude(), gpsTracking.getLatitude(), System.currentTimeMillis());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (uid != null && friendId != null) {
            databaseReference.child("current_location").child(friendId).child(uid).setValue(databaseLocations);
        }
    }
}
