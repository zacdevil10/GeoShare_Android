package uk.co.appsbystudio.geoshare.utils.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.appsbystudio.geoshare.R;

public class ChangePasswordDialog extends DialogFragment {

    private FirebaseUser user;

    private EditText oldPasswordEntry, newPasswordEntry, confirmPasswordEntry;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);

        View view = View.inflate(getActivity(), R.layout.dialog_change_password, null);

        user = FirebaseAuth.getInstance().getCurrentUser();

        oldPasswordEntry = view.findViewById(R.id.old_password_input);
        newPasswordEntry = view.findViewById(R.id.new_password_input);
        confirmPasswordEntry = view.findViewById(R.id.confirm_new_password_input);

        optionsMenu.setTitle("Are you sure you want to delete this account?");

        optionsMenu.setView(view)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        validate();
                    }
                })
                .setNegativeButton("Cancel", null);

        final AlertDialog dialog = optionsMenu.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        return dialog;
    }

    private void validate() {
        final String oldPasswordText = oldPasswordEntry.getText().toString();
        final String newPasswordText = newPasswordEntry.getText().toString();
        final String confirmPasswordText = confirmPasswordEntry.getText().toString();

        if (TextUtils.isEmpty(oldPasswordText)) {
            oldPasswordEntry.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(newPasswordText)) {
            newPasswordEntry.setError(getString(R.string.error_field_required));
            return;
        }

        if (TextUtils.isEmpty(confirmPasswordText)) {
            confirmPasswordEntry.setError(getString(R.string.error_field_required));
            return;
        }

        if (!newPasswordText.equals(confirmPasswordText)) {
            confirmPasswordEntry.setError(getString(R.string.error_field_required));
            return;
        }

        if (user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPasswordText);

            user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Re-authenticated");
                    updatePassword(newPasswordText);
                }
            });
        }
    }

    private void updatePassword(String password) {
        user.updatePassword(password).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dismiss();
            }
        });
    }
}
