package uk.co.appsbystudio.geoshare.friends.manager.search

import com.google.firebase.auth.FirebaseAuth

class FriendSearchPresenterImpl(private val view: FriendSearchView, private val interactor: FriendSearchInteractor): FriendSearchPresenter, FriendSearchInteractor.OnFirebaseListener {

    override fun search(entry: String) {
        if (entry.isNotEmpty()) {
            view.clear()
            interactor.getSearchResults(entry, entry, this)
        } else {
            view.clear()
        }
    }

    override fun scroll(entry: String, exit: String) {
        interactor.getSearchResults(entry, exit, this)
    }

    override fun request(uid: String) {
        interactor.sendRequest(uid, this)
    }

    override fun addResults(uid: String?, name: String?) {
        val user = FirebaseAuth.getInstance().currentUser
        if (uid != null && uid != user?.uid && name != null) view.add(uid, name)
    }

    override fun success() {
        view.updateRecycler()
        view.showToast("Request sent")
    }

    override fun error(error: String) {
        view.showToast(error)
    }
}