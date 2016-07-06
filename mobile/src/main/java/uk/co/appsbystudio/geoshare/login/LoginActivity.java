package uk.co.appsbystudio.geoshare.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

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

        String pIDDatabase = new ReturnData().getpID(this);

        ProgressDialog progressDialog = new ProgressDialog(this, R.style.DialogTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");



        if (pIDDatabase != null) {

            progressDialog.show();

            if (pIDDatabase.length() != 0) {

                new AutoLogin(this, new ReturnData().getpID(this), new ReturnData().getUsername(this), progressDialog).execute();
            }
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, loginFragment).commit();
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
}
