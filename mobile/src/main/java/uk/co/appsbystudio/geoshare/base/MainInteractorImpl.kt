package uk.co.appsbystudio.geoshare.base

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.TrackingInfo
import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation
import java.io.File

class MainInteractorImpl(private val storage: String): MainInteractor {

    private var user: FirebaseUser? = null

    private var friendsRef: DatabaseReference? = null
    private var nameRef: DatabaseReference? = null
    private var trackingRef: DatabaseReference? = null
    private var profileUpdateRef: DatabaseReference? = null

    private lateinit var friendsListener: ChildEventListener
    private lateinit var trackingListener: ChildEventListener
    private lateinit var usersListener: ValueEventListener
    private lateinit var profileUpdateListener: ChildEventListener

    init {
        user = FirebaseAuth.getInstance().currentUser
    }

    override fun getFriends(listener: MainInteractor.OnFirebaseRequestFinishedListener) {
        if (user != null) {
            friendsRef = FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/${user?.uid}")
            friendsRef?.keepSynced(true)

            friendsListener = object : ChildEventListener {

                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    getName(dataSnapshot.key, listener)
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    listener.friendRemoved(dataSnapshot.key)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    listener.error(databaseError.message)
                }
            }

            friendsRef?.addChildEventListener(friendsListener)
        }
    }

    fun getName(uid: String?, listener: MainInteractor.OnFirebaseRequestFinishedListener) {
        nameRef = FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.USERS}/$uid")
        usersListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userInformation = dataSnapshot.getValue(UserInformation::class.java)
                listener.friendAdded(uid, userInformation?.name)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        nameRef?.addListenerForSingleValueEvent(usersListener)
    }

    override fun getTrackingState(listener: MainInteractor.OnFirebaseRequestFinishedListener) {
        if (user != null) {
            trackingRef = FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.TRACKING}/${user?.uid}/${FirebaseHelper.TRACKING}")
            trackingRef?.keepSynced(true)

            trackingListener = object : ChildEventListener {

                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val trackingInfo = dataSnapshot.getValue(TrackingInfo::class.java)
                    listener.trackingAdded(dataSnapshot.key, trackingInfo?.tracking)
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                    onChildAdded(dataSnapshot, s)
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    listener.trackingRemoved(dataSnapshot.key)
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    listener.error(databaseError.message)
                }
            }

            trackingRef?.addChildEventListener(trackingListener)
        }
    }

    override fun setUpdatedProfileListener(listener: MainInteractor.OnFirebaseRequestFinishedListener) {
        profileUpdateRef = FirebaseDatabase.getInstance().reference.child(FirebaseHelper.PICTURE)

        profileUpdateListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val imageFile = File("""$storage/${dataSnapshot.key}.png""")
                val timestamp = dataSnapshot.getValue(Long::class.java)
                if (imageFile.exists() && timestamp != null && timestamp > imageFile.lastModified()) {
                    imageFile.delete()

                    listener.profileUpdated()
                }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                onChildAdded(dataSnapshot, s)
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                onChildAdded(dataSnapshot, null)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        profileUpdateRef?.addChildEventListener(profileUpdateListener)
    }

    override fun removeToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            if (user != null) FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.TOKEN}/${user?.uid}/${it.token}").removeValue()
        }

    }

    override fun removeAllListeners() {
        friendsRef?.removeEventListener(friendsListener)
        trackingRef?.removeEventListener(trackingListener)
        nameRef?.removeEventListener(usersListener)
        profileUpdateRef?.removeEventListener(profileUpdateListener)
    }
}