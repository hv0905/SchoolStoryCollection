package me.kareluo.imaging.core.file;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import me.kareluo.imaging.Imaging;

public class IMGContentDecoder extends IMGDecoder {

    private ContentResolver _resolver;

    public IMGContentDecoder(Uri uri) {
        super(uri);
    }

    public IMGContentDecoder(Uri uri,ContentResolver resolver){
        super(uri);
        _resolver = resolver;
    }


    @Override
    public Bitmap decode(BitmapFactory.Options options) {
        if(_resolver == null)
            return null;
        InputStream is;
        try {
            is = _resolver.openInputStream(getUri());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        return BitmapFactory.decodeStream(is,null,options);
    }

    @Override
    public int getExifRotationAngle(){
        int result = 0;
        try {
            InputStream is = _resolver.openInputStream(getUri());
            result = Imaging.getExifOrientation(is);
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;

    }

    public ContentResolver getResolver(){
        return _resolver;
    }

    public void setResolver(ContentResolver resolver){
        _resolver = resolver;
    }
}
