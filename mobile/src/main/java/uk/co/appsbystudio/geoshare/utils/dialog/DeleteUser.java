package uk.co.appsbystudio.geoshare.utils.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import uk.co.appsbystudio.geoshare.R;

public class DeleteUser extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        optionsMenu.setTitle("Are you sure you want to delete this account?").setPositiveButton("YES", null).setNegativeButton("NO", null);
        return optionsMenu.create();
    }
}
