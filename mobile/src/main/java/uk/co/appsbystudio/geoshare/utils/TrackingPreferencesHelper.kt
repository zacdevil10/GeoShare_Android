package uk.co.appsbystudio.geoshare.utils

import android.content.SharedPreferences

class TrackingPreferencesHelper(private val preferences: SharedPreferences?) {

    fun setTrackingState(uid: String, state: Boolean) {
        preferences?.edit()?.putBoolean(uid, state)?.apply()
    }

    fun getTrackingState(uid: String): Boolean? {
        return preferences?.getBoolean(uid, false)
    }

    fun getAll(): MutableMap<String, *>? {
        return preferences?.all
    }

    fun exists(uid: String): Boolean? {
        return preferences?.contains(uid)
    }

    fun removeEntry(uid: String) {
        preferences?.edit()?.remove(uid)?.apply()
    }

    fun clear() {
        preferences?.edit()?.clear()?.apply()
    }
}