package net.sakuratrak.schoolstorycollection.core;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;

import java.io.File;
import java.util.Map;

public final class AppSettingsMaster {

    private static final String SETTINGS_INTERNAL_WORKBOOK_LOC = "internalWorkbookLoc";
    private static final String SETTINGS_INTERNAL_WORKBOOK_LOC_PUBLIC = "public";
    private static final String SETTINGS_OPTIMIZE_IMAGE = "optimizeImage";
    private static final String SETTINGS_STARTUP_SUBJECT_ID = "startupSubjectId";

    public static Map<String,?> _settings;

    public static File getWorkbookDb(Context context){
        String set = PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_INTERNAL_WORKBOOK_LOC,null);
        if(set == null || set.equals(SETTINGS_INTERNAL_WORKBOOK_LOC_PUBLIC)) {
            File databases = new File(AppMaster.getPublicWorkbookDir(),AppMaster.DIR_DATABASES);
            return new File(databases,AppMaster.FILE_WORKBOOK_DB);
        }else{
            //internal
            return new File(context.getExternalFilesDir(AppMaster.DIR_DATABASES),AppMaster.FILE_WORKBOOK_DB);
        }
    }

    public static File getWorkBookImageDir(Context context){
        String set = PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_INTERNAL_WORKBOOK_LOC,null);
        if(set == null || set.equals(SETTINGS_INTERNAL_WORKBOOK_LOC_PUBLIC)){
            return new File(AppMaster.getPublicWorkbookDir(),AppMaster.DIR_IMAGES);
        }else{
            //internal
            return context.getExternalFilesDir(AppMaster.DIR_IMAGES);
        }
    }

    public static boolean getIfOptimizeImage(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SETTINGS_OPTIMIZE_IMAGE,false);
    }

    public static int getStartupSubjectId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(SETTINGS_STARTUP_SUBJECT_ID,0);
    }

    public static void setStartupSubjectId(Context context,int id){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(SETTINGS_STARTUP_SUBJECT_ID,id).apply();
    }

}
