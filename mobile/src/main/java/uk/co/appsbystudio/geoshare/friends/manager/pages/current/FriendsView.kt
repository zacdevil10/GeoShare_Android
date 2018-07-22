package uk.co.appsbystudio.geoshare.friends.manager.pages.current

interface FriendsView {

    fun addFriend(uid: String)

    fun removeFriend(uid: String)

    fun adapterRemoveFriend(uid: String)

    fun showProfile(uid: String)

    fun showToast(message: String)
}