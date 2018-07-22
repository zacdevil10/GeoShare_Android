package uk.co.appsbystudio.geoshare.friends.manager.pages.pending

import uk.co.appsbystudio.geoshare.friends.manager.FriendsManager
import uk.co.appsbystudio.geoshare.utils.firebase.AddFriendsInfo

class FriendsPendingPresenterImpl(private val view: FriendsPendingView, private val interactor: FriendsPendingInteractor): FriendsPendingPresenter, FriendsPendingInteractor.OnFirebaseListener {

    override fun friendRequests() {
        interactor.getRequests(this)
    }

    override fun requestAction(accept: Boolean, uid: String) {
        if (accept) {
            interactor.acceptRequest(uid)
        } else {
            interactor.rejectRequest(uid)
        }
    }

    override fun stop() {
        interactor.removeListener()
    }

    override fun add(uid: String?, info: AddFriendsInfo?) {
        if (uid != null && info != null) {
            FriendsManager.pendingUid[uid] = info.outgoing
            if (info.outgoing) {
                view.addOutgoing(uid)
            } else {
                view.addIncoming(uid)
            }
            view.showNoRequestsText()
        }
    }

    override fun remove(uid: String?, info: AddFriendsInfo?) {
        if (uid != null) {
            if (FriendsManager.pendingUid.containsKey(uid)) FriendsManager.pendingUid.remove(uid)
            if (info != null && info.outgoing) {
                view.removeOutgoing(uid)
            } else {
                view.removeIncoming(uid)
            }
            view.showNoRequestsText()
        }
    }

    override fun error(error: String) {
        view.showToast(error)
    }
}