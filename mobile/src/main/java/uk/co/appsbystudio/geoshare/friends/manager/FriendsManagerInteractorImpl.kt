package uk.co.appsbystudio.geoshare.friends.manager

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper
import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation

class FriendsManagerInteractorImpl: FriendsManagerInteractor {

    var user: FirebaseUser? = null

    private var friendsRef: DatabaseReference? = null
    private lateinit var friendsListener: ChildEventListener
    private var nameRef: DatabaseReference? = null
    private lateinit var usersListener: ValueEventListener

    init {
        user = FirebaseAuth.getInstance().currentUser
    }

    override fun getFriends(listener: FriendsManagerInteractor.OnFirebaseRequestFinishedListener) {
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

    fun getName(uid: String?, listener: FriendsManagerInteractor.OnFirebaseRequestFinishedListener) {
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

    override fun removeListener() {
        friendsRef?.removeEventListener(friendsListener)
    }
}