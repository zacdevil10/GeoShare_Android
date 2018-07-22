package uk.co.appsbystudio.geoshare.friends.manager

interface FriendsManagerPresenter {

    fun friends()

    fun viewpagerItem(item: Int)

    fun search()

    fun invalidSession()

    fun stop()
}