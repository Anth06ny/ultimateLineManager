package com.formation.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {

    private static final int NOTIFICATION_REQUEST_CODE = 13; //au hasard
    private static final int NOTIFICATION_ID = 14; //au hasard

    //UNiquemeent pour JellyBean
    public static void createNotification(final Context context, Class<?> activityToLaunchOnClick) {

        final NotificationManager mNotification = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Ce qu'on lance au clic sur la notification
        final Intent launchNotifiactionIntent = new Intent(context, activityToLaunchOnClick);
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_REQUEST_CODE, launchNotifiactionIntent,
                PendingIntent.FLAG_ONE_SHOT);

        //La notification
        final Notification.Builder builder = new Notification.Builder(context).setWhen(System.currentTimeMillis()).setTicker("Ticker")
                .setSmallIcon(R.drawable.ic_launcher).setContentTitle("ContentTitle").setContentText("ContentText").setContentIntent(pendingIntent);

        //création de la notification
        mNotification.notify(NOTIFICATION_ID, builder.build());
    }
}
