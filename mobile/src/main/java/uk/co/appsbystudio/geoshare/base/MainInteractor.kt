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

    fun getFriends(listener: OnFirebaseRequestFinishedListener)

    fun getTrackingState(listener: OnFirebaseRequestFinishedListener)

    fun setUpdatedProfileListener(listener: OnFirebaseRequestFinishedListener)

    fun removeToken()

    fun removeAllListeners()

}