package net.sakuratrak.schoolstorycollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // 开机的时候需要重新设置提醒
        if (AppSettingsMaster.getIfShowAlarm(context)) {
            AlarmReceiver.setupAlarm(context,false);
        }
    }
}
