package uk.co.appsbystudio.geoshare.friends.manager.pages.pending

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_friends_pending.*

import java.util.ArrayList

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.adapters.FriendsPendingAdapter
import uk.co.appsbystudio.geoshare.friends.adapters.FriendsRequestAdapter
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewFriendNotification

class FriendsPendingFragment : Fragment(), FriendsPendingView, FriendsRequestAdapter.Callback, FriendsPendingAdapter.Callback {

    private var presenter: FriendsPendingPresenter? = null

    private var friendsIncomingAdapter: FriendsRequestAdapter? = null
    private var friendsOutgoingAdapter: FriendsPendingAdapter? = null

    private val uidIncoming = ArrayList<String>()
    private val uidOutgoing = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friends_pending, container, false)

        presenter = FriendsPendingPresenterImpl(this, FriendsPendingInteractorImpl())

        presenter?.friendRequests()

        NewFriendNotification.cancel(context!!)

        friendsIncomingAdapter = FriendsRequestAdapter(context, uidIncoming, FirebaseDatabase.getInstance().reference, this@FriendsPendingFragment)
        friendsOutgoingAdapter = FriendsPendingAdapter(context, uidOutgoing, FirebaseDatabase.getInstance().reference, this@FriendsPendingFragment)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler(recycler_incoming_pending)
        setupRecycler(recycler_outgoing_pending)

        recycler_incoming_pending.adapter = friendsIncomingAdapter
        recycler_outgoing_pending.adapter = friendsOutgoingAdapter
    }

    private fun setupRecycler(friendsLists: RecyclerView) {
        friendsLists.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onAcceptReject(accept: Boolean?, uid: String) {
        presenter?.requestAction(accept!!, uid)
    }

    override fun onReject(uid: String) {
        presenter?.requestAction(false, uid)
    }

    override fun addIncoming(uid: String) {
        uidIncoming.add(uid)
        friendsIncomingAdapter?.notifyDataSetChanged()
    }

    override fun addOutgoing(uid: String) {
        uidOutgoing.add(uid)
        friendsOutgoingAdapter?.notifyDataSetChanged()
    }

    override fun removeIncoming(uid: String) {
        uidIncoming.remove(uid)
        friendsIncomingAdapter?.notifyDataSetChanged()
    }

    override fun removeOutgoing(uid: String) {
        uidOutgoing.remove(uid)
        friendsOutgoingAdapter?.notifyDataSetChanged()
    }

    override fun showNoRequestsText() {
        text_no_outgoing_pending.visibility = if (uidOutgoing.isEmpty()) View.VISIBLE else View.GONE
        text_no_incoming_pending.visibility = if (uidIncoming.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.stop()
    }
}