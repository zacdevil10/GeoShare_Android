package uk.co.appsbystudio.geoshare.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;

import uk.co.appsbystudio.geoshare.R;

public class ConfirmationDialog extends DialogFragment {

    @Override @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        builder.setMessage("User account created.\nWe've sent you a verification email.")
                .setPositiveButton("Ok", null);
        return builder.create();
    }
}
