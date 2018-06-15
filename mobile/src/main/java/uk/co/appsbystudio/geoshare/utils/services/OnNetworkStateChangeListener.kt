package uk.co.appsbystudio.geoshare.utils.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import uk.co.appsbystudio.geoshare.utils.isConnected
import uk.co.appsbystudio.geoshare.utils.isConnectedMobile
import uk.co.appsbystudio.geoshare.utils.isConnectedWifi
import java.util.*

class OnNetworkStateChangeListener : BroadcastReceiver() {

    private val listeners: MutableSet<NetworkStateReceiverListener> = HashSet()
    private var connected: Boolean? = null
    private var isConnectedWifi: Boolean? = null
    private var isConnectedMobile: Boolean? = null

    interface NetworkStateReceiverListener {
        fun networkAvailable()
        fun networkUnavailable()
        fun networkWifi()
        fun networkMobile()
    }

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent == null || intent.extras == null) return

        if (isConnected(context)) {
            connected = true
            if (isConnectedWifi(context)) isConnectedWifi = true
            if (isConnectedMobile(context)) isConnectedMobile = true
        } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, java.lang.Boolean.FALSE)) {
            connected = false
            isConnectedWifi = false
            isConnectedMobile = false
        }

        notifyStateToAll()
    }

    private fun notifyStateToAll() {
        for (listener in listeners) {
            notifyState(listener)
        }
    }

    private fun notifyState(listener: NetworkStateReceiverListener?) {
        if (connected == null || listener == null || isConnectedMobile == null || isConnectedWifi == null) {
            return
        }

        if (connected!!) {
            listener.networkAvailable()
        } else {
            listener.networkUnavailable()
        }

        if (isConnectedMobile!!) {
            listener.networkMobile()
        }

        if (isConnectedWifi!!) {
            listener.networkWifi()
        }
    }

    fun addListener(listener: NetworkStateReceiverListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: NetworkStateReceiverListener) {
        listeners.remove(listener)
    }
}
