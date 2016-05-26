package uk.co.appsbystudio.geoshare;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchResult;

import java.io.InputStream;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.appsbystudio.geoshare.database.DatabaseHelper;
import uk.co.appsbystudio.geoshare.database.databaseModel.RecentSearchModel;
import uk.co.appsbystudio.geoshare.database.databaseModel.UserModel;
import uk.co.appsbystudio.geoshare.json.JSONRequests;
import uk.co.appsbystudio.geoshare.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private NavigationView navigationView;
    private RecyclerView rightNavigationView;
    private DrawerLayout drawerLayout;

    CircleImageView profileImage;

    TextView usernameTextView;

    String pID;
    String mUsername;

    DatabaseHelper db;

    MapView mapView;
    GoogleMap map;

    SearchBox searchBox;
    boolean isSearching;

    protected static final int REQUEST_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameTextView = (TextView) findViewById(R.id.username);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.left_nav_view);
        rightNavigationView = (RecyclerView) findViewById(R.id.right_friends_drawer);


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
                        Toast.makeText(getApplicationContext(), "Maps", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.friends:
                        Toast.makeText(getApplicationContext(), "Friends", Toast.LENGTH_LONG).show();
                        return true;
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "Settings", Toast.LENGTH_LONG).show();
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

        View header = navigationView.getHeaderView(0);

        db = new DatabaseHelper(this);

        List<UserModel> userModelList = db.getUsername();
        for (UserModel id: userModelList) {
            mUsername = id.getUsername();
        }

        //profileImage = (CircleImageView) findViewById(R.id.profile_image);

        System.out.println(mUsername);

        new DownloadImageTask((CircleImageView) header.findViewById(R.id.profile_image)).execute("http://geoshare.appsbystudio.co.uk/api/user/" + mUsername + "/img/");


        usernameTextView = (TextView) header.findViewById(R.id.username);
        usernameTextView.setText(getString(R.string.user_message) + mUsername);

        searchBox = (SearchBox) findViewById(R.id.searchbox);
        searchBox.enableVoiceRecognition(this);

        searchBox.setHint("Search...");
        searchBox.setLogoText("Search...");

        List<RecentSearchModel> recentSearchModelList = db.getSearchHistory();
        for (RecentSearchModel term : recentSearchModelList) {
            SearchResult option = new SearchResult(term.getTerm(), getResources().getDrawable(R.drawable.ic_history_black_48dp));
            searchBox.addSearchable(option);
        }

        searchBox.setMenuListener(new SearchBox.MenuListener() {
            @Override
            public void onMenuClick() {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        searchBox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {
                isSearching = true;
            }

            @Override
            public void onSearchCleared() {

            }

            @Override
            public void onSearchClosed() {
                isSearching = false;
            }

            @Override
            public void onSearchTermChanged(String s) {
                if (s.isEmpty()) {
                    searchBox.setLogoText("Search...");
                } else {
                    searchBox.setLogoText(s);
                }
            }

            @Override
            public void onSearch(String s) {
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new java.util.Date());

                RecentSearchModel recentSearchModel = new RecentSearchModel(null, s, currentDateTimeString);

                db.addSearchHistory(recentSearchModel);
            }

            @Override
            public void onResultClick(SearchResult searchResult) {

            }
        });

        db.close();

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.6306508, -10.1533731), 14);
        map.animateCamera(cameraUpdate);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            searchBox.populateEditText(matches.get(0));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void logout() {
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

    public void deleteUser() {
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && isSearching) {
            searchBox.toggleSearch();
            isSearching = false;
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void loginReturn() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        this.finish();
    }


    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        CircleImageView bmImage;

        public DownloadImageTask(CircleImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
