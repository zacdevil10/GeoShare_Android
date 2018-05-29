package uk.co.appsbystudio.geoshare.setup.fragments.profile

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class SetupProfilePresenterImpl(private val setupProfileView: SetupProfileView) : SetupProfilePresenter {

    override fun imageAvailable() {
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val uid = user.uid
            FirebaseStorage.getInstance().reference.child("profile_pictures/$uid.png").downloadUrl.addOnSuccessListener {
                setupProfileView.updateUIText()
            }
        }
    }

}