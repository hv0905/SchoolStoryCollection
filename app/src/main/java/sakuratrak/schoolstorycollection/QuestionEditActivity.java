package sakuratrak.schoolstorycollection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import me.kareluo.imaging.IMGEditActivity;
import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import sakuratrak.schoolstorycollection.core.QuestionInfo;
import sakuratrak.schoolstorycollection.core.QuestionType;

public class QuestionEditActivity extends AppCompatActivity {

    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_QUESTION_TYPE_ID = "question_type_id";

    private static final int REQUEST_UNIT_CHOOSE = 1;
    private static final int REQUEST_IMAGE_CAMERA_QUESTION = 201;
    private static final int REQUEST_IMAGE_GET_QUESTION = 202;
    private static final int REQUEST_IMAGE_CAMERA_ANSWER = 203;
    private static final int REQUEST_IMAGE_GET_ANSWER = 204;
    private static final int REQUEST_IMAGE_EDIT_QUESTION = 205;

    private static final int PERMISSION_CAMERA_RESULT_QUESTION = 101;
    private static final int PERMISSION_CAMERA_RESULT_ANSWER = 102;

    private LearningSubject _currentSubject;
    private LearningUnitInfo _unit;
    private QuestionType _questionType;
    private File _currentTempCameraPhoto;
    private File _currentTargetPhoto;
    private QuestionInfo _context;

    //region views
    private View _root;
    private View _unitSelectLayout;
    private TextView _unitText;
    private FrameLayout _answerContainer;
    private View _answerContent;
    private RecyclerView _questionImgRecycle;
    //endregion

    private ImageListEditAdapter _questionImgAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _context = QuestionInfo.create();
        setContentView(R.layout.activity_question_edit);
        _currentSubject = (LearningSubject) getIntent().getSerializableExtra(EXTRA_SUBJECT);
        _questionType = QuestionType.id2Obj(getIntent().getIntExtra(EXTRA_QUESTION_TYPE_ID, 0));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        }
        _unitSelectLayout = findViewById(R.id.unitSelectLayout);
        _unitText = findViewById(R.id.textUnit);
        _answerContainer = findViewById(R.id.answerContainer);
        _questionImgRecycle = findViewById(R.id.questionImgRecycle);


        _questionImgRecycle.setNestedScrollingEnabled(false);


        _unitSelectLayout.setOnClickListener(v -> {
            Intent intent = new Intent(this, LearningUnitChoosingActivity.class);
            intent.putExtra(LearningUnitChoosingActivity.EXTRA_SUBJECT, _currentSubject);
            intent.putExtra(LearningUnitChoosingActivity.EXTRA_SHOW_NONE, true);
            startActivityForResult(intent, REQUEST_UNIT_CHOOSE);
        });

        switch (_questionType) {
            case SINGLE_CHOICE:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_single_choice, _answerContainer);
                break;
            case MULTIPLY_CHOICE:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_multiply_choice, _answerContainer);
                break;
            case TYPEABLE_BLANK:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_fill, _answerContainer);
                break;
            case BLANK:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_single_choice, _answerContainer);
                break;
            case ANSWER:
                _answerContent = LayoutInflater.from(this).inflate(R.layout.element_answer_define_single_choice, _answerContainer);
                break;
        }
        _questionImgRecycle.setLayoutManager(new LinearLayoutManager(getParent(), LinearLayoutManager.VERTICAL, false));
        _questionImgAdapter = new ImageListEditAdapter(this, new ArrayList<>(), true);
        _questionImgAdapter.setAddButtonClicked(v -> {
            CommonAlerts.AskPhoto(this, (dialog, which) -> {
                //todo choose photo
                switch (which) {
                    case 0: {
                        if (!PermissionAdmin.get(this, Manifest.permission.CAMERA, PERMISSION_CAMERA_RESULT_QUESTION)) {
                            return;
                        }
                        takePhoto(REQUEST_IMAGE_CAMERA_QUESTION);
                        break;
                    }
                    case 1: {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivityForResult(intent, REQUEST_IMAGE_GET_QUESTION);
                        }
                        break;
                    }
                }
            }, null).show();
        });
        _questionImgRecycle.setAdapter(_questionImgAdapter);
    }

    private void takePhoto(int requestId) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //try {
        _currentTempCameraPhoto = new File(getExternalCacheDir(), UUID.randomUUID().toString() + ".jpg");
        Uri fileUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".files", _currentTempCameraPhoto);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, requestId);
        //}
