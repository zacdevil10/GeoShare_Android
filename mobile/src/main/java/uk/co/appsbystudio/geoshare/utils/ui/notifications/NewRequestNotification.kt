package uk.co.appsbystudio.geoshare.utils.ui.notifications

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import uk.co.appsbystudio.geoshare.R

object NewRequestNotification {
    /**
     * The unique identifier for this type of notification.
     */
    private const val NOTIFICATION_TAG = "NewRequest"

    fun notify(context: Context, exampleString: String?, number: Int) {
        val builder = NotificationCompat.Builder(context, "request_channel")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_new_share_location)
                .setContentTitle(exampleString)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker(exampleString)
                .setNumber(number)
                .setAutoCancel(true)

        notify(context, builder.build())
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private fun notify(context: Context, notification: Notification) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("request_channel", "New location notification", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(notificationChannel)
        }
        nm.notify(NOTIFICATION_TAG, 0, notification)

    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_TAG, 0)
    }
}
