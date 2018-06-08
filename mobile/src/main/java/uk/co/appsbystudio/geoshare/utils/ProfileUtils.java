package uk.co.appsbystudio.geoshare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.R;

public class ProfileUtils {

    public static void setProfilePicture(final String userId, final CircleImageView view, final String storageDirectory) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid;
        if (user != null) uid = user.getUid();
        File fileCheck = new File(storageDirectory + "/" + userId + ".png");

        if (fileCheck.exists()) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(storageDirectory + "/" + userId + ".png");
            view.setImageBitmap(imageBitmap);
        } else {
            //If the file doesn't exist, download from Firebase
            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("profile_pictures/" + userId + ".png");
            profileRef.getFile(Uri.fromFile(new File(storageDirectory + "/" + userId + ".png")))
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap imageBitmap = BitmapFactory.decodeFile(storageDirectory + "/" + userId + ".png");
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
