package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsSearchAdapter;
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.UserSignedOutListener;

public class FriendSearchActivity extends AppCompatActivity implements FriendsSearchAdapter.Callback {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private DatabaseReference databaseReference;

    private String uid;

    private FriendsSearchAdapter searchAdapter;

    private final ArrayList<String> names = new ArrayList<>();
    private final ArrayList<String> userId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_search);

        auth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        if (auth.getCurrentUser() != null) uid = auth.getCurrentUser().getUid();

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        RecyclerView searchResults = findViewById(R.id.searchResults);
        searchResults.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchResults.setLayoutManager(layoutManager);

        searchAdapter = new FriendsSearchAdapter(this, names, userId, this);
        searchResults.setAdapter(searchAdapter);

        SearchView searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchInputListener());

        authStateListener = new UserSignedOutListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void onSendRequest(String friendId) {
        databaseReference.child("pending").child(uid).child(friendId).child("outgoing")
                .setValue(true);
        databaseReference.child("pending").child(friendId).child(uid).child("outgoing")
                .setValue(false);
    }

    private class SearchInputListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            if (s.length() == 0) {
                searchAdapter.notifyItemRangeRemoved(0, names.size());
                names.clear();
                userId.clear();
                searchAdapter.notifyDataSetChanged();
            } else {
                Query query = databaseReference
                        .child("users")
                        .orderByChild("caseFoldedName")
                        .startAt(s.toLowerCase()).endAt(s.toLowerCase() + "~");
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        searchAdapter.notifyItemRangeRemoved(0, names.size());
                        names.clear();
                        userId.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (!ds.getKey().equals(uid)) {
                                names.add(ds.child("name").getValue(String.class));
                                userId.add(ds.getKey());
                                searchAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            return false;
        }
    }
}
