package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.all

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_profile_friends_all.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.profile.friends.adapters.FriendshipStatusAdapter
import java.util.*

class ProfileFriendsAllFragment : Fragment(), ProfileFriendsAllView, FriendshipStatusAdapter.Callback {

    lateinit var uid: String

    private var presenter: ProfileFriendsAllPresenter? = null
    private var friendAdapter: FriendshipStatusAdapter? = null

    private val uidArray = ArrayList<String>()

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

        friendAdapter = FriendshipStatusAdapter(context, uidArray, this)

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

    override fun onSendRequest(friendId: String) {
        presenter?.request(friendId)
    }

    override fun addItem(uid: String?) {
        if (uid != null && !uidArray.contains(uid)) {
            uidArray.add(uid)
            friendAdapter?.notifyDataSetChanged()
        }
        text_no_friends_profile.visibility = if (uidArray.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun removeItem(uid: String?) {
        uidArray.remove(uid)
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
