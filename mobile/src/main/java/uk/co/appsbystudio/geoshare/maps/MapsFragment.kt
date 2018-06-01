package uk.co.appsbystudio.geoshare.maps

import android.Manifest
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.IntentFilter
import android.content.IntentSender
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.map_layout_main.*

import java.io.File
import java.util.HashMap
import java.util.Locale
import java.util.Objects

import uk.co.appsbystudio.geoshare.Application
import uk.co.appsbystudio.geoshare.GPSTracking
import uk.co.appsbystudio.geoshare.MainActivity
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.Connectivity
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.directions.DirectionsDownloadTask
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.TrackingInfo
import uk.co.appsbystudio.geoshare.utils.json.UrlUtil
import uk.co.appsbystudio.geoshare.utils.services.OnNetworkStateChangeListener
import uk.co.appsbystudio.geoshare.utils.MarkerAnimatorLabelTask

import uk.co.appsbystudio.geoshare.utils.bitmapCanvas

class MapsFragment : Fragment(), MapsView, OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener, SharedPreferences.OnSharedPreferenceChangeListener, OnNetworkStateChangeListener.NetworkStateReceiverListener {

    private var mapsPresenter: MapsPresenter? = null

    private var googleMap: GoogleMap? = null

    private var networkStateChangeListener: OnNetworkStateChangeListener? = null

    private var isTracking: Boolean = false

    private var mobileNetwork: Boolean = false

    private var gpsTracking: GPSTracking? = null

    private var listenerLocation: Location? = null
    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null
    private var locationServiceEnabled: Boolean = false

    private var bestProvider: String? = null
    private var updateFrequency: Int = 0

    private var settingsSharedPreferences: SharedPreferences? = null
    private var showOnMapPreferences: SharedPreferences? = null

    private var myLocation: Marker? = null
    private var selectedMarker: Marker? = null

    private var nearbyCircle: Circle? = null

    private var snackbar: Snackbar? = null

    private val friendMarkerList = HashMap<String?, Marker?>()

    private val standardZoomLevel = 16

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        mapsPresenter = MapsPresenterImpl(this, MapsInteractorImpl())

        var mapFragment: MapFragment? = null
        if (activity != null) {
            mapFragment = activity?.fragmentManager?.findFragmentById(R.id.map) as MapFragment
            mapFragment.getMapAsync(this)
        }

        if (savedInstanceState == null && mapFragment != null) {
            mapFragment.retainInstance = true
        }

