package uk.co.appsbystudio.geoshare.maps;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    private PlaceAutocompleteFragment placeAutocompleteFragment;
    private MapFragment mapFragment;
    private Marker selectedLocation;
    private GoogleMap googleMap;

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

        //View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        //BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

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
        }
        googleMap.getUiSettings().setCompassEnabled(false);
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (selectedLocation != null) selectedLocation.remove();
                selectedLocation = googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        });

        /*
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title((String) place.getName()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 16));
            }

            @Override
            public void onError(Status status) { }
        });
        //*/
    }

    private void addMarker(Double longitude, Double latitude) {
        if (this.googleMap != null) {
            this.googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 9));
        }
    }

    private BroadcastReceiver onShowOnMapRequest = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String markerState = intent.getStringExtra("markerState");
            Double longitude = intent.getDoubleExtra("long", 0);
            Double latitude = intent.getDoubleExtra("lat", 0);

            if (markerState.contentEquals("default")) addMarker(longitude, latitude);

            ((MainActivity) getActivity()).showMapFragment();
        }
    };

}