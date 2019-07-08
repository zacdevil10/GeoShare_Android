package uk.co.appsbystudio.geoshare.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.bottom_sheet_map.*
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.map_layout_main.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.base.MainView
import uk.co.appsbystudio.geoshare.utils.GPSTracking
import uk.co.appsbystudio.geoshare.utils.SettingsPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.listeners.GetUserFromDatabase
import java.util.*
import kotlin.collections.HashMap

class MapsFragment : Fragment(), MapsView, OnMapReadyCallback {

    private var fragmentCallback: MainView? = null

    private var savedInstance: Boolean = false

    private var presenter: MapsPresenter? = null
    private var settingsHelper: SettingsPreferencesHelper? = null
    private var showMarkerHelper: ShowMarkerPreferencesHelper? = null

    private var googleMap: GoogleMap? = null

    private var syncState: Boolean = false
    private var isTracking: Boolean = false

    private var gpsTracking: GPSTracking? = null

    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null
    private var locationServiceEnabled: Boolean = false

    private var myLocation: Marker? = null
    private var selectedMarker: Marker? = null

    private var nearbyCircle: Circle? = null

    private var snackbar: Snackbar? = null

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    private var friendMarkerList = HashMap<String?, Marker?>()
    private var friendMarkerTimestamp = HashMap<String?, Long?>()

