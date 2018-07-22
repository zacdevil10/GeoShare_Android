package uk.co.appsbystudio.geoshare.friends.manager.pages.current

interface FriendsPresenter {

    fun friends()

    fun removeFriend(uid: String)

    fun stop()
}