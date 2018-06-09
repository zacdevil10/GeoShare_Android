package uk.co.appsbystudio.geoshare.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.*
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
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.res.ResourcesCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.bottom_sheet_map.*
import kotlinx.android.synthetic.main.fragment_maps.*
import kotlinx.android.synthetic.main.map_layout_main.*
import uk.co.appsbystudio.geoshare.GPSTracking
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.base.MainView
import uk.co.appsbystudio.geoshare.utils.SettingsPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import java.util.*
import kotlin.collections.HashMap

class MapsFragment : Fragment(), MapsView, OnMapReadyCallback {

    private var fragmentCallback: MainView? = null

    private var savedInstance: Boolean = false

    private var mapsPresenter: MapsPresenter? = null
    private var settingsHelper: SettingsPreferencesHelper? = null
    private var showMarkerHelper: ShowMarkerPreferencesHelper? = null

    private var googleMap: GoogleMap? = null

    private var syncState: Boolean = false
    private var isTracking: Boolean = false

    private var gpsTracking: GPSTracking? = null

    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null
    private var locationServiceEnabled: Boolean = false

    private var bestProvider: String? = null

    private var settingsSharedPreferences: SharedPreferences? = null

    private var myLocation: Marker? = null
    private var selectedMarker: Marker? = null

    private var nearbyCircle: Circle? = null

    private var snackbar: Snackbar? = null

    private var bottomSheetBehavior: BottomSheetBehavior<View>? = null

    private var friendMarkerList = HashMap<String?, Marker?>()
    private var friendMarkerTimestamp = HashMap<String?, Long?>()

    private var storageDirectory: String? = null

    companion object {
        private const val DEFAULT_ZOOM = 16
        private const val GET_PERMS = 1
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        storageDirectory = context?.cacheDir.toString()
        try {
            fragmentCallback = context as MainActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement AuthView")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        settingsSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        settingsHelper = SettingsPreferencesHelper(settingsSharedPreferences)
        showMarkerHelper = ShowMarkerPreferencesHelper(context?.getSharedPreferences("showOnMap", Context.MODE_PRIVATE))
        mapsPresenter = MapsPresenterImpl(this, settingsHelper, MapsHelperImpl(settingsSharedPreferences), MapsInteractorImpl())

        gpsTracking = GPSTracking(context)

        var mapFragment: MapFragment? = null

        if (activity != null) {
            mapFragment = activity?.fragmentManager?.findFragmentById(R.id.map) as MapFragment
            mapFragment.getMapAsync(this)
        }

        mapFragment?.retainInstance = true

        savedInstance = savedInstanceState != null

        mapsPresenter?.registerNetworkReceiver()
        mapsPresenter?.registerSettingsPreferencesListener()

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

        if (savedInstanceState != null) {
            if (selectedMarker != null && myLocation != null) {
                mapsPresenter?.updateBottomSheet(savedInstanceState.getString("selected_marker_uid"), myLocation!!.position, selectedMarker!!.position, friendMarkerTimestamp[selectedMarker!!.tag])
            }
        }

        mapsPresenter?.updateBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)

        fab_tracking_map?.setOnClickListener {
            if (!isTracking) {
                mapsPresenter?.updateTrackingState(true)

                if (myLocation != null) {
                    mapsPresenter?.moveMapCamera(LatLng(myLocation!!.position.latitude, myLocation!!.position.longitude), DEFAULT_ZOOM, true)
                }
            }
            selectedMarker = null
            mapsPresenter?.updateBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    override fun onResume() {
        super.onResume()
        mapsPresenter?.updateTrackingState(isTracking)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        setup()

        googleMap.setOnMapClickListener {
            if (selectedMarker != null) {
                mapsPresenter?.moveMapCamera(LatLng(selectedMarker!!.position.latitude, selectedMarker!!.position.longitude), DEFAULT_ZOOM, true)
                selectedMarker = null
            }
            mapsPresenter?.updateBottomSheetState(BottomSheetBehavior.STATE_HIDDEN)
        }

        googleMap.setOnCameraMoveStartedListener({
            //if (it == 1 && selectedMarker != null) selectedMarker = null

            if (it == 1 && isTracking) mapsPresenter?.updateTrackingState(false)
        })

        googleMap.setOnMarkerClickListener(GoogleMap.OnMarkerClickListener { marker ->
            if (marker.tag == 0) return@OnMarkerClickListener true

            if (selectedMarker != null) {
                if (selectedMarker!!.tag === marker.tag) {
                    //Marker already selected
                    mapsPresenter?.updateBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
                    return@OnMarkerClickListener true
                }
            }

            selectedMarker = marker

            mapsPresenter?.updateBottomSheetState(BottomSheetBehavior.STATE_EXPANDED)
            mapsPresenter?.updateBottomSheet(selectedMarker?.tag as String, myLocation!!.position, selectedMarker!!.position, friendMarkerTimestamp[selectedMarker!!.tag])

            mapsPresenter?.moveMapCamera(marker.position, 18, true)

            if (isTracking) mapsPresenter?.updateTrackingState(false)

            true
        })
    }

    fun setup() {
        mapsPresenter?.updateMapStyle(false)

        if (activity != null) {
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), GET_PERMS)
                return
            }
        }

