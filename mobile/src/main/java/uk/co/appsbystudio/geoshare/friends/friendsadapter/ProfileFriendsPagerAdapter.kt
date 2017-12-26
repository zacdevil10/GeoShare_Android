package uk.co.appsbystudio.geoshare.friends.friendsadapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import uk.co.appsbystudio.geoshare.friends.profile.pages.ProfileFriendsAllFragment
import uk.co.appsbystudio.geoshare.friends.profile.pages.ProfileFriendsMutualFragment

class ProfileFriendsPagerAdapter (fm: FragmentManager?, val uid: String) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return setFriendsAllFragment()
            1 -> return ProfileFriendsMutualFragment()
        }
        return ProfileTestFragment()
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position) {
            0 -> return "Friends"
            1 -> return "Mutual Friends"
        }
        return ""
    }

    private fun setFriendsAllFragment(): ProfileFriendsAllFragment {
        val profileInfoFragment = ProfileFriendsAllFragment()
        val args = Bundle()
        args.putString("uid", uid)
        profileInfoFragment.arguments = args
        return profileInfoFragment
    }
}