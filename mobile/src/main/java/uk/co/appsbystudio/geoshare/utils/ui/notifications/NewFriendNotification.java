package uk.co.appsbystudio.geoshare.utils.ui.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import uk.co.appsbystudio.geoshare.MainActivity;
import uk.co.appsbystudio.geoshare.R;
import uk.co.appsbystudio.geoshare.friends.FriendsManager;

public class NewFriendNotification {

    private static final String NOTIFICATION_TAG = "NewFriend";

    /*private DatabaseReference databaseReference;
    private FirebaseAuth auth;*/

    public static void notify(final Context context,
                              final String message, final int number) {
        final Resources res = context.getResources();

        Intent result = new Intent(context, FriendsManager.class);
        result.putExtra("tab", 1);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(FriendsManager.class);
        stackBuilder.addNextIntent(result);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // This image is used as the notification's large icon (thumbnail).
        final Bitmap picture = BitmapFactory.decodeResource(res, R.drawable.ic_profile_picture);

        final String title = res.getString(R.string.new_friend_notification_title_template, message);
        final String text = res.getString(R.string.new_friend_notification_placeholder_text_template);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "friend_channel")
                .setSmallIcon(R.drawable.ic_stat_new_friend)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLargeIcon(picture)
                .setTicker(message)
                .setNumber(number)
                .setContentIntent(pendingIntent)
                /*.addAction(
                        R.drawable.ic_close_white_48px,
                        res.getString(R.string.action_accept),
                        PendingIntent.getActivity(
                                context,
                                0,
                                Intent.createChooser(new Intent(Intent.ACTION_SEND)
                                        .setType("text/plain")
                                        .putExtra(Intent.EXTRA_TEXT, "Dummy text"), "Dummy title"),
                                PendingIntent.FLAG_UPDATE_CURRENT))
                .addAction(
                        R.drawable.ic_close_white_48px,
                        res.getString(R.string.action_decline),
                        null)*/

                // Automatically dismiss the notification when it is touched.
                .setAutoCancel(true);

        notify(context, builder.build());
    }

    private static void notify(final Context context, final Notification notification) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel("friend_channel", "New friend notification", NotificationManager.IMPORTANCE_DEFAULT);
                nm.createNotificationChannel(notificationChannel);
            }
            nm.notify(NOTIFICATION_TAG, 0, notification);
        }
    }

    /**
     * Cancels any notifications of this type previously shown using
     * {@link #notify(Context, String, int)}.
     */
    public static void cancel(final Context context) {
        final NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.cancel(NOTIFICATION_TAG, 0);
        }
    }

    /*private void accept(String uid) {
        if (auth.getCurrentUser() != null) {
            databaseReference.child("friends").child(auth.getCurrentUser().getUid()).child(uid).setValue(true);
            databaseReference.child("friends").child(uid).child(auth.getCurrentUser().getUid()).setValue(true);
        }

    }

    private void reject(String uid) {
        if (auth.getCurrentUser() != null) {
            databaseReference.child("pending").child(auth.getCurrentUser().getUid()).child(uid).removeValue();
            databaseReference.child("pending").child(uid).child(auth.getCurrentUser().getUid()).removeValue();
        }
    }*/
}
