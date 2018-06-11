package uk.co.appsbystudio.geoshare.utils.firebase.listeners

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

import java.io.File

import uk.co.appsbystudio.geoshare.base.adapters.FriendsNavAdapter
import uk.co.appsbystudio.geoshare.friends.manager.pages.pending.adapters.FriendsPendingAdapter
import uk.co.appsbystudio.geoshare.friends.manager.pages.pending.adapters.FriendsRequestAdapter

class UpdatedProfilePicturesListener(private val navAdapter: FriendsNavAdapter?, private val storageDirectory: String) : ChildEventListener {

    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
        onChildChanged(dataSnapshot, s)
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
        val imageFile = File(storageDirectory + "/" + dataSnapshot.key + ".png")
        val timestamp = dataSnapshot.getValue(Long::class.java)
        if (imageFile.exists() && timestamp != null && timestamp > imageFile.lastModified()) {
            imageFile.delete()

            navAdapter?.notifyDataSetChanged()
        }
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        onChildChanged(dataSnapshot, null)
    }

    override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {

    }

    override fun onCancelled(databaseError: DatabaseError) {

    }
}
