package uk.co.appsbystudio.geoshare.friends.manager.pages.current

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_friends.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.manager.pages.current.adapter.FriendsCurrentAdapter
import uk.co.appsbystudio.geoshare.utils.ShowMarkerPreferencesHelper
import uk.co.appsbystudio.geoshare.utils.TrackingPreferencesHelper
import java.util.*

class FriendsFragment : Fragment(), FriendsView, FriendsCurrentAdapter.Callback {

    private var presenter: FriendsPresenter? = null

    private var friendsCurrentAdapter: FriendsCurrentAdapter? = null

    private val uidArray = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friends, container, false)

        presenter = FriendsPresenterImpl(this, FriendsInteractorImpl(),
                TrackingPreferencesHelper(context?.getSharedPreferences("tracking", MODE_PRIVATE)),
                ShowMarkerPreferencesHelper(context?.getSharedPreferences("showOnMap", MODE_PRIVATE)))

        presenter?.friends()

        friendsCurrentAdapter = FriendsCurrentAdapter(context, uidArray, this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_friends_manager.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = friendsCurrentAdapter
        }
    }

    override fun onRemoveFriend(friendId: String) {
        presenter?.removeFriend(friendId)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.stop()
    }

    override fun addFriend(uid: String) {
        uidArray.add(uid)
        friendsCurrentAdapter?.notifyDataSetChanged()

        if (uidArray.isNotEmpty()) text_no_friends_manager?.visibility = View.GONE
    }

    override fun removeFriend(uid: String) {
        uidArray.remove(uid)
        friendsCurrentAdapter?.notifyDataSetChanged()

        if (uidArray.isEmpty()) text_no_friends_manager?.visibility = View.VISIBLE
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}