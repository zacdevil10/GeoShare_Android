package uk.co.appsbystudio.geoshare.friends.manager.search

interface FriendSearchInteractor {

    interface OnFirebaseListener {
        fun addResults(uid: String?, name: String?)
        fun success()
        fun error(error: String)
    }

    fun getSearchResults(entry: String, exit: String, listener: OnFirebaseListener)

    fun sendRequest(uid: String, listener: OnFirebaseListener)
}