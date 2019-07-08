package uk.co.appsbystudio.geoshare.utils.ui.notifications

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import uk.co.appsbystudio.geoshare.R

object NewShareLocationNotification {

    private const val NOTIFICATION_TAG = "NewShareLocation"

    fun notify(context: Context,
               exampleString: String?, number: Int) {
        val res = context.resources


        val title = res.getString(R.string.new_share_location_notification_title_template, exampleString)
        val text = res.getString(R.string.new_share_location_notification_placeholder_text_template)

        val builder = NotificationCompat.Builder(context, "share_channel").apply {
            setDefaults(Notification.DEFAULT_ALL)
            setSmallIcon(R.drawable.ic_stat_new_share_location)
            setContentTitle(title)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setTicker(exampleString)
            setNumber(number)
            setStyle(NotificationCompat.BigTextStyle()
                    .setBigContentTitle(title)
                    .setSummaryText("New location"))
            setAutoCancel(true)
        }

        notify(context, builder.build())
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private fun notify(context: Context, notification: Notification) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("share_channel", "New location notification", NotificationManager.IMPORTANCE_DEFAULT)
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
