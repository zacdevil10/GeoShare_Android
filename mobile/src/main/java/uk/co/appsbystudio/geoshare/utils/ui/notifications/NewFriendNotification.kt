package uk.co.appsbystudio.geoshare.utils.ui.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.friends.manager.FriendsManager

object NewFriendNotification {

    private const val NOTIFICATION_TAG = "NewFriend"

    fun notify(context: Context,
               message: String?, number: Int) {
        val res = context.resources

        val result = Intent(context, FriendsManager::class.java)
        result.putExtra("tab", 1)

        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(FriendsManager::class.java)
        stackBuilder.addNextIntent(result)

        val pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val title = res.getString(R.string.new_friend_notification_title_template, message)
        val text = res.getString(R.string.new_friend_notification_placeholder_text_template)

        val builder = NotificationCompat.Builder(context, "friend_channel").apply {
            setSmallIcon(R.drawable.ic_stat_new_friend)
            setContentTitle(title)
            setContentText(text)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setTicker(message)
            setNumber(number)
            setContentIntent(pendingIntent)
            setAutoCancel(true)
        }

        notify(context, builder.build())
    }

    private fun notify(context: Context, notification: Notification) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("friend_channel", "New friend notification", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(notificationChannel)
        }
        nm.notify(NOTIFICATION_TAG, 0, notification)
    }

    /**
     * Cancels any notifications of this type previously shown using
     * [.notify].
     */
    fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        nm.cancel(NOTIFICATION_TAG, 0)
    }
}
