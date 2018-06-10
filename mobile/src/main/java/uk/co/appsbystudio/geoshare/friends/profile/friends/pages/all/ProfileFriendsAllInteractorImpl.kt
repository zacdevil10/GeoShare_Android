package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.all

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfileFriendsAllInteractorImpl: ProfileFriendsAllInteractor {

    private lateinit var friendsListener: ChildEventListener
    private var friendsRef: DatabaseReference? = null

    override fun getFriends(uid: String?, listener: ProfileFriendsAllInteractor.OnFirebaseListener) {
        val user = FirebaseAuth.getInstance().currentUser
        friendsRef = FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/$uid")
        friendsListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, string: String?) {
                if (dataSnapshot.key != user?.uid) listener.add(dataSnapshot.key)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, string: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                listener.remove(dataSnapshot.key)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, string: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        friendsRef?.addChildEventListener(friendsListener)
    }

    override fun sendFriendRequest(uid: String?, listener: ProfileFriendsAllInteractor.OnFirebaseListener) {
        val user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.PENDING}/${user?.uid}/$uid/outgoing").setValue(true)
                .addOnSuccessListener {
                    listener.success("Friend request sent")
                }.addOnFailureListener {
                    listener.error(it.message.toString())
                }

        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.PENDING}/$uid/${user?.uid}/outgoing").setValue(false)
    }

    override fun removeListener() {
        friendsRef?.removeEventListener(friendsListener)
    }
}