package net.sakuratrak.schoolstorycollection.core;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

import net.sakuratrak.schoolstorycollection.AndroidHelper;

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

    public static File getLocalThumbCacheDir(Context context){
        File cacheRoot = context.getCacheDir();
        File imgPreviewCache = new File(cacheRoot,"img-thumbs");
        if(!imgPreviewCache.isDirectory()){
            imgPreviewCache.mkdir();
        }
        return imgPreviewCache;
    }

    public static File getThumbFile(Context context,String imgId){
        File previewImgFile = new File(AppMaster.getLocalThumbCacheDir(context),imgId);
        if(!previewImgFile.exists()){
            //create preview img file
            File fullSizeImg = new File(AppSettingsMaster.getWorkBookImageDir(context),imgId);
            Bitmap thump = AndroidHelper.getThumbImg(fullSizeImg,500);
            try {
                AndroidHelper.saveBitmap2File(previewImgFile,thump,Bitmap.CompressFormat.JPEG,90);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return previewImgFile;
    }
}
