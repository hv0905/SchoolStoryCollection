package net.sakuratrak.schoolstorycollection.core;

import java.util.Arrays;
import java.util.List;

public final class ImageAnswer extends Answer {
    public List<String> Image;

    public ImageAnswer() {
    }

    public ImageAnswer(List<String> image) {
        Image = image;
    }

    @Override
    public String toMetaData() {
        return AppHelper.stringList2String(Image);
    }

    public static ImageAnswer fromMetaData(String metaData){
        return new ImageAnswer(Arrays.asList(AppHelper.string2StringArray(metaData)));
    }
}
