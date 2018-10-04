package sakuratrak.schoolstorycollection;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class AndroidHelper {
    public static File createLocalImageFile(Context context) throws IOException {
        File imgDir = context.getExternalCacheDir();
        return File.createTempFile("img_",".jpg",imgDir);
    }
}
