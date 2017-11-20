package uk.co.appsbystudio.geoshare.utils.setup;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.ProfileSelectionResult;
import uk.co.appsbystudio.geoshare.utils.dialog.ProfilePictureOptions;
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper;
import uk.co.appsbystudio.geoshare.utils.setup.fragments.GetStartedFragment;
import uk.co.appsbystudio.geoshare.utils.setup.fragments.PermissionsFragment;
import uk.co.appsbystudio.geoshare.utils.setup.fragments.RadiusSetupFragment;
import uk.co.appsbystudio.geoshare.utils.setup.fragments.SetupProfileFragment;
import uk.co.appsbystudio.geoshare.utils.ui.NoSwipeViewPager;

public class InitialSetupActivity extends AppCompatActivity implements RadiusSetupFragment.SetupFinishInterface, ProfileSelectionResult.Callback {

    private String userId;

    private static final int GET_PERMS = 1;

    private NoSwipeViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_setup);

        PreferenceManager.setDefaultValues(this, R.xml.pref_main, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }

        //FCM Token
        String token = FirebaseInstanceId.getInstance().getToken();

        DatabaseReference tokenRef = FirebaseDatabase.getInstance().getReference(FirebaseHelper.TOKEN);

        if (auth.getCurrentUser() != null && token != null) {
            tokenRef.child(userId).child(token).child("platform").setValue("android");
        }

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

        viewPager.setPagingEnabled();

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
            case R.id.backButton:
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
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
        new ProfileSelectionResult(this).profilePictureResult(this, requestCode, resultCode, data, userId);
    }

    @Override
    public void onFinish() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void profileUploadSuccess() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
    }
}
