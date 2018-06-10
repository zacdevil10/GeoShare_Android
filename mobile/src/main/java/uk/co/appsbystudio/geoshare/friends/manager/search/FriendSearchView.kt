package uk.co.appsbystudio.geoshare.friends.manager.search

interface FriendSearchView {

    fun add(uid: String, name: String)

    fun clear()

    fun updateRecycler()

    fun showToast(message: String)
}