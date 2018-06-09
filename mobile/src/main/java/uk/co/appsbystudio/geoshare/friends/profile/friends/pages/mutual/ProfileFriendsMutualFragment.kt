package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.mutual

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_profile_friends_mutual.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.adapters.FriendshipStatusAdapter
import java.util.*

class ProfileFriendsMutualFragment : Fragment(), ProfileFriendsMutualView {

    private var presenter: ProfileFriendsMutualPresenter? = null

    private var friendAdapter: FriendshipStatusAdapter? = null

    lateinit var uid: String

    private val uidArray = ArrayList<String>()

    companion object {
        fun newInstance(uid: String?) = ProfileFriendsMutualFragment().apply {
            arguments = Bundle().apply {
                putString("uid", uid)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_friends_mutual, container, false)

        uid = arguments?.getString("uid").toString()

        presenter = ProfileFriendsMutualPresenterImpl(this, ProfileFriendsMutualInteractorImpl())

        friendAdapter = FriendshipStatusAdapter(context, uidArray, null)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter?.friends(uid)

        recycler_friends_mutual_profile.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
            adapter = friendAdapter
        }
    }

    override fun addItem(uid: String?) {
        if (!uidArray.contains(uid) && uid != null) {
            uidArray.add(uid)
            friendAdapter?.notifyDataSetChanged()
        }
    }

    override fun removeItem(uid: String?) {
        uidArray.remove(uid)
        friendAdapter?.notifyDataSetChanged()
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
