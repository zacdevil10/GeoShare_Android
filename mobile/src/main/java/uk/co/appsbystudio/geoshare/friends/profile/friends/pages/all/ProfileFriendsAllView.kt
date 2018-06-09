package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.all

interface ProfileFriendsAllView {

    fun addItem(uid: String?)

    fun removeItem(uid: String?)

    fun updateRecycler()

    fun showToast(message: String)
}