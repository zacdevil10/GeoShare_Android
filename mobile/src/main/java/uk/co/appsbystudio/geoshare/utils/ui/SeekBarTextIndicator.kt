package uk.co.appsbystudio.geoshare.utils.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatSeekBar
import android.util.AttributeSet

class SeekBarTextIndicator(context: Context, attrs: AttributeSet) : AppCompatSeekBar(context, attrs) {

    private var mThumb: Drawable? = null

    override fun setThumb(thumb: Drawable) {
        super.setThumb(thumb)
        mThumb = thumb
    }
}
