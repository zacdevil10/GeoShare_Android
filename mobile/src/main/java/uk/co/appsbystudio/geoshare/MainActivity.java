package uk.co.appsbystudio.geoshare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.database.DatabaseHelper;
import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;
import uk.co.appsbystudio.geoshare.friends.FriendsManagerFragment;
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;
import uk.co.appsbystudio.geoshare.json.JSONRequests;
import uk.co.appsbystudio.geoshare.login.LoginActivity;
import uk.co.appsbystudio.geoshare.maps.MapsFragment;
import uk.co.appsbystudio.geoshare.settings.ProfilePictureOptions;
import uk.co.appsbystudio.geoshare.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private final ArrayList footerItems = new ArrayList<>();

    private MapsFragment mapsFragment;
    private FriendsManagerFragment friendsManagerFragment;
    private SettingsFragment settingsFragment;

    private String pID;
    private String mUsername;
    private Integer remember;

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        footerItems.add("Settings");
        footerItems.add("Logout");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.left_nav_view);
        RecyclerView rightNavigationView = (RecyclerView) findViewById(R.id.right_friends_drawer);
        ListView footerView = (ListView) findViewById(R.id.footer);
        ListAdapter listAdapter = new ArrayAdapter<>(this, R.layout.friends_list_item, R.id.friend_name, footerItems);
        footerView.setAdapter(listAdapter);

        navigationView.getMenu().getItem(0).setChecked(true);

        View header = navigationView.getHeaderView(0);

        CircleImageView profilePicture = (CircleImageView) header.findViewById(R.id.profile_image);

        mapsFragment = new MapsFragment();
        friendsManagerFragment = new FriendsManagerFragment();
        settingsFragment = new SettingsFragment();

        db = new DatabaseHelper(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mapsFragment).commit();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                if (item.isChecked()) {
                    item.setChecked(false);
                } else {
                    item.setChecked(true);
                }

                drawerLayout.closeDrawers();

                switch (item.getItemId()) {
                    case R.id.maps:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mapsFragment).commit();
                        return true;
                    case R.id.friends:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, friendsManagerFragment).commit();
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, settingsFragment).commit();
                        return true;
                    case R.id.delete:
                        deleteUser();
                        return true;
                    case R.id.logout:
                        logout();
                        return true;
                }
                return true;
            }
        });

        List<UserModel> userModelList = db.getUsername();
        for (UserModel id: userModelList) {
            mUsername = id.getUsername();
            pID = id.getpID();
        }

        new DownloadImageTask((CircleImageView) header.findViewById(R.id.profile_image)).execute("http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/img/");

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePictureSettings();
            }
        });

        TextView usernameTextView = (TextView) header.findViewById(R.id.username);
        usernameTextView.setText(getString(R.string.user_message) + mUsername);

        db.close();

    }

    private void profilePictureSettings() {
        android.app.FragmentManager fragmentManager = getFragmentManager();
        android.app.DialogFragment profileDialog = new ProfilePictureOptions();
        profileDialog.show(fragmentManager, "");
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            rememberLogout();
            super.onBackPressed();
        }
    }

    private void rememberLogout() {
        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pID = id.getpID();
            mUsername = id.getUsername();
            remember = id.getRemember();
        }

        if (remember == 1) {
            return;
        } else {
            new JSONRequests().onDeleteRequest("http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/session/" + pID, pID, this);
        }

        db.clearAllUserData();
        db.close();
    }

    private void logout() {
        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pID = id.getpID();
            mUsername = id.getUsername();
        }

        new JSONRequests().onDeleteRequest("http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/session/" + pID, pID, this);

        db.clearAllUserData();
        db.close();

        loginReturn();
    }

    private void deleteUser() {
        List<UserModel> userModelList = db.getAllUsers();
        for (UserModel id: userModelList) {
            pID = id.getpID();
            mUsername = id.getUsername();
        }

        new JSONRequests().onDeleteRequest("http://geoshare.appsbystudio.co.uk/api/user/" + mUsername, pID, this);

        db.clearAllUserData();
        db.close();

        loginReturn();
    }

    private void loginReturn() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}