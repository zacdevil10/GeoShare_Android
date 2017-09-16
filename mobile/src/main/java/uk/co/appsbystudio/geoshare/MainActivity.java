package uk.co.appsbystudio.geoshare;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.friends.FriendsManager;
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsNavAdapter;
import uk.co.appsbystudio.geoshare.login.LoginActivity;
import uk.co.appsbystudio.geoshare.maps.MapsFragment;
import uk.co.appsbystudio.geoshare.maps.PlacesSearchFragment;
import uk.co.appsbystudio.geoshare.places.PlacesFragment;
import uk.co.appsbystudio.geoshare.services.TrackingService;
import uk.co.appsbystudio.geoshare.settings.ProfilePictureOptions;
import uk.co.appsbystudio.geoshare.settings.SettingsFragment;
import uk.co.appsbystudio.geoshare.settings.ShareALocationDialog;
import uk.co.appsbystudio.geoshare.settings.ShareOptions;
import uk.co.appsbystudio.geoshare.utils.UserInformation;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean LOCAL_LOGV = true;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseFriendsRef;
    private DatabaseReference isTrackingRef;
    private StorageReference storageReference;

    private DrawerLayout drawerLayout;
    private DrawerLayout rightDrawer;
    private View header;
    private String userId;

    private final ArrayList<String> uid = new ArrayList<>();
    private final HashMap<String, Boolean> hasTracking = new HashMap<>();

    private final MapsFragment mapsFragment = new MapsFragment();
    private final PlacesFragment placesFragment = new PlacesFragment();
    private final SettingsFragment settingsFragment = new SettingsFragment();

    private final PlacesSearchFragment placesSearchFragment = new PlacesSearchFragment();

    private NavigationView navigationView;

    private FriendsNavAdapter friendsNavAdapter;

    private Bitmap bitmap;
    private File imageFile;

    private FloatingActionButton search;
    private BottomSheetBehavior bottomSheetBehavior;

    private SharedPreferences sharedPreferences;

    public static File cacheDir;

    Animation animShowFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent gpsService = new Intent(this, TrackingService.class);
        startService(gpsService);

        cacheDir = this.getCacheDir();

        //Firebase initialisation
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userId = firebaseUser != null ? firebaseUser.getUid() : null;
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();
        databaseFriendsRef = database.getReference("friends/" + userId);
        databaseFriendsRef.keepSynced(true);
        isTrackingRef = database.getReference("current_location/" + userId + "/tracking");
        isTrackingRef.keepSynced(true);
        storageReference = FirebaseStorage.getInstance().getReference();

        sharedPreferences = getSharedPreferences("tracking", MODE_PRIVATE);

        /* HANDLES FOR VARIOUS VIEWS */
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        rightDrawer = (DrawerLayout) findViewById(R.id.right_nav_drawer);
        navigationView = (NavigationView) findViewById(R.id.left_nav_view);
        RecyclerView rightNavigationView = (RecyclerView) findViewById(R.id.right_friends_drawer);
        if (rightNavigationView != null) rightNavigationView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        if (rightNavigationView != null) rightNavigationView.setLayoutManager(layoutManager);

        //Get friends and populate right nav drawer
        getFriends();
        getTrackingStatus();
        friendsNavAdapter = new FriendsNavAdapter(this, rightNavigationView, uid, hasTracking, databaseReference);
        if (rightNavigationView != null) rightNavigationView.setAdapter(friendsNavAdapter);

        //TODO: Update from deprecated method
        rightDrawer.setScrimColor(getResources().getColor(android.R.color.transparent));

        navigationView.getMenu().getItem(0).setChecked(true);
        header = navigationView.getHeaderView(0);
        CircleImageView profilePicture = (CircleImageView) header.findViewById(R.id.profile_image);

        /* RECENT APPS COLOR */
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            ActivityManager.TaskDescription taskDesc;
            taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.recent_color));
            this.setTaskDescription(taskDesc);
        }

        /* BOTTOM SHEET FRAGMENT SWAPPING */
        //getSupportFragmentManager().beginTransaction().add(R.id.bottom_sheet_container, placesSearchFragment).commit();

        /* LEFT NAV DRAWER FUNCTIONALITY/FRAGMENT SWAPPING */
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame_map, mapsFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().show(mapsFragment).commit();
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.maps:
                        if (LOCAL_LOGV) Log.v(TAG, "Add maps fragment");
                        getSupportFragmentManager().beginTransaction().remove(settingsFragment).commit();
                        getSupportFragmentManager().beginTransaction().remove(placesFragment).commit();
                        getSupportFragmentManager().beginTransaction().show(mapsFragment).commit();
                        return true;
                    case R.id.friends:
                        if (LOCAL_LOGV) Log.v(TAG, "Opening friends manager");
                        item.setChecked(false);
                        Intent intent = new Intent(MainActivity.this, FriendsManager.class);
                        startActivity(intent);
                        return true;
                    /*
                    case R.id.places:
                        if (LOCAL_LOGV) Log.v(TAG, "Add places fragment");
                        getSupportFragmentManager().beginTransaction().hide(mapsFragment).commit();
                        getFragmentManager().executePendingTransactions();
                        if(!placesFragment.isAdded()) getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, placesFragment).commit();
                        return true;
                    //*/
                    case R.id.settings:
                        if (LOCAL_LOGV) Log.v(TAG, "Add settings fragment");
                        getSupportFragmentManager().beginTransaction().hide(mapsFragment).commit();
                        getFragmentManager().executePendingTransactions();
                        if(!settingsFragment.isAdded()) getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, settingsFragment).commit();
                        return true;
                    case R.id.logout:
                        if (LOCAL_LOGV) Log.v(TAG, "Calling logout()");
                        item.setChecked(false);
                        logout();
                        return true;
                    case R.id.feedback:
                        if (LOCAL_LOGV) Log.v(TAG, "Sending feedback");
                        item.setChecked(false);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setType("text/plain");
                        emailIntent.setData(Uri.parse("mailto:support@appsbystudio.co.uk"));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "GeoShare Feedback");
                        if (emailIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(Intent.createChooser(emailIntent, "Send email via"));
                        } else {
                            //TODO: Make toast.
                            if (LOCAL_LOGV) Log.v(TAG, "No email applications found on this device!");
                        }
                        return true;
                }
                return true;
            }
        });

        /* POPULATE LEFT NAV DRAWER HEADER FIELDS */
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LOCAL_LOGV) Log.v(TAG, "Clicked on profile picture");
                profilePictureSettings();
            }
        });

        //Check if users profile picture is stored in the cache
        File fileCheck = new File(getCacheDir() + "/" + userId + ".png");

        if (fileCheck.exists()) {
            //If file exists, set image view image as profile picture from storage
            //TODO: Allow for updating picture on different devices
            /* Could mean that this method will not work without getting the picture every time
                or adding a last updated section to the users profile picture
                and comparing with the date of the file created.
             */
            Bitmap imageBitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + userId + ".png");
            ((CircleImageView) header.findViewById(R.id.profile_image)).setImageBitmap(imageBitmap);
        } else {
            //If the file doesn't exist, download from Firebase
            StorageReference profileRef = storageReference.child("profile_pictures/" + userId + ".png");
            profileRef.getFile(Uri.fromFile(fileCheck))
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap imageBitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + userId + ".png");
                            ((CircleImageView) header.findViewById(R.id.profile_image)).setImageBitmap(imageBitmap);
                        }
            });
        }

        //Get users name and add to welcome message
        final TextView usernameTextView = (TextView) header.findViewById(R.id.username);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserInformation userInformation = dataSnapshot.child("users").child(userId).getValue(UserInformation.class);
                assert userInformation != null;
                System.out.println(userInformation.getName());
                String welcome = String.format(getResources().getString(R.string.welcome_user_header), userInformation.getName());
                usernameTextView.setText(welcome);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        search = (FloatingActionButton) findViewById(R.id.searchLocationShare);

        //Set the animation for the FAB when the bottom sheet is made in/visible
        final Animation animHideFab = AnimationUtils.loadAnimation(this, R.anim.scale_down);
        animShowFab = AnimationUtils.loadAnimation(this, R.anim.scale_up);

        animHideFab.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                search.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animShowFab.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                search.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search.startAnimation(animHideFab);
                //getSupportFragmentManager().beginTransaction().hide(mapsFragment).commit();
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_up, R.anim.exit_down).replace(R.id.content_frame, placesSearchFragment).addToBackStack("").commit();
                //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    /* FIREBASE GET LIST OF FRIENDS */
    //TODO: Can move this to onCreate
    private void getFriends() {
        databaseFriendsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                uid.add(dataSnapshot.getKey());
                friendsNavAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                uid.remove(dataSnapshot.getKey());
                friendsNavAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getTrackingStatus() {
        isTrackingRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Boolean tracking = dataSnapshot.getValue(Boolean.class);
                hasTracking.put(dataSnapshot.getKey(), true);
                friendsNavAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                hasTracking.remove(dataSnapshot.getKey());
                friendsNavAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showMapFragment() {
        if (LOCAL_LOGV) Log.v(TAG, "Showing map activity");
        getSupportFragmentManager().beginTransaction().show(mapsFragment).commit();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    public void backFromSearch() {
        if (LOCAL_LOGV) Log.v(TAG, "Back from searching");
        getSupportFragmentManager().beginTransaction().remove(placesSearchFragment).commit();
        search.startAnimation(animShowFab);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");

            imageFile = new File(this.getCacheDir(), userId + ".png");

            try {
                FileOutputStream stream = new FileOutputStream(imageFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 1, stream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            Uri uri = Uri.fromFile(imageFile);

            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setFixAspectRatio(true).start(this);

        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1, 1).setFixAspectRatio(true).start(this);
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            if (LOCAL_LOGV) Log.v(TAG, "Oppening image crop tool");
            CropImage.ActivityResult activityResult = CropImage.getActivityResult(data);
            Uri uri = activityResult.getUri();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageFile = new File(this.getCacheDir(), userId + ".png");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                if (bitmap != null) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                }

                fileOutputStream.close();

                StorageReference profileRef = storageReference.child("profile_pictures/" + userId + ".png");
                profileRef.putFile(Uri.fromFile(imageFile))
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                if (LOCAL_LOGV) Log.v(TAG, "Picture has been uploaded");
                                Bitmap imageBitmap = BitmapFactory.decodeFile(getCacheDir() + "/" + userId + ".png");
                                ((CircleImageView) header.findViewById(R.id.profile_image)).setImageBitmap(imageBitmap);

                            }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    /* FRAGMENTS CALL THIS TO OPEN NAV DRAWER */
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void openFriendsDrawer() {
        rightDrawer.openDrawer(GravityCompat.END);
    }

    /* CLICK FUNCTIONALITY FOR PROFILE PIC */
    private void profilePictureSettings() {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.DialogFragment profileDialog = new ProfilePictureOptions();
        profileDialog.show(fragmentManager, "");
    }

    /* DIALOG FOR SENDING YOUR CURRENT LOCATION TO A FRIEND */
    public void sendLocationDialog(String name, String friendId) {
        if (LOCAL_LOGV) Log.v(TAG, "Oppening send location dialog");
        Bundle arguments = new Bundle();
        arguments.putString("name", name);
        arguments.putString("friendId", friendId);
        arguments.putString("uid", userId);

        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.DialogFragment friendDialog = new ShareOptions();
        friendDialog.setArguments(arguments);
        friendDialog.show(fragmentManager, "");
    }

    /* DIALOG FOR SHARING A MAP LOCATION WITH A FRIEND */
    public void shareALocation() {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.DialogFragment shareALocationDialog = new ShareALocationDialog();
        shareALocationDialog.show(fragmentManager, "");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            if (LOCAL_LOGV) Log.v(TAG, "Drawer closed");
            drawerLayout.closeDrawer(GravityCompat.START);
        } else if (rightDrawer.isDrawerOpen(GravityCompat.END)) {
            if (LOCAL_LOGV) Log.v(TAG, "Drawer closed");
            rightDrawer.closeDrawer(GravityCompat.END);
        } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            if (LOCAL_LOGV) Log.v(TAG, "Bottom sheet closed");
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            if (LOCAL_LOGV) Log.v(TAG, "Closing app");
            super.onBackPressed();
        }
    }

    /* FIREBASE AUTH LOG OUT */
    private void logout() {
        if (FirebaseAuth.getInstance() != null) {
            if (LOCAL_LOGV) Log.v(TAG, "Logging out");
            FirebaseAuth.getInstance().signOut();
            sharedPreferences.edit().clear().apply();
            loginReturn();
        } else {
            if (LOCAL_LOGV) Log.v(TAG, "Could not log out");
        }
    }

    private void loginReturn() {
        if (LOCAL_LOGV) Log.v(TAG, "Returning to login activity");
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}