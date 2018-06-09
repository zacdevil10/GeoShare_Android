package uk.co.appsbystudio.geoshare.friends.manager

interface FriendsManagerPresenter {

    fun viewpagerItem(item: Int)

    fun search()

    fun invalidSession()
}