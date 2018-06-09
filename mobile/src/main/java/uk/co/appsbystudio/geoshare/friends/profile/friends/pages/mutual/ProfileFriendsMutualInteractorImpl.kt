package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.mutual

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import uk.co.appsbystudio.geoshare.base.MainActivity
import uk.co.appsbystudio.geoshare.utils.firebase.FirebaseHelper

class ProfileFriendsMutualInteractorImpl: ProfileFriendsMutualInteractor {

    override fun getFriends(uid: String, listener: ProfileFriendsMutualInteractor.OnFirebaseListener) {
        val friendsList = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, string: String?) {
                if (MainActivity.friendsId.contains(dataSnapshot.key)) listener.added(dataSnapshot.key)
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

        FirebaseDatabase.getInstance().reference.child("${FirebaseHelper.FRIENDS}/$uid").addChildEventListener(friendsList)
    }
}