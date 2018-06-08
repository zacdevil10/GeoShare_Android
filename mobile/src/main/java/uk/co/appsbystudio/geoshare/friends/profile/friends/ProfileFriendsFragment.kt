package uk.co.appsbystudio.geoshare.friends.profile.friends

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.utils.ui.NoSwipeViewPager

class ProfileFriendsFragment : Fragment() {

    var uid: String? = null

    companion object {
        fun newInstance(uid: String?) = ProfileFriendsFragment().apply {
            arguments = Bundle().apply {
                putString("uid", uid)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_friends, container, false)

        val friendsViewPager: NoSwipeViewPager = view.findViewById(R.id.profile_friends_view_pager)
        val friendsTabLayout: TabLayout = view.findViewById(R.id.profile_friends_tab)

        if (arguments != null) {
            uid = arguments!!.getString("uid")
        }

        friendsViewPager.setPagingEnabled(true)
        friendsViewPager.adapter = ProfileFriendsPagerAdapter(childFragmentManager, uid!!)
        friendsTabLayout.setupWithViewPager(friendsViewPager)

        return view
    }

}
