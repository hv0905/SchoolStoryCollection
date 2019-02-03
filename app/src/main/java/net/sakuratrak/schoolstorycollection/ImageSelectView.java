package net.sakuratrak.schoolstorycollection;

import android.Manifest.permission;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build.VERSION_CODES;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.widget.Toast;

import net.sakuratrak.schoolstorycollection.ImageListAdapter.DataContext;
import net.sakuratrak.schoolstorycollection.R.array;
import net.sakuratrak.schoolstorycollection.R.string;
import net.sakuratrak.schoolstorycollection.core.AppMaster;
import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import me.kareluo.imaging.IMGEditActivity;

/**
 * Provide a imageSelectView base on RecycleView
 * You need to invoke onActivityResult, onRequestPermissionResult in your activity 's event.
 */
public class ImageSelectView extends RecyclerView {

    private List<String> _images;
    private ImageListAdapter _mainAdapter;
    private int _codeCamera = 0;
    private int _codeGet = 1;
    private int _codeEdit = 2;
    private File _currentTempCameraPhoto;
    private File _currentTargetPhoto;
    private OnClickListener _onItemToggle;
    private float _addContrast = 1.8f;

    public ImageSelectView(@NonNull Context context) {
        super(context);
        init();
    }

    public ImageSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void init() {
        setLayoutManager(new LinearLayoutManager(getContext(), VERTICAL, false));
        _images = new ArrayList<>();
        _mainAdapter = new ImageListAdapter(new ArrayList<>(), true);
        _mainAdapter.setAddButtonClicked(v -> CommonAlerts.AskPhoto(getContext(), (dialog, which) -> {
            //todo choose photo
            switch (which) {
                case 0: {
                    if (!PermissionAdmin.get(getActivity(), permission.CAMERA, _codeCamera)) {
                        return;
                    }
                    takePhoto(_codeCamera);
                    break;
                }
                case 1: {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        getActivity().startActivityForResult(intent, _codeGet);
                    }
                    break;
                }
            }
        }, null).show());
        setAdapter(_mainAdapter);
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == _codeCamera) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), IMGEditActivity.class);
                intent.putExtra(IMGEditActivity.EXTRA_IMAGE_URI, Uri.fromFile(_currentTempCameraPhoto));
                _currentTargetPhoto = new File(AppSettingsMaster.getWorkBookImageDir(getActivity()), UUID.randomUUID().toString() + ".jpg");
                intent.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, _currentTargetPhoto.getAbsolutePath());
                if (AppSettingsMaster.getIfOptimizeImage(getContext()))
                    intent.putExtra(IMGEditActivity.EXTRA_ADD_CONTRAST, _addContrast);
                getActivity().startActivityForResult(intent, _codeEdit);
            }

        } else if (requestCode == _codeGet) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), IMGEditActivity.class);
                assert data != null;
                intent.putExtra(IMGEditActivity.EXTRA_IMAGE_URI, data.getData());
                _currentTargetPhoto = new File(AppSettingsMaster.getWorkBookImageDir(getActivity()), UUID.randomUUID().toString() + ".jpg");
                intent.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, _currentTargetPhoto.getAbsolutePath());
                if (AppSettingsMaster.getIfOptimizeImage(getContext()))
                    intent.putExtra(IMGEditActivity.EXTRA_ADD_CONTRAST, _addContrast);
                getActivity().startActivityForResult(intent, _codeEdit);
            }

        } else if (requestCode == _codeEdit) {
            if (resultCode == Activity.RESULT_OK) {
                if (_currentTempCameraPhoto != null) {
                    //noinspection ResultOfMethodCallIgnored
                    _currentTempCameraPhoto.delete();
                    _currentTempCameraPhoto = null;
                }
                _images.add(_currentTargetPhoto.getName());
                _currentTargetPhoto = null;
                onItemToggle();
                refresh();
            }
        }
    }

    public void refresh() {
        ArrayList<DataContext> dataContext = _mainAdapter.get_dataContext();
        dataContext.clear();
        for (int i = 0; i < _images.size(); i++) {
            String path = _images.get(i);
            DataContext item = new DataContext();
            item.imgSrc = Uri.fromFile(AppMaster.getImgFileDisplay(getContext(), path));
            final int finalI = i;
            item.imageClicked = v -> showImageOptionMenu(finalI);
            dataContext.add(item);
        }
        _mainAdapter.set_dataContext(dataContext);
    }

    private void showImageOptionMenu(int index) {
        new Builder(getContext()).setItems(array.imageOptionMenu, (dialog, which) -> {
            switch (which) {
                case 0:
                    //noinspection ResultOfMethodCallIgnored
//                    File toDel = new File(AppSettingsMaster.getWorkBookImageDir(getActivity()), _images.get(index));
//                    if (toDel.isFile()) toDel.delete();
                    AppMaster.removeImgFile(getContext(),_images.get(index));
                    _images.remove(index);
                    onItemToggle();
                    refresh();
                    break;
            }
        }).setPositiveButton(string.cancel, null).show();
    }


    @RequiresApi(api = VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == _codeCamera) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //continue
                takePhoto(requestCode);
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (((AppCompatActivity) getContext()).shouldShowRequestPermissionRationale(permissions[0])) {
                    Toast.makeText(getContext(), string.notice_permission_camera, Toast.LENGTH_LONG).show();
                } else {
                    //go to settings
                    new Builder(getContext()).setTitle(string.errPermissionDenied).setMessage(string.setCameraPermission).setPositiveButton(string.confirm, null).show();
                }
            }
        }
    }

    private void takePhoto(int requestCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //try {
        _currentTempCameraPhoto = new File(getContext().getExternalCacheDir(), UUID.randomUUID().toString() + ".jpg");
        Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".files", _currentTempCameraPhoto);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        getActivity().startActivityForResult(intent, requestCode);
    }

    public void setCodes(int code0, int code1, int code2) {
        _codeCamera = code0;
        _codeGet = code1;
        _codeEdit = code2;
    }

    public int get_codeCamera() {
        return _codeCamera;
    }

    public void set_codeCamera(int _codeCamera) {
        this._codeCamera = _codeCamera;
    }

    public int get_codeGet() {
        return _codeGet;
    }

    public void set_codeGet(int _codeGet) {
        this._codeGet = _codeGet;
    }

    public int get_codeEdit() {
        return _codeEdit;
    }

    public void set_codeEdit(int _codeEdit) {
        this._codeEdit = _codeEdit;
    }

    private AppCompatActivity getActivity() {
        return (AppCompatActivity) getContext();
    }

    public List<String> getImages() {
        return _images;
    }

    public void setImages(List<String> _images) {
        this._images = _images;
        refresh();
    }

    private void onItemToggle() {
        if (_onItemToggle != null)
            _onItemToggle.onClick(this);
    }

    public OnClickListener getOnItemToggleListener() {
        return _onItemToggle;
    }

    public void setOnItemToggleListener(OnClickListener _onItemToggle) {
        this._onItemToggle = _onItemToggle;
    }


    public float get_addContrast() {
        return _addContrast;
    }

    /**
     * disable:change to 1
     */
    public void set_addContrast(float _addContrast) {
        this._addContrast = _addContrast;
    }
}
