package uk.co.appsbystudio.geoshare.friends.manager.pages.current

interface FriendsInteractor {

    interface OnFirebaseListener {
        fun add(uid: String?)
        fun remove(uid: String?)
        fun unfriended(uid: String)
        fun error(error: String)
    }

    fun getFriends(listener: OnFirebaseListener)

    fun removeFriend(uid: String, listener: OnFirebaseListener)

    fun removeListener()
}