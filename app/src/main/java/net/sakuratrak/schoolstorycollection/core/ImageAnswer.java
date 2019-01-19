package net.sakuratrak.schoolstorycollection.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ImageAnswer extends Answer {
    public List<String> Image;

    public ImageAnswer() {
    }

    public ImageAnswer(List<String> image) {
        Image = image;
    }

    public static ImageAnswer fromMetaData(String metaData) {
        return new ImageAnswer(new ArrayList<>(Arrays.asList(AppHelper.string2StringArray(metaData))));
    }

    @Override
    public String toMetaData() {
        return AppHelper.stringList2String(Image);
    }
}
