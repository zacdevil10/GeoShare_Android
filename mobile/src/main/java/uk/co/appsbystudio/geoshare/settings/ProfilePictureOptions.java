package uk.co.appsbystudio.geoshare.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import uk.co.appsbystudio.geoshare.R;

public class ProfilePictureOptions extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity());
        optionsMenu.setTitle("").setItems(R.array.profilePictureOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {

                } if (which == 1) {

                } if (which == 2) {

                }
            }
        });

        return optionsMenu.create();
    }
}
