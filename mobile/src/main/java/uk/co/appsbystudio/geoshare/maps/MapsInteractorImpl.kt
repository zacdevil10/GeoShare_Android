package uk.co.appsbystudio.geoshare.maps

import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import uk.co.appsbystudio.geoshare.MainActivity
import uk.co.appsbystudio.geoshare.utils.bitmapCanvas
import uk.co.appsbystudio.geoshare.utils.firebase.*
import java.io.File

class MapsInteractorImpl: MapsInteractor {

    var staticRef: DatabaseReference? = null
    var trackingRef: DatabaseReference? = null

    private var trackingListener: ChildEventListener? = null

    override fun staticFriends(listener: MapsInteractor.OnFirebaseRequestFinishedListener) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            staticRef = FirebaseDatabase.getInstance().getReference(FirebaseHelper.CURRENT_LOCATION + "/${user.uid}")
            staticRef?.keepSynced(true)

            val staticLocationListener = object : ChildEventListener {

                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.exists()) {
                        val databaseLocations = dataSnapshot.getValue(DatabaseLocations::class.java)
                        getUserProfileImage(dataSnapshot.key, databaseLocations, listener)
                    }
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.exists()) {
                        val databaseLocations = dataSnapshot.getValue(DatabaseLocations::class.java)
                        listener.locationChanged(dataSnapshot.key, databaseLocations)
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    listener.locationRemoved(dataSnapshot.key)
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    listener.error(databaseError.message)
                }
            }

            staticRef?.addChildEventListener(staticLocationListener)
        }
    }

    override fun trackingFriends(listener: MapsInteractor.OnFirebaseRequestFinishedListener) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            trackingRef = FirebaseDatabase.getInstance().getReference("${FirebaseHelper.TRACKING}/${user.uid}/${FirebaseHelper.TRACKING}")
            trackingRef?.keepSynced(true)

            trackingListener = object : ChildEventListener {

                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    onChildChanged(dataSnapshot, s)
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.exists()) {
                        val trackingInfo = dataSnapshot.getValue(TrackingInfo::class.java)
                        if (trackingInfo != null && trackingInfo.isTracking) {
                            getTrackingLocation(dataSnapshot.key!!, listener)
                        }
                    }
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    listener.locationRemoved(dataSnapshot.key)
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    listener.error(databaseError.message)
                }
            }
        }
    }

    fun getTrackingLocation(uid: String, listener: MapsInteractor.OnFirebaseRequestFinishedListener) {
        val trackingLocationListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val databaseLocations = dataSnapshot.getValue(DatabaseLocations::class.java)
                    if (listener.markerExists(uid)) {
                        listener.locationChanged(uid, databaseLocations)
                    } else {
                        getUserProfileImage(uid, databaseLocations, listener)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.TRACKING}/$uid/${FirebaseHelper.LOCATION}").addListenerForSingleValueEvent(trackingLocationListener)
    }

    fun getUserProfileImage(uid: String?, databaseLocations: DatabaseLocations?, listener: MapsInteractor.OnFirebaseRequestFinishedListener) {
        FirebaseStorage.getInstance().reference.child(FirebaseHelper.PROFILE_PICTURE + "/" + uid + ".png")
                .getFile(Uri.fromFile(File(MainActivity.cacheDir.toString() + "/" + uid + ".png")))
                .addOnSuccessListener {
                    val image = BitmapFactory.decodeFile(MainActivity.cacheDir.toString() + "/" + uid + ".png") .bitmapCanvas(116, 155, false, 0)
                    listener.locationAdded(uid, image, databaseLocations)
                }
                .addOnFailureListener {
                    listener.locationAdded(uid, null.bitmapCanvas(116, 155, false, 0), databaseLocations)
                }
    }

    override fun trackingSync(sync: Boolean) {
        trackingRef?.keepSynced(sync)

        if (sync) {
            trackingRef?.addChildEventListener(trackingListener as ChildEventListener)
        } else {
            trackingRef?.removeEventListener(trackingListener as ChildEventListener)
        }
    }
}