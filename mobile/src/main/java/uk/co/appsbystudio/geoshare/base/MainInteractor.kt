package uk.co.appsbystudio.geoshare.base

interface MainInteractor {

    interface OnFirebaseRequestFinishedListener{
        fun friendAdded(key: String?, name: String?)

        fun friendRemoved(key: String?)

        fun trackingAdded(key: String?, trackingState: Boolean?)

        fun trackingRemoved(key: String?)

        fun profileUpdated()

        fun error(error: String)
    }

    fun getFriends(listener: MainInteractor.OnFirebaseRequestFinishedListener)

    fun getTrackingState(listener: MainInteractor.OnFirebaseRequestFinishedListener)

    fun setUpdatedProfileListener(listener: MainInteractor.OnFirebaseRequestFinishedListener)

    fun removeToken()

    fun removeAllListeners()

}