package uk.co.appsbystudio.geoshare.utils.services;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.messaging.RemoteMessage;

import uk.co.appsbystudio.geoshare.utils.geocoder.GeocodingFromLatLngTask;
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewFriendNotification;
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewShareLocationNotification;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (remoteMessage.getData().get("tag").equals("request") && sharedPreferences.getBoolean("friend_notification", true)) {
            NewFriendNotification.notify(this, remoteMessage.getData().get("title"), 1);
        } else if (remoteMessage.getData().get("tag").equals("share") && sharedPreferences.getBoolean("location_notification", true)) {
            NewShareLocationNotification.notify(this, remoteMessage.getData().get("title"), 1);
        }
    }
}
