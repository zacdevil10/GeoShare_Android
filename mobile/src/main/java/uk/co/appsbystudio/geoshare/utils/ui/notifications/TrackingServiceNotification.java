package uk.co.appsbystudio.geoshare.utils.ui.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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

                // Set appropriate defaults for the notification light, sound,
                // and vibration.
                .setDefaults(Notification.DEFAULT_ALL)

                // Set required fields, including the small icon, the
                // notification title, and text.
                .setSmallIcon(R.drawable.icon_white)
                .setContentTitle(title)

                // All fields below this line are optional.

                // Use a default priority (recognized on devices running Android
                // 4.1 or later)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                // Set ticker text (preview) information for this notification.
                .setTicker("Tracking service is running")

                // Show a number. This is useful when stacking notifications of
                // a single type.
                .setNumber(number)

                // If this notification relates to a past or upcoming event, you
                // should set the relevant time information using the setWhen
                // method below. If this call is omitted, the notification's
                // timestamp will by set to the time at which it was shown.
                // TODO: Call setWhen if this notification relates to a past or
                // upcoming event. The sole argument to this method should be
                // the notification timestamp in milliseconds.
                //.setWhen(...)

                // Set the pending intent to be initiated when the user touches
                // the notification.
                .setContentIntent(
                        PendingIntent.getActivity(
                                context,
                                3,
                                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")),
                                PendingIntent.FLAG_UPDATE_CURRENT))

                // Show an expanded list of items on devices running Android 4.1
                // or later.
                .setStyle(new NotificationCompat.InboxStyle()
                        .setBigContentTitle(title)
                        .setSummaryText("Tracking service"))

                .setAutoCancel(false)
                .setOngoing(true)

                .setLocalOnly(true);

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
