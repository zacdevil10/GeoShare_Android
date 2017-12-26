package uk.co.appsbystudio.geoshare.friends.profile

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.profile.profileadapter.ProfileFriendsPagerAdapter
import uk.co.appsbystudio.geoshare.utils.ui.NoSwipeViewPager

class ProfileFriendsFragment : Fragment() {

    var uid: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_profile_friends, container, false)

        val friendsViewPager: NoSwipeViewPager = view.findViewById(R.id.profile_friends_view_pager)
        val friendsTabLayout: TabLayout = view.findViewById(R.id.profile_friends_tab)

        if (arguments != null) {
            uid = arguments!!.getString("uid")
        }

        friendsViewPager.adapter = ProfileFriendsPagerAdapter(childFragmentManager, uid!!)
        friendsTabLayout.setupWithViewPager(friendsViewPager)

        return view
    }

}
