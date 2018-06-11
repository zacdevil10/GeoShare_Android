package uk.co.appsbystudio.geoshare.utils.services

import android.content.Context
import android.preference.PreferenceManager
import com.google.firebase.messaging.RemoteMessage
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewFriendNotification
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewShareLocationNotification

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (remoteMessage != null) {
            if (remoteMessage.data["tag"] == "request" && sharedPreferences.getBoolean("friend_notification", true)) {
                NewFriendNotification.notify(this, remoteMessage.data["title"], 1)
            } else if (remoteMessage.data["tag"] == "share" && sharedPreferences.getBoolean("location_notification", true)) {
                NewShareLocationNotification.notify(this, remoteMessage.data["title"], 1)
            } else if (remoteMessage.data["tag"] == "dfnf") {
                getSharedPreferences("tracking", Context.MODE_PRIVATE).edit().remove(remoteMessage.data["name"]).apply()
            } else if (remoteMessage.data["tag"] == "location_update") {
                print("Location update notification")
                //TODO: Show notification
            }
        }
    }
}
