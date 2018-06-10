package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.mutual

interface ProfileFriendsMutualInteractor {

    interface OnFirebaseListener {
        fun added(uid: String?)
        fun removed(uid: String?)
        fun error(error: String)
    }

    fun getFriends(uid: String, listener: OnFirebaseListener)

    fun removeListener()
}