        setupLocationChangeListener()

        if (!locationServiceEnabled) {
            locationSettingsRequest(context!!)
            return
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

        if (!savedInstance) mapsPresenter?.run {
            getStaticFriends()
            getTrackingFriends(storageDirectory)
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

        mapsPresenter?.updateNearbyFriendsRadius(currentLocation)
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
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationServiceEnabled = locationManager != null && locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

        val criteria = Criteria()
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false
        criteria.isSpeedRequired = false
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isCostAllowed = true

        bestProvider = locationManager!!.getBestProvider(criteria, false)

        locationManager?.requestLocationUpdates(bestProvider, 0, 0f, locationListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            GET_PERMS -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setup()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selected_marker_uid", selectedMarker?.tag.toString())
    }

    override fun onDestroy() {
        super.onDestroy()
        mapsPresenter?.run {
            unregisterSettingsPreferencesListener()
            unregisterNetworkReceiver()
        }
        locationManager?.removeUpdates(locationListener)
        savedInstance = false
    }

    fun findFriendOnMap(friendId: String) {
        if (friendMarkerList.containsKey(friendId)) {
            val marker = friendMarkerList[friendId]
            if (marker != null) mapsPresenter?.moveMapCamera(marker.position, 18, true)

            if (isTracking) mapsPresenter?.updateTrackingState(false)
        } else {
            mapsPresenter?.setError("This person has not shared a location with you.")
        }
    }

    override fun addFriendMarker(uid: String?, markerPointer: Bitmap?, databaseLocations: DatabaseLocations?) {
        if (databaseLocations != null) {
            val markerOptions = MarkerOptions()
                    .position(LatLng(databaseLocations.lat, databaseLocations.longitude))
                    .icon(BitmapDescriptorFactory.fromBitmap(markerPointer))
            val friendMarker = googleMap?.addMarker(markerOptions)
            if (showMarkerHelper?.getMarkerVisibilityState(uid) != null) friendMarker?.isVisible = showMarkerHelper!!.getMarkerVisibilityState(uid)!!

            friendMarker?.tag = uid

            friendMarkerList[uid] = friendMarker
            friendMarkerTimestamp[uid] = databaseLocations.timestamp

            if (myLocation != null) mapsPresenter?.updateNearbyFriendsRadius(LatLng(myLocation!!.position.latitude, myLocation!!.position.longitude))
        }
    }

    override fun updateFriendMarker(uid: String?, databaseLocations: DatabaseLocations?) {
        val friendMarker = friendMarkerList[uid]
        if (databaseLocations != null) friendMarker?.position = LatLng(databaseLocations.lat, databaseLocations.longitude)

        friendMarkerList[uid] = friendMarker
    }

    override fun removeFriendMarker(uid: String?) {
        val friendMarker = friendMarkerList[uid]

        friendMarker?.remove()

        friendMarkerList.remove(uid)
    }

    override fun updateLocationMarkerIndicator(location: LatLng) {
        if (myLocation == null) {
            val myLocationMarker = BitmapFactory.decodeResource(resources, R.drawable.navigation)
            val scaledLocation = Bitmap.createScaledBitmap(myLocationMarker, 72, 72, false)

            myLocation = googleMap?.addMarker(MarkerOptions()
                    .position(location)
                    .flat(true)
                    .icon(BitmapDescriptorFactory.fromBitmap(scaledLocation))
                    .anchor(0.5f, 0.5f))
            myLocation?.tag = 0
        }
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
        showMarkerHelper?.setMarkerVisibilityState(uid, visible)
    }

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

    override fun setBottomSheetState(state: Int) {
        bottomSheetBehavior?.state = state
    }

    override fun setMapStyle(style: Int) {
        googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, style))
    }

    override fun updateTrackingButton(trackingState: Boolean) {
        isTracking = trackingState

        fab_tracking_map?.imageTintList = if (!trackingState) {
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, android.R.color.darker_gray, null))
        } else {
            ColorStateList.valueOf(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
        }
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
        text_nearby_count_map?.text = if (nearbyCount != 1) String.format(Locale.getDefault(), "%d FRIENDS AROUND YOU", nearbyCount) else String.format(Locale.getDefault(), "%d FRIEND AROUND YOU", nearbyCount)
    }

    override fun updateBottomSheetText(name: String?, address: String, timestamp: String?, distance: String) {
        text_name_map.text = name
        text_address_map.text = address
        text_timestamp_map.text = timestamp
        text_distance_map.text = distance
    }

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
        mapsPresenter?.updateNearbyFriendsCount(centerPoint, friendMarkerList)
    }

    override fun updateRadiusCircleSize(radius: Int?) {
        if (radius != null) {
            nearbyCircle?.radius = radius.toDouble()
            if (myLocation != null) mapsPresenter?.updateNearbyFriendsCount(myLocation!!.position, friendMarkerList)
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

    override fun networkAvailable() {
        val snack = snackbar
        if (snack != null) {
            if (snack.isShown) {
                snackbar?.dismiss()
            }
        }
    }

    override fun networkError(message: String) {
        snackbar = Snackbar.make(coordinator_map, "No network connection detected", Snackbar.LENGTH_INDEFINITE)
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
            mapsPresenter?.run {
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