package uk.co.appsbystudio.geoshare.utils

import android.graphics.BitmapFactory
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView
import uk.co.appsbystudio.geoshare.Application
import uk.co.appsbystudio.geoshare.R
import java.io.File

fun CircleImageView?.setProfilePicture(userId: String?, storageDirectory: String) {
    val fileCheck = File("$storageDirectory/$userId.png")

    if (fileCheck.exists()) {
        val imageBitmap = BitmapFactory.decodeFile("$storageDirectory/$userId.png")
        this?.setImageBitmap(imageBitmap)
    } else {
        //If the file doesn't exist, download from Firebase
        val storageReference = FirebaseStorage.getInstance().reference
        val profileRef = storageReference.child("profile_pictures/$userId.png")
        profileRef.getFile(Uri.fromFile(File("$storageDirectory/$userId.png")))
                .addOnSuccessListener {
                    val imageBitmap = BitmapFactory.decodeFile("$storageDirectory/$userId.png")
                    this?.setImageBitmap(imageBitmap)
                }
                .addOnFailureListener {
                    this?.setImageDrawable(Application.context?.resources?.getDrawable(R.drawable.ic_profile_picture, null))
                }
    }
}