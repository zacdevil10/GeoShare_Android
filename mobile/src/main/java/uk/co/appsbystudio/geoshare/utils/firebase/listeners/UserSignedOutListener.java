package uk.co.appsbystudio.geoshare.utils.firebase.listeners;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserSignedOutListener implements com.google.firebase.auth.FirebaseAuth.AuthStateListener {

    private final Activity activity;

    public UserSignedOutListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            activity.finish();
        }
    }
}
