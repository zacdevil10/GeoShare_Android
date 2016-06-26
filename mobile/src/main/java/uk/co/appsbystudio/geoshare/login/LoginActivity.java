package uk.co.appsbystudio.geoshare.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.json.AutoLogin;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    private LoginFragment loginFragment;
    private SignupFragment signupFragment;
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.mainCoordinator);

        loginFragment = new LoginFragment();
        signupFragment = new SignupFragment();

        getSession();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, loginFragment).commit();
    }

    public void buttonCallback(View button) {
        //TODO: use switch
        if(button.getId() == R.id.sign_up) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right).replace(loginFragment.getId(), signupFragment).addToBackStack(null).commit();
        }
        if (button.getId() == R.id.sign_up_sign_up) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_right, R.anim.exit_left, R.anim.enter_left, R.anim.exit_right).replace(signupFragment.getId(), loginFragment).commit();
        }
    }

    private void getSession() {

        String pIDDatabase = new ReturnData().getpID(this);


        if (pIDDatabase != null && isConnected()) {

            if (pIDDatabase.length() != 0) {

                new AutoLogin(this, new ReturnData().getpID(this), new ReturnData().getUsername(this)).execute();

            } else {
                if (!isConnected()) {
                    System.out.println("No network");
                }
                if (pIDDatabase == null) {
                    System.out.println("Session gone");
                }
            }
        }
    }

    private boolean connection_status = false;

    private boolean isConnected() {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(0);
            if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                connection_status = true;
            } else {
                networkInfo = connectivityManager.getNetworkInfo(1);
                if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    connection_status = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return connection_status;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGoogleSigninResult(result);
        }
    }

    private void handleGoogleSigninResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            System.out.println("Success");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
