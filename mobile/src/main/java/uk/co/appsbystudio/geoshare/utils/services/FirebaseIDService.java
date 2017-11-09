package uk.co.appsbystudio.geoshare.utils.services;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper;

public class FirebaseIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        System.out.println("Firebase id service");
        //Add to secure part of firebase database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseHelper.TOKEN);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null) {
            databaseReference.child(firebaseAuth.getCurrentUser().getUid()).child(token).child("platform").setValue("android");
        }
    }
}
