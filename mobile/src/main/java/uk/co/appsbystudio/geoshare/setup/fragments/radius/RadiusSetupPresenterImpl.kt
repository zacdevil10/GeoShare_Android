package uk.co.appsbystudio.geoshare.setup.fragments.radius

class RadiusSetupPresenterImpl(private val view: RadiusSetupView): RadiusSetupPresenter {

    override fun onSeekBarPositionChanged(position: Int) {
        view.updateSeekBarText(position)
    }

}