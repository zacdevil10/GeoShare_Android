package uk.co.appsbystudio.geoshare.utils.setup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.settings.ProfilePictureOptions;
import uk.co.appsbystudio.geoshare.utils.setup.fragments.GetStartedFragment;
import uk.co.appsbystudio.geoshare.utils.setup.fragments.PermissionsFragment;
import uk.co.appsbystudio.geoshare.utils.setup.fragments.RadiusSetupFragment;
import uk.co.appsbystudio.geoshare.utils.setup.fragments.SetupProfileFragment;
import uk.co.appsbystudio.geoshare.utils.ui.NoSwipeViewPager;

public class InitialSetupActivity extends AppCompatActivity {

    private Bitmap bitmap;
    private File imageFile;
    private String userId;
    private StorageReference storageReference;

    private static final int GET_PERMS = 1;

    private NoSwipeViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        PreferenceManager.setDefaultValues(this, R.xml.pref_main, false);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        viewPager = findViewById(R.id.view_pager);
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new GetStartedFragment();
                    case 1:
                        return new PermissionsFragment();
                    case 2:
                        return new SetupProfileFragment();
                    case 3:
                        return new RadiusSetupFragment();
                    default:
                        return null;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };

        viewPager.setPagingEnabled(false);

        viewPager.setAdapter(fragmentPagerAdapter);
    }

    public void onButtonPressed(View id) {
        switch (id.getId()) {
            case R.id.notNowButton:
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                break;
            case R.id.getStartedButton:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    viewPager.setCurrentItem(1);
                } else {
                    viewPager.setCurrentItem(2);
                }
                break;
            case R.id.getPermsButton:
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) + ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, GET_PERMS);
                    }
                }
                break;
            case R.id.setPictureButton:
                profilePictureSettings();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GET_PERMS:
                if (grantResults.length > 0 && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    viewPager.setCurrentItem(2);
                }
        }
    }

    private void profilePictureSettings() {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.DialogFragment profileDialog = new ProfilePictureOptions();
        profileDialog.show(fragmentManager, "");
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
                profileRef.putFile(Uri.fromFile(imageFile))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                            }
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
