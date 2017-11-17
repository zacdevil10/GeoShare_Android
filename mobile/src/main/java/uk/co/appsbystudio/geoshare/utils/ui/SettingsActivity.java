package uk.co.appsbystudio.geoshare.utils.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.appsbystudio.geoshare.R;

public class SettingsActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private File imageFile;
    private String userId;
    private StorageReference storageReference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");

            imageFile = new File(this.getCacheDir(), userId + ".png");

            try {
                FileOutputStream stream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 1, stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Uri uri = Uri.fromFile(imageFile);

            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setFixAspectRatio(true).start(this);

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setFixAspectRatio(true).start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            Uri uri = activityResult.getUri();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageFile = new File(this.getCacheDir(), userId + ".png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                }

                fileOutputStream.close();

                StorageReference profileRef = storageReference.child("profile_pictures/" + userId + ".png");
                profileRef.putFile(Uri.fromFile(imageFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
