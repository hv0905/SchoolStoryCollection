package sakuratrak.schoolstorycollection;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import me.kareluo.imaging.IMGEditActivity;

import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import sakuratrak.schoolstorycollection.core.LearningUnitStorageFile;
import sakuratrak.schoolstorycollection.core.QuestionType;

public class MainActivity extends AppCompatActivity {

    //region ui_control
    private TextView _mTextMessage;
    private RecyclerView _itemList;
    private RecyclerView _unitList;
    private FloatingActionButton _addItemBtn;
    private FloatingActionButton _addUnitBtn;
    private AppCompatSpinner _subjectSpinner;
    private ConstraintLayout _workbookLayout;
    private ConstraintLayout _quizLayout;
    private ConstraintLayout _unitLayout;
    private ConstraintLayout _settingLayout;
    private BottomNavigationView _navigation;
    private Button _unitManageBtn;
    private Button _tempbtn;
    private Toolbar _toolbar;
    //endregion

    //region fields
    private File _cameraCurrentFile;
    //endregion

    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region loadUI
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //endregion


        //region get UI elements
        _mTextMessage = findViewById(R.id.message);
        _navigation = findViewById(R.id.bottomNav);
        _addItemBtn = findViewById(R.id.addItemBtn);
        _itemList = findViewById(R.id.itemList);
        _unitList = findViewById(R.id.unitList);
        _addUnitBtn = findViewById(R.id.addUnitBtn);
        _workbookLayout = findViewById(R.id.workbookLayout);
        _quizLayout = findViewById(R.id.quizLayout);
        _unitLayout = findViewById(R.id.unitLayout);
        _settingLayout = findViewById(R.id.settingLayout);
        _tempbtn = findViewById(R.id.tempbtn);
        _unitManageBtn = findViewById(R.id.main_settings_unitManageBtn);
        _subjectSpinner = findViewById(R.id.subjectSpinner);
        _toolbar = findViewById(R.id.toolbar);
        //endregion

        //设置工具栏
        setSupportActionBar(_toolbar);

        //region Events

        _navigation.setOnNavigationItemSelectedListener(item -> {
            _workbookLayout.setVisibility(View.INVISIBLE);
            _quizLayout.setVisibility(View.INVISIBLE);
            _unitLayout.setVisibility(View.INVISIBLE);
            _settingLayout.setVisibility(View.INVISIBLE);
            _subjectSpinner.setVisibility(View.VISIBLE);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    _workbookLayout.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    _quizLayout.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_settings:
                    _settingLayout.setVisibility(View.VISIBLE);
                    _subjectSpinner.setVisibility(View.INVISIBLE);
                    return true;
                case R.id.navigation_unit:
                    _unitLayout.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        });


        _addItemBtn.setOnClickListener(v -> {
            //navigate to add activity
            //Snackbar.make(v,"Add button clicked!!!",2000).show();
            CommonAlerts.AskQuestionType(MainActivity.this, (dialogInterface, i) -> {
                dialogInterface.dismiss();
                final QuestionType type = QuestionType.id2Obj(i);
                //System.out.println(type.toString());

            }, null);
        });

        _tempbtn.setOnClickListener(v -> CommonAlerts.AskPhoto(this, (dialog, which) -> {
            switch (which) {
                case 0: {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        Snackbar.make(v, "App被玩坏了...异常:相机使用权限申请出错", Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    try {
                        File privateFile = AndroidHelper.createLocalImageFile(this);
                        Uri fileUri = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getApplicationContext().getPackageName() + ".files", privateFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                        _cameraCurrentFile = privateFile;
                        startActivityForResult(intent, IntentResults.REQUEST_IMAGE_CAMERA);
                    } catch (IOException e) {
                        Snackbar.make(v, "App被玩坏了...异常:IOException在创建图像文件时抛出", Snackbar.LENGTH_LONG).show();
                    }
                    break;
                }
                case 1: {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, IntentResults.REQUEST_IMAGE_GET);
                    }
                    break;
                }
            }
        }, null));


        _unitManageBtn.setOnClickListener(v -> {
            //选择科目
            CommonAlerts.AskSubjectType(MainActivity.this, (dialog, which) -> {
                dialog.dismiss();
                //继续
                LearningSubject sub = LearningSubject.id2Obj(which);
                Intent in = new Intent(MainActivity.this, LearningUnitManageActivity.class);
                in.putExtra("subject", sub);
                MainActivity.this.startActivity(in);
            }, (dialog, which) -> dialog.dismiss());
        });


        _addUnitBtn.setOnClickListener(v -> {
            final EditText et = new EditText(MainActivity.this);
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.ic_book_black_24dp).setTitle("创建单元")
                    .setMessage("单元名称:")
                    .setView(et).setNegativeButton("完成", (dialog, which) -> {
                        if (et.getText().toString().trim().isEmpty()) {
                            new AlertDialog.Builder(MainActivity.this).setMessage("请输入单元名称").setTitle("错误").setNegativeButton("确定", null).setIcon(R.drawable.ic_warning_black_24dp).show();
                            return;
                        }
                        ArrayList<LearningUnitInfo> list = LearningUnitStorageFile.getDefault().getUnits(getCurrentSubject());
                        if (list == null) list = new ArrayList<>();
                        list.add(new LearningUnitInfo(et.getText().toString().trim()));
                        LearningUnitStorageFile.getDefault().setUnits(getCurrentSubject(), list);
                        refreshUnit();
                        try {
                            LearningUnitStorageFile.getDefault().saveToInternalStorage(MainActivity.this);
                        } catch (IOException io) {
                            notifyUnitSaveError(v);
                        }

                    })
                    .setPositiveButton("取消", (dialog, which) -> dialog.dismiss());

            ab.show();
        });

        _subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //load unit
                refreshUnit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //ignored

            }
        });

        //endregion


        //region init recycler
        _itemList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        _unitList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        ArrayAdapter<CharSequence> subjectDropdown = ArrayAdapter.createFromResource(this, R.array.learning_subjects, R.layout.layout_spinner_item);
        subjectDropdown.setDropDownViewResource(R.layout.layout_spinner_dropdown);


        _subjectSpinner.setAdapter(subjectDropdown);

        //QuestionItemAdapter qia = new QuestionItemAdapter(lstr);
        //_itemList.setAdapter(qia);
        refreshUnit();


        //endregion


