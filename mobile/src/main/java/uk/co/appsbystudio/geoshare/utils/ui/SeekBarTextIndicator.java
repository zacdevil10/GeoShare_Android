package uk.co.appsbystudio.geoshare.utils.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;

public class SeekBarTextIndicator extends AppCompatSeekBar {

    public SeekBarTextIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Drawable mThumb;

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        mThumb = thumb;
    }

    public void getSeekBarThumb() {
    }
}
