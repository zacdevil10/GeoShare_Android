package uk.co.appsbystudio.geoshare.friends.manager

interface FriendsManagerInteractor {

    interface OnFirebaseRequestFinishedListener{
        fun friendAdded(key: String?, name: String?)

        fun friendRemoved(key: String?)

        fun error(error: String)
    }

    fun getFriends(listener: OnFirebaseRequestFinishedListener)

    fun removeListener()
}