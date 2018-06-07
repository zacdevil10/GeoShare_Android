package uk.co.appsbystudio.geoshare.utils.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;

import uk.co.appsbystudio.geoshare.BuildConfig;
import uk.co.appsbystudio.geoshare.R;

public class ProfilePictureOptions extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder optionsMenu = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        optionsMenu.setTitle("Change profile picture").setItems(R.array.profilePictureOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent selectPicture = new Intent();
                        selectPicture.setType("image/*");
                        selectPicture.setAction(Intent.ACTION_GET_CONTENT);
                        getActivity().startActivityForResult(Intent.createChooser(selectPicture, "Select Picture"), 2);
                        break;
                    case 1:
                        File imageFile = null;

                        imageFile = createImageFile();

                        if (imageFile != null) {
                            Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".fileProvider", imageFile);
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                            if (takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                                getActivity().startActivityForResult(takePicture, 1);
                            }
                        }
                        break;
                }
            }
        }).setNegativeButton("Cancel", null);
        return optionsMenu.create();
    }

    private File createImageFile() {
        String imageFileName = "profile_picture";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = new File(storageDir, imageFileName + ".png");

        String currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
