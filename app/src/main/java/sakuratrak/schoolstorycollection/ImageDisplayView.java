package sakuratrak.schoolstorycollection;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import java.io.File;
import java.util.ArrayList;

import cc.shinichi.library.ImagePreview;
import cc.shinichi.library.bean.ImageInfo;
import sakuratrak.schoolstorycollection.core.AppSettingsMaster;

public final class ImageDisplayView extends RecyclerView {

    private ArrayList<String> _images;
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

    void init(){
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
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
            item.imgSrc = Uri.fromFile(new File(AppSettingsMaster.getWorkBookImageDir(getActivity()), path));
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

    private ArrayList<ImageInfo> getInfoList(){
        ArrayList<ImageInfo> result = new ArrayList<>();
        for(String item : _images){
            ImageInfo dst = new ImageInfo();
            String url = Uri.fromFile(new File(AppSettingsMaster.getWorkBookImageDir(getActivity()), item)).toString();
            dst.setOriginUrl(url);
            dst.setThumbnailUrl(url);
            result.add(dst);
        }
        return result;
    }

    public void setImages(ArrayList<String> _images) {
        this._images = _images;
        refresh();
    }

    public ArrayList<String> getImages() {
        return _images;
    }

    public Activity getActivity(){
        return (Activity)getContext();
    }
}
