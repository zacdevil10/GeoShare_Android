package uk.co.appsbystudio.geoshare.utils.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


class NoSwipeViewPager(context: Context, attributeSet: AttributeSet) : ViewPager(context, attributeSet) {

    private var paging: Boolean = false

    init {
        this.paging = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return this.paging && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return this.paging && super.onInterceptTouchEvent(ev)

    }

    fun setPagingEnabled(enabled: Boolean) {
        this.paging = enabled
    }
}
