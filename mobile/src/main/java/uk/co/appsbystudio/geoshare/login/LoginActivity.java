package uk.co.appsbystudio.geoshare.login;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import uk.co.appsbystudio.geoshare.R;

public class LoginActivity extends AppCompatActivity {

    LoginFragment loginFragment;
    SignupFragment signupFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginFragment = new  LoginFragment();
        signupFragment = new SignupFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, loginFragment).commit();
    }

    public void buttonCallback(View button) {
        if(button.getId() == R.id.sign_up) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                    .replace(loginFragment.getId(), signupFragment).addToBackStack(null).commit();
        }
        if (button.getId() == R.id.sign_up_sign_up) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right)
                    .replace(signupFragment.getId(), loginFragment).commit();
        }
    }
}
