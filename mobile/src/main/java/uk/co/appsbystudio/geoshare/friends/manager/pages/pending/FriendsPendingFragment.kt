package uk.co.appsbystudio.geoshare.friends.manager.pages.pending

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_friends_pending.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.manager.FriendsManager
import uk.co.appsbystudio.geoshare.friends.manager.FriendsManagerView
import uk.co.appsbystudio.geoshare.friends.manager.pages.pending.adapters.FriendsPendingAdapter
import uk.co.appsbystudio.geoshare.friends.manager.pages.pending.adapters.FriendsRequestAdapter
import uk.co.appsbystudio.geoshare.utils.ui.notifications.NewFriendNotification
import java.util.*

class FriendsPendingFragment : Fragment(), FriendsPendingView {

    private var fragmentCallback: FriendsManagerView? = null
    private var presenter: FriendsPendingPresenter? = null

    private var friendsIncomingAdapter: FriendsRequestAdapter? = null
    private var friendsOutgoingAdapter: FriendsPendingAdapter? = null

    private val uidIncoming = ArrayList<String>()
    private val uidOutgoing = ArrayList<String>()

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            fragmentCallback = context as FriendsManager
        } catch (e: ClassCastException) {
            throw ClassCastException("""${activity.toString()}must implement FriendsManagerView""")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friends_pending, container, false)

        presenter = FriendsPendingPresenterImpl(this, FriendsPendingInteractorImpl())

        presenter?.friendRequests()

        NewFriendNotification.cancel(context!!)

        friendsIncomingAdapter = FriendsRequestAdapter(context, uidIncoming, this@FriendsPendingFragment)
        friendsOutgoingAdapter = FriendsPendingAdapter(context, uidOutgoing, this@FriendsPendingFragment)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_incoming_pending.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = friendsIncomingAdapter
        }

        recycler_outgoing_pending.apply {
            setHasFixedSize(false)
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(context)
            adapter = friendsOutgoingAdapter
        }

        scroll_recycler_pending.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            when {
                scrollY > oldScrollY -> fragmentCallback?.fabState(false)
                scrollY < oldScrollY -> fragmentCallback?.fabState(true)
            }
        })
    }

    override fun accept(uid: String, accept: Boolean) {
        presenter?.requestAction(accept, uid)
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