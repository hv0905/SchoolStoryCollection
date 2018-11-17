package sakuratrak.schoolstorycollection.core;

import android.content.Context;
import android.support.v7.preference.PreferenceManager;

import java.io.File;
import java.util.Map;

public final class AppSettingsMaster {

    private static final String SETTINGS_INTERNAL_WORKBOOK_LOC = "internalWorkbookLoc";
    private static final String SETTINGS_INTERNAL_WORKBOOK_LOC_PUBLIC = "public";

    public static Map<String,?> _settings;

    public static File getWorkbookDb(Context context){
        if(_settings == null) refreshSetting(context);
        if(_settings.get(SETTINGS_INTERNAL_WORKBOOK_LOC) == null || _settings.get(SETTINGS_INTERNAL_WORKBOOK_LOC).equals(SETTINGS_INTERNAL_WORKBOOK_LOC_PUBLIC)) {
            File databases = new File(AppMaster.getPublicWorkbookDir(),AppMaster.DIR_DATABASES);
            return new File(databases,AppMaster.FILE_WORKBOOK_DB);
        }else{
            //internal
            return new File(context.getExternalFilesDir(AppMaster.DIR_DATABASES),AppMaster.FILE_WORKBOOK_DB);
        }
    }

    public static File getWorkBookImageDir(Context context){
        if(_settings == null) refreshSetting(context);
        if(_settings.get(SETTINGS_INTERNAL_WORKBOOK_LOC) == null || _settings.get(SETTINGS_INTERNAL_WORKBOOK_LOC).equals(SETTINGS_INTERNAL_WORKBOOK_LOC_PUBLIC)){
            return new File(AppMaster.getPublicWorkbookDir(),AppMaster.DIR_IMAGES);
        }else{
            //internal
            return context.getExternalFilesDir(AppMaster.DIR_IMAGES);
        }
    }

    public static void refreshSetting(Context context){
        _settings = PreferenceManager.getDefaultSharedPreferences(context).getAll();
    }


}
