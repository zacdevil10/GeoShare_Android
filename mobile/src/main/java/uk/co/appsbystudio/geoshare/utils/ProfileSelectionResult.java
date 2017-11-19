package uk.co.appsbystudio.geoshare.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class ProfileSelectionResult {

    public ProfileSelectionResult() {}

    public interface Callback {
        interface Main {
            void updateProfilePicture();
        }

        void profileUploadSuccess();
    }

    private Callback callback;
    private Callback.Main main;

    public ProfileSelectionResult(Callback.Main main) {
        this.main = main;
    }

    public ProfileSelectionResult(Callback callback) {
        this.callback = callback;
    }

    public void profilePictureResult(final Activity activity, int requestCode, int resultCode, Intent data, final String userId) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1:
                    String imageFileName = "profile_picture";
                    File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                    File image = new File(storageDir, imageFileName + ".png");

                    CropImage.activity(Uri.fromFile(image))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .setFixAspectRatio(true)
                            .start(activity);
                    break;
                case 2:
                    Uri uri = data.getData();
                    if (uri != null)
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1)
                                .setFixAspectRatio(true).start(activity);
                    break;
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();

                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference profileRef = storageReference.child("profile_pictures/" + userId + ".png");
                profileRef.putFile(resultUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Thread thread = new Thread() {
                                    @Override
                                    public void run() {
                                        try {
                                            if (callback != null) callback.profileUploadSuccess();
                                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), resultUri);
                                            File file = new File(activity.getCacheDir(), userId + ".png");
                                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                                            if (main != null) main.updateProfilePicture();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                };

                                thread.start();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "Hmm...Something went wrong.\nPlease check your internet connection and try again.", Toast.LENGTH_LONG).show();
                            }
                });
            }
        }
    }
}
