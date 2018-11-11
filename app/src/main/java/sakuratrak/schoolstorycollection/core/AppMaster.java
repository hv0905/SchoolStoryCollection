package sakuratrak.schoolstorycollection.core;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

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
}
