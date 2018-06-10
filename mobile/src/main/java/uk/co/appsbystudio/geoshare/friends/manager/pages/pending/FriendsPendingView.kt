package uk.co.appsbystudio.geoshare.friends.manager.pages.pending

interface FriendsPendingView {

    fun addIncoming(uid: String)

    fun addOutgoing(uid: String)

    fun removeIncoming(uid: String)

    fun removeOutgoing(uid: String)

    fun showNoRequestsText()

    fun showToast(message: String)
}