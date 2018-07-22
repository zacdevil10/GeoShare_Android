package uk.co.appsbystudio.geoshare.utils.ui.notifications

import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import uk.co.appsbystudio.geoshare.R
import uk.co.appsbystudio.geoshare.base.MainActivity

object NewRequestNotification {

    fun notify(context: Context, title: String?, number: Int, id: String?) {
        val builder = NotificationCompat.Builder(context, "request_channel")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.ic_stat_new_share_location)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setNumber(number)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, Intent(context, MainActivity::class.java), 0))

        notify(context, builder.build(), id)
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    private fun notify(context: Context, notification: Notification, tag: String?) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel("request_channel", "New location notification", NotificationManager.IMPORTANCE_DEFAULT)
            nm.createNotificationChannel(notificationChannel)
        }
        nm.notify(tag, 3, notification)
    }

    @TargetApi(Build.VERSION_CODES.ECLAIR)
    fun cancel(context: Context?, id: String?) {
        val nm = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(id, 3)
    }
}
