package uk.co.appsbystudio.geoshare.utils

import android.content.SharedPreferences

class SettingsPreferencesResources(private val preferences: SharedPreferences?) {

    fun friendNotificationState(): Boolean? {
        return preferences?.getBoolean("friend_notification", true)
    }

    fun locationNotificationState(): Boolean? {
        return preferences?.getBoolean("location_notification", true)
    }

    fun syncFrequency(): Int? {
        return preferences?.getString("sync_frequency", "60")?.toInt()
    }

    fun mobileSyncState(): Boolean? {
        return preferences?.getBoolean("mobile_network", true)
    }

    fun updateFrequency(): Int? {
        return preferences?.getString("update_frequency", "5")?.toInt()
    }

    fun nearbyRadius(): Int? {
        return preferences?.getInt("nearby_radius", 100)
    }

}