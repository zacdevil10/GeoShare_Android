package uk.co.appsbystudio.geoshare.settings;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import uk.co.appsbystudio.geoshare.GPSTracking;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.services.TrackingService;
import uk.co.appsbystudio.geoshare.utils.DatabaseLocations;

public class ShareOptions extends DialogFragment {

    private boolean[] defaultSet = new boolean[2];

    private PendingIntent pendingIntent;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        final String name = args.getString("name");
        final String friendId = args.getString("friendId");
        final String uid = args.getString("uid");

        defaultSet[0] = true;
        defaultSet[1] = false;

        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        //* Use this when tracking is implemented
        optionsMenu.setTitle("Share your location with " + name + "?").setMultiChoiceItems(R.array.shareLocationOptions, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                switch (which) {
                    case 0:
                        if (!isChecked) ((AlertDialog) dialog).getListView().setItemChecked(1, false);
                        break;
                    case 1:
                        if (isChecked) ((AlertDialog) dialog).getListView().setItemChecked(0, true);
                        break;
                }
            }
        }).setPositiveButton("Share", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (((AlertDialog) dialog).getListView().getCheckedItemPositions().get(0) && !((AlertDialog) dialog).getListView().getCheckedItemPositions().get(1)) {
                    GPSTracking gpsTracking = new GPSTracking(getActivity());
                    DatabaseLocations databaseLocations = new DatabaseLocations(gpsTracking.getLongitude(), gpsTracking.getLatitude(), System.currentTimeMillis());

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    if (uid != null && friendId != null) {
                        databaseReference.child("current_location").child(friendId).child(uid).setValue(databaseLocations);
                    }
                } else if (((AlertDialog) dialog).getListView().getCheckedItemPositions().get(0) && ((AlertDialog) dialog).getListView().getCheckedItemPositions().get(1)) {
                    //TODO: Call alarm timer to send location at regular time intervals
                    System.out.println("Multi tracking");
                    Intent intent = new Intent(getActivity(), TrackingService.class);
                    intent.putExtra("friendId", friendId);
                    intent.putExtra("uid", uid);

                    pendingIntent = PendingIntent.getService(getActivity(), 0, intent, 0);

                    startTracking();
                }
            }
        }).setNegativeButton("Cancel", null);
        //*/

        return optionsMenu.create();
    }

    private void startTracking() {
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        int interval = 8000;
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        System.out.println("Alarm started");
    }
}
