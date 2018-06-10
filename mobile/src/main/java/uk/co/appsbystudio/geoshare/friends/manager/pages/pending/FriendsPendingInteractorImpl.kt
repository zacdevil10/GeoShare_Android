package uk.co.appsbystudio.geoshare.friends.manager.pages.pending

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.utils.firebase.AddFriendsInfo
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class FriendsPendingInteractorImpl: FriendsPendingInteractor {

    private var requestRef: DatabaseReference? = null
    private lateinit var requestListener: ChildEventListener

    override fun getRequests(listener: FriendsPendingInteractor.OnFirebaseListener) {
        val user = FirebaseAuth.getInstance().currentUser
        requestRef = FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.PENDING}/${user?.uid}")
        requestRef?.keepSynced(true)

        requestListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                listener.add(dataSnapshot.key, dataSnapshot.getValue(AddFriendsInfo::class.java))
                //TODO: Get name first!
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                listener.remove(dataSnapshot.key, dataSnapshot.getValue(AddFriendsInfo::class.java))
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        requestRef?.addChildEventListener(requestListener)
    }

    override fun acceptRequest(uid: String) {
        val user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/${user?.uid}/$uid").setValue(true)
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/$uid/${user?.uid}").setValue(true)
    }

    override fun rejectRequest(uid: String) {
        val user = FirebaseAuth.getInstance().currentUser
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.PENDING}/${user?.uid}/$uid").removeValue()
        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.PENDING}/$uid/${user?.uid}").removeValue()
    }

    override fun removeListener() {
        requestRef?.removeEventListener(requestListener)
    }
}