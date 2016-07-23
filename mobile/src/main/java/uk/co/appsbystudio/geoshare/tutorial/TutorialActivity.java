package uk.co.appsbystudio.geoshare.tutorial;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.json.DeleteRequestTask;

public class TutorialActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.newInstance("Welcome to GeoShare!", "Swipe to set up the your app experience!", R.mipmap.login_screen_background, Color.parseColor("#3F51B5")));
        addSlide(AppIntroFragment.newInstance("Share", "Share your locations with friends", R.mipmap.login_screen_background, Color.parseColor("#3F51B5")));
        addSlide(AppIntroFragment.newInstance("Add profile picture", "Add a profile picture so that people can recognise you.", R.mipmap.login_screen_background, Color.parseColor("#3F51B5")));
        addSlide(AppIntroFragment.newInstance("You're all set!", "Have fun!", R.mipmap.ic_launcher, Color.parseColor("#3F51B5")));

        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        askForPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);

        showSkipButton(false);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        rememberLogout();
        super.onBackPressed();
    }

    private void rememberLogout() {
        if (new ReturnData().getRemember(this) != 1) {
            new DeleteRequestTask().onDeleteRequest("https://geoshare.appsbystudio.co.uk/api/user/" + new ReturnData().getUsername(this) + "/session/" + new ReturnData().getpID(this), new ReturnData().getpID(this), this);
            new ReturnData().clearSession(this);
        }
    }
}
