package uk.co.appsbystudio.geoshare.tutorial;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.ramotion.paperonboarding.PaperOnboardingFragment;
import com.ramotion.paperonboarding.PaperOnboardingPage;
import com.ramotion.paperonboarding.listeners.PaperOnboardingOnRightOutListener;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.login.LoginActivity;

public class TutorialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        FragmentManager fragmentManager = getFragmentManager();

        PaperOnboardingPage scr1 = new PaperOnboardingPage("Find people",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit,",
                Color.parseColor("#4CAF50"), R.drawable.ic_people_black_48px, R.drawable.ic_add_white_48px);
        PaperOnboardingPage scr2 = new PaperOnboardingPage("Plan",
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit,",
                Color.parseColor("#2196F3"), R.drawable.ic_map_black_48px, R.drawable.ic_map_black_48px);
        PaperOnboardingPage scr3 = new PaperOnboardingPage("Something else",
                "You can also do other stuff with the app (not yet)\nSwipe right to continue to the app ->",
                Color.parseColor("#9E9E9E"), R.drawable.ic_lock_black_48px, R.drawable.ic_lock_black_48px);

        ArrayList<PaperOnboardingPage> elements = new ArrayList<>();
        elements.add(scr1);
        elements.add(scr2);
        elements.add(scr3);

        PaperOnboardingFragment onBoardingFragment = PaperOnboardingFragment.newInstance(elements);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, onBoardingFragment);
        fragmentTransaction.commit();

        onBoardingFragment.setOnRightOutListener(new PaperOnboardingOnRightOutListener() {
            @Override
            public void onRightOut() {
                login();
            }
        });
    }

    private void login() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}
