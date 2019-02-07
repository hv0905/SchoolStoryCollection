package net.sakuratrak.schoolstorycollection.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import net.sakuratrak.schoolstorycollection.AndroidHelper;
import net.sakuratrak.schoolstorycollection.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AppMaster {

    public static final String APP_STORAGE_DIR_ROOT = "SchoolStoryCollection";
    public static final String NOMEDIA = ".nomedia";
    public static final String DIR_IMAGES = "images";
    public static final String DIR_DATABASES = "databases";

    public static final String FILE_WORKBOOK_DB = "workbook.db";

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static File getPublicWorkbookDir() {
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root, APP_STORAGE_DIR_ROOT);
        if (!dir.isDirectory()) {
            if (dir.isFile()) dir.delete();
            dir.mkdir();
        }
        File noMedia = new File(dir, NOMEDIA);
        if (!noMedia.exists()) {
            try {
                noMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File images = new File(dir, DIR_IMAGES);
        if (!images.isDirectory()) {
            if (images.isFile()) images.delete();
            images.mkdir();
        }
        File databases = new File(dir, DIR_DATABASES);
        if (!databases.isDirectory()) {
            if (databases.isFile()) databases.delete();
            databases.mkdir();
        }
        return dir;
    }

    public static File getLocalThumbCacheDir(Context context) {
        File cacheRoot = context.getCacheDir();
        File imgPreviewCache = new File(cacheRoot, "img-thumbs");
        if (!imgPreviewCache.isDirectory()) {
            //noinspection ResultOfMethodCallIgnored
            imgPreviewCache.mkdir();
        }
        return imgPreviewCache;
    }

    public static File getThumbFile(Context context, String imgId) {
        File previewImgFile = getThumbFileWithoutCreate(context, imgId);
        if (!previewImgFile.exists()) {
            //create preview img file
            File fullSizeImg = getImgFileDisplay(context, imgId);
            Bitmap thump = AndroidHelper.getThumbImg(fullSizeImg, 500);
            try {
                AndroidHelper.saveBitmap2File(previewImgFile, thump, Bitmap.CompressFormat.JPEG, 90);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return previewImgFile;
    }

    public static File getThumbFileWithoutCreate(Context context, String imgId) {
        return new File(AppMaster.getLocalThumbCacheDir(context), imgId);
    }

    public static void removeThumbFile(Context context, String imgId) {
        getThumbFileWithoutCreate(context, imgId).delete();
    }

    public static File getImgFileDisplay(Context context, String imgId) {
        File imgFile = getImgFile(context, imgId);
        if (imgFile.isFile()) return imgFile;
        try {
            imgFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //将备选考进去
        InputStream in = context.getResources().openRawResource(R.raw.img_notify_disappear);
        FileOutputStream out;
        try {
            out = new FileOutputStream(imgFile.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        byte[] buff = new byte[1024];
        int read;
        try {
            try {
                while ((read = in.read(buff)) > 0) {
                    out.write(buff, 0, read);
                }
            } finally {
                in.close();
                out.close();
            }
        } catch (IOException ioEx) {
            ioEx.printStackTrace();
        }
        return imgFile;
    }

    public static File getImgFile(Context context, String imgId) {
        return new File(AppSettingsMaster.getWorkBookImageDir(context), imgId);
    }

    public static void removeImgFile(Context context, String imgId) {
        getImgFile(context, imgId).delete();
    }

}
