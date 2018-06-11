package uk.co.appsbystudio.geoshare.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect

import uk.co.appsbystudio.geoshare.Application
import uk.co.appsbystudio.geoshare.R

fun Bitmap?.bitmapCanvas(w: Int, h: Int): Bitmap {
    val config = Bitmap.Config.ARGB_8888
    val bmp = Bitmap.createBitmap(w, h, config)
    val canvas = Canvas(bmp)

    val mapMarker = BitmapFactory.decodeResource(Application.context?.resources, R.drawable.map_marker_point_shadow)
    val scaledMarker = Bitmap.createScaledBitmap(mapMarker, 116, 155, false)

    canvas.drawBitmap(scaledMarker, 0f, 0f, null)
    if (this != null) canvas.drawBitmap(this.crop(), 18f, 22f, null)

    return bmp
}

private fun Bitmap.crop(): Bitmap {
    val output = Bitmap.createBitmap(this.width,
            this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)

    val color = -0xbdbdbe
    val paint = Paint()
    val rect = Rect(0, 0, this.width, this.height)

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = color
    canvas.drawCircle((this.width / 2).toFloat(), (this.height / 2).toFloat(),
            (this.width / 2).toFloat(), paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(this, rect, rect, paint)
    return Bitmap.createScaledBitmap(output, 80, 80, false)
}