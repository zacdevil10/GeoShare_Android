package uk.co.appsbystudio.geoshare.utils.services

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.firebase.DatabaseLocations
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.ui.notifications.TrackingServiceNotification

class TrackingService : Service(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var locationListener: LocationListener? = null
    private var locationManager: LocationManager? = null
    private var bestProvider: String? = null

    private lateinit var sharedPreferences: SharedPreferences

    private var user: FirebaseUser? = null

    private var hasTrue = false

    private var receiver: Intent? = null
    private var stopServiceIntent: PendingIntent? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isRunning = true
        return Service.START_STICKY
    }

    override fun onCreate() {
        super.onCreate()

        user = FirebaseAuth.getInstance().currentUser

        isRunning = false

        TrackingServiceNotification.notify(this, 1)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        receiver = Intent(this, StopTrackingService::class.java)
        stopServiceIntent = PendingIntent.getBroadcast(this, 1, receiver, PendingIntent.FLAG_CANCEL_CURRENT)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //TODO: Check for permissions before starting service
            return
        }

        setupLocationListener()
    }

    @SuppressLint("MissingPermission")
    private fun setupLocationListener() {
        locationListener = LocationListener()
        locationManager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        TIME_TO_UPDATE = sharedPreferences.getString("sync_frequency", "60").toInt().times(1000).toLong()

        val criteria = Criteria()
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = true
        criteria.isSpeedRequired = false
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.accuracy = Criteria.ACCURACY_FINE
        criteria.isCostAllowed = true

        bestProvider = locationManager?.getBestProvider(criteria, false)

        locationManager?.requestLocationUpdates(bestProvider, TIME_TO_UPDATE, DISTANCE_TO_CHANGE, locationListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        if (locationManager != null) {
            locationManager!!.removeUpdates(locationListener)
        }

        TrackingServiceNotification.cancel(this)

        isRunning = false
    }

    @SuppressLint("MissingPermission")
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String) {
        if (s == "sync_frequency") {
            TIME_TO_UPDATE = (Integer.parseInt(sharedPreferences.getString("sync_frequency", "600")) * 1000).toLong()

            locationManager?.removeUpdates(locationListener)
            locationManager?.requestLocationUpdates(bestProvider, TIME_TO_UPDATE, DISTANCE_TO_CHANGE, locationListener)

        }
    }

    private fun stopService() {
        this.stopSelf()
    }

    private inner class LocationListener : android.location.LocationListener {

        override fun onLocationChanged(location: Location) {
            val sharedPreferences = getSharedPreferences("tracking", Context.MODE_PRIVATE)
            val shares = sharedPreferences.all

            //Notifications
            val builder = NotificationCompat.Builder(applicationContext, "tracking_channel")
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                    .setSmallIcon(R.drawable.icon_white)
                    .setContentTitle("Tracking service is running")
                    .setTicker("Tracking service is running")
                    .setNumber(1)
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .setLocalOnly(true)
                    .addAction(R.drawable.ic_close_white_48px, "Stop tracking", stopServiceIntent)
                    .setPriority(NotificationCompat.PRIORITY_MIN)

            val databaseReference = FirebaseDatabase.getInstance().reference

            var userId: String? = null
            if (user != null) {
                userId = user!!.uid
            }

            if (!shares.entries.isEmpty()) {
                for ((key, value) in shares) {
                    if (value as Boolean) {
                        hasTrue = true
                        break
                    } else {
                        hasTrue = false
                        if (userId != null) {
                            databaseReference.child("${FirebaseHelper.TRACKING}/$userId/location").removeValue()
                        }
                    }
                }

                if (hasTrue) {
                    val databaseLocations = DatabaseLocations(location.longitude, location.latitude, System.currentTimeMillis())
                    if (userId != null) {
                        databaseReference.child("${FirebaseHelper.TRACKING}/$userId/location").setValue(databaseLocations)
                        for ((key, value) in shares) {
                            if (value as Boolean) {
                                databaseReference.child("${FirebaseHelper.TRACKING}/$key/${FirebaseHelper.TRACKING}/$userId/timestamp").setValue(System.currentTimeMillis())
                            }
                        }
                    }
                } else {
                    try {
                        stopServiceIntent?.send(this@TrackingService, 1, receiver)
                    } catch (e: PendingIntent.CanceledException) {
                        e.printStackTrace()
                    }

                }
            } else {
                try {
                    stopServiceIntent?.send(this@TrackingService, 1, receiver)
                } catch (e: PendingIntent.CanceledException) {
                    e.printStackTrace()
                }

            }

            TrackingServiceNotification.notify(applicationContext, builder.build())
        }

        override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {

        }

        override fun onProviderEnabled(s: String) {

        }

        override fun onProviderDisabled(s: String) {

        }
    }

    companion object {

        private const val DISTANCE_TO_CHANGE: Float = 0f
        private var TIME_TO_UPDATE: Long = 0

        var isRunning: Boolean = false
    }
}