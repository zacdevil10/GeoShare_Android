package uk.co.appsbystudio.geoshare.setup.fragments.radius

class RadiusSetupPresenterImpl(private val radiusSetupView: RadiusSetupView): RadiusSetupPresenter {

    override fun onSeekBarPositionChanged(position: Int) {
        radiusSetupView.updateSeekBarText(position)
    }

}