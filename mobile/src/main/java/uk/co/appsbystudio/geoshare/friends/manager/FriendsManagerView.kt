package uk.co.appsbystudio.geoshare.friends.manager

interface FriendsManagerView {

    fun updateFriendsList(uid: String? = null, name: String? = null)

    fun removeFromFriendList(uid: String?)

    fun setViewpagerItem(item: Int)

    fun searchIntent()

    fun fabState(visible: Boolean)

    fun finish()

    fun showToast(message: String)
}