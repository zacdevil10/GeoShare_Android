package uk.co.appsbystudio.geoshare.setup.fragments.radius

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import kotlinx.android.synthetic.main.fragment_radius_setup.*
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.setup.InitialSetupActivity
import uk.co.appsbystudio.geoshare.setup.InitialSetupView

class RadiusSetupFragment : Fragment(), RadiusSetupView {

    private var fragmentCallback: InitialSetupView? = null
    private var presenter: RadiusSetupPresenter? = null

    private var progressParameter: Int = 0

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        try {
            fragmentCallback = context as InitialSetupActivity
        } catch (e: ClassCastException) {
            throw ClassCastException(activity.toString() + "must implement InitialSetupView")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = RadiusSetupPresenterImpl(this)

        return inflater.inflate(R.layout.fragment_radius_setup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        seek_radius.max = 200
        seek_radius.progress = 100

        val seekListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                text_progress_radius.text = i.toString()
                text_progress_radius.measure(0, 0)

                val thumb = seek_radius.thumb.bounds

                progressParameter = thumb.centerX() - text_progress_radius.measuredWidth

                presenter?.onSeekBarPositionChanged(progressParameter)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        }

        seek_radius.setOnSeekBarChangeListener(seekListener)

        button_back_radius.setOnClickListener {
            fragmentCallback?.onBack()
        }

        button_finish_radius.setOnClickListener {
            fragmentCallback?.onFinish(text_progress_radius.text.toString().toInt())
        }
    }

    override fun updateSeekBarText(progress: Int) {
        val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.ABOVE, seek_radius.id)

        params.marginStart = progress
        params.addRule(RelativeLayout.ALIGN_START, seek_radius.id)

        text_progress_radius.layoutParams = params
    }
}
