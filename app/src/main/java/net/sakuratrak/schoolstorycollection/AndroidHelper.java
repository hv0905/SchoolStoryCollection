package net.sakuratrak.schoolstorycollection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AndroidHelper {
    public static File createLocalImageFile(Context context) throws IOException {
        File imgDir = context.getExternalCacheDir();
        return File.createTempFile("img_", ".jpg", imgDir);
    }

    //android的ThumbnailUtils效率太低了

    /**
     * Generate a ThumbImage without load the original bitmap twice
     */
    public static Bitmap getThumbImg(String src, int maxSize) {
        BitmapFactory.Options input = new BitmapFactory.Options();
        input.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(src, input);
        int maxVal = input.outWidth > input.outHeight ? input.outWidth : input.outHeight;
        double ratio = (double) maxVal / maxSize;

        BitmapFactory.Options output = new BitmapFactory.Options();

        output.inSampleSize = (int) ratio;

        return BitmapFactory.decodeFile(src, output);


    }

    public static Bitmap getThumbImg(File src, int maxSize) {
        return getThumbImg(src.getAbsolutePath(), maxSize);
    }

    public static boolean saveBitmap2File(File dst, Bitmap src, Bitmap.CompressFormat format, int quality) throws IOException {
        if (!dst.isFile()) {
            if (!dst.createNewFile()) {
                return false;
            }
        }
        FileOutputStream output = new FileOutputStream(dst);
        src.compress(format, quality, output);
        output.flush();
        output.close();
        return true;
    }

    public static boolean saveBitmap2File(File dst, Bitmap src) throws IOException {
        return saveBitmap2File(dst, src, Bitmap.CompressFormat.PNG, 100);
    }


}
