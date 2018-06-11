package uk.co.appsbystudio.geoshare.utils.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider

import java.io.File

import uk.co.appsbystudio.geoshare.BuildConfig
import uk.co.appsbystudio.geoshare.R

class ProfilePictureOptions : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val optionsMenu = AlertDialog.Builder(activity, R.style.DialogTheme)
        optionsMenu.setTitle("Change profile picture").setItems(R.array.profilePictureOptions) { dialog, which ->
            when (which) {
                0 -> {
                    val selectPicture = Intent()
                    selectPicture.type = "image/*"
                    selectPicture.action = Intent.ACTION_GET_CONTENT
                    activity.startActivityForResult(Intent.createChooser(selectPicture, "Select Picture"), 2)
                }
                1 -> {
                    val imageFile: File = createImageFile()

                    val uri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".fileProvider", imageFile)
                    val takePicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                    if (takePicture.resolveActivity(activity.packageManager) != null) {
                        activity.startActivityForResult(takePicture, 1)
                    }

                }
            }
        }.setNegativeButton("Cancel") { _, _ -> dismiss() }
        return optionsMenu.create()
    }

    private fun createImageFile(): File {
        val imageFileName = "profile_picture"
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File(storageDir, "$imageFileName.png")

        return image
    }
}
