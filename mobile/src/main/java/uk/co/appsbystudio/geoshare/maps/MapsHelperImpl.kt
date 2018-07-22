package uk.co.appsbystudio.geoshare.maps

import android.content.SharedPreferences
import uk.co.appsbystudio.geoshare.utils.services.OnNetworkStateChangeListener

class MapsHelperImpl(private val settingsPreferences: SharedPreferences?): MapsHelper, OnNetworkStateChangeListener.NetworkStateReceiverListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private var onNetworkStateChangeListener = OnNetworkStateChangeListener()
    private var networkListener: MapsHelper.OnNetworkStateChangedListener? = null
    private var settingsListener: MapsHelper.OnSharePreferencesChangedListener? = null

    override fun registerNetworkReceiver(listener: MapsHelper.OnNetworkStateChangedListener) {
        networkListener = listener
        onNetworkStateChangeListener.addListener(this)
        listener.registerNetworkReceiver(onNetworkStateChangeListener)
    }

    override fun unregisterNetworkReceiver(listener: MapsHelper.OnNetworkStateChangedListener) {
        onNetworkStateChangeListener.removeListener(this)
        listener.unregisterNetworkReceiver(onNetworkStateChangeListener)
    }

    override fun networkAvailable() {
        networkListener?.available(true)
    }

    override fun networkUnavailable() {
        networkListener?.available(false)
    }

    override fun networkWifi() {
        networkListener?.networkType(0)
    }

    override fun networkMobile() {
        networkListener?.networkType(1)
    }

    override fun registerSharedPreferencesListener(listener: MapsHelper.OnSharePreferencesChangedListener) {
        settingsListener = listener
        settingsPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun unregisterSharedPreferencesListener() {
        settingsPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            "nearby_radius" -> settingsListener?.updatedNearbyRadius()
            "dark_map" -> settingsListener?.updateTheme()
        }
    }
}