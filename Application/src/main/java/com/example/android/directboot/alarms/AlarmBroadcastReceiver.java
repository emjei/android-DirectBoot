package com.example.android.directboot.alarms;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.example.android.directboot.R;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    public static final String ALARM_WENT_OFF_ACTION = AlarmBroadcastReceiver.class.getName()
            + ".ALARM_WENT_OFF";


    public static final String KEY_ALARM_ID = "alarm_id";

    public static final String KEY_ALARM_MONTH = "alarm_month";

    public static final String KEY_ALARM_DATE = "alarm_date";

    public static final String KEY_ALARM_HOUR = "alarm_hour";

    public static final String KEY_ALARM_MINUTE = "alarm_minute";

    @Override
    public void onReceive(Context context, Intent intent) {
        Alarm alarm = AlarmUtil.readAlarm(intent.getExtras());

        NotificationManager notificationManager = context
                .getSystemService(NotificationManager.class);

        String channelId = "default";
        initChannels(notificationManager, channelId);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_fbe_notification)
                        .setCategory(Notification.CATEGORY_ALARM)
                        .setSound(Settings.System.DEFAULT_ALARM_ALERT_URI)
                        .setContentTitle(context.getString(R.string.alarm_went_off, alarm.hour,
                                alarm.minute));
        notificationManager.notify(alarm.id, builder.build());

        AlarmStorage alarmStorage = new AlarmStorage(context);
        alarmStorage.deleteAlarm(alarm);
        Intent wentOffIntent = new Intent(ALARM_WENT_OFF_ACTION);
        wentOffIntent.putExtras(AlarmUtil.writeAlarm(alarm));
        LocalBroadcastManager.getInstance(context).sendBroadcast(wentOffIntent);
    }

    private static void initChannels(NotificationManager notificationManager, String channelId) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                channelId,
                "DirectBoot",
                NotificationManager.IMPORTANCE_HIGH
        );

        notificationManager.createNotificationChannel(channel);
    }
}
