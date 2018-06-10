package uk.co.appsbystudio.geoshare.setup.fragments.permissions

class PermissionsPresenterImpl(private val view: PermissionsView) : PermissionsPresenter {

    override fun onResult(message: String) {
        view.onResult(message)
    }
}