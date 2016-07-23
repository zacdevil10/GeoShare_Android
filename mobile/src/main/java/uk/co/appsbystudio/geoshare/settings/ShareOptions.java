package uk.co.appsbystudio.geoshare.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import uk.co.appsbystudio.geoshare.GPSTracking;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.json.SingleShareLocationTask;

public class ShareOptions extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();
        final String name = args.getString("name");

        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

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
                    //TODO: Send current location once
                    new SingleShareLocationTask(getActivity(), name, new GPSTracking(getActivity()).getLongitude(), new GPSTracking(getActivity()).getLatitude()).execute();
                    System.out.println("Single share");
                } else if (((AlertDialog) dialog).getListView().getCheckedItemPositions().get(0) && ((AlertDialog) dialog).getListView().getCheckedItemPositions().get(1)) {
                    //TODO: Call alarm timer to send location at regular time intervals
                    System.out.println("Multi share");
                }
            }
        }).setNegativeButton("Cancel", null);

        return optionsMenu.create();
    }
}
