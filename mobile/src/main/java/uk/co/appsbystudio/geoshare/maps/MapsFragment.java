package uk.co.appsbystudio.geoshare.maps;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import uk.co.appsbystudio.geoshare.GPSTracking;
import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.json.GeocodingFromLatLngTask;

public class MapsFragment extends Fragment implements OnMapReadyCallback, LocationListener {

    private MapFragment mapFragment;
    private Marker selectedLocation;
    private GoogleMap googleMap;

    private FloatingActionButton searchShare;

    private ArrayList<Marker> markerArrayList = new ArrayList<Marker>();

    public MapsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* INFLATE LAYOUT */
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        /* HANDLES FOR VARIOUS VIEWS */
        if (mapFragment == null) {
            mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        }

        final RecyclerView searchResults = (RecyclerView) view.findViewById(R.id.searchItems);
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

        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.maps_coordinator);
        searchShare = (FloatingActionButton) view.findViewById(R.id.searchLocationShare);
        searchShare.setTag(1);

        final EditText searchPlaces = (EditText) view.findViewById(R.id.places_search);

        searchPlaces.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 2) {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        //BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        searchShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer integer = (Integer) view.getTag();
                if (integer == 1) {
                    System.out.println("Search");
                    searchPlaces.requestFocus();
                    ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(searchPlaces, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    System.out.println("Share");
                    ((MainActivity) getActivity()).shareALocation();
                }
            }
        });

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(onShowOnMapRequest, new IntentFilter("show.on.map"));

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(new GPSTracking(getContext()).getLatitude(), new GPSTracking(getContext()).getLongitude()), 15));
        }
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (selectedLocation != null) {
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
                    searchShare.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_share_white_48px));
                    searchShare.setTag(2);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (selectedLocation != null) {
                    selectedLocation.remove();
                    searchShare.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_search_white_48px));
                    searchShare.setTag(1);
                }
            }
        });
    }

    private void addMarker(Double longitude, Double latitude) {
        if (this.googleMap != null) {

            if (selectedLocation != null) {
                selectedLocation.remove();
            }

            selectedLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9));
            searchShare.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_share_white_48px));
            searchShare.setTag(2);
        }
    }

    private final BroadcastReceiver onShowOnMapRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String markerState = intent.getStringExtra("markerState");
            Double longitude = intent.getDoubleExtra("long", 0);
            Double latitude = intent.getDoubleExtra("lat", 0);

            if (markerState.contentEquals("default")) addMarker(longitude, latitude);

            ((MainActivity) getActivity()).showMapFragment();
        }
    };

    @Override
    public void onLocationChanged(Location location) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10));
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