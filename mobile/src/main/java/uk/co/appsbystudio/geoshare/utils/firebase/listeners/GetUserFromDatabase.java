package uk.co.appsbystudio.geoshare.utils.firebase.listeners;

import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation;

public class GetUserFromDatabase implements ValueEventListener {

    private final String uid;
    private final TextView view;

    public GetUserFromDatabase(String uid, TextView view) {
        this.uid = uid;
        this.view = view;
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        UserInformation userInformation = dataSnapshot.child("users").child(uid).getValue(UserInformation.class);
        assert userInformation != null;
        view.setText(userInformation.getName());
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
