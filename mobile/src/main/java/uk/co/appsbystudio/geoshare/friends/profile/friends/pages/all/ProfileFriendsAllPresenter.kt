package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.all

interface ProfileFriendsAllPresenter {

    fun friends(uid: String)

    fun request(uid: String)

    fun stop()
}