        networkStateChangeListener = OnNetworkStateChangeListener()
        networkStateChangeListener?.addListener(this)
        Application.getContext().registerReceiver(networkStateChangeListener, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

        settingsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        settingsSharedPreferences?.registerOnSharedPreferenceChangeListener(this)

        showOnMapPreferences = Application.getContext().getSharedPreferences("showOnMap", Context.MODE_PRIVATE)

        mobileNetwork = settingsSharedPreferences?.getBoolean("mobile_network", true)!!

        view.findViewById<View>(R.id.drawer_open).setOnClickListener {
            //TODO: Callback
            (activity as MainActivity).openDrawer()
        }

        view.findViewById<View>(R.id.friend_drawer).setOnClickListener {
            //TODO: Callback
            (activity as MainActivity).openFriendsDrawer()
        }

        return view
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

    override fun onDestroy() {
        super.onDestroy()
        settingsSharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        networkStateChangeListener?.removeListener(this)
        Application.getContext().unregisterReceiver(networkStateChangeListener)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        /*MapStyleManager styleManager = MapStyleManager.attachToMap(getContext(), googleMap);
        styleManager.addStyle(R.raw.map_style);*/

        setup()

        googleMap.setOnMapClickListener {
            if (selectedMarker != null) {
                mapsPresenter?.moveMapCamera(LatLng(selectedMarker!!.position.latitude, selectedMarker!!.position.longitude), standardZoomLevel, true)
                selectedMarker = null
            }
        }

        googleMap.setOnCameraMoveStartedListener(this)

        googleMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
            if (marker.tag == 0) {
                return@OnMarkerClickListener true
            }

            if (selectedMarker != null) {
                if (selectedMarker!!.tag === marker.tag) {
                    println("Already clicked")
                    return@OnMarkerClickListener true
                }
            }

            selectedMarker = marker

            //TODO: Display location address in bottom panel thingy

            val friendId = marker.tag as String?

            val myLocationLatLng = myLocation!!.position
            val destination = marker.position

            if (directions != null) directions!!.remove()

            if (Connectivity.isConnected(Application.getContext())) {
                if (destination !== myLocationLatLng) {
                    val url = UrlUtil.getDirectionsUrl(myLocationLatLng, destination)
                    val directionsDownloadTask = DirectionsDownloadTask(googleMap)
                    directionsDownloadTask.execute(url)
                }
            }

            mapsPresenter!!.moveMapCamera(destination, 18, true)

            if (isTracking) {
                isTracking = false
                fab_tracking_map.imageTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
            }

            true
        })
    }

    fun setup() {
        val style = MapStyleOptions.loadRawResourceStyle(Application.getContext(), R.raw.map_style)
        this.googleMap?.setMapStyle(style)

        if (ActivityCompat.checkSelfPermission(Application.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Application.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), GET_PERMS)
            return
        }

        setupLocationChangeListener()

        if (!locationServiceEnabled) {
            locationSettingsRequest(Application.getContext())
            return
        }

        isTracking = true

        if (gpsTracking == null) {
            gpsTracking = GPSTracking(context)
        }

        googleMap?.isMyLocationEnabled = false
        googleMap?.isBuildingsEnabled = false
        googleMap?.uiSettings?.isCompassEnabled = false
        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        googleMap?.uiSettings?.isMapToolbarEnabled = false

        /* FIREBASE TRACKING SETUP */
        //setTrackingReference();
        mapsPresenter?.getTrackingFriends()
        mapsPresenter?.setTrackingSync(true)

        /* USING CUSTOM GPS TRACKING MARKER */
        val currentLocation = LatLng(gpsTracking!!.latitude, gpsTracking!!.longitude)

        val myLocationMarker = BitmapFactory.decodeResource(resources, R.drawable.navigation)
        val scaledLocation = Bitmap.createScaledBitmap(myLocationMarker, 72, 72, false)

        mapsPresenter?.moveMapCamera(LatLng(gpsTracking!!.latitude, gpsTracking!!.longitude), standardZoomLevel, false)

        myLocation = googleMap?.addMarker(MarkerOptions()
                .position(currentLocation)
                .flat(true)
                .icon(BitmapDescriptorFactory.fromBitmap(scaledLocation))
                .anchor(0.5f, 0.5f)
        )
        myLocation?.tag = 0

        mapsPresenter?.updateNearbyFriendsRadius(200, currentLocation)

        fab_tracking_map.setOnClickListener {
            if (!isTracking) {
                isTracking = true
                fab_tracking_map.imageTintList = ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))

                if (listenerLocation != null) {
                    mapsPresenter?.moveMapCamera(LatLng(listenerLocation!!.latitude, listenerLocation!!.longitude), standardZoomLevel, true)
                } else {
                    mapsPresenter?.moveMapCamera(LatLng(gpsTracking!!.latitude, gpsTracking!!.longitude), standardZoomLevel, true)
                }

                if (selectedMarker != null) selectedMarker = null
            }
        }

        mapsPresenter?.getStaticFriends()
    }

    private fun locationSettingsRequest(context: Context) {
        val googleApiClient = GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build()
        googleApiClient.connect()

        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = (10000 / 2).toLong()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)

        val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
        result.setResultCallback { locationSettingsResult ->
            val status = locationSettingsResult.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> println("ALL LOCATION SETTINGS ARE SATISFIED")
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                    status.startResolutionForResult(activity, 213)
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> println("STUFF")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationChangeListener() {
        /* SETTING UP LOCATION CHANGE LISTENER */
        locationListener = LocationListener()
        locationManager = Application.getContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationServiceEnabled = locationManager != null && locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        val criteria = Criteria()
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = true
        criteria.isSpeedRequired = false
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isCostAllowed = true

        bestProvider = locationManager!!.getBestProvider(criteria, false)

        updateFrequency = Integer.parseInt(settingsSharedPreferences!!.getString("update_frequency", "5")) * 1000

        locationManager!!.requestLocationUpdates(bestProvider, updateFrequency.toLong(), 0f, locationListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            GET_PERMS -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setup()
            }
        }
    }

    /*private void setTrackingReference() {
        if (mobileNetwork || Connectivity.isConnectedWifi(Application.getContext())) {
            syncTrackingRef();
        } else {
            unsyncTrackingRef();
        }
    }*/

    fun findFriendOnMap(friendId: String) {
        if (friendMarkerList.containsKey(friendId)) {
            val marker = friendMarkerList[friendId]
            val cameraPosition = CameraPosition.Builder().target(marker?.position).zoom(18f).build()
            googleMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

            if (isTracking) {
                isTracking = false
                fab_tracking_map.imageTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
            }
        } else {
            Toast.makeText(activity, "This person has not shared a location with you.", Toast.LENGTH_SHORT).show()
        }
    }
    /* END OF FRIEND MARKER METHODS */

    override fun addFriendMarker(uid: String?, markerPointer: Bitmap?, databaseLocations: DatabaseLocations?) {
        println("Adding marker")
        val friendMarker = googleMap?.addMarker(MarkerOptions()
                .position(LatLng(databaseLocations?.lat!!, databaseLocations.longitude!!))
                .icon(BitmapDescriptorFactory.fromBitmap(markerPointer)))

        friendMarker?.tag = uid

        friendMarkerList[uid] = friendMarker
    }

    override fun updateFriendMarker(uid: String?, databaseLocations: DatabaseLocations?) {
        val friendMarker = friendMarkerList[uid]
        friendMarker?.position = LatLng(databaseLocations!!.lat!!, databaseLocations.longitude!!)

        friendMarkerList[uid] = friendMarker
    }

    override fun removeFriendMarker(uid: String?) {
        val friendMarker = friendMarkerList[uid]

        friendMarker?.remove()

        friendMarkerList.remove(uid)
    }

    /**
     * Check if the friend marker already exists
     *
     * @param uid Friends id
     */
    override fun markerExists(uid: String): Boolean {
        return friendMarkerList.containsKey(uid)
    }

    override fun setMarkerVisibility(uid: String, visible: Boolean) {
        if (friendMarkerList.containsKey(uid)) {
            val marker = friendMarkerList[uid]
            marker?.isVisible = visible
        }
        if (showOnMapPreferences != null) showOnMapPreferences!!.edit().putBoolean(uid, visible).apply()
    }

    override fun setAllMarkersVisibility(visible: Boolean) {
        if (googleMap != null) {
            for (markerId in friendMarkerList.keys) {
                val marker = friendMarkerList[markerId]
                marker?.setVisible(visible)
                showOnMapPreferences!!.edit().putBoolean(markerId, visible).apply()
            }
            showOnMapPreferences!!.edit().putBoolean("all", visible).apply()
        }
    }

    override fun findOnMap() {

    }

    override fun updateCameraPosition(latLng: LatLng, zoomLevel: Int, animated: Boolean) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(zoomLevel.toFloat()).build()
        if (animated) {
            googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } else {
            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    override fun updateNearbyText(nearbyCount: Int) {
        text_nearby_count_map.text = if (nearbyCount != 1) String.format(Locale.getDefault(), "Nearby\n%d Friends", nearbyCount) else String.format(Locale.getDefault(), "Nearby\n%d Friend", nearbyCount)
    }

    override fun updateNearbyRadiusCircle(radius: Int, centerPoint: LatLng) {
        if (nearbyCircle != null) {
            nearbyCircle!!.center = centerPoint
            nearbyCircle!!.radius = radius.toDouble()
        } else {
            nearbyCircle = googleMap!!.addCircle(
                    CircleOptions()
                            .center(centerPoint)
                            .radius(radius.toDouble())
                            .fillColor(resources.getColor(R.color.colorPrimaryTransparent))
                            .strokeWidth(0f))
        }

        mapsPresenter?.updateNearbyFriendsCount(friendMarkerList)
    }

    override fun showError(message: String) {

    }

    override fun onCameraMoveStarted(i: Int) {
        //If the user moves the map view, don't centre myLocation marker when location changes
        if (i == 1 && selectedMarker != null) {
            selectedMarker = null
        }

        if (i == 1 && isTracking) {
            isTracking = false
            fab_tracking_map.imageTintList = ColorStateList.valueOf(resources.getColor(android.R.color.darker_gray))
        }
    }

    /* SETTINGS CHANGE EVENT */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        when (s) {
            "update_frequency" -> {
                updateFrequency = Integer.parseInt(sharedPreferences.getString("update_frequency", "5")) * 1000
                if (ActivityCompat.checkSelfPermission(Application.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Application.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return
                }
                locationManager!!.removeUpdates(locationListener)
                locationManager!!.requestLocationUpdates(bestProvider, updateFrequency.toLong(), 0f, locationListener)
            }
            "mobile_network" -> mobileNetwork = sharedPreferences.getBoolean("mobile_network", true)
            "nearby_radius" -> mapsPresenter!!.updateNearbyFriendsRadius(200, LatLng(gpsTracking!!.latitude, gpsTracking!!.longitude))
        }//setTrackingReference();
    }

    /* NETWORK CHANGE EVENTS */
    override fun networkAvailable() {
        if (snackbar != null && snackbar!!.isShown) {
            snackbar!!.dismiss()
        }
    }

    override fun networkUnavailable() {
        snackbar = Snackbar.make(map_coordinator, "No network connection detected", Snackbar.LENGTH_INDEFINITE)

        snackbar?.setAction("DISMISS") { snackbar?.dismiss() }?.show()
    }

    override fun networkWifi() {
        //syncTrackingRef();
    }

    override fun networkMobile() {
        if (!mobileNetwork) {
            //unsyncTrackingRef();
        }
    }

    private inner class LocationListener : android.location.LocationListener {

        override fun onLocationChanged(location: Location) {
            if (isTracking) {
                //Will only move the camera if the users current location is in focus
                val cameraPosition = CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(standardZoomLevel.toFloat()).build()
                googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            listenerLocation = location

            val latLng = LatLng(location.latitude, location.longitude)

            if (myLocation != null) myLocation!!.position = latLng

            /* GET NUMBER OF FRIENDS WITHIN A GIVEN RADIUS */
            mapsPresenter?.updateNearbyFriendsCount(friendMarkerList)
            mapsPresenter?.updateNearbyFriendsRadius(200, latLng)
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

        }

        override fun onProviderEnabled(s: String) {

        }

        override fun onProviderDisabled(s: String) {

        }
    }

    companion object {

        var directions: Polyline? = null

        private val GET_PERMS = 1
    }
}