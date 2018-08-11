package uk.co.appsbystudio.geoshare.friends.profile

interface ProfilePresenter {

    fun removeFriendDialog(uid: String?)

    fun removeFriend(uid: String?)

    fun setViewPagerPosition(position: Int)
}