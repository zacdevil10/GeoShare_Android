package uk.co.appsbystudio.geoshare.friends.profile.info

interface ProfileInfoView {

    fun setShareText(string: String)

    fun setLocationItemText(location: String, timestamp: String)

    fun showShareDialog()

    fun deleteButtonVisibility(visible: Int)

    fun showToast(error: String)
}