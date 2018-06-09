package uk.co.appsbystudio.geoshare.friends.profile.friends.pages.all

interface ProfileFriendsAllInteractor {

    interface OnFirebaseListener {
        fun add(uid: String?)
        fun remove(uid: String?)
        fun success(message: String)
        fun error(error: String)
    }

    fun getFriends(uid: String?, listener: OnFirebaseListener)

    fun sendFriendRequest(uid: String?, listener: OnFirebaseListener)
}