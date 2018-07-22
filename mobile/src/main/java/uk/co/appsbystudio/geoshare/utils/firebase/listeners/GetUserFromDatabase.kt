package uk.co.appsbystudio.geoshare.utils.firebase.listeners

import android.widget.TextView

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

import uk.co.appsbystudio.geoshare.utils.firebase.UserInformation

class GetUserFromDatabase(private val view: TextView) : ValueEventListener {

    override fun onDataChange(dataSnapshot: DataSnapshot) {
        val user = dataSnapshot.getValue(UserInformation::class.java)
        view.text = user?.name
    }

    override fun onCancelled(databaseError: DatabaseError) {

    }
}
