package uk.co.appsbystudio.geoshare.utils.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage

import java.io.File

import uk.co.appsbystudio.geoshare.MainActivity

import uk.co.appsbystudio.geoshare.utils.bitmapCanvas

private var image: Bitmap? = null

fun String.downloadProfilePicture(): Bitmap? {
    FirebaseStorage.getInstance().reference.child(FirebaseHelper.PROFILE_PICTURE + "/" + this + ".png")
            .getFile(Uri.fromFile(File(MainActivity.cacheDir.toString() + "/" + this + ".png")))
            .addOnSuccessListener { image = BitmapFactory.decodeFile(MainActivity.cacheDir.toString() + "/" + this + ".png") }
            .addOnFailureListener { image = null }

    return image?.bitmapCanvas(116, 155, false, 0, "")
}

