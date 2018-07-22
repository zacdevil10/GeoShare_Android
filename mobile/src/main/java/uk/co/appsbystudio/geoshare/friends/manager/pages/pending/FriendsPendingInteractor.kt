package uk.co.appsbystudio.geoshare.friends.manager.pages.pending

import uk.co.appsbystudio.geoshare.utils.firebase.AddFriendsInfo

interface FriendsPendingInteractor {

    interface OnFirebaseListener {
        fun add(uid: String?, info: AddFriendsInfo?)
        fun remove(uid: String?, info: AddFriendsInfo?)
        fun error(error: String)
    }

    fun getRequests(listener: OnFirebaseListener)

    fun acceptRequest(uid: String)

    fun rejectRequest(uid: String)

    fun removeListener()
}