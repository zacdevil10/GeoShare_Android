package uk.co.appsbystudio.geoshare.friends.manager.pages.pending

interface FriendsPendingView {

    fun accept(uid: String, accept: Boolean)

    fun addIncoming(uid: String)

    fun addOutgoing(uid: String)

    fun removeIncoming(uid: String)

    fun removeOutgoing(uid: String)

    fun showNoRequestsText()

    fun showToast(message: String)
}