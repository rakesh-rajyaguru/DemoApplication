package com.rakesh.demoapplication.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.rakesh.demoapplication.SensorService;

/**
 * Created by fabio
 * on 24/01/2016.
 */
public class SensorRestarterBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase("com.rakesh.demoapplication.RestartSensor")) {
            context.startService(new Intent(context, SensorService.class));
            Toast.makeText(context, "\"Service Restart\"", Toast.LENGTH_SHORT).show();
//            PendingIntent contentIntent = PendingIntent.getActivity(context, 1, intent,
//                    PendingIntent.FLAG_UPDATE_CURRENT);
//            NotificationManager notificationManager = (NotificationManager) context
//                    .getSystemService(Context.NOTIFICATION_SERVICE);
//            Notification notification = new NotificationCompat.Builder(context)
//                    .setAutoCancel(false)
//                    .setDefaults(Notification.DEFAULT_ALL)
//                    .setWhen(System.currentTimeMillis())
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setTicker("Service Restart")
//                    .setContentTitle("Service Restart")
//                    .setContentText("Service Restart")
//                    .setContentIntent(contentIntent)
//                    .setAutoCancel(true)
//                    .build();
//            notification.flags = Notification.FLAG_AUTO_CANCEL;
//            notificationManager.notify(1, notification);
        }

    }


}
