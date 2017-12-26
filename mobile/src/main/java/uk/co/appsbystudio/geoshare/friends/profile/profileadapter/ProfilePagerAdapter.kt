package uk.co.appsbystudio.geoshare.friends.profile.profileadapter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import uk.co.appsbystudio.geoshare.friends.profile.ProfileFriendsFragment
import uk.co.appsbystudio.geoshare.friends.profile.ProfileInfoFragment
import uk.co.appsbystudio.geoshare.friends.profile.ProfileStaticMapFragment

class ProfilePagerAdapter(fm: FragmentManager?, val pages: Int, val uid: String) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        when(position) {
            0 -> return setInfoFragment()
            1 -> return setStaticMapFragment()
            2 -> return setFriendsFragment()
        }
        return ProfileInfoFragment()
    }

    override fun getCount(): Int {
        return pages
    }

    private fun setInfoFragment(): ProfileInfoFragment {
        val profileInfoFragment = ProfileInfoFragment()
        val args = Bundle()
        args.putString("uid", uid)
        profileInfoFragment.arguments = args
        return profileInfoFragment
    }

    private fun setStaticMapFragment(): ProfileStaticMapFragment {
        val profileStaticMapFragment = ProfileStaticMapFragment()
        val args = Bundle()
        args.putString("uid", uid)
        profileStaticMapFragment.arguments = args
        return profileStaticMapFragment
    }

    private fun setFriendsFragment(): ProfileFriendsFragment {
        val profileFriendsFragment = ProfileFriendsFragment()
        val args = Bundle()
        args.putString("uid", uid)
        profileFriendsFragment.arguments = args
        return profileFriendsFragment
    }
}