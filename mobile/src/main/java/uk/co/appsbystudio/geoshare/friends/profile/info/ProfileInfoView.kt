package uk.co.appsbystudio.geoshare.friends.profile.info

import androidx.lifecycle.LiveData

interface ProfileInfoView {

    fun setShareText(string: String)

    fun setLocationItemText(address: LiveData<String>? = null, timestamp: String? = "Never")

    fun showShareDialog()

    fun deleteButtonVisibility(visible: Int)

    fun showToast(error: String)
}