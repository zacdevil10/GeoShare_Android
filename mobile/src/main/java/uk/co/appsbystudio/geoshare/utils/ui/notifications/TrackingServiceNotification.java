package uk.co.appsbystudio.geoshare.utils.ui.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;

import uk.co.appsbystudio.geoshare.R;

public class TrackingServiceNotification {

    private static final String NOTIFICATION_TAG = "TrackingService";

    public static void notify(final Context context, final int number) {
        final Resources res = context.getResources();

        final SpannableStringBuilder exampleItem = new SpannableStringBuilder();
        exampleItem.append("Dummy");
        exampleItem.setSpan(new ForegroundColorSpan(Color.WHITE), 0, exampleItem.length(), 0);
        exampleItem.append("   Example content");

        final String title = res.getString(R.string.tracking_service_notification_title_template);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "tracking_channel")
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.drawable.icon_white)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setTicker("Tracking service is running")
                .setNumber(number)
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle(title)
                        .setSummaryText("Tracking service"))
                .setAutoCancel(false)
                .setOngoing(true)
                .setLocalOnly(true)
                .setPriority(Notification.PRIORITY_LOW);

        notify(context, builder.build());
    }

    public static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("tracking_channel", "Tracking notification", NotificationManager.IMPORTANCE_DEFAULT);
                nm.createNotificationChannel(notificationChannel);
            }
            nm.notify(NOTIFICATION_TAG, 3, notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, int)}.
     */
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(NOTIFICATION_TAG, 3);
        }
    }
}
