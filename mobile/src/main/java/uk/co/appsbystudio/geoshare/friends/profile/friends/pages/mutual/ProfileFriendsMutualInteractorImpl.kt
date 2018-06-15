package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.mutual

import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.friends.manager.FriendsManager
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfileFriendsMutualInteractorImpl: ProfileFriendsMutualInteractor {

    private lateinit var friendsListener: ChildEventListener
    private var friendsRef: DatabaseReference? = null

    override fun getFriends(uid: String, listener: ProfileFriendsMutualInteractor.OnFirebaseListener) {
        friendsRef = FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/$uid")
        friendsListener = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, string: String?) {
                if (FriendsManager.friendsMap.containsKey(dataSnapshot.key)) listener.added(dataSnapshot.key)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, string: String?) {

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                listener.removed(dataSnapshot.key)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, string: String?) {

            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        friendsRef?.addChildEventListener(friendsListener)
    }

    override fun removeListener() {
        friendsRef?.removeEventListener(friendsListener)
    }
}