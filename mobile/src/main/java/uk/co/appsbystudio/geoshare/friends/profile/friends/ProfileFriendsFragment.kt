package uk.co.appsbystudio.geoshare.friends.profile.friends

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_profile_friends.*

import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.profile.friends.pages.ProfileFriendsAllFragment
import uk.co.appsbystudio.geoshare.friends.profile.friends.pages.mutual.ProfileFriendsMutualFragment

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

        uid = arguments?.getString("uid")

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                return when (position) {
                    0 -> ProfileFriendsAllFragment.newInstance(uid)
                    1 -> ProfileFriendsMutualFragment.newInstance(uid)
                    else -> null
                }
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return when(position) {
                    0 -> "Friends"
                    1 -> "Mutual Friends"
                    else -> null
                }
            }

            override fun getCount(): Int {
                return 2
            }
        }


        view_pager_friends_profile?.apply {
            this.adapter = adapter
            setPagingEnabled(true)
        }

        tabs_friends_profile?.setupWithViewPager(view_pager_friends_profile)
    }

}
