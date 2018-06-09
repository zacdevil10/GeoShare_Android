package uk.co.appsbystudio.geoshare.friends.manager

class FriendsManagerPresenterImpl(private val view: FriendsManagerView): FriendsManagerPresenter {

    override fun viewpagerItem(item: Int) {
        view.setViewpagerItem(item)
    }

    override fun search() {
        view.searchIntent()
    }

    override fun invalidSession() {
        view.finish()
    }
}