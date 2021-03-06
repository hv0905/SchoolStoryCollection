package net.sakuratrak.schoolstorycollection.core;

import android.content.ContentValues;
import android.content.Context;
import android.provider.MediaStore;

import java.io.File;
import java.util.Calendar;
import java.util.List;

public final class AppHelper {

    public static String stringList2String(List<String> strList) {
        StringBuilder sb = new StringBuilder();
        for (String item :
                strList) {
            sb.append(item).append(';');
        }
        return sb.toString();
    }

    public static String[] string2StringArray(String str) {
        return str.split(";");
    }

    public static int getDeltaDay(Calendar first, Calendar second) {
        Calendar early;
        Calendar late;
        if (first.before(second)) {
            early = first;
            late = second;
        } else {
            early = second;
            late = first;
        }

        int dayEarly = early.get(Calendar.DAY_OF_YEAR);
        int dayLate = late.get(Calendar.DAY_OF_YEAR);
        if (early.get(Calendar.YEAR) < late.get(Calendar.YEAR)) {
            dayLate += 365;
        }
        return dayLate - dayEarly;

    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static boolean clearDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    //https://stackoverflow.com/questions/20859584/how-to-save-image-in-android-gallery
    public static void addImageToGallery(String filePath, Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}
