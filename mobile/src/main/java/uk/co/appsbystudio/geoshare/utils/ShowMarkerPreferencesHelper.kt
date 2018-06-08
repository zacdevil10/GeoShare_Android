package uk.co.appsbystudio.geoshare.utils

import android.content.SharedPreferences

class ShowMarkerPreferencesHelper(private val preferences: SharedPreferences?) {

    fun setMarkerVisibilityState(uid: String?, state: Boolean) {
        preferences?.edit()?.putBoolean(uid, state)?.apply()
    }

    fun setAllMarkersVisibilityState(state: Boolean) {
        preferences?.edit()?.putBoolean("all", state)?.apply()
    }

    fun getMarkerVisibilityState(uid: String?): Boolean? {
        return preferences?.getBoolean(uid, true)
    }

    fun getAllMarkersVisibilityState(): Boolean? {
        return preferences?.getBoolean("all", true)
    }

    fun clear() {
        preferences?.edit()?.clear()?.apply()
    }

}