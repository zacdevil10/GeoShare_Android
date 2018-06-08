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
        Map<String, ?> shares = sharedPreferences.getAll();

        for (Map.Entry<String, ?> hasShared : shares.entrySet()) {
            Boolean value = (Boolean) hasShared.getValue();
            if (value) {
                Intent trackingService = new Intent(Application.getContext(), TrackingService.class);
                Application.getContext().startService(trackingService);
                break;
            }
        }
    }
}
