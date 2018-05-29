package uk.co.appsbystudio.geoshare.setup

interface InitialSetupView {

    fun onNext()

    fun onBack()

    fun onFinish(radius: Int)

    fun hasPermissions(): Boolean

    fun requestPermissions()

    fun onShowProfileDialog()

    fun onError(error: String)
}