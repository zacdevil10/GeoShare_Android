package uk.co.appsbystudio.geoshare.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.json.ImageUpload;

public class ProfilePictureOptions extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity());
        optionsMenu.setTitle("Upload profile picture").setItems(R.array.profilePictureOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent selectPicture = new Intent();
                    selectPicture.setType("image/*");
                    selectPicture.setAction(Intent.ACTION_GET_CONTENT);
                    getActivity().startActivityForResult(Intent.createChooser(selectPicture, "Select Picture"), 2);
                } if (which == 1) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                        getActivity().startActivityForResult(takePicture, 1);
                    }
                }
            }
        });

        return optionsMenu.create();
    }
}
