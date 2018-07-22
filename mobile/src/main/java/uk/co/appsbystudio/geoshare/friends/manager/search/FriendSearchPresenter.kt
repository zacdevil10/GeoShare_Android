package uk.co.appsbystudio.geoshare.friends.manager.search

interface FriendSearchPresenter {

    fun search(entry: String)

    fun scroll(entry: String, exit: String)

    fun request(uid: String)
}