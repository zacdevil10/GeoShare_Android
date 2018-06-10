package uk.co.appsbystudio.geoshare.friends.manager.pages.current

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class FriendsInteractorImpl: FriendsInteractor {

    private var friendsRef: DatabaseReference? = null
    private lateinit var friendsListener: ChildEventListener

    override fun getFriends(listener: FriendsInteractor.OnFirebaseListener) {
        val user = FirebaseAuth.getInstance().currentUser

        friendsRef = FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/${user?.uid}")
        friendsRef?.keepSynced(true)

        friendsListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                listener.add(dataSnapshot.key)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                listener.remove(dataSnapshot.key)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        friendsRef?.addChildEventListener(friendsListener)
    }

    override fun removeFriend(uid: String, listener: FriendsInteractor.OnFirebaseListener) {
        val user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/${user?.uid}/$uid").removeValue()
                .addOnSuccessListener {
                    listener.unfriended(uid)
                }.addOnFailureListener {
                    listener.error(it.message.toString())
                }
    }

    override fun removeListener() {
        friendsRef?.removeEventListener(friendsListener)
    }
}