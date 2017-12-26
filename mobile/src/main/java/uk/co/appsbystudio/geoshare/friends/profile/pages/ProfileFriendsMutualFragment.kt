package uk.co.appsbystudio.geoshare.friends.profile.pages

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import uk.co.appsbystudio.geoshare.R

class ProfileFriendsMutualFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile_friends_mutual, container, false)
    }

}
