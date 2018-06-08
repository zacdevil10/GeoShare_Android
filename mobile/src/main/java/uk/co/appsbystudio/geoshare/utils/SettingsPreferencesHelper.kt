package uk.co.appsbystudio.geoshare.utils

import android.content.SharedPreferences

class SettingsPreferencesHelper(private val preferences: SharedPreferences?) {

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

    fun nearbyRadius(): Int? {
        return preferences?.getString("nearby_radius", "100")?.toInt()
    }

    fun clear() {
        preferences?.edit()?.clear()?.apply()
    }
}