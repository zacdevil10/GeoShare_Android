package uk.co.appsbystudio.geoshare.utils

import android.content.SharedPreferences

class SettingsPreferencesHelper(private val preferences: SharedPreferences?) {

    fun setDisplayName(name: String?) {
        preferences?.edit()?.putString("display_name", name)?.apply()
    }

    fun setNearbyRadius(radius: Int) {
        preferences?.edit()?.putString("nearby_radius", radius.toString())?.apply()
    }

    fun setFirstRun(state: Boolean) {
        preferences?.edit()?.putBoolean("first_run", state)?.apply()
    }

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

    fun mapTheme(): Boolean? {
        return preferences?.getBoolean("dark_map", false)
    }

    fun clear() {
        preferences?.edit()?.clear()?.apply()
    }
}