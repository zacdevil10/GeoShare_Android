package uk.co.appsbystudio.geoshare.utils.services;

import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Map;

import uk.co.appsbystudio.geoshare.Application;

import static android.content.Context.MODE_PRIVATE;

public class StartTrackingService extends Thread {

    @Override
    public void run() {
        SharedPreferences sharedPreferences = Application.getContext().getSharedPreferences("tracking", MODE_PRIVATE);
        Map<String, Boolean> shares = (Map<String, Boolean>) sharedPreferences.getAll();

        for (Map.Entry<String, Boolean> hasShared : shares.entrySet()) {
            if (hasShared.getValue()) {
                Intent trackingService = new Intent(Application.getContext(), TrackingService.class);
                Application.getContext().startService(trackingService);
                break;
            }
        }
    }
}
