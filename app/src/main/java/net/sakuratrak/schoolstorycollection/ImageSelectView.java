package net.sakuratrak.schoolstorycollection;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import me.kareluo.imaging.IMGEditActivity;
import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;

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
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        _images = new ArrayList<>();
        _mainAdapter = new ImageListAdapter(new ArrayList<>(), true);
        _mainAdapter.setAddButtonClicked(v -> CommonAlerts.AskPhoto(getContext(), (dialog, which) -> {
            //todo choose photo
            switch (which) {
                case 0: {
                    if (!PermissionAdmin.get(getActivity(), Manifest.permission.CAMERA, _codeCamera)) {
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
                if(AppSettingsMaster.getIfOptimizeImage(getContext()))
                    intent.putExtra(IMGEditActivity.EXTRA_ADD_CONTRAST,_addContrast);
                getActivity().startActivityForResult(intent, _codeEdit);
            }

        } else if (requestCode == _codeGet) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(getContext(), IMGEditActivity.class);
                assert data != null;
                intent.putExtra(IMGEditActivity.EXTRA_IMAGE_URI, data.getData());
                _currentTargetPhoto = new File(AppSettingsMaster.getWorkBookImageDir(getActivity()), UUID.randomUUID().toString() + ".jpg");
                intent.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, _currentTargetPhoto.getAbsolutePath());
                if(AppSettingsMaster.getIfOptimizeImage(getContext()))
                    intent.putExtra(IMGEditActivity.EXTRA_ADD_CONTRAST,_addContrast);
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
        ArrayList<ImageListAdapter.DataContext> dataContext = _mainAdapter.get_dataContext();
        dataContext.clear();
        for (int i = 0; i < _images.size(); i++) {
            String path = _images.get(i);
            ImageListAdapter.DataContext item = new ImageListAdapter.DataContext();
            item.imgSrc = Uri.fromFile(new File(AppSettingsMaster.getWorkBookImageDir(getActivity()), path));
            final int finalI = i;
            item.imageClicked = v -> showImageOptionMenu(finalI);
            dataContext.add(item);
        }
        _mainAdapter.set_dataContext(dataContext);
    }

    private void showImageOptionMenu(int index) {
        new AlertDialog.Builder(getContext()).setItems(R.array.imageOptionMenu, (dialog, which) -> {
            switch (which) {
                case 0:
                    //noinspection ResultOfMethodCallIgnored
                    new File(AppSettingsMaster.getWorkBookImageDir(getActivity()), _images.get(index)).delete();
                    _images.remove(index);
                    onItemToggle();
                    refresh();
                    break;
            }
        }).setPositiveButton(R.string.cancel, null).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == _codeCamera) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //continue
                takePhoto(requestCode);
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                if (((AppCompatActivity) getContext()).shouldShowRequestPermissionRationale(permissions[0])) {
                    Toast.makeText(getContext(), R.string.notice_permission_camera, Toast.LENGTH_LONG).show();
                } else {
                    //go to settings
                    new AlertDialog.Builder(getContext()).setTitle(R.string.errPermissionDenied).setMessage(R.string.setCameraPermission).setPositiveButton(R.string.confirm, null).show();
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

    private void onItemToggle(){
        if(_onItemToggle != null)
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

    /** disable:change to 1*/
    public void set_addContrast(float _addContrast) {
        this._addContrast = _addContrast;
    }
}
