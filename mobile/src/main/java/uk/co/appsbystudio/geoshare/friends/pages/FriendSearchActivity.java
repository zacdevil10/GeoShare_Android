package uk.co.appsbystudio.geoshare.friends.pages;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendshipStatusAdapter;
import uk.co.appsbystudio.geoshare.utils.DeleteUnusedImagesFromCache;
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.UserSignedOutListener;

public class FriendSearchActivity extends AppCompatActivity implements FriendshipStatusAdapter.Callback {

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private DatabaseReference databaseReference;

    private String uid;

    private FriendshipStatusAdapter searchAdapter;

    private final ArrayList<String> userId = new ArrayList<>();

    private String oldestUserName;
    private String currentSearchRequest;

    private final ArrayList<String> imageCacheId = new ArrayList<>();

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
        searchResults.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (!recyclerView.canScrollVertically(1) && !currentSearchRequest.equals("")) {
                    Query query = databaseReference
                            .child("users")
                            .orderByChild("caseFoldedName")
                            .startAt(oldestUserName.toLowerCase()).endAt(currentSearchRequest.toLowerCase() + "~")
                            .limitToFirst(20);
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (!ds.getKey().equals(uid) && !userId.contains(ds.getKey())) {
                                    addUsersToView(ds);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });

        searchAdapter = new FriendshipStatusAdapter(this, databaseReference, userId, this);
        searchResults.setAdapter(searchAdapter);

        SearchView searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchInputListener());

        authStateListener = new UserSignedOutListener(this);
    }

    private void addUsersToView(DataSnapshot ds) {
        userId.add(ds.getKey());
        if (!imageCacheId.contains(ds.getKey())
                && !MainActivity.friendsId.containsKey(ds.getKey())
                && !MainActivity.pendingId.containsKey(ds.getKey())) imageCacheId.add(ds.getKey());
        searchAdapter.notifyDataSetChanged();

        oldestUserName = ds.child("name").getValue(String.class);
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
        databaseReference.child("pending").child(uid).child(friendId).child("outgoing").setValue(true)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        success();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        success();
                    }
                });
        databaseReference.child("pending").child(friendId).child(uid).child("outgoing").setValue(false);
    }

    public void success() {
        searchAdapter.notifyDataSetChanged();
    }

    private class SearchInputListener implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            currentSearchRequest = s;
            if (s.length() == 0) {
                clearList();
                searchAdapter.notifyDataSetChanged();
            } else {
                Query query = databaseReference
                        .child("users")
                        .orderByChild("caseFoldedName")
                        .startAt(s.toLowerCase()).endAt(s.toLowerCase() + "~")
                        .limitToFirst(20);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        clearList();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (!ds.getKey().equals(uid)) {
                                addUsersToView(ds);
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

    private void clearList() {
        searchAdapter.notifyItemRangeRemoved(0, userId.size());
        userId.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new DeleteUnusedImagesFromCache(imageCacheId).run();
    }
}
