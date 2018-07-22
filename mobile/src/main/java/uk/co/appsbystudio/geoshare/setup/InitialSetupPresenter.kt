package uk.co.appsbystudio.geoshare.setup

interface InitialSetupPresenter {

    fun addDeviceToken()

    fun onPermissionsResult()

    fun onError(error: String)

}