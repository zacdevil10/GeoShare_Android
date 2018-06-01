package uk.co.appsbystudio.geoshare.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF

import uk.co.appsbystudio.geoshare.Application
import uk.co.appsbystudio.geoshare.R

import uk.co.appsbystudio.geoshare.utils.ellipsize


fun Bitmap?.bitmapCanvas(w: Int, h: Int, selected: Boolean, alpha: Int, address: String = ""): Bitmap {
    val config = Bitmap.Config.ARGB_8888
    val bmp = Bitmap.createBitmap(w, h, config)
    val canvas = Canvas(bmp)

    val mapMarker = BitmapFactory.decodeResource(Application.getContext().resources, R.drawable.map_marker_point_shadow)
    val scaledMarker = Bitmap.createScaledBitmap(mapMarker, 116, 155, false)

    if (selected) {
        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.alpha = alpha
        paint.isAntiAlias = true

        val rect = RectF(58f, 16f, (canvas.width - (canvas.height - 20) / 2).toFloat(), (canvas.height - 20).toFloat())
        val rectF = RectF(58f, 16f, canvas.width.toFloat(), (canvas.height - 20).toFloat())
        canvas.drawRect(rect, paint)
        canvas.drawRoundRect(rectF, ((canvas.height - 20) / 2).toFloat(), ((canvas.height - 20) / 2).toFloat(), paint)

        val split = address.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        val greenPaint = Paint()
        greenPaint.color = Application.getContext().resources.getColor(R.color.colorPrimary)
        greenPaint.style = Paint.Style.FILL
        greenPaint.textSize = 32f
        greenPaint.alpha = alpha
        greenPaint.isAntiAlias = true

        canvas.drawText(split[0].ellipsize(22), 120f, 48f, greenPaint)
        canvas.drawText(split[1].ellipsize(22), 120f, 84f, greenPaint)
        canvas.drawText(split[2].ellipsize(22), 120f, 122f, greenPaint)
    }

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

