package uk.co.appsbystudio.geoshare.utils.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class StopTrackingService : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val service = Intent(context, TrackingService::class.java)
        context?.stopService(service)
    }
}
