package uk.co.appsbystudio.geoshare.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.authentication.AuthActivity;
import uk.co.appsbystudio.geoshare.friends.FriendsManager;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsNavAdapter;
import uk.co.appsbystudio.geoshare.maps.MapsFragment;
import uk.co.appsbystudio.geoshare.utils.Connectivity;
import uk.co.appsbystudio.geoshare.utils.ProfileSelectionResult;
import uk.co.appsbystudio.geoshare.utils.ProfileUtils;
import uk.co.appsbystudio.geoshare.utils.dialog.ProfilePictureOptions;
import uk.co.appsbystudio.geoshare.utils.dialog.ShareOptions;
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper;
import uk.co.appsbystudio.geoshare.utils.firebase.TrackingInfo;
import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation;
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.UpdatedProfilePicturesListener;
import uk.co.appsbystudio.geoshare.utils.services.StartTrackingService;
import uk.co.appsbystudio.geoshare.utils.services.TrackingService;
import uk.co.appsbystudio.geoshare.utils.ui.SettingsActivity;

public class MainActivity extends AppCompatActivity implements MainView, SharedPreferences.OnSharedPreferenceChangeListener,
        FriendsNavAdapter.Callback, ProfileSelectionResult.Callback.Main {

    private MainPresenter mainPresenter;

    public static File cacheDir;

    //FIREBASE
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference databaseReference;

    private String userId;

    private DrawerLayout drawerLayout;
    private View header;

    private final MapsFragment mapsFragment = new MapsFragment();

    private DrawerLayout rightDrawer;
    private FriendsNavAdapter friendsNavAdapter;

    private final ArrayList<String> uidList = new ArrayList<>();
    private final HashMap<String, Boolean> hasTracking = new HashMap<>();

    private SharedPreferences settingsSharedPreferences;
    private SharedPreferences trackingPreferences;
    private SharedPreferences showOnMapPreferences;

    public static final HashMap<String, Boolean> friendsId = new HashMap<>();
    public static final HashMap<String, Boolean> pendingId = new HashMap<>();
    public static final HashMap<String, String> friendNames = new HashMap<>();
    private CircleImageView profileImageView;

    private boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenterImpl(this, new MainInteractorImpl());

        cacheDir = this.getCacheDir();

        //SharedPreferences
        settingsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        settingsSharedPreferences.registerOnSharedPreferenceChangeListener(this);
        trackingPreferences = getSharedPreferences("tracking", MODE_PRIVATE);
        showOnMapPreferences = getSharedPreferences("showOnMap", MODE_PRIVATE);

        setTracking();

        //Firebase initialisation
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userId = firebaseUser != null ? firebaseUser.getUid() : null;

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        /* HANDLES FOR VARIOUS VIEWS */
        drawerLayout = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.left_nav_view);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame_map, mapsFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().show(mapsFragment).commit();
        }
        setupDrawerContent(navigationView);

        rightDrawer = findViewById(R.id.right_nav_drawer);

        RecyclerView rightNavigationView = findViewById(R.id.right_friends_drawer);
        if (rightNavigationView != null) rightNavigationView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        if (rightNavigationView != null) rightNavigationView.setLayoutManager(layoutManager);

        rightDrawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        //Get friends and populate right nav drawer
        mainPresenter.getFriends();
        mainPresenter.getFriendsTrackingState();

        friendsNavAdapter = new FriendsNavAdapter(rightNavigationView, uidList, hasTracking, this);
        if (rightNavigationView != null) rightNavigationView.setAdapter(friendsNavAdapter);

        header = navigationView.getHeaderView(0);

        profileImageView = header.findViewById(R.id.profile_image);

        /* POPULATE LEFT NAV DRAWER HEADER FIELDS */
        header.findViewById(R.id.profile_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePictureSettings();
            }
        });

        setDisplayName();
        ProfileUtils.setProfilePicture(userId, (CircleImageView) header.findViewById(R.id.profile_image));
        databaseReference.child("picture").addChildEventListener(new UpdatedProfilePicturesListener(friendsNavAdapter));

        ((Switch) findViewById(R.id.show_hide_markers)).setChecked(showOnMapPreferences.getBoolean("all", true));

        ((Switch) findViewById(R.id.show_hide_markers)).setOnCheckedChangeListener(new ToggleAllMarkersVisibility());

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {
                    ProfileUtils.resetDeviceSettings(settingsSharedPreferences, trackingPreferences, showOnMapPreferences);
                    mainPresenter.auth();
                }
            }
        };



        findViewById(R.id.show_hide_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible) {
                    findViewById(R.id.filter_content).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.filter_content).setVisibility(View.VISIBLE);
                }
                isVisible = !isVisible;
            }
        });
    }

    private void setTracking() {
        boolean mobileNetwork = settingsSharedPreferences.getBoolean("mobile_network", true);

        //Tracking
        if (mobileNetwork || Connectivity.isConnectedWifi(this)) {
            Thread startTrackingService = new StartTrackingService();
            if (!TrackingService.isRunning) {
                startTrackingService.start();
            }
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.maps:
                        mainPresenter.showFragment(mapsFragment);
                        return true;
                    case R.id.friends:
                        item.setChecked(false);
                        mainPresenter.friends();
                        return true;
                    case R.id.settings:
                        item.setChecked(false);
                        mainPresenter.settings();
                        return true;
                    case R.id.logout:
                        item.setChecked(false);
                        mainPresenter.logout();
                        return true;
                    case R.id.feedback:
                        item.setChecked(false);
                        mainPresenter.feedback();
                        return true;
                }
                return true;
            }
        });
    }

    private void setDisplayName() {
        if (firebaseUser != null) {
            String welcome = String.format(getResources().getString(R.string.welcome_user_header), firebaseUser.getDisplayName());
            ((TextView) header.findViewById(R.id.username)).setText(welcome);
            settingsSharedPreferences.edit().putString("display_name", firebaseUser.getDisplayName()).apply();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 213) {
                mapsFragment.setup();
            } else {
                new ProfileSelectionResult(this).profilePictureResult(this, requestCode, resultCode, data, userId);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public void updateFriendsList(@Nullable String uid, @Nullable String name) {
        uidList.add(uid);
        if (!friendsId.containsKey(uid)) friendsId.put(uid, true);
        if (!friendNames.containsKey(uid)) friendNames.put(uid, name);
        friendsNavAdapter.notifyDataSetChanged();
        findViewById(R.id.add_friends).setVisibility(View.GONE);
    }

    @Override
    public void removeFromFriendList(@Nullable String uid) {
        uidList.remove(uid);
        if (friendsId.containsKey(uid)) friendsId.remove(uid);
        if (friendNames.containsKey(uid)) friendNames.remove(uid);
        friendsNavAdapter.notifyDataSetChanged();
        if (friendsId.isEmpty()) findViewById(R.id.add_friends).setVisibility(View.VISIBLE);
    }

    @Override
    public void updateTrackingState(@Nullable String uid, @Nullable Boolean trackingState) {
        hasTracking.put(uid, trackingState);
        friendsNavAdapter.notifyDataSetChanged();
    }

    @Override
    public void removeTrackingState(@Nullable String uid) {
        hasTracking.remove(uid);
        friendsNavAdapter.notifyDataSetChanged();
    }

    @Override
    public void swapFragment(@NotNull Fragment fragment) {
        getSupportFragmentManager().beginTransaction().show(fragment).commit();
    }

    @Override
    public void friendsIntent() {
        startActivity(new Intent(MainActivity.this, FriendsManager.class));
    }

    @Override
    public void settingsIntent() {
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }

    @Override
    public void logoutIntent() {
        startActivity(new Intent(MainActivity.this, AuthActivity.class));
        finish();
    }

    @Override
    public void feedbackIntent() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("text/plain");
        emailIntent.setData(Uri.parse("mailto:support@appsbystudio.co.uk"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "GeoShare Feedback");
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send email via"));
        } else {
            Toast.makeText(MainActivity.this, "No email applications found on this device!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void openNavDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void openFriendsNavDrawer() {
        rightDrawer.openDrawer(GravityCompat.END);
    }

    @Override
    public void closeNavDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void closeFriendsNavDrawer() {
        rightDrawer.closeDrawer(GravityCompat.END);
    }

    @Override
    public void showError(@NotNull String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showErrorSnackbar(@NotNull String message) {
        Snackbar.make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_SHORT)
                .setAction("RETRY?", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mainPresenter.logout();
                    }
                });
    }

    /* CLICK FUNCTIONALITY FOR PROFILE PIC */
    private void profilePictureSettings() {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.DialogFragment profileDialog = new ProfilePictureOptions();
        profileDialog.show(fragmentManager, "profile_dialog");
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("mobile_network")) {
            boolean mobileNetwork = sharedPreferences.getBoolean("mobile_network", true);
            Intent trackingService = new Intent(this, TrackingService.class);
            if (mobileNetwork) {
                startService(trackingService);
            } else if (Connectivity.isConnectedMobile(this)){
                stopService(trackingService);
            }
        } else if (s.equals("display_name")) {
            String name = sharedPreferences.getString(s, "DEFAULT");
            databaseReference.child("users").child(userId).child("name").setValue(name);
            databaseReference.child("users").child(userId).child("caseFoldedName").setValue(name.toLowerCase());

            UserProfileChangeRequest profileChangeRequest =  new UserProfileChangeRequest.Builder().setDisplayName(name).build();

            firebaseUser.updateProfile(profileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    setDisplayName();
                }
            });
        }
    }

    @Override
    public void setMarkerHidden(String friendId, boolean visible) {
        mapsFragment.setMarkerVisibility(friendId, visible);
        showOnMapPreferences.edit().putBoolean(friendId, visible).apply();
    }

    @Override
    public void findOnMapClicked(String friendId) {
        mapsFragment.findFriendOnMap(friendId);
    }

    @Override
    public void sendLocationDialog(String name, String friendId) {
        Bundle arguments = new Bundle();
        arguments.putString("name", name);
        arguments.putString("friendId", friendId);
        arguments.putString("uid", userId);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.DialogFragment friendDialog = new ShareOptions();
        friendDialog.setArguments(arguments);
        friendDialog.show(fragmentManager, "location_dialog");
    }

    @Override
    public void stopSharing(FirebaseUser user, final String friendId) {
        databaseReference.child(FirebaseHelper.TRACKING).child(friendId).child("tracking").child(user.getUid()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        trackingPreferences.edit().putBoolean(friendId, false).apply();
                        friendsNavAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO: Show a message (with "try again?" ?)
                    }
                });
    }

    @Override
    public void updateProfilePicture() {
        ProfileUtils.setProfilePicture(userId, profileImageView);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (rightDrawer.isDrawerOpen(GravityCompat.END)) {
            rightDrawer.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        settingsSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private class ToggleAllMarkersVisibility implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            mapsFragment.setAllMarkersVisibility(b);
        }
    }
}