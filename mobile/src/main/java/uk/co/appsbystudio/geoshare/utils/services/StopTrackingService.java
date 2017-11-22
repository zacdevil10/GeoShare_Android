package uk.co.appsbystudio.geoshare.utils.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StopTrackingService extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, TrackingService.class);
        context.stopService(service);
    }
}