    companion object {
        private const val DEFAULT_ZOOM = 16
        private const val GET_PERMS = 1
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            fragmentCallback = context as MainActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement AuthView")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        val settingsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        settingsHelper = SettingsPreferencesHelper(settingsSharedPreferences)
        showMarkerHelper = ShowMarkerPreferencesHelper(context?.getSharedPreferences("showOnMap", Context.MODE_PRIVATE))
        presenter = MapsPresenterImpl(this, settingsHelper, MapsHelperImpl(settingsSharedPreferences), MapsInteractorImpl())

        gpsTracking = GPSTracking(context)

        var mapFragment: MapFragment? = null

        if (activity != null) {
            mapFragment = activity?.fragmentManager?.findFragmentById(R.id.map) as MapFragment
            mapFragment.getMapAsync(this)
        }

        mapFragment?.retainInstance = true

        savedInstance = savedInstanceState != null

        presenter?.registerNetworkReceiver()
        presenter?.registerSettingsPreferencesListener()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        retainInstance = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        image_drawer_map?.setOnClickListener {
            fragmentCallback?.openNavDrawer()
        }

        image_friend_drawer_map?.setOnClickListener {
            fragmentCallback?.openFriendsNavDrawer()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(constraint_bottom_sheet)
        bottomSheetBehavior?.isHideable = true

        presenter?.updateBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)

        fab_tracking_map?.setOnClickListener {
            if (!isTracking) {
                presenter?.updateTrackingState(true)

                if (myLocation != null) {
                    presenter?.moveMapCamera(LatLng(myLocation!!.position.latitude, myLocation!!.position.longitude), DEFAULT_ZOOM, true)
                }
            }
            selectedMarker = null
            presenter?.updateBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter?.updateTrackingState(isTracking)

        if (selectedMarker != null && myLocation != null) {
            presenter?.updateBottomSheet(selectedMarker!!.tag.toString(), myLocation!!.position, selectedMarker!!.position, friendMarkerTimestamp[selectedMarker!!.tag])
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        setup()

        googleMap.setOnMapClickListener {
            if (selectedMarker != null) {
                presenter?.moveMapCamera(LatLng(selectedMarker!!.position.latitude, selectedMarker!!.position.longitude), DEFAULT_ZOOM, true)
                selectedMarker = null
            }
            presenter?.updateBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
        }

        googleMap.setOnCameraMoveStartedListener {
            //if (it == 1 && selectedMarker != null) selectedMarker = null

            if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE && isTracking) presenter?.updateTrackingState(false)
        }

        googleMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
            if (marker.tag == 0) return@OnMarkerClickListener true

            if (selectedMarker != null) {
                if (selectedMarker!!.tag === marker.tag) {
                    //Marker already selected
                    presenter?.updateBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
                    return@OnMarkerClickListener true
                }
            }

            selectedMarker = marker

            presenter?.updateBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
            presenter?.updateBottomSheet(selectedMarker?.tag as String, myLocation?.position!!, selectedMarker?.position!!, friendMarkerTimestamp[selectedMarker?.tag as String])

            presenter?.moveMapCamera(marker.position, 18, true)

            if (isTracking) presenter?.updateTrackingState(false)

            true
        })
    }

    fun setup() {
        presenter?.updateMapStyle()

        if (activity != null) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), GET_PERMS)
                return
            }
        }

        setupLocationChangeListener()

        if (!locationServiceEnabled) {
            locationSettingsRequest(context)
        }

        googleMap?.apply {
            isMyLocationEnabled = false
            isBuildingsEnabled = false
            uiSettings?.isCompassEnabled = false
            uiSettings?.isMapToolbarEnabled = false
        }

        /* FIREBASE LOCATION TRACKING SETUP */
        var currentLocation = gpsTracking?.latLng

        if (currentLocation == null) {
            currentLocation = LatLng(51.512037, -0.092165)
        }

        if (!savedInstance) presenter?.run {
            getStaticFriends(context?.cacheDir.toString())
            getTrackingFriends(context?.cacheDir.toString())
            syncState = setTrackingSync(true)
            moveMapCamera(currentLocation, DEFAULT_ZOOM, false)
            updateTrackingState(true)
        }

        val myLocationMarker = BitmapFactory.decodeResource(resources, R.drawable.navigation)
        val scaledLocation = Bitmap.createScaledBitmap(myLocationMarker, 72, 72, false)

        if (myLocation == null) {
            myLocation = googleMap?.addMarker(MarkerOptions()
                    .position(currentLocation)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(scaledLocation))
                    .anchor(0.5f, 0.5f))
            myLocation?.tag = 0
        }

        presenter?.updateNearbyFriendsRadius(currentLocation)
    }

    /**
     * Check to see if location services are enabled
     */
    private fun locationSettingsRequest(context: Context?) {
        if (context != null) {
            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = 10000
            locationRequest.fastestInterval = (10000 / 2).toLong()

            val locationSettingsRequest = LocationSettingsRequest.Builder()
            locationSettingsRequest.addLocationRequest(locationRequest)

            val results: Task<LocationSettingsResponse> = LocationServices.getSettingsClient(context).checkLocationSettings(locationSettingsRequest.build())

            results.addOnCompleteListener {
                try {
                    val response = it.getResult(ApiException::class.java)
                    println(response)
                } catch (exception: ApiException) {
                    when (exception.statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            try {
                                val resolvableApiException: ResolvableApiException = exception as ResolvableApiException
                                resolvableApiException.startResolutionForResult(activity, 213)
                            } catch (e: IntentSender.SendIntentException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationChangeListener() {
        /* SETTING UP LOCATION CHANGE LISTENER */
        locationListener = LocationListener()
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationServiceEnabled = locationManager != null && locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        val criteria = Criteria()
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isSpeedRequired = false
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isCostAllowed = true

        val bestProvider = locationManager!!.getBestProvider(criteria, false)

        locationManager?.requestLocationUpdates(bestProvider, 0, 0f, locationListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            GET_PERMS -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setup()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.run {
            unregisterSettingsPreferencesListener()
            unregisterNetworkReceiver()
            stop()
        }
        locationManager?.removeUpdates(locationListener)
        savedInstance = false
    }

    /**
     * Find the marker on the map
     *
     * @param friendId is the uid of the friend and the tag of the marker
     */
    fun findFriendOnMap(friendId: String) {
        if (friendMarkerList.containsKey(friendId)) {
            val marker = friendMarkerList[friendId]
            if (marker != null) presenter?.moveMapCamera(marker.position, 18, true)

            if (isTracking) presenter?.updateTrackingState(false)
        } else {
            presenter?.setError("This person has not shared a location with you.")
        }
    }

    /**
     * Add a new marker to the map
     *
     * @param uid is the uid of the friend
     * @param markerPointer is the custom marker image
     * @param databaseLocations contains the coordinates and timestamp of the location
     */
    override fun addFriendMarker(uid: String?, markerPointer: Bitmap?, databaseLocations: DatabaseLocations?) {
        if (databaseLocations != null) {
            val markerOptions = MarkerOptions()
                    .position(LatLng(databaseLocations.lat, databaseLocations.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(markerPointer))
            val friendMarker = googleMap?.addMarker(markerOptions)
            if (showMarkerHelper?.getMarkerVisibilityState(uid) != null) friendMarker?.isVisible = showMarkerHelper?.getMarkerVisibilityState(uid)!!

            friendMarker?.tag = uid

            friendMarkerList[uid] = friendMarker
            friendMarkerTimestamp[uid] = databaseLocations.timestamp

            if (myLocation != null) presenter?.updateNearbyFriendsRadius(LatLng(myLocation!!.position.latitude, myLocation!!.position.longitude))
        }
    }

    /**
     * Update an existing marker
     *
     * @param uid is the uid of the friend
     * @param databaseLocations is the new location and timestamp for the marker
     */
    override fun updateFriendMarker(uid: String?, databaseLocations: DatabaseLocations?) {
        val friendMarker = friendMarkerList[uid]
        if (databaseLocations != null) friendMarker?.position = LatLng(databaseLocations.lat, databaseLocations.longitude)

        friendMarkerList[uid] = friendMarker
        friendMarkerTimestamp[uid] = databaseLocations?.timestamp

        if (selectedMarker != null && selectedMarker?.tag == uid) {
            presenter?.updateBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
            presenter?.updateBottomSheet(uid!!, myLocation?.position!!, friendMarker?.position!!, friendMarkerTimestamp[uid])

            if (databaseLocations != null) presenter?.moveMapCamera(LatLng(databaseLocations.lat, databaseLocations.longitude), 18, true)
        }
    }

    /**
     * Remove a marker from the map
     *
     * @param uid is the uid of the friend
     */
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

    /**
     * Set the visibility of individual markers
     *
     * @param uid is the uid of the friend
     * @param visible is the visibility state to change the marker to
     */
    override fun setMarkerVisibility(uid: String, visible: Boolean) {
        if (friendMarkerList.containsKey(uid)) {
            val marker = friendMarkerList[uid]
            marker?.isVisible = visible
        }
        showMarkerHelper?.setMarkerVisibilityState(uid, visible)
    }

    /**
     * Set the visibility of all markers
     *
     * @param visible is the visibility state to change the markers to
     * */
    override fun setAllMarkersVisibility(visible: Boolean) {
        if (googleMap != null) {
            for (markerId in friendMarkerList.keys) {
                val marker = friendMarkerList[markerId]
                marker?.isVisible = visible
                showMarkerHelper?.setMarkerVisibilityState(markerId, visible)
            }
            showMarkerHelper?.setAllMarkersVisibilityState(visible)
        }
    }

    /**
     * Set the state of the bottom sheet
     *
     * @param state is the state of the bottom sheet (visible, hidden etc)
     * */
    override fun setBottomSheetState(state: Int) {
        bottomSheetBehavior?.state = state

        if (state == BottomSheetBehavior.STATE_HIDDEN) {
            progress_map.visibility = View.VISIBLE
            text_name_map.text = ""
            text_address_map.text = ""
            text_timestamp_map.text = ""
            text_distance_map.text = ""
        }
    }

    /**
     * Change the map style
     *
     * @param style is the resource location for the json style files
     */
    override fun setMapStyle(style: Int) {
        googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, style))
    }

    /**
     * Sets the tracking state for the myLocation marker
     *
     * @param trackingState determines whether or not the map should be tracking
     */
    override fun updateTrackingButton(trackingState: Boolean) {
        isTracking = trackingState

        fab_tracking_map?.imageTintList = if (!trackingState) {
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, android.R.color.darker_gray, null))
        } else {
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
        }
    }

    /**
     * Updates the maps camera position
     *
     * @param latLng is the coordinates of the centre point of the camera
     * @param zoomLevel is the zoom level of the camera
     * @param animated is true if the camera moves to the new position and false if it jumps to the new position
     */
    override fun updateCameraPosition(latLng: LatLng, zoomLevel: Int, animated: Boolean) {
        val cameraPosition = CameraPosition.Builder().target(latLng).zoom(zoomLevel.toFloat()).build()
        if (animated) {
            googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } else {
            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }

    /**
     * Sets the nearby text at the top of the fragment
     *
     * @param nearbyCount is the number of friends within the specified radius
     */
    override fun updateNearbyText(nearbyCount: Int) {
        text_nearby_count_map?.text = if (nearbyCount != 1) String.format(Locale.getDefault(), "%d FRIENDS AROUND YOU", nearbyCount) else String.format(Locale.getDefault(), "%d FRIEND AROUND YOU", nearbyCount)
    }

    /**
     * Updates the bottom sheet text when a marker is selected
     *
     * @param uid is the uid of the friend
     * @param address is the address given by the reverse geocoder
     * @param timestamp is the time elapsed since the location was shared
     * @param distance is the distance between the friends marker and myLocation marker
     */
    override fun updateBottomSheetText(uid: String?, address: LiveData<String>, timestamp: String?, distance: String) {
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.USERS}/$uid").addListenerForSingleValueEvent(GetUserFromDatabase(text_name_map))
        text_timestamp_map.text = timestamp
        text_distance_map.text = distance

        val observer = Observer<String> { t ->
            text_address_map.text = t
            progress_map.visibility = View.GONE
        }

        address.observe(this, observer)
    }

    /**
     * If the myLocation marker moves, this function is called to move the circle
     *
     * @param radius is the size of the circle in meters
     * @param centerPoint is the coordinates of the centre point
     */
    override fun updateNearbyRadiusCircle(radius: Int?, centerPoint: LatLng) {
        if (nearbyCircle != null) {
            nearbyCircle?.center = centerPoint
            nearbyCircle?.radius = radius?.toDouble()!!
        } else if (nearbyCircle == null && !savedInstance) {
            nearbyCircle = googleMap?.addCircle(
                    CircleOptions()
                            .center(centerPoint)
                            .radius(radius?.toDouble()!!)
                            .fillColor(ResourcesCompat.getColor(resources, R.color.colorPrimaryTransparent, null))
                            .strokeWidth(5f)
                            .strokeColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            )
        }
        presenter?.updateNearbyFriendsCount(centerPoint, friendMarkerList)
    }

    /**
     * Changes the radius of the circle
     *
     * @param radius is the radius of the circle in meters
     */
    override fun updateRadiusCircleSize(radius: Int?) {
        if (radius != null) {
            nearbyCircle?.radius = radius.toDouble()
            if (myLocation != null) presenter?.updateNearbyFriendsCount(myLocation!!.position, friendMarkerList)
        }
    }

    override fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun registerNetworkReceiver(broadcastReceiver: BroadcastReceiver) {
        context?.registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun unregisterNetworkReceiver(broadcastReceiver: BroadcastReceiver) {
        context?.unregisterReceiver(broadcastReceiver)
    }

    /**
     * When a network connection is detected, the snackbar is dismissed
     */
    override fun networkAvailable() {
        val snack = snackbar
        if (snack != null) {
            if (snack.isShown) {
                snackbar?.dismiss()
            }
        }
    }

    /**
     * When no network is detected, display a snackbar
     *
     * @param message Is the error message
     */
    override fun networkError(message: String) {
        snackbar = Snackbar.make(coordinator_map, message, Snackbar.LENGTH_INDEFINITE)
        snackbar?.setAction("DISMISS") { snackbar?.dismiss() }?.show()
    }

    private inner class LocationListener : android.location.LocationListener {

        override fun onLocationChanged(location: Location) {
            if (isTracking) {
                //Will only move the camera if the users current location is in focus
                val cameraPosition = CameraPosition.Builder().target(LatLng(location.latitude, location.longitude)).zoom(DEFAULT_ZOOM.toFloat()).build()
                googleMap?.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            val latLng = LatLng(location.latitude, location.longitude)

            if (myLocation != null) myLocation?.position = latLng

            /* GET NUMBER OF FRIENDS WITHIN A GIVEN RADIUS */
            presenter?.run {
                updateNearbyFriendsCount(latLng, friendMarkerList)
                updateNearbyFriendsRadius(latLng)
            }
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

        }

        override fun onProviderEnabled(s: String) {

        }

        override fun onProviderDisabled(s: String) {

        }
    }
}