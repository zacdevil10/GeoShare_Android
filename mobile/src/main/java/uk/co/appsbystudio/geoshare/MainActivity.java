package uk.co.appsbystudio.geoshare;

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

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.friends.FriendsManagerFragment;
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;
import uk.co.appsbystudio.geoshare.json.JSONRequests;
import uk.co.appsbystudio.geoshare.maps.MapsFragment;
import uk.co.appsbystudio.geoshare.settings.ProfilePictureOptions;
import uk.co.appsbystudio.geoshare.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private RecyclerView rightNavigationView;
    private ListView footerView;
    private ListAdapter footerListAdapter;
    private final ArrayList footerItems = new ArrayList<>();

    private MapsFragment mapsFragment;
    private FriendsManagerFragment friendsManagerFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String username = new ReturnData().getUsername(this);

        footerItems.add("Settings");
        footerItems.add("Logout");

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.left_nav_view);
        rightNavigationView = (RecyclerView) findViewById(R.id.right_friends_drawer);
        footerView = (ListView) findViewById(R.id.footer);
        footerListAdapter = new ArrayAdapter<>(this, R.layout.friends_list_item, R.id.friend_name, footerItems);
        footerView.setAdapter(footerListAdapter);

        assert navigationView != null;
        navigationView.getMenu().getItem(0).setChecked(true);

        View header = navigationView.getHeaderView(0);

        CircleImageView profilePicture = (CircleImageView) header.findViewById(R.id.profile_image);

        mapsFragment = new MapsFragment();
        friendsManagerFragment = new FriendsManagerFragment();
        settingsFragment = new SettingsFragment();

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
                        getSupportFragmentManager().beginTransaction().remove(settingsFragment).commit();
                        getSupportFragmentManager().beginTransaction().remove(friendsManagerFragment).commit();
                        getSupportFragmentManager().beginTransaction().show(mapsFragment).commit();
                        return true;
                    case R.id.friends:
                        getSupportFragmentManager().beginTransaction().remove(settingsFragment).commit();
                        getSupportFragmentManager().beginTransaction().hide(mapsFragment).commit();
                        getFragmentManager().executePendingTransactions();
                        if (friendsManagerFragment.isAdded()) {
                            System.out.println("Already added");
                        } else {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, friendsManagerFragment).commit();
                        }
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().hide(mapsFragment).commit();
                        getSupportFragmentManager().beginTransaction().remove(friendsManagerFragment).commit();
                        getFragmentManager().executePendingTransactions();
                        if (settingsFragment.isAdded()) {
                            System.out.println("Already added");
                        } else {
                            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, settingsFragment).commit();
                        }
                        return true;
                }
                return true;
            }
        });

        new DownloadImageTask((CircleImageView) header.findViewById(R.id.profile_image)).execute("http://geoshare.appsbystudio.co.uk/api/user/" + username + "/img/");

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePictureSettings();
            }
        });

        TextView usernameTextView = (TextView) header.findViewById(R.id.username);
        usernameTextView.setText(getString(R.string.user_message) + username);

    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
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
        Integer remember = new ReturnData().getRemember(this);
        String pID = new ReturnData().getpID(this);
        String username = new ReturnData().getUsername(this);

        if (remember != 1) {
            new JSONRequests().onDeleteRequest("http://geoshare.appsbystudio.co.uk/api/user/" + username + "/session/" + pID, pID, this);
        }
    }
}