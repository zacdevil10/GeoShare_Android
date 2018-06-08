package uk.co.appsbystudio.geoshare.friends.profile.friends

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import uk.co.appsbystudio.geoshare.friends.profile.friends.pages.ProfileFriendsAllFragment
import uk.co.appsbystudio.geoshare.friends.profile.friends.pages.ProfileFriendsMutualFragment

class ProfileFriendsPagerAdapter (fm: FragmentManager?, val uid: String) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> setFriendsAllFragment()
            1 -> setFriendsMutualFragment()
            else -> setFriendsMutualFragment()
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when(position) {
            0 -> "Friends"
            1 -> "Mutual Friends"
            else -> ""
        }
    }

    private fun setFriendsAllFragment(): ProfileFriendsAllFragment {
        val profileInfoFragment = ProfileFriendsAllFragment()
        val args = Bundle()
        args.putString("uid", uid)
        profileInfoFragment.arguments = args
        return profileInfoFragment
    }

    private fun setFriendsMutualFragment(): ProfileFriendsMutualFragment {
        val profileFriendsMutualFragment = ProfileFriendsMutualFragment()
        val args = Bundle()
        args.putString("uid", uid)
        profileFriendsMutualFragment.arguments = args
        return profileFriendsMutualFragment
    }
}