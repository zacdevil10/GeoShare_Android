package uk.co.appsbystudio.geoshare.utils.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import uk.co.appsbystudio.geoshare.R;

public class DeleteUser extends DialogFragment {

    private FirebaseUser user;

    private EditText passwordEntry;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        LayoutInflater inflater =  getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_delete_user, null);

        user = FirebaseAuth.getInstance().getCurrentUser();

        passwordEntry = view.findViewById(R.id.password_input);

        optionsMenu.setTitle("Are you sure you want to delete this account?");

        optionsMenu.setView(view)
            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //validate();
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
        String passwordText = passwordEntry.getText().toString();

        if (TextUtils.isEmpty(passwordText)) {
            passwordEntry.setError(getString(R.string.error_field_required));
            return;
        }

        if (user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), passwordText);

            user.reauthenticate(credential).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    System.out.println("Re-authenticated");
                    user.delete();
                    dismiss();
                }
            });
        }
    }
}
