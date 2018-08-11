package uk.co.appsbystudio.geoshare.friends.profile

interface ProfileView {

    fun setPosition(position: Int)

    fun showDialog(uid: String?)

    fun showError(error: String)

    fun closeProfile()
}