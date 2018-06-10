package uk.co.appsbystudio.geoshare.friends.manager.search

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FriendSearchInteractorImpl: FriendSearchInteractor {

    override fun getSearchResults(entry: String, exit: String, listener: FriendSearchInteractor.OnFirebaseListener) {
        val query = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (ds in dataSnapshot.children) listener.addResults(ds.key, ds.child("name").getValue(String::class.java))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                listener.error(databaseError.message)
            }
        }

        FirebaseDatabase.getInstance().reference.child("users").orderByChild("caseFoldedName")
                .startAt(entry.toLowerCase()).endAt("${exit.toLowerCase()}~")
                .limitToFirst(20)
                .addListenerForSingleValueEvent(query)
    }

    override fun sendRequest(uid: String, listener: FriendSearchInteractor.OnFirebaseListener) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            FirebaseDatabase.getInstance().reference.child("pending/${user.uid}/$uid/outgoing").setValue(true)
                    .addOnFailureListener {
                        listener.error(it.message.toString())
                    }.addOnSuccessListener {
                        listener.success()
                    }
            FirebaseDatabase.getInstance().reference.child("pending/$uid/${user.uid}/outgoing").setValue(false)
        }
    }
}