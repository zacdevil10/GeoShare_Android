package uk.co.appsbystudio.geoshare.maps

import android.content.BroadcastReceiver

interface MapsHelper {

    interface OnNetworkStateChangedListener {
        fun registerNetworkReceiver(broadcastReceiver: BroadcastReceiver)

        fun available(state: Boolean)

        fun networkType(type: Int)

        fun unregisterNetworkReceiver(broadcastReceiver: BroadcastReceiver)
    }

    interface OnSharePreferencesChangedListener {
        fun updatedNearbyRadius()
        fun updateTheme()
    }

    //Network broadcast listener
    fun registerNetworkReceiver(listener: OnNetworkStateChangedListener)

    fun unregisterNetworkReceiver(listener: OnNetworkStateChangedListener)

    //Settings shared preferences listener
    fun registerSharedPreferencesListener(listener: OnSharePreferencesChangedListener)

    fun unregisterSharedPreferencesListener()

}