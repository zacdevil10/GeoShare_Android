package uk.co.appsbystudio.geoshare.base

import android.support.v4.app.Fragment

interface MainPresenter {

    fun getFriends()

    fun getFriendsTrackingState()

    fun showFragment(fragment: Fragment)

    fun friends()

    fun settings()

    fun feedback()

    fun logout()

    fun auth()

}