package uk.co.appsbystudio.geoshare.base

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.TrackingInfo
import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation

class MainInteractorImpl: MainInteractor {

    override fun getFriends(listener: MainInteractor.OnFirebaseRequestFinishedListener) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val friendsRef = FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/${user.uid}")
            friendsRef.keepSynced(true)

            val friendsListener = object : ChildEventListener {

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

            friendsRef.addChildEventListener(friendsListener)
        }
    }

    fun getName(uid: String?, listener: MainInteractor.OnFirebaseRequestFinishedListener) {
        val usersListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userInformation = dataSnapshot.getValue(UserInformation::class.java)
                listener.friendAdded(uid, userInformation?.name)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.USERS}/$uid").addListenerForSingleValueEvent(usersListener)
    }

    override fun getTrackingState(listener: MainInteractor.OnFirebaseRequestFinishedListener) {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val trackingStatusRef = FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.TRACKING}/${user.uid}/${FirebaseHelper.TRACKING}")
            trackingStatusRef.keepSynced(true)

            val trackingListener = object : ChildEventListener {

                override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                    val trackingInfo = dataSnapshot.getValue(TrackingInfo::class.java)
                    listener.trackingAdded(dataSnapshot.key, trackingInfo?.isTracking)
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

            trackingStatusRef.addChildEventListener(trackingListener)
        }
    }

    override fun removeToken() {
        val user = FirebaseAuth.getInstance().currentUser
        val token = FirebaseInstanceId.getInstance().token
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.TOKEN}/${user?.uid}/$token").removeValue()
    }
}