package ru.mobnius.core.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.text.Html;

import androidx.core.app.NotificationCompat;

import ru.mobnius.core.R;

import static androidx.core.app.NotificationCompat.PRIORITY_DEFAULT;

public class NotificationUtil {
    public static String STABLE_CHANNEL_ID = "core-mobnius";
    public static String STABLE_CHANNEL_NAME = "mobnius notification";
    public static int STABLE_CHANNEL_NOTIFY_ID = 2314;

    public static int CHANNEL_NOTIFY_ID = 1411;

    public static Notification showStableMessage(Context context, String title, String message, int icon, PendingIntent intent) {
        NotificationChannel channel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel(NotificationUtil.STABLE_CHANNEL_ID,
                    NotificationUtil.STABLE_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);
        }

        Notification notification =  new NotificationCompat.Builder(context, NotificationUtil.STABLE_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(Html.fromHtml(message)).setPriority(PRIORITY_DEFAULT)
                .setSmallIcon(icon)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(intent)
                .setCategory(NotificationCompat.CATEGORY_SERVICE).build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        return notification;
    }

    public static void showMessage(Context context, String message, Intent intent, int CHANNEL_NOTIFY_ID) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, STABLE_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("Уведомление").setPriority(PRIORITY_DEFAULT)
                .setContentText(Html.fromHtml(message))
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE);

        if(intent != null) {
            PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentIntent(pi);
        }
        Notification notification = builder.build();
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        if (notificationManager != null) {
            notificationManager.notify(CHANNEL_NOTIFY_ID, notification);
        }
    }
}
