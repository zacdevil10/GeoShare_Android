package uk.co.appsbystudio.geoshare.friends.manager.pages.pending

interface FriendsPendingPresenter {

    fun friendRequests()

    fun requestAction(accept: Boolean, uid: String)

    fun stop()

}