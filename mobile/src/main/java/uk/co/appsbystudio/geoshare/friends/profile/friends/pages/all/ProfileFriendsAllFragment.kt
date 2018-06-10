package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.all

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_profile_friends_all.*

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.adapters.FriendshipStatusAdapter
import java.util.ArrayList

class ProfileFriendsAllFragment : Fragment(), ProfileFriendsAllView, FriendshipStatusAdapter.Callback {

    lateinit var uid: String

    private var presenter: ProfileFriendsAllPresenter? = null
    private var friendAdapter: FriendshipStatusAdapter? = null

    private val friendId = ArrayList<String>()

    companion object {
        fun newInstance(uid: String?) = ProfileFriendsAllFragment().apply {
            arguments = Bundle().apply {
                putString("uid", uid)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_friends_all, container, false)

        uid = arguments?.getString("uid").toString()

        presenter = ProfileFriendsAllPresenterImpl(this, ProfileFriendsAllInteractorImpl())

        friendAdapter = FriendshipStatusAdapter(context, friendId, this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) presenter?.friends(uid)

        recycler_friends_all_profile.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = friendAdapter
        }
    }

    override fun onSendRequest(friendId: String?) {
        if (friendId != null) presenter?.request(friendId)
    }

    override fun addItem(uid: String?) {
        if (!friendId.contains(uid) && uid != null) {
            friendId.add(uid)
            friendAdapter?.notifyDataSetChanged()
        }
    }

    override fun removeItem(uid: String?) {
        friendId.remove(uid)
        friendAdapter?.notifyDataSetChanged()
    }

    override fun updateRecycler() {
        friendAdapter?.notifyDataSetChanged()
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.stop()
    }
}
