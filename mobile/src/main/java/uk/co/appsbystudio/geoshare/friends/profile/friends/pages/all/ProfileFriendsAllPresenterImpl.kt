package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.all

class ProfileFriendsAllPresenterImpl(private val view: ProfileFriendsAllView, private val interactor: ProfileFriendsAllInteractor):
        ProfileFriendsAllPresenter, ProfileFriendsAllInteractor.OnFirebaseListener {

    override fun friends(uid: String) {
        interactor.getFriends(uid, this)
    }

    override fun request(uid: String) {
        interactor.sendFriendRequest(uid, this)
    }

    override fun add(uid: String?) {
        view.addItem(uid)
    }

    override fun remove(uid: String?) {
        view.removeItem(uid)
    }

    override fun success(message: String) {
        view.showToast(message)
    }

    override fun error(error: String) {
        view.showToast(error)
    }
}