//        _unitList.setAdapter(uda);

    }

    //Intent接收事件
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case IntentResults.REQUEST_IMAGE_CAMERA:
                if (resultCode != RESULT_OK) return;
                Intent intent = new Intent(this, IMGEditActivity.class);
                intent.putExtra(IMGEditActivity.EXTRA_IMAGE_URI, Uri.fromFile(_cameraCurrentFile));
                startActivityForResult(intent, IntentResults.REQUEST_IMAGE_EDIT);
                break;
            case IntentResults.REQUEST_IMAGE_GET:
                if (resultCode != RESULT_OK) return;
                Uri currentUri = data.getData();
                if(currentUri == null) return;
                Intent intent1 = new Intent(this, IMGEditActivity.class);
                intent1.putExtra(IMGEditActivity.EXTRA_IMAGE_URI, currentUri);
                startActivityForResult(intent1, IntentResults.REQUEST_IMAGE_EDIT);
                break;
            case IntentResults.REQUEST_IMAGE_EDIT:

                break;
        }
    }

    //从顶部组合框中获取目前选中的科目
    public LearningSubject getCurrentSubject() {
        return LearningSubject.id2Obj(_subjectSpinner.getSelectedItemPosition());
    }

    //载入单元与统计列表
    private void refreshUnit() {
        ArrayList<UnitDisplayAdapter.UnitDisplayInfo> udi = new ArrayList<>();
        if (!LearningUnitStorageFile.defaultLoaded()) {
            LearningUnitStorageFile defaults = LearningUnitStorageFile.readFromInternalStorage(this);
            if (defaults == null) {
                defaults = new LearningUnitStorageFile();
            }
            LearningUnitStorageFile.setDefault(defaults);
        }
        ArrayList<LearningUnitInfo> luis = LearningUnitStorageFile.getDefault().getUnits(getCurrentSubject());
        if (luis == null)
            luis = new ArrayList<>();
        for (LearningUnitInfo item : luis) {
            UnitDisplayAdapter.UnitDisplayInfo udiItem = new UnitDisplayAdapter.UnitDisplayInfo(item.ExerciseLogs.size(), item.computeCorrectRatio(), item.Name);
            udiItem.RmClicked = v -> notifyRmUnit(v, udiItem, item);
            udiItem.ResetClicked = v -> notifyResetUnit(v, udiItem, item);
            udi.add(udiItem);
        }
        UnitDisplayAdapter uda = new UnitDisplayAdapter(udi);
        _unitList.setAdapter(uda);
    }


    private void notifyUnitSaveError(View v) {
        Snackbar.make(v, R.string.failSaveUnitError, Snackbar.LENGTH_LONG).show();
    }

    private void notifyRmUnit(View v, UnitDisplayAdapter.UnitDisplayInfo udi, LearningUnitInfo info) {
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle(R.string.confirmRm_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmRm_msg), udi.Title));
        ad.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.confirm, (dialog, which) -> {
            Objects.requireNonNull(LearningUnitStorageFile.getDefault().getUnits(getCurrentSubject())).remove(info);
            try {
                LearningUnitStorageFile.getDefault().saveToInternalStorage(MainActivity.this);
            } catch (IOException e) {
                notifyUnitSaveError(v);
            }
            refreshUnit();
        });
        ad.show();
    }

    private void notifyResetUnit(View v, UnitDisplayAdapter.UnitDisplayInfo udi, LearningUnitInfo info) {
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle(R.string.confirmLog_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmLog_msg), udi.Title));
        ad.setNegativeButton(R.string.cancel, null).setPositiveButton(R.string.confirm, (dialog, which) -> {

        });
        ad.show();
    }

}
