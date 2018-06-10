package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.mutual

class ProfileFriendsMutualPresenterImpl(private val view: ProfileFriendsMutualView, private val interactor: ProfileFriendsMutualInteractor): ProfileFriendsMutualPresenter, ProfileFriendsMutualInteractor.OnFirebaseListener {

    override fun friends(uid: String) {
        interactor.getFriends(uid, this)
    }

    override fun stop() {
        interactor.removeListener()
    }

    override fun added(uid: String?) {
        view.addItem(uid)
    }

    override fun removed(uid: String?) {
        view.removeItem(uid)
    }

    override fun error(error: String) {
        view.showToast(error)
    }
}