package uk.co.appsbystudio.geoshare.utils.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.File;
import java.net.URI;

import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.R;

public class ProfilePictureOptions extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        optionsMenu.setTitle("Upload profile picture").setItems(R.array.profilePictureOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent selectPicture = new Intent();
                    selectPicture.setType("image/*");
                    selectPicture.setAction(Intent.ACTION_GET_CONTENT);
                    getActivity().startActivityForResult(Intent.createChooser(selectPicture, "Select Picture"), 2);
                } if (which == 1) {
                    File imageFile = new File(Application.getAppContext().getCacheDir() + "/profile_picture.png");
                    Uri uri = Uri.fromFile(imageFile);
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                        getActivity().startActivityForResult(takePicture, 1);
                    }
                }
            }
        }).setNegativeButton("Cancel", null);
        return optionsMenu.create();
    }
}
