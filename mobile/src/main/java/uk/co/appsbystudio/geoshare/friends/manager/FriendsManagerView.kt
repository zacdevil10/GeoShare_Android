package uk.co.appsbystudio.geoshare.friends.manager

interface FriendsManagerView {

    fun setViewpagerItem(item: Int)

    fun searchIntent()

    fun fabState(visible: Boolean)

    fun finish()
}