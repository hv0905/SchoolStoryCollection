package net.sakuratrak.schoolstorycollection.core;

import android.content.Context;

import java.io.File;
import java.util.Calendar;

import androidx.preference.PreferenceManager;

public final class AppSettingsMaster {

    public static final String SETTINGS_INTERNAL_WORKBOOK_LOC = "internalWorkbookLoc";
    public static final String SETTINGS_INTERNAL_WORKBOOK_LOC_PUBLIC = "public";
    public static final String SETTINGS_OPTIMIZE_IMAGE = "optimizeImage";
    public static final String SETTINGS_STARTUP_SUBJECT_ID = "startupSubjectId";
    public static final String SETTINGS_ALARM_TIME = "alarmTime";
    public static final String SETTINGS_SHOW_ALARM = "showAlarm";
    public static final String SETTINGS_QUIZ_AUTO_NEXT = "quiz_AutoNext";
    public static final String SETTINGS_DIALOG_HIDE_CONFIRM = "dialog_hide_confirm";
    public static final String SETTINGS_DIALOG_UNIT_HIDE_CONFIRM = "dialog_unit_hide_confirm";
    public static final String SETTINGS_QUIZ_SIZE = "quiz_size";

    public static File getWorkbookDb(Context context) {
        String set = PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_INTERNAL_WORKBOOK_LOC, null);
        if (set == null || set.equals(SETTINGS_INTERNAL_WORKBOOK_LOC_PUBLIC)) {
            File databases = new File(AppMaster.getPublicWorkbookDir(), AppMaster.DIR_DATABASES);
            return new File(databases, AppMaster.FILE_WORKBOOK_DB);
        } else {
            //internal
            return new File(context.getExternalFilesDir(AppMaster.DIR_DATABASES), AppMaster.FILE_WORKBOOK_DB);
        }
    }

    public static File getWorkBookImageDir(Context context) {
        String set = PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_INTERNAL_WORKBOOK_LOC, null);
        if (set == null || set.equals(SETTINGS_INTERNAL_WORKBOOK_LOC_PUBLIC)) {
            return new File(AppMaster.getPublicWorkbookDir(), AppMaster.DIR_IMAGES);
        } else {
            //internal
            return context.getExternalFilesDir(AppMaster.DIR_IMAGES);
        }
    }

    public static boolean getIfOptimizeImage(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_OPTIMIZE_IMAGE, false);
    }

    public static int getStartupSubjectId(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(SETTINGS_STARTUP_SUBJECT_ID, 0);
    }

    public static void setStartupSubjectId(Context context, int id) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(SETTINGS_STARTUP_SUBJECT_ID, id).apply();
    }

    public static Calendar getAlarmTime(Context context) {
        String str = PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_ALARM_TIME, "");
        String[] sp = str.split(":");
        if (sp.length != 2) return null;
        Calendar date = Calendar.getInstance();
        date.set(Calendar.HOUR_OF_DAY, Integer.parseInt(sp[0]));
        date.set(Calendar.MINUTE, Integer.parseInt(sp[1]));
        return date;
    }

    public static boolean getIfShowAlarm(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_SHOW_ALARM, false);
    }

    public static boolean getIfQuizAutoNext(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_QUIZ_AUTO_NEXT, false);
    }

    public static boolean getBooleanVal(Context context, String key, boolean def) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, def);
    }

    public static void setBooleanVal(Context context, String key, boolean val) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(key, val).apply();
    }

    public static int getQuizSize(Context context) {
        return Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_QUIZ_SIZE, "10"));
    }

}
