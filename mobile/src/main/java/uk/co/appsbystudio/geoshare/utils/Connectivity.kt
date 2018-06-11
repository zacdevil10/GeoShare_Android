package uk.co.appsbystudio.geoshare.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

private fun getNetworkInfo(context: Context): NetworkInfo? {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    return connectivityManager.activeNetworkInfo
}

fun isConnected(context: Context): Boolean {
    val info = getNetworkInfo(context)
    return info != null && info.isConnected
}

fun isConnectedWifi(context: Context): Boolean {
    val info = getNetworkInfo(context)
    return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_WIFI
}

fun isConnectedMobile(context: Context): Boolean {
    val info = getNetworkInfo(context)
    return info != null && info.isConnected && info.type == ConnectivityManager.TYPE_MOBILE
}
