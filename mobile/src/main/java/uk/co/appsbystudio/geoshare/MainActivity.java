package uk.co.appsbystudio.geoshare;

import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.database.ReturnData;
import uk.co.appsbystudio.geoshare.friends.FriendsManagerFragment;
import uk.co.appsbystudio.geoshare.json.DownloadImageTask;
import uk.co.appsbystudio.geoshare.json.JSONRequests;
import uk.co.appsbystudio.geoshare.login.LoginActivity;
import uk.co.appsbystudio.geoshare.maps.MapsFragment;
import uk.co.appsbystudio.geoshare.settings.ProfilePictureOptions;
import uk.co.appsbystudio.geoshare.settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    private MapsFragment mapsFragment;
    private FriendsManagerFragment friendsManagerFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String username = new ReturnData().getUsername(this);

        /* HANDLES FOR VARIOUS VIEWS */
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.left_nav_view);
        RecyclerView rightNavigationView = (RecyclerView) findViewById(R.id.right_friends_drawer);

        assert navigationView != null;
        navigationView.getMenu().getItem(0).setChecked(true);
        View header = navigationView.getHeaderView(0);
        CircleImageView profilePicture = (CircleImageView) header.findViewById(R.id.profile_image);

        mapsFragment = new MapsFragment();
        friendsManagerFragment = new FriendsManagerFragment();
        settingsFragment = new SettingsFragment();

        /* RECENT APPS COLOR */
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        ActivityManager.TaskDescription taskDesc = new ActivityManager.TaskDescription(getString(R.string.app_name), bm, getResources().getColor(R.color.recent_color));
        this.setTaskDescription(taskDesc);

        /* LEFT NAV DRAWER FUNCTIONALITY/FRAGMENT SWAPPING */
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame_map, mapsFragment).commit();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
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
                        if(!friendsManagerFragment.isAdded()) getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, friendsManagerFragment).commit();
                        return true;
                    case R.id.settings:
                        getSupportFragmentManager().beginTransaction().hide(mapsFragment).commit();
                        getSupportFragmentManager().beginTransaction().remove(friendsManagerFragment).commit();
                        getFragmentManager().executePendingTransactions();
                        if(!settingsFragment.isAdded()) getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, settingsFragment).commit();
                        return true;
                    case R.id.logout:
                        item.setChecked(false);
                        logout();
                        return true;
                    case R.id.feedback:
                        item.setChecked(false);
                        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                        emailIntent.setType("text/plain");
                        emailIntent.setData(Uri.parse("mailto:support@appsbystudio.co.uk"));
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "GeoShare Feedback");
                        if (emailIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(Intent.createChooser(emailIntent, "Send email via"));
                        } else {
                            //TODO: Make toast.
                            System.out.println("No email applications found on this device!");
                        }
                        return true;
                }
                return true;
            }
        });

        /* POPULATE LEFT NAV DRAWER HEADER FIELDS */
        new DownloadImageTask((CircleImageView) header.findViewById(R.id.profile_image), this).execute("http://geoshare.appsbystudio.co.uk/api/user/" + username + "/img/");
        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilePictureSettings();
            }
        });

        TextView usernameTextView = (TextView) header.findViewById(R.id.username);
        usernameTextView.setText(getString(R.string.user_message) + username);
    }

    /* FRAGMENTS CALL THIS TO OPEN NAV DRAWER */
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /* CLICK FUNCTIONALITY FOR PROFILE PIC */
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

    public void logout() {
        String pID = new ReturnData().getpID(this);
        String username = new ReturnData().getUsername(this);

        new JSONRequests().onDeleteRequest("http://geoshare.appsbystudio.co.uk/api/user/" + username + "/session/" + pID, pID, this);

        new ReturnData().clearData(this);

        loginReturn();
    }

    public void loginReturn() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}