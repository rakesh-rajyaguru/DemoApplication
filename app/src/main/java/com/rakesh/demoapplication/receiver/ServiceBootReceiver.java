package com.rakesh.demoapplication.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.rakesh.demoapplication.R;
import com.rakesh.demoapplication.SensorService;

/**
 * Created by fabio
 * on 24/01/2016.
 */
public class
ServiceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(ServiceBootReceiver.class.getSimpleName(), "Booting Completed");
        context.startService(intent);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = new NotificationCompat.Builder(context)
                .setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("Booting Completed")
                .setContentTitle("Booting Completed")
                .setContentText("Booting Completed")
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notification);
    }
}
