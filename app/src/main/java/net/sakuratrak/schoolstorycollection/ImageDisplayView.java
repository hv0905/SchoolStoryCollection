package net.sakuratrak.schoolstorycollection;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import net.sakuratrak.schoolstorycollection.core.AppMaster;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.bean.ImageInfo;

public final class ImageDisplayView extends RecyclerView {

    private List<String> _images;
    private ImageListAdapter _mainAdapter;

    public ImageDisplayView(@NonNull Context context) {
        super(context);
        init();

    }

    public ImageDisplayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public ImageDisplayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    void init() {
        setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        _images = new ArrayList<>();
        _mainAdapter = new ImageListAdapter(new ArrayList<>(), false);
        setAdapter(_mainAdapter);
    }

    public void refresh() {
        ArrayList<ImageListAdapter.DataContext> dataContext = _mainAdapter.get_dataContext();
        dataContext.clear();
        for (int i = 0; i < _images.size(); i++) {
            String path = _images.get(i);
            ImageListAdapter.DataContext item = new ImageListAdapter.DataContext();
            item.imgSrc = Uri.fromFile(AppMaster.getImgFileDisplay(getContext(), path));
            final int finalI = i;
            item.imageClicked = v -> imgClick(finalI);
            dataContext.add(item);
        }
        _mainAdapter.set_dataContext(dataContext);
    }

    private void imgClick(int id) {

        ImagePreview
                .getInstance()
                .setContext(getActivity())
                .setIndex(id)
                .setImageInfoList(getInfoList())
                .setShowDownButton(true)
                .setLoadStrategy(ImagePreview.LoadStrategy.AlwaysOrigin)
                .setFolderName("DCIM/SchoolStoryCollection")
                .setScaleLevel(1, 5, 16)
                .setZoomTransitionDuration(300)
                .setEnableDragClose(true)// 是否启用上拉/下拉关闭。默认不启用
                .start();
    }

    private ArrayList<ImageInfo> getInfoList() {
        ArrayList<ImageInfo> result = new ArrayList<>();
        for (String item : _images) {
            ImageInfo dst = new ImageInfo();
            String url = Uri.fromFile(AppMaster.getImgFileDisplay(getContext(), item)).toString();
            dst.setOriginUrl(url);
            dst.setThumbnailUrl(url);
            result.add(dst);
        }
        return result;
    }

    public List<String> getImages() {
        return _images;
    }

    public void setImages(List<String> _images) {
        this._images = _images;
        refresh();
    }

    public Activity getActivity() {
        return (Activity) getContext();
    }
}
