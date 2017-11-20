package uk.co.appsbystudio.geoshare.maps;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.GPSTracking;
import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.utils.BitmapUtils;
import uk.co.appsbystudio.geoshare.utils.Connectivity;
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations;
import uk.co.appsbystudio.geoshare.utils.directions.DirectionsDownloadTask;
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper;
import uk.co.appsbystudio.geoshare.utils.firebase.TrackingInfo;
import uk.co.appsbystudio.geoshare.utils.json.UrlUtil;
import uk.co.appsbystudio.geoshare.utils.services.OnNetworkStateChangeListener;
import uk.co.appsbystudio.geoshare.utils.ui.MapStyleManager;
import uk.co.appsbystudio.geoshare.utils.MarkerAnimatorLabelTask;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
        SharedPreferences.OnSharedPreferenceChangeListener,
        OnNetworkStateChangeListener.NetworkStateReceiverListener {

    private View view;

    private FirebaseUser user;

    private DatabaseReference databaseReference;
    private DatabaseReference shareReference;
    private DatabaseReference trackingReference;

    private GoogleMap googleMap;

    private OnNetworkStateChangeListener networkStateChangeListener;

    private boolean isTracking;
    private FloatingActionButton trackingButton;

    private boolean mobileNetwork;

    private GPSTracking gpsTracking;

    private Location listenerLocation;
    private LocationListener locationListener;
    private LocationManager locationManager;

    private String bestProvider;
    private int updateFrequency;

    private boolean isSynced = false;

    private SharedPreferences settingsSharedPreferences;
    private SharedPreferences showOnMapPreferences;

    private TextView friendsNearText;

    private Marker myLocation;
    private Marker selectedMarker;

    private Circle nearbyCircle;
    private Circle accuracyCircle;

    public static Polyline directions;

    private Snackbar snackbar;

    private final ValueAnimator initAnimator = ValueAnimator.ofArgb(0, 163);
    private final ValueAnimator endAnimator = ValueAnimator.ofArgb(164, 255);

    private final HashMap<String, Marker> friendMarkerList = new HashMap<>();
    private final HashMap<String, Long> friendLocationTime = new HashMap<>();

    private final int standardZoomLevel = 16;

    private static final int GET_PERMS = 1;

    public MapsFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        MapFragment mapFragment = null;
        if (getActivity() != null) {
            mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        if (savedInstanceState == null && mapFragment != null) {
            mapFragment.setRetainInstance(true);
        }

        networkStateChangeListener = new OnNetworkStateChangeListener();
        networkStateChangeListener.addListener(this);
        Application.getContext().registerReceiver(networkStateChangeListener, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        settingsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        settingsSharedPreferences.registerOnSharedPreferenceChangeListener(this);

        showOnMapPreferences = Application.getContext().getSharedPreferences("showOnMap", Context.MODE_PRIVATE);

        mobileNetwork = settingsSharedPreferences.getBoolean("mobile_network", true);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        if (user != null) {
            shareReference = FirebaseDatabase.getInstance().getReference(FirebaseHelper.CURRENT_LOCATION + "/" + user.getUid());
            shareReference.keepSynced(true);

            trackingReference = FirebaseDatabase.getInstance().getReference(FirebaseHelper.TRACKING + "/" + user.getUid() + "/" + FirebaseHelper.TRACKING);
        }

        friendsNearText = view.findViewById(R.id.friendNearText);

        view.findViewById(R.id.drawer_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).openDrawer();
            }
        });

        view.findViewById(R.id.friend_drawer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).openFriendsDrawer();
            }
        });

        trackingButton = view.findViewById(R.id.trackingFab);

        return view;
    }

    /*@Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("marker_list", friendMarkerList);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            friendMarkerList = savedInstanceState
        }
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        settingsSharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        networkStateChangeListener.removeListener(this);
        Application.getContext().unregisterReceiver(networkStateChangeListener);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        MapStyleManager styleManager = MapStyleManager.attachToMap(getContext(), this.googleMap);
        styleManager.addStyle(R.raw.map_style);

        setup();

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selectedMarker != null) {
                    setCameraPosition(selectedMarker.getPosition().latitude, selectedMarker.getPosition().longitude, standardZoomLevel, true);
                    resetMarkerIcon(selectedMarker);
                }
            }
        });

        googleMap.setOnCameraMoveStartedListener(this);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if (Objects.equals(marker.getTag(), 0)) {
                    return true;
                }

                if (selectedMarker != null) {
                    if (selectedMarker.getTag() == marker.getTag()) {
                        System.out.println("Already clicked");
                        return true;
                    }
                    resetMarkerIcon(selectedMarker);
                }

                selectedMarker = marker;

                String friendId = (String) marker.getTag();

                LatLng myLocationLatLng = myLocation.getPosition();
                LatLng destination = marker.getPosition();

                if (directions != null) directions.remove();

                if (destination != myLocationLatLng) {
                    String url = UrlUtil.getDirectionsUrl(myLocationLatLng, destination);
                    DirectionsDownloadTask directionsDownloadTask = new DirectionsDownloadTask(googleMap);
                    directionsDownloadTask.execute(url);
                }

                setCameraPosition(destination.latitude, destination.longitude, 18, true);

                new MarkerAnimatorLabelTask(marker, initAnimator, endAnimator, marker.getPosition().latitude, marker.getPosition().longitude, friendLocationTime.get(friendId))
                        .execute();

                if (isTracking) {
                    isTracking = false;
                    trackingButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.darker_gray)));
                }

                return true;
            }
        });
    }

    private void setup() {
        if (ActivityCompat.checkSelfPermission(Application.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(Application.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GET_PERMS);
            return;
        }

        isTracking = true;

        if (gpsTracking == null) {
            gpsTracking = new GPSTracking(getContext());
        }

        googleMap.setMyLocationEnabled(false);
        googleMap.setBuildingsEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        /* FIREBASE TRACKING SETUP */
        setTrackingReference();

        /* USING CUSTOM GPS TRACKING MARKER */
        LatLng currentLocation = new LatLng(gpsTracking.getLatitude(), gpsTracking.getLongitude());

        Bitmap myLocationMarker = BitmapFactory.decodeResource(getResources(), R.drawable.navigation);
        Bitmap scaledLocation = Bitmap.createScaledBitmap(myLocationMarker, 72, 72, false);

        setCameraPosition(gpsTracking.getLatitude(), gpsTracking.getLongitude(), standardZoomLevel, false);
        myLocation = googleMap.addMarker(new MarkerOptions()
                .position(currentLocation)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(scaledLocation))
                .anchor(0.5f, 0.5f)
        );
        myLocation.setTag(0);

        nearbyRadius(currentLocation);
        accuracyCircle(currentLocation, gpsTracking.getLocation().getAccuracy());

        setupLocationChangeListener();

        trackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isTracking) {
                    isTracking = true;
                    trackingButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));

                    if (listenerLocation != null) {
                        setCameraPosition(listenerLocation.getLatitude(), listenerLocation.getLongitude(), standardZoomLevel, true);
                    } else {
                        setCameraPosition(gpsTracking.getLatitude(), gpsTracking.getLongitude(), standardZoomLevel, true);
                    }

                    if (selectedMarker != null) resetMarkerIcon(selectedMarker);
                }
            }
        });

        if (user != null) {
            shareReference.addChildEventListener(staticLocationListener);
        }
    }

    @SuppressLint("MissingPermission")
    private void setupLocationChangeListener() {
    /* SETTING UP LOCATION CHANGE LISTENER */
        locationListener = new LocationListener();
        locationManager = (LocationManager) Application.getContext().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);

        bestProvider = locationManager.getBestProvider(criteria, false);

        updateFrequency = Integer.parseInt(settingsSharedPreferences.getString("update_frequency", "5")) * 1000;

        locationManager.requestLocationUpdates(bestProvider, updateFrequency, 0, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case GET_PERMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setup();
                }
        }
    }

    private void setTrackingReference() {
        if (mobileNetwork || Connectivity.isConnectedWifi(Application.getContext())) {
            syncTrackingRef();
        } else {
            unsyncTrackingRef();
        }
    }

    private void unsyncTrackingRef() {
        if (isSynced) {
            trackingReference.keepSynced(false);
            trackingReference.removeEventListener(trackingEventListener);
            isSynced = false;
        }
    }

    private void syncTrackingRef() {
        if (!isSynced) {
            trackingReference.keepSynced(true);
            trackingReference.addChildEventListener(trackingEventListener);
            isSynced = true;
        }
    }

    private final ChildEventListener staticLocationListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            DatabaseLocations databaseLocations = dataSnapshot.getValue(DatabaseLocations.class);
            if (databaseLocations != null) addFriendMarker(dataSnapshot.getKey(), databaseLocations);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            DatabaseLocations databaseLocations = dataSnapshot.getValue(DatabaseLocations.class);
            if (databaseLocations != null) updateFriendMarker(dataSnapshot.getKey(), databaseLocations);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            removeFriendMarker(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private final ChildEventListener trackingEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            onChildChanged(dataSnapshot, s);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            final String friendId = dataSnapshot.getKey();
            TrackingInfo trackingInfo = dataSnapshot.getValue(TrackingInfo.class);
            if (trackingInfo != null && trackingInfo.isTracking()) {
                getTrackingFriends(friendId);
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            removeFriendMarker(dataSnapshot.getKey());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void getTrackingFriends(final String friendId) {
        databaseReference.child(FirebaseHelper.TRACKING).child(friendId).child(FirebaseHelper.LOCATION)
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseLocations databaseLocations = dataSnapshot.getValue(DatabaseLocations.class);
                if (databaseLocations != null) {
                    if (friendMarkerList.containsKey(friendId)) {
                        updateFriendMarker(friendId, databaseLocations);
                    } else {
                        addFriendMarker(friendId, databaseLocations);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /* FRIEND MARKER METHODS */
    private void addFriendMarker(final String friendId, final DatabaseLocations databaseLocations) {
        if (this.googleMap != null && friendId != null && !friendMarkerList.containsKey(friendId)) {
            File fileCheck = new File(MainActivity.cacheDir + "/" + friendId + ".png");
            if (fileCheck.exists()) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + friendId + ".png");
                setupFriendMarkersArt(imageBitmap, databaseLocations, friendId);
            } else {
                final StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference profileRef = storageReference.child(FirebaseHelper.PROFILE_PICTURE + "/" + friendId + ".png");
                profileRef.getFile(Uri.fromFile(new File(MainActivity.cacheDir + "/" + friendId + ".png")))
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + friendId + ".png");
                                setupFriendMarkersArt(imageBitmap, databaseLocations, friendId);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                setupFriendMarkersArt(null, databaseLocations, friendId);
                            }
                        });
            }
            friendLocationTime.put(friendId, databaseLocations.getTimestamp());
            nearbyFriends();
        }
    }

    private void updateFriendMarker(String friendId, final DatabaseLocations databaseLocations) {
        Marker friendMarker = friendMarkerList.get(friendId);
        if (friendMarker != null) {
            friendMarker.setPosition(new LatLng(databaseLocations.getLat(), databaseLocations.getLongitude()));
            friendMarkerList.put(friendId, friendMarker);
            friendLocationTime.put(friendId, databaseLocations.getTimestamp());
            nearbyFriends();
        }
    }

    private void removeFriendMarker(String friendId) {
        Marker friendMarker = friendMarkerList.get(friendId);
        if (friendMarker != null) friendMarker.remove();
        friendMarkerList.remove(friendId);
        friendLocationTime.remove(friendId);
        nearbyFriends();
    }

    public void setMarkerVisibility(String friendId, boolean visible) {
        if (friendMarkerList.containsKey(friendId)) {
            Marker marker = friendMarkerList.get(friendId);
            marker.setVisible(visible);
        }
        showOnMapPreferences.edit().putBoolean(friendId, visible).apply();
    }

    public void setAllMarkersVisibility(boolean visible) {
        if (googleMap != null) {
            for (String markerId : friendMarkerList.keySet()) {
                Marker marker = friendMarkerList.get(markerId);
                marker.setVisible(visible);
                showOnMapPreferences.edit().putBoolean(markerId, visible).apply();
            }
        }
    }

    public void findFriendOnMap(String friendId) {
        if (friendMarkerList.containsKey(friendId)) {
            Marker marker = friendMarkerList.get(friendId);
            CameraPosition cameraPosition = new CameraPosition.Builder().target(marker.getPosition()).zoom(18).build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            if (isTracking) {
                isTracking = false;
                trackingButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.darker_gray)));
            }
        }
    }
    /* END OF FRIEND MARKER METHODS */

    private void setCameraPosition(double latitude, double longitude, int zoom, boolean animate) {
        LatLng latLng = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
        if (animate) {
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        } else {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void setupFriendMarkersArt(Bitmap imageBitmap, DatabaseLocations databaseLocations, String friendId) {
        Marker friendMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(databaseLocations.getLat(), databaseLocations.getLongitude()))
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.bitmapCanvas(imageBitmap, 116, 155, false, 0, null)))
                .visible(showOnMapPreferences.getBoolean(friendId, true)));
        friendMarker.setTag(friendId);
        friendMarkerList.put(friendId, friendMarker);
    }

    private void resetMarkerIcon(Marker marker) {
        if (initAnimator.isRunning()) initAnimator.cancel();
        if (endAnimator.isRunning()) endAnimator.cancel();
        File fileCheck = new File(MainActivity.cacheDir + "/" + marker.getTag() + ".png");
        if (fileCheck.exists()) {
            Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + marker.getTag() + ".png");
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.bitmapCanvas(imageBitmap, 116, 155, false, 0, null)));
            marker.setAnchor(0.5f, 1);
        } else {
            marker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.bitmapCanvas(null, 116, 155, false, 0, null)));
            marker.setAnchor(0.5f, 1);
        }
        selectedMarker = null;
    }

    /* NEARBY METHODS */

    //GET NEARBY FRIENDS IN A GIVEN RADIUS
    private void nearbyFriends() {
        int count = 0;

        int radius = Integer.parseInt(settingsSharedPreferences.getString("nearby_radius", "0"));

        for (String markerId : friendMarkerList.keySet()) {
            LatLng markerLocation = friendMarkerList.get(markerId).getPosition();
            Location tempLocation = new Location(LocationManager.GPS_PROVIDER);

            tempLocation.setLatitude(markerLocation.latitude);
            tempLocation.setLongitude(markerLocation.longitude);

            if (gpsTracking.getLocation().distanceTo(tempLocation) < radius) {
                count = count + 1;
            }
        }

        friendsNearText
                .setText((count != 1) ? String.format(Locale.getDefault(), "Nearby\n%d Friends", count) : String.format(Locale.getDefault(), "Nearby\n%d Friend", count));
    }

    private void nearbyRadius(LatLng latLng) {
        int radius = Integer.parseInt(settingsSharedPreferences.getString("nearby_radius", "100"));

        if (nearbyCircle != null) {
            nearbyCircle.setCenter(latLng);
            nearbyCircle.setRadius(radius);
        } else {
            CircleOptions nearbyCircleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(radius)
                    .fillColor(Application.getContext().getResources().getColor(R.color.colorPrimaryTransparent))
                    .strokeWidth(0);

            nearbyCircle = googleMap.addCircle(nearbyCircleOptions);
        }

        nearbyFriends();
    }

    /* END OF NEARBY METHODS */

    /* LOCATION ACCURACY */
    private void accuracyCircle(LatLng latLng, float accuracy) {
        if (accuracyCircle != null) {
            accuracyCircle.setCenter(latLng);
        } else {
            CircleOptions accuracyCircleOptions = new CircleOptions()
                    .center(latLng)
                    .radius(accuracy)
                    .fillColor(Application.getContext().getResources().getColor(R.color.colorPrimaryDarkerTransparent))
                    .strokeWidth(0);

            accuracyCircle = googleMap.addCircle(accuracyCircleOptions);
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
        //If the user moves the map view, don't centre myLocation marker when location changes
        if (i == 1 && selectedMarker != null) {
            if (initAnimator.isRunning()) initAnimator.cancel();
            if (endAnimator.isRunning()) endAnimator.cancel();
            File fileCheck = new File(MainActivity.cacheDir + "/" + selectedMarker.getTag() + ".png");
            if (fileCheck.exists()) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + selectedMarker.getTag() + ".png");
                selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.bitmapCanvas(imageBitmap, 116, 155, false, 0, null)));
                selectedMarker.setAnchor(0.5f, 1);
            } else {
                selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapUtils.bitmapCanvas(null, 116, 155, false, 0, null)));
                selectedMarker.setAnchor(0.5f, 1);
            }
            selectedMarker = null;
        }

        if (i == 1 && isTracking) {
            isTracking = false;
            trackingButton.setImageTintList(ColorStateList.valueOf(getResources().getColor(android.R.color.darker_gray)));
        }
    }

    /* SETTINGS CHANGE EVENT */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        switch (s) {
            case "update_frequency":
                updateFrequency = Integer.parseInt(sharedPreferences.getString("update_frequency", "5")) * 1000;
                if (ActivityCompat.checkSelfPermission(Application.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(Application.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.removeUpdates(locationListener);
                locationManager.requestLocationUpdates(bestProvider, updateFrequency, 0, locationListener);
                break;
            case "mobile_network":
                mobileNetwork = sharedPreferences.getBoolean("mobile_network", true);
                setTrackingReference();
                break;
            case "nearby_radius":
                nearbyRadius(new LatLng(gpsTracking.getLatitude(), gpsTracking.getLongitude()));
                break;
        }
    }

    /* NETWORK CHANGE EVENTS */
    @Override
    public void networkAvailable() {
        if (snackbar.isShown()) {
            snackbar.dismiss();
        }
    }

    @Override
    public void networkUnavailable() {
        snackbar = Snackbar.make(view.findViewById(R.id.map_coordinator), "No network connection detected", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("DISMISS", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        }).show();
    }

    @Override
    public void networkWifi() {
        syncTrackingRef();
    }

    @Override
    public void networkMobile() {
        if (!mobileNetwork) {
            unsyncTrackingRef();
        }
    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (isTracking) {
                //Will only move the camera if the users current location is in focus
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(standardZoomLevel).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                /*System.out.println(azimuth);*/
            }

            listenerLocation = location;

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

            myLocation.setPosition(latLng);

            /* GET NUMBER OF FRIENDS WITHIN A GIVEN RADIUS */
            nearbyFriends();
            nearbyRadius(latLng);
            accuracyCircle(latLng, location.getAccuracy());
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }
}