package uk.co.appsbystudio.geoshare.friends.profile

interface ProfileView {

    fun showDialog(uid: String?)

    fun showError(error: String)

    fun closeProfile()
}