//        catch (IOException e) {
//            Snackbar.make(_root, R.string.cameraIoExpt, Snackbar.LENGTH_LONG).show();
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ok:
                //todo ok...
                finish();
                return true;
            case android.R.id.home:
                finish();
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case REQUEST_UNIT_CHOOSE:
                if (resultCode == RESULT_OK) {
                    assert data != null;
                    if (data.getBooleanExtra(LearningUnitChoosingActivity.RESULT_SELECTED, false)) {
                        try {
                            _unit = DbManager.getHelper(this).getLearningUnitInfos().queryForId(data.getIntExtra(LearningUnitChoosingActivity.RESULT_UNIT_ID, 0));
                            _unitText.setText(_unit.getName());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        _unit = null;
                        _unitText.setText(R.string.emptyUnit);
                    }
                }
                break;
            case REQUEST_IMAGE_CAMERA_QUESTION:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(this, IMGEditActivity.class);
                    intent.putExtra(IMGEditActivity.EXTRA_IMAGE_URI, Uri.fromFile(_currentTempCameraPhoto));
                    _currentTargetPhoto = new File(getExternalFilesDir("images"), UUID.randomUUID().toString() + ".jpg");
                    intent.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, _currentTargetPhoto.getAbsolutePath());
                    startActivityForResult(intent, REQUEST_IMAGE_EDIT_QUESTION);
                }
                break;
            case REQUEST_IMAGE_GET_QUESTION:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent(this, IMGEditActivity.class);
                    intent.putExtra(IMGEditActivity.EXTRA_IMAGE_URI, data.getData());
                    _currentTargetPhoto = new File(getExternalFilesDir("images"), UUID.randomUUID().toString() + ".jpg");
                    intent.putExtra(IMGEditActivity.EXTRA_IMAGE_SAVE_PATH, _currentTargetPhoto.getAbsolutePath());
                    startActivityForResult(intent, REQUEST_IMAGE_EDIT_QUESTION);
                }
                break;
            case REQUEST_IMAGE_EDIT_QUESTION:
                if (resultCode == RESULT_OK) {
                    if (_currentTempCameraPhoto != null) {
                        //noinspection ResultOfMethodCallIgnored
                        _currentTempCameraPhoto.delete();
                        _currentTempCameraPhoto = null;
                    }
                    _context.getQuestionImage().add(_currentTargetPhoto.getName());
                    _currentTargetPhoto = null;
                    refreshQuestionImageList();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CAMERA_RESULT_QUESTION:
            case PERMISSION_CAMERA_RESULT_ANSWER:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //continue
                    takePhoto(requestCode == PERMISSION_CAMERA_RESULT_QUESTION ? REQUEST_IMAGE_CAMERA_QUESTION : REQUEST_IMAGE_CAMERA_ANSWER);
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (shouldShowRequestPermissionRationale(permissions[0])) {
                        Toast.makeText(this, R.string.notice_permission_camera, Toast.LENGTH_LONG).show();
                    } else {
                        //go to settings
                        new AlertDialog.Builder(this).setTitle("权限申请失败").setMessage("请到设置允许本应用使用相机权限").setPositiveButton(R.string.confirm, null).show();
                    }
                }
                break;
        }
    }


    public void refreshQuestionImageList() {
        ArrayList<ImageListEditAdapter.ImageListEditDataContext> dataContext = _questionImgAdapter.get_dataContext();
        dataContext.clear();
        for (String path : _context.getQuestionImage()) {
            ImageListEditAdapter.ImageListEditDataContext item = new ImageListEditAdapter.ImageListEditDataContext();
            item.imgSrc = Uri.fromFile(new File(getExternalFilesDir("images"), path));
            dataContext.add(item);
        }
        _questionImgAdapter.set_dataContext(dataContext);
    }

}
