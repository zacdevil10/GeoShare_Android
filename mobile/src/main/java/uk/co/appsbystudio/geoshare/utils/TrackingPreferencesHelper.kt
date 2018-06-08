package uk.co.appsbystudio.geoshare.utils

import android.content.SharedPreferences

class TrackingPreferencesHelper(private val preferences: SharedPreferences?) {

    fun setTrackingState(uid: String, state: Boolean) {
        preferences?.edit()?.putBoolean(uid, state)?.apply()
    }

    fun getAll(): MutableMap<String, *>? {
        return preferences?.all
    }

    fun clear() {
        preferences?.edit()?.clear()?.apply()
    }
}