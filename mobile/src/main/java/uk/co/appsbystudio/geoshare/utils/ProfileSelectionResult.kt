package uk.co.appsbystudio.geoshare.utils

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import uk.co.appsbystudio.geoshare.base.MainView
import uk.co.appsbystudio.geoshare.setup.InitialSetupView
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

class ProfileSelectionResult(private var main: MainView? = null, private var initial: InitialSetupView? = null) {

    fun profilePictureResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?, uid: String?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                1 -> {
                    val imageFileName = "profile_picture"
                    val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    val image = File(storageDir, "$imageFileName.png")

                    CropImage.activity(Uri.fromFile(image))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1).setRequestedSize(256, 256)
                            .setFixAspectRatio(true)
                            .start(activity)
                }
                2 -> {
                    val uri = data!!.data
                    if (uri != null)
                        CropImage.activity(uri)
                                .setGuidelines(CropImageView.Guidelines.ON)
                                .setAspectRatio(1, 1).setRequestedSize(256, 256)
                                .setFixAspectRatio(true).start(activity)
                }
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null && uid != null) {
            val result = CropImage.getActivityResult(data)

            if (resultCode == RESULT_OK) {
                val resultUri = result.uri

                val storageReference = FirebaseStorage.getInstance().reference
                val profileRef = storageReference.child("profile_pictures/$uid.png")
                profileRef.putFile(resultUri)
                        .addOnSuccessListener {
                            FirebaseDatabase.getInstance().getReference("picture").child(uid).setValue(Date().time)
                            initial?.onNext()

                            try {
                                val bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, resultUri)
                                val file = File(activity.cacheDir, "$uid.png")
                                val fileOutputStream = FileOutputStream(file)
                                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)

                                main?.updateProfilePicture()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }.addOnFailureListener { Toast.makeText(activity, "Hmm...Something went wrong.\nPlease check your internet connection and try again.", Toast.LENGTH_LONG).show() }
            }
        }
    }
}
