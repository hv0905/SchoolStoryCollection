package net.sakuratrak.schoolstorycollection;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;

import java.util.Calendar;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "quizNotify";

    private static final int DEFAULT_INS_ID = 1000;

    private static final String TAG = "alarmReceiver";

    public static void setupAlarm(Context context, boolean forceDayPlus) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        Calendar d = AppSettingsMaster.getAlarmTime(context);
        if (d == null) return;
        Log.d(TAG, "setupAlarm: your alarm is at " + d.get(Calendar.HOUR_OF_DAY) + ":" + d.get(Calendar.MINUTE));
        calendar.set(Calendar.HOUR_OF_DAY, d.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, d.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 0);
        boolean dayPlus = forceDayPlus;
        if ((!dayPlus) && calendar.before(Calendar.getInstance())) {
            dayPlus = true;
        }
        if (dayPlus) {
            //新的一天也多多关照啦
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Log.d(TAG, "setupAlarm: dayplus" + dayPlus);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        Log.d(TAG, "setupAlarm: next alarm:" + calendar.toString());
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);

    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "alert!!!");
        Uri sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notify);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                CharSequence name = "完成错题提醒";
                String description = "每天提醒你完成错题~\n要修改时间,请到应用设置中修改";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                channel.setSound(
                        sound,
                        new AudioAttributes.Builder().build()
                );
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("嗨,别来无恙啊")
                .setContentText("这是你今日份的练习,请收好~")
                .setSound(sound)
                .setSmallIcon(R.drawable.ic_icon_white_32dp)
                .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra(MainActivity.EXTRA_FROM_NOTIFY, true);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);


        notificationManager.notify(DEFAULT_INS_ID, builder.build());
    }
}
