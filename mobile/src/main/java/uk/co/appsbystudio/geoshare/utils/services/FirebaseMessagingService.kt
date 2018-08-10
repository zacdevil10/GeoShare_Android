package uk.co.appsbystudio.geoshare.utils.services

import android.content.Context
import android.preference.PreferenceManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.RemoteMessage
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewFriendNotification
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewRequestNotification
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewShareLocationNotification

class FirebaseMessagingService : com.google.firebase.messaging.FirebaseMessagingService() {

    override fun onNewToken(s: String?) {
        sendRegistrationToServer(s)
    }

    private fun sendRegistrationToServer(token: String?) {
        //Add to secure part of firebase database
        val databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseHelper.TOKEN)
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null && token != null) {
            databaseReference.child("${user.uid}/$token/platform").setValue("android")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

        if (remoteMessage != null) {
            when {
                remoteMessage.data["tag"] == "request" && sharedPreferences.getBoolean("friend_notification", true) -> //Friend request
                    NewFriendNotification.notify(this, remoteMessage.data["title"], 1)
                remoteMessage.data["tag"] == "share" && sharedPreferences.getBoolean("location_notification", true) -> //New shared location
                    NewShareLocationNotification.notify(this, remoteMessage.data["title"], 1)
                remoteMessage.data["tag"] == "dfnf" -> //Stop tracking
                    getSharedPreferences("tracking", Context.MODE_PRIVATE).edit().remove(remoteMessage.data["name"]).apply()
                remoteMessage.data["tag"] == "location_update" -> //Location request
                    NewRequestNotification.notify(this, remoteMessage.data["title"], 3, remoteMessage.data["uid"])
            }
        }

    }
}
