package uk.co.appsbystudio.geoshare.utils;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.services.TrackingService;

public class ProfileUtils {

    public static void setProfilePicture(final String userId, final CircleImageView view) {
        File fileCheck = new File(MainActivity.cacheDir + "/" + userId + ".png");

        //getLatestProfilePicture(userId);

        if (fileCheck.exists()) {
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

    private static void getLatestProfilePicture(String uid) {
        final File file = new File(MainActivity.cacheDir + "/" + uid + ".png");
        DatabaseReference picturesNotifyRef = FirebaseDatabase.getInstance().getReference("picture");
        picturesNotifyRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                onChildChanged(dataSnapshot, s);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (file.exists() && dataSnapshot.getValue(Long.class) != null && dataSnapshot.getValue(Long.class) > file.lastModified()) {
                    boolean fileDeleted = file.delete();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void resetDeviceSettings(SharedPreferences settingsSharedPreferences, SharedPreferences trackingPreferences, SharedPreferences showOnMapPreferences) {
        settingsSharedPreferences.edit().clear().apply();
        trackingPreferences.edit().clear().apply();
        showOnMapPreferences.edit().clear().apply();

        Intent trackingService = new Intent(Application.getContext(), TrackingService.class);
        Application.getContext().stopService(trackingService);
    }
}
