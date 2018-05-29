package uk.co.appsbystudio.geoshare.setup.fragments.permissions

class PermissionsPresenterImpl(private val permissionsView: PermissionsView) : PermissionsPresenter {

    override fun onResult(message: String) {
        permissionsView.onResult(message)
    }
}