package uk.co.appsbystudio.geoshare.login;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.json.AutoLogin;

public class LoginActivity extends AppCompatActivity {

    private LoginFragment loginFragment;
    private SignUpFragment signupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFragment = new LoginFragment();
        signupFragment = new SignUpFragment();

        new AutoLogin(this, new ReturnData().getpID(this), new ReturnData().getUsername(this)).execute();
    }

    public void buttonCallback(View button) {
        switch (button.getId()) {
            case R.id.sign_up:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right).replace(loginFragment.getId(), signupFragment).addToBackStack(null).commit();
                break;
            case R.id.sign_up_sign_up:
                getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right).replace(signupFragment.getId(), loginFragment).commit();
        }
    }

    public void doAnimation() {
        final ImageView logo = (ImageView) findViewById(R.id.startup_logo);
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.logo_slide);
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.fragment_container);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.gravity = Gravity.TOP;
                linearLayout.setLayoutParams(layoutParams);

                logo.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, loginFragment).commit();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        logo.startAnimation(anim);
        //startText.startAnimation(translate);
    }
}
