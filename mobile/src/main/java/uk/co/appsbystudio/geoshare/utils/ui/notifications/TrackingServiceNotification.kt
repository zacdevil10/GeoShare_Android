package uk.co.appsbystudio.geoshare.utils.ui.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import uk.co.appsbystudio.geoshare.R

object TrackingServiceNotification {

    private const val NOTIFICATION_TAG = "TrackingService"

    fun notify(context: Context, number: Int) {
        val res = context.resources

        val exampleItem = SpannableStringBuilder()
        exampleItem.append("Dummy")
        exampleItem.setSpan(ForegroundColorSpan(Color.WHITE), 0, exampleItem.length, 0)
        exampleItem.append("   Example content")

        val title = res.getString(R.string.tracking_service_notification_title_template)

        val builder = NotificationCompat.Builder(context, "tracking_channel").apply {
            setSmallIcon(R.drawable.icon_white)
            setContentTitle(title)
            setTicker("Tracking service is running")
            setNumber(number)
            setStyle(NotificationCompat.InboxStyle()
                    .setBigContentTitle(title)
                    .setSummaryText("Tracking service"))
            setAutoCancel(false)
            setOngoing(true)
            setLocalOnly(true)
            priority = NotificationCompat.PRIORITY_MIN
        }

        notify(context, builder.build())
    }

    fun notify(context: Context, notification: Notification) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("tracking_channel", "Tracking notification", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(notificationChannel)
        }
        nm.notify(NOTIFICATION_TAG, 3, notification)

    }

    /**
     * Cancels any notifications of this type previously shown using
     * [.notify].
     */
    fun cancel(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(NOTIFICATION_TAG, 3)
    }
}
