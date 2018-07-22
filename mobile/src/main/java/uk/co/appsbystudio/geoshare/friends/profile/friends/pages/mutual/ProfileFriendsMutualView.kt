package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.mutual

interface ProfileFriendsMutualView {

    fun addItem(uid: String?)

    fun removeItem(uid: String?)

    fun showToast(message: String)
}