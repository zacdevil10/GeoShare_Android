package uk.co.appsbystudio.geoshare.friends.manager

class FriendsManagerPresenterImpl(private val view: FriendsManagerView, private val interactor: FriendsManagerInteractor): FriendsManagerPresenter, FriendsManagerInteractor.OnFirebaseRequestFinishedListener {

    override fun friends() {
        interactor.getFriends(this)
    }

    override fun viewpagerItem(item: Int) {
        view.setViewpagerItem(item)
    }

    override fun search() {
        view.searchIntent()
    }

    override fun invalidSession() {
        view.finish()
    }

    override fun stop() {
        interactor.removeListener()
    }

    override fun friendAdded(key: String?, name: String?) {
        view.updateFriendsList(key, name)
    }

    override fun friendRemoved(key: String?) {
        view.removeFromFriendList(key)
    }

    override fun error(error: String) {
        view.showToast(error)
    }
}