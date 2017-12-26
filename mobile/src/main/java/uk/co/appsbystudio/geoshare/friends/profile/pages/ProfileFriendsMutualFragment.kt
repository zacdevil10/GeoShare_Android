package uk.co.appsbystudio.geoshare.friends.profile.pages

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.*
import uk.co.appsbystudio.geoshare.MainActivity

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.friendsadapter.FriendsSearchAdapter
import java.util.ArrayList

class ProfileFriendsMutualFragment : Fragment() {

    private var databaseFriendsRef: DatabaseReference? = null
    private var databaseReference: DatabaseReference? = null

    private var friendAdapter: FriendsSearchAdapter? = null

    var uid: String? = null

    private val friendId = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_friends_mutual, container, false)

        if (arguments != null) {
            uid = arguments!!.getString("uid")
        }

        val database = FirebaseDatabase.getInstance()
        databaseReference = database.reference

        databaseFriendsRef = database.getReference("friends/" + uid)
        databaseFriendsRef?.keepSynced(true)

        val friendsAll: RecyclerView = view.findViewById(R.id.profile_friends_mutual_list)
        friendsAll.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        friendsAll.layoutManager = layoutManager

        getFriends()

        friendAdapter = FriendsSearchAdapter(context, databaseReference, friendId, null)
        friendsAll.adapter = friendAdapter

        return view
    }

    private fun getFriends() {
        val friendsList = object : ChildEventListener {

            override fun onChildAdded(dataSnapshot: DataSnapshot, string: String?) {
                if (!friendId.contains(dataSnapshot.key) && MainActivity.friendsId.contains(dataSnapshot.key)) friendId.add(dataSnapshot.key)
                friendAdapter!!.notifyDataSetChanged()
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, string: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                friendId.remove(dataSnapshot.key)
                friendAdapter!!.notifyDataSetChanged()
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, string: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCancelled(databaseError: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }

        databaseFriendsRef?.addChildEventListener(friendsList)
    }

}
