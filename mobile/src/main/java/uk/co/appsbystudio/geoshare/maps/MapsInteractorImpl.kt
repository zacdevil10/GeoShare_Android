package uk.co.appsbystudio.geoshare.maps

import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import uk.co.appsbystudio.geoshare.utils.bitmapCanvas
import uk.co.appsbystudio.geoshare.utils.firebase.*
import java.io.File

class MapsInteractorImpl: MapsInteractor {

    private var staticRef: DatabaseReference? = null
    private var trackingRef: DatabaseReference? = null

    private var trackingListener: ChildEventListener? = null

    private var syncState: Boolean = false

    override fun staticFriends(storageDirectory: String?, listener: MapsInteractor.OnFirebaseRequestFinishedListener) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            staticRef = FirebaseDatabase.getInstance().getReference(FirebaseHelper.CURRENT_LOCATION + "/${user.uid}")
            staticRef?.keepSynced(true)

            val staticLocationListener = object : ChildEventListener {

                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    if (dataSnapshot.exists()) {
                        val databaseLocations = dataSnapshot.getValue(DatabaseLocations::class.java)
                        getUserProfileImage(dataSnapshot.key, databaseLocations, storageDirectory, listener)
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

    override fun trackingFriends(storageDirectory: String?, listener: MapsInteractor.OnFirebaseRequestFinishedListener) {
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
                        if (trackingInfo != null && trackingInfo.tracking) {
                            getTrackingLocation(dataSnapshot.key!!, storageDirectory, listener)
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

    fun getTrackingLocation(uid: String, storageDirectory: String?, listener: MapsInteractor.OnFirebaseRequestFinishedListener) {
        val trackingLocationListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val databaseLocations = dataSnapshot.getValue(DatabaseLocations::class.java)
                    if (listener.markerExists(uid)) {
                        listener.locationChanged(uid, databaseLocations)
                    } else {
                        getUserProfileImage(uid, databaseLocations, storageDirectory, listener)
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.TRACKING}/$uid/${FirebaseHelper.LOCATION}").addListenerForSingleValueEvent(trackingLocationListener)
    }

    fun getUserProfileImage(uid: String?, databaseLocations: DatabaseLocations?, storageDirectory: String?, listener: MapsInteractor.OnFirebaseRequestFinishedListener) {
        val file = File(storageDirectory.toString() + "/" + uid + ".png")
        if (file.exists()) {
            val image = BitmapFactory.decodeFile("$storageDirectory/$uid.png") .bitmapCanvas(116, 155)
            listener.locationAdded(uid, image, databaseLocations)
        } else {
            FirebaseStorage.getInstance().reference.child("""${FirebaseHelper.PROFILE_PICTURE}/$uid.png""")
                    .getFile(Uri.fromFile(file))
                    .addOnSuccessListener {
                        val image = BitmapFactory.decodeFile("$storageDirectory/$uid.png") .bitmapCanvas(116, 155)
                        listener.locationAdded(uid, image, databaseLocations)
                    }
                    .addOnFailureListener {
                        listener.locationAdded(uid, null.bitmapCanvas(116, 155), databaseLocations)
                    }
        }
    }

    override fun trackingSync(sync: Boolean): Boolean {
        trackingRef?.keepSynced(sync)

        if (sync && !syncState) {
            syncState = true
            trackingRef?.addChildEventListener(trackingListener as ChildEventListener)
        } else if (!sync) {
            syncState = false
            trackingRef?.removeEventListener(trackingListener as ChildEventListener)
        }

        return syncState
    }
}