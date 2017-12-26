package uk.co.appsbystudio.geoshare.friends.pages;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsAdapter;

import static android.content.Context.MODE_PRIVATE;

public class FriendsFragment extends Fragment implements FriendsAdapter.Callback {

    private DatabaseReference databaseFriendsRef;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private SharedPreferences trackingPreferences;
    private SharedPreferences showOnMapPreferences;

    private FriendsAdapter friendsAdapter;

    private TextView noFriendsText;

    private final ArrayList<String> userId = new ArrayList<>();

    public FriendsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        trackingPreferences = Application.getContext().getSharedPreferences("tracking", MODE_PRIVATE);
        showOnMapPreferences = Application.getContext().getSharedPreferences("showOnMap", MODE_PRIVATE);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        if (auth.getCurrentUser() != null) {
            databaseFriendsRef = database.getReference("friends/" + auth.getCurrentUser().getUid());
            databaseFriendsRef.keepSynced(true);
        }

        RecyclerView friendsList = view.findViewById(R.id.friend_list);
        friendsList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        friendsList.setLayoutManager(layoutManager);

        noFriendsText = view.findViewById(R.id.friends_no_friends);

        getFriends();

        friendsAdapter = new FriendsAdapter(getContext(), userId, databaseReference, this);
        friendsList.setAdapter(friendsAdapter);

        return view;
    }

    private void getFriends() {
        databaseFriendsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                userId.add(dataSnapshot.getKey());
                friendsAdapter.notifyDataSetChanged();

                if (userId.size() > 0) {
                    noFriendsText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                userId.remove(dataSnapshot.getKey());
                friendsAdapter.notifyDataSetChanged();

                if (userId.size() == 0) {
                    noFriendsText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRemoveFriend(String friendId) {
        if (auth.getCurrentUser() != null) {
            databaseReference.child("friends").child(auth.getCurrentUser().getUid()).child(friendId).removeValue()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Could not remove friend", Toast.LENGTH_SHORT).show();
                    }
                });
            if (trackingPreferences.contains(friendId)) trackingPreferences.edit().remove(friendId).apply();
            if (trackingPreferences.contains(friendId)) showOnMapPreferences.edit().remove(friendId).apply();
        }
    }
}