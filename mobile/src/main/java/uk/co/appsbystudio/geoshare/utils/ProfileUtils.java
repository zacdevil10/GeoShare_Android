package uk.co.appsbystudio.geoshare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;

public class ProfileUtils {

    public static void setProfilePicture(final String userId, final CircleImageView view) {
        File fileCheck = new File(MainActivity.cacheDir + "/" + userId + ".png");

        if (fileCheck.exists()) {
            //If file exists, set image view image as profile picture from storage
            //TODO: Allow for updating picture on different devices
            /* Could mean that this method will not work without getting the picture every time
                or adding a last updated section to the users profile picture
                and comparing with the date of the file created.
             */
            Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + userId + ".png");
            view.setImageBitmap(imageBitmap);
        } else {
            //If the file doesn't exist, download from Firebase
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("profile_pictures/" + userId + ".png");
            profileRef.getFile(Uri.fromFile(new File(MainActivity.cacheDir + "/" + userId + ".png")))
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + userId + ".png");
                            view.setImageBitmap(imageBitmap);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            view.setImageDrawable(Application.getContext().getResources().getDrawable(R.drawable.ic_profile_picture));
                        }
                    });
        }
    }
}
