package com.detroitlabs.feedback;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import java.util.Random;

/**
 * Created by andrewgiang on 3/18/15.
 */
public class NotificationController {
    private static final int REQUEST_FEEDBACK = 22;
    private final int notificationId;

    private final NotificationManagerCompat manager;
    private final String title;
    private final String notificationContentText;
    private final int notificationIcon;

    public NotificationController(Context context, String title, String notificationContentText, @DrawableRes int notificationIcon) {
        this.title = title;
        this.notificationContentText = notificationContentText;
        this.notificationIcon = notificationIcon;
        this.manager = NotificationManagerCompat.from(context);
        notificationId = new Random().nextInt();
    }

    public void startNotification(Activity activity) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
        PendingIntent intent = PendingIntent.getBroadcast(activity, REQUEST_FEEDBACK, new Intent(Feedback.ACTION_FEEDBACK), PendingIntent.FLAG_CANCEL_CURRENT);
        builder.
                setSmallIcon(notificationIcon).
                setContentTitle(title).
                setContentText(notificationContentText).
                setContentIntent(intent);

        manager.notify(notificationId, builder.build());
    }

    public void stopNotification() {
        manager.cancelAll();
    }
}
