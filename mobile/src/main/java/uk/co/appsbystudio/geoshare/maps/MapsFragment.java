package uk.co.appsbystudio.geoshare.maps;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
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
import com.google.android.gms.maps.model.PolylineOptions;
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
import java.util.concurrent.ExecutionException;

import uk.co.appsbystudio.geoshare.Application;
import uk.co.appsbystudio.geoshare.GPSTracking;
import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.json.GeocodingFromLatLngTask;
import uk.co.appsbystudio.geoshare.utils.DatabaseLocations;
import uk.co.appsbystudio.geoshare.utils.DirectionsDownloadTask;
import uk.co.appsbystudio.geoshare.utils.MapStyleManager;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, SharedPreferences.OnSharedPreferenceChangeListener, SensorEventListener {
    private static final String TAG = "MapsFragment";
    private static final boolean LOCAL_LOGV = true;

    private Marker selectedLocation;
    private boolean isTracking;
    private boolean mobileNetwork;
    private GoogleMap googleMap;

    private GPSTracking gpsTracking;

    private DatabaseReference databaseReference;
    private DatabaseReference shareReference;
    private DatabaseReference trackingReference;

    private LocationListener locationListener;
    private LocationManager locationManager;

    private String bestProvider;
    private int updateFrequency;

    private SharedPreferences sharedPreferences;

    private TextView friendsNearText;

    private Marker myLocation;

    private Circle nearbyCircle;
    private Circle accuracyCircle;

    public static Polyline directions;

    private HashMap<String, Marker> friendMarkerList = new HashMap<>();
    //private HashMap<String, Marker> rememberFriendMarker = new HashMap<>();

    private int standardZoomLevel = 16;

    public MapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* INFLATE LAYOUT */
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        /* HANDLES FOR VARIOUS VIEWS */
        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (savedInstanceState == null) {
            mapFragment.setRetainInstance(true);
        }

        if (gpsTracking == null) gpsTracking = new GPSTracking(getContext());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mobileNetwork = sharedPreferences.getBoolean("mobile_network", true);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference();

        shareReference = FirebaseDatabase.getInstance().getReference("current_location/" + user.getUid());
        shareReference.keepSynced(true);

        trackingReference = FirebaseDatabase.getInstance().getReference("current_location/" + user.getUid() + "/tracking");
        if (mobileNetwork) trackingReference.keepSynced(true);

        friendsNearText = view.findViewById(R.id.friendNearText);

        final RecyclerView searchResults = view.findViewById(R.id.searchItems);
        searchResults.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        searchResults.setLayoutManager(layoutManager);

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

        /* SETTING UP LOCATION CHANGE LISTENER */
        locationListener = new LocationListener();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(true);
        criteria.setSpeedRequired(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setCostAllowed(true);

        bestProvider = locationManager.getBestProvider(criteria, false);

        updateFrequency = Integer.parseInt(sharedPreferences.getString("update_frequency", "DEFAULT")) * 1000;

        SensorManager sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;

        googleMap.setPadding(0, (int) (72 * getResources().getDisplayMetrics().density + 0.5f), 0, 0);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        googleMap.setMyLocationEnabled(false);
        googleMap.setBuildingsEnabled(false);

        /* USING CUSTOM GPS TRACKING MARKER */
        LatLng currentLocation = new LatLng(gpsTracking.getLatitude(), gpsTracking.getLongitude());

        Bitmap myLocationMarker = BitmapFactory.decodeResource(getResources(), R.drawable.navigation);
        Bitmap scaledLocation = Bitmap.createScaledBitmap(myLocationMarker, 72, 72, false);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(currentLocation).zoom(standardZoomLevel).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        myLocation = googleMap.addMarker(new MarkerOptions().position(currentLocation).flat(true).icon(BitmapDescriptorFactory.fromBitmap(scaledLocation)).anchor(0.5f, 0.5f));

        locationManager.requestLocationUpdates(bestProvider, updateFrequency, 0, locationListener);

        nearbyRadius(currentLocation);
        accuracyCircle(currentLocation, gpsTracking.getLocation().getAccuracy());

        //TODO: Get bearings

        isTracking = true;

        MapStyleManager styleManager = MapStyleManager.attachToMap(getContext(), googleMap);
        styleManager.addStyle(14, R.raw.map_style);

        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                /*if (selectedLocation != null) {
                    selectedLocation.remove();
                }
                try {
                    if (googleMap.getCameraPosition().zoom > 14) {
                        selectedLocation = googleMap.addMarker(new MarkerOptions().position(latLng).title(new GeocodingFromLatLngTask(getContext(), latLng.latitude, latLng.longitude).execute().get().getAddressLine(0)));
                    } else if (googleMap.getCameraPosition().zoom > 11) {
                        selectedLocation = googleMap.addMarker(new MarkerOptions().position(latLng).title(new GeocodingFromLatLngTask(getContext(), latLng.latitude, latLng.longitude).execute().get().getAddressLine(2)));
                    } else {
                        selectedLocation = googleMap.addMarker(new MarkerOptions().position(latLng).title(new GeocodingFromLatLngTask(getContext(), latLng.latitude, latLng.longitude).execute().get().getAddressLine(1)));
                    }

                    googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }*/
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selectedLocation != null) {
                    selectedLocation.remove();
                }
            }
        });

        googleMap.setOnCameraMoveStartedListener(this);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                LatLng myLocationLatLng = myLocation.getPosition();
                LatLng destination = marker.getPosition();

                if (directions != null) directions.remove();

                if (destination != myLocationLatLng) {
                    String url = getDirectionsUrl(myLocationLatLng, destination);
                    DirectionsDownloadTask directionsDownloadTask = new DirectionsDownloadTask(googleMap);
                    directionsDownloadTask.execute(url);
                }

                CameraPosition cameraPosition = new CameraPosition.Builder().target(destination).zoom(standardZoomLevel).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                marker.showInfoWindow();

                return true;
            }
        });

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);

                TextView markerAddressText = view.findViewById(R.id.marker_address);

                try {
                    Address address = new GeocodingFromLatLngTask(getContext(), marker.getPosition().latitude, marker.getPosition().longitude).execute().get();
                    markerAddressText.setText(address.getFeatureName() + " " + address.getThoroughfare() + "\n" + address.getLocality() + ", " + address.getSubLocality() + ", " + address.getPostalCode());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                return view;
            }
        });

        shareReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (LOCAL_LOGV) Log.v(TAG, "shareReference Child Added");
                if (dataSnapshot.getKey().equals("tracking")) {
                    //Check to see who is streaming location and get location
                    if (LOCAL_LOGV) Log.v(TAG, "shareReference Child is tracking");
                } else if (dataSnapshot.getKey().equals("location")) {
                    if (LOCAL_LOGV) Log.v(TAG, "shareReference Child is location");
                } else {
                    if (LOCAL_LOGV) Log.v(TAG, "shareReference Child is friend id");
                    DatabaseLocations databaseLocations = dataSnapshot.getValue(DatabaseLocations.class);
                    addFriendMarker(dataSnapshot.getKey(), databaseLocations.getLongitude(), databaseLocations.getLat(), false);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (LOCAL_LOGV) Log.v(TAG, "shareReference Child Changed");
                //removeFriendMarker(dataSnapshot.getKey());
                if (dataSnapshot.getKey().equals("tracking")) {
                    if (LOCAL_LOGV) Log.v(TAG, "shareReference Child is tracking updated");
                } else if (dataSnapshot.getKey().equals("location")) {
                    if (LOCAL_LOGV) Log.v(TAG, "shareReference Child is location updated");
                } else {
                    if (LOCAL_LOGV) Log.v(TAG, "shareReference Child is friend id updating");
                    DatabaseLocations databaseLocations = dataSnapshot.getValue(DatabaseLocations.class);
                    updateFriendMarker(dataSnapshot.getKey(), databaseLocations.getLongitude(), databaseLocations.getLat());
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (LOCAL_LOGV) Log.v(TAG, "shareReference Child Removed");
                if (dataSnapshot.getKey().equals("tracking")) {
                    if (LOCAL_LOGV) Log.v(TAG, "shareReference Child is tracking removed");
                } else if (dataSnapshot.getKey().equals("location")) {
                    if (LOCAL_LOGV) Log.v(TAG, "shareReference Child is location removed");
                } else {
                    if (LOCAL_LOGV) Log.v(TAG, "shareReference Child is friend id removed");
                    removeFriendMarker(dataSnapshot.getKey());
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (!mobileNetwork) {
            trackingReference.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (LOCAL_LOGV) Log.v(TAG, "trackingReference Child Added");
                    final String friendId = dataSnapshot.getKey();
                    if (LOCAL_LOGV)
                        Log.v(TAG, "trackingReference Child Added friendId: " + friendId);

                    if (dataSnapshot.child("tracking").getValue(Boolean.class) != null) {
                        if (dataSnapshot.child("tracking").getValue(Boolean.class)) {
                            if (dataSnapshot.child("showOnMap").getValue(Boolean.class) != null) {
                                if (dataSnapshot.child("showOnMap").getValue(Boolean.class)) {
                                    getTrackingFriends(friendId);
                                }
                            } else {
                                getTrackingFriends(friendId);
                            }
                        }
                    }

                    nearbyFriends();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    if (LOCAL_LOGV) Log.v(TAG, "trackingReference Child Updated");
                    final String friendId = dataSnapshot.getKey();
                    if (LOCAL_LOGV)
                        Log.v(TAG, "trackingReference Child Updated friendId: " + friendId);

                    if (dataSnapshot.child("tracking").getValue(Boolean.class) != null && dataSnapshot.child("tracking").getValue(Boolean.class)) {
                        if (dataSnapshot.child("showOnMap").getValue(Boolean.class) != null) {
                            if (dataSnapshot.child("showOnMap").getValue(Boolean.class)) {
                                getTrackingFriends(friendId);
                            } else {
                                if (friendMarkerList.containsKey(friendId))
                                    removeFriendMarker(friendId);
                            }
                        } else {
                            getTrackingFriends(friendId);
                        }
                    } else {
                        if (friendMarkerList.containsKey(friendId)) {
                            removeFriendMarker(friendId);
                        }
                    }

                    nearbyFriends();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if (LOCAL_LOGV) Log.v(TAG, "trackingReference Child Removed");
                    removeFriendMarker(dataSnapshot.getKey());
                    nearbyFriends();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void setTrackingReference() {
        //TODO: Enable and disable tracking sync when on mobile network
        /*
        Remove all friend tracking markers
         */
    }

    private void getTrackingFriends(final String friendId) {
        if (LOCAL_LOGV) Log.v(TAG, "trackingReference getting tracking friends");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DatabaseLocations databaseLocations = dataSnapshot.child("current_location").child(friendId).child("location").getValue(DatabaseLocations.class);
                if (databaseLocations != null) {
                    if (friendMarkerList.containsKey(friendId)) {
                        updateFriendMarker(friendId, databaseLocations.getLongitude(), databaseLocations.getLat());
                    } else {
                        addFriendMarker(friendId, databaseLocations.getLongitude(), databaseLocations.getLat(), false);
                    }

                    nearbyFriends();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /* FRIEND MARKER METHODS */
    private void addFriendMarker(final String friendId, final Double longitude, final Double latitude, final Boolean isUpdating) {
        if (this.googleMap != null) {
            if (friendId != null) {
                File fileCheck = new File(MainActivity.cacheDir + "/" + friendId + ".png");
                if (fileCheck.exists()) {
                    Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + friendId + ".png");
                    Marker friendMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromBitmap(bitmapCanvas(imageBitmap))));
                    // if (!isUpdating) dropPinEffect(friendMarker);
                    friendMarker.setTag(friendId);
                    friendMarkerList.put(friendId, friendMarker);
                } else {
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                    StorageReference profileRef = storageReference.child("profile_pictures/" + friendId + ".png");
                    profileRef.getFile(Uri.fromFile(new File(MainActivity.cacheDir + "/" + friendId + ".png")))
                            .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                    Bitmap imageBitmap = BitmapFactory.decodeFile(MainActivity.cacheDir + "/" + friendId + ".png");
                                    Marker friendMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromBitmap(bitmapCanvas(imageBitmap))));
                                    // if (!isUpdating) dropPinEffect(friendMarker);
                                    friendMarker.setTag(friendId);
                                    friendMarkerList.put(friendId, friendMarker);
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Marker friendMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).icon(BitmapDescriptorFactory.fromBitmap(bitmapCanvas(null))));
                                    // if (!isUpdating) dropPinEffect(friendMarker);
                                    friendMarker.setTag(friendId);
                                    friendMarkerList.put(friendId, friendMarker);
                                }
                            });
                }
            }
        }
    }

    private void updateFriendMarker(String friendId, final Double longitude, final Double latitude) {
        Marker friendMarker = friendMarkerList.get(friendId);
        friendMarker.setPosition(new LatLng(latitude, longitude));
    }

    private void dropPinEffect(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    handler.postDelayed(this, 15);
                } else {
                    marker.showInfoWindow();

                }
            }
        });
    }

    private Bitmap bitmapCanvas(Bitmap profileImage) {
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(116, 155, config);
        Canvas canvas = new Canvas(bmp);

        if (isAdded()) {
            Bitmap mapMarker = BitmapFactory.decodeResource(getResources(), R.drawable.map_marker_point_shadow);
            Bitmap scaledMarker = Bitmap.createScaledBitmap(mapMarker, 116, 155, false);

            canvas.drawBitmap(scaledMarker, 0, 0, null);
            if (profileImage != null) canvas.drawBitmap(getCroppedBitmap(profileImage), 16, 16, null);
        }

        return bmp;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return Bitmap.createScaledBitmap(output, 84, 84, false);
    }

    private void removeFriendMarker(String friendId) {
        Marker friendMarker = friendMarkerList.get(friendId);
        if (friendMarker != null) friendMarker.remove();
        friendMarkerList.remove(friendId);
    }

    private void removeAllFriendMarkers() {
        //TODO: Remove all friend markers
        for (String markerId : friendMarkerList.keySet()) {
            Marker marker = friendMarkerList.get(markerId);
            marker.remove();
        }
    }
    /* END OF FRIEND MARKER METHODS */


    /* DIRECTIONS METHODS */
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String sOrigin = "origin=" + origin.latitude + "," + origin.longitude;
        String sDest = "destination=" + dest.latitude + "," + dest.longitude;

        String params = sOrigin + "&" + sDest + "&sensor=false";

        return "https://maps.googleapis.com/maps/api/directions/json?" + params + "&key=" + getString(R.string.server_key);
    }

    //Add a standard marker to the map
    private void addMarker(Double longitude, Double latitude) {
        if (this.googleMap != null) {

            if (selectedLocation != null) {
                selectedLocation.remove();
            }

            selectedLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9));
        }
    }

    /* NEARBY METHODS */

    //GET NEARBY FRIENDS IN A GIVEN RADIUS
    private void nearbyFriends() {
        int count = 0;

        int radius = Integer.parseInt(sharedPreferences.getString("nearby_radius", "DEFAULT"));

        for (String markerId : friendMarkerList.keySet()) {
            LatLng markerLocation = friendMarkerList.get(markerId).getPosition();
            Location tempLocation = new Location(LocationManager.GPS_PROVIDER);

            tempLocation.setLatitude(markerLocation.latitude);
            tempLocation.setLongitude(markerLocation.longitude);

            //TODO: Shared preferences
            if (gpsTracking.getLocation().distanceTo(tempLocation) < radius) {
                count = count + 1;
            }
        }

        friendsNearText.setText("Nearby\n" + count + " Friends");
    }

    private void nearbyRadius(LatLng latLng) {
        if (nearbyCircle != null) nearbyCircle.remove();

        int radius = Integer.parseInt(sharedPreferences.getString("nearby_radius", "DEFAULT"));

        CircleOptions nearbyCircleOptions = new CircleOptions().center(latLng).radius(radius).fillColor(Application.getAppContext().getResources().getColor(R.color.colorPrimaryTransparent)).strokeWidth(0);

        nearbyCircle = googleMap.addCircle(nearbyCircleOptions);
    }

    /* END OF NEARBY METHODS */

    private void accuracyCircle(LatLng latLng, float accuracy) {

        if (accuracyCircle != null) {
            accuracyCircle.setCenter(latLng);
        } else {
            CircleOptions accuracyCircleOptions = new CircleOptions().center(latLng).radius(accuracy).fillColor(Application.getAppContext().getResources().getColor(R.color.colorPrimaryDarkerTransparent)).strokeWidth(0);
            accuracyCircle = googleMap.addCircle(accuracyCircleOptions);
        }
    }

    @Override
    public void onCameraMoveStarted(int i) {
        //If the user moves the map view, don't centre myLocation marker when location changes
        if (i == 1) isTracking = false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if (s.equals("update_frequency")) {
            updateFrequency = Integer.parseInt(sharedPreferences.getString("update_frequency", "DEFAULT")) * 1000;
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.removeUpdates(locationListener);
            locationManager.requestLocationUpdates(bestProvider, updateFrequency, 0, locationListener);
        } else if (s.equals("mobile_network")) {
            mobileNetwork = sharedPreferences.getBoolean("mobile_network", true);
        }
    }

    float[] inR = new float[16];
    float[] I = new float[16];
    float[] gravity = new float[3];
    float[] magneticField = new float[3];
    float[] orientVals = new float[3];

    double azimuth = 0;
    double pitch = 0;
    double roll = 0;

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) return;

        switch (sensorEvent.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                gravity = sensorEvent.values.clone();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticField = sensorEvent.values.clone();
                break;
        }

        if (gravity != null && magneticField != null) {
            boolean success = SensorManager.getRotationMatrix(inR, I, gravity, magneticField);

            GeomagneticField geoField = new GeomagneticField(
                    (float) gpsTracking.getLatitude(),
                    (float) gpsTracking.getLongitude(),
                    (float) gpsTracking.getLocation().getAltitude(),
                    System.currentTimeMillis()
            );

            if (success) {
                SensorManager.getOrientation(inR, orientVals);
                azimuth = Math.toDegrees(orientVals[0]);
                pitch = Math.toDegrees(orientVals[1]);
                roll = Math.toDegrees(orientVals[2]);

                azimuth -= geoField.getDeclination();

                if (myLocation != null) {
                    myLocation.setRotation((float) azimuth);
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private class LocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            if (isTracking) {
                //Will only move the camera if the users current location is in focus
                CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(standardZoomLevel).build();
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                System.out.println(azimuth);
            }

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