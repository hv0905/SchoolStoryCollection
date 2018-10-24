package sakuratrak.schoolstorycollection;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.kareluo.imaging.IMGEditActivity;
import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;
import sakuratrak.schoolstorycollection.core.QuestionType;

public class MainActivity extends AppCompatActivity {

    //region ui_control
    private TextView _mTextMessage;
    private RecyclerView _itemList;
    private RecyclerView _unitList;
    private FloatingActionButton _addItemBtn;
    private FloatingActionButton _addUnitBtn;
    private Spinner _subjectSpinner;
    private ConstraintLayout _workbookLayout;
    private ConstraintLayout _quizLayout;
    private ConstraintLayout _unitLayout;
    private LinearLayout _settingLayout;
    private BottomNavigationView _navigation;
    private Button _unitManageBtn;
    private Button _aboutBtn;
    private Button _tempBtn;
    private Toolbar _toolbar;
    private LinearLayout _unitEmptyNotice;

    private MenuItem _filterMenu;
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
        _tempBtn = findViewById(R.id.tempbtn);
        _unitManageBtn = findViewById(R.id.main_settings_unitManageBtn);
        //_subjectSpinner = findViewById(R.id.subjectSpinner);
        _toolbar = findViewById(R.id.toolbar);
        _unitEmptyNotice = findViewById(R.id.unitEmptyNotice);
        _aboutBtn = findViewById(R.id.main_settings_about);
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
            _filterMenu.setVisible(false);
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    _workbookLayout.setVisibility(View.VISIBLE);
                    _filterMenu.setVisible(true);
                    return true;
                case R.id.navigation_dashboard:
                    _quizLayout.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_unit:
                    _unitLayout.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_settings:
                    _settingLayout.setVisibility(View.VISIBLE);
                    _subjectSpinner.setVisibility(View.INVISIBLE);
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

        _tempBtn.setOnClickListener(v -> CommonAlerts.AskPhoto(this, (dialog, which) -> {
            switch (which) {
                case 0: {
                    if (!PermissionAdmin.get(this, Manifest.permission.CAMERA,1)) {
                        Snackbar.make(v, "Story酱被玩坏了...异常:相机使用权限申请出错", Snackbar.LENGTH_LONG).show();
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
                        Snackbar.make(v, "Story酱被玩坏了...异常:IOException在创建图像文件时抛出", Snackbar.LENGTH_LONG).show();
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
//            CommonAlerts.AskSubjectType(MainActivity.this, (dialog, which) -> {
//                LearningSubject sub = LearningSubject.id2Obj(which);
//                Intent in = new Intent(MainActivity.this, LearningUnitChoosingActivity.class);
//                in.putExtra("subject", sub);
//                startActivity(in);
//            }, null);

            startActivity(new Intent(this,NewQuestionActivity.class));
        });


        _addUnitBtn.setOnClickListener(v -> {
            final EditText et = new EditText(MainActivity.this);
            et.setSingleLine();
            AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this).setIcon(R.drawable.ic_book_black_24dp).setTitle("创建单元")
                    .setMessage("单元名称:")
                    .setView(et).setNegativeButton("完成", (dialog, which) -> {
                        if (et.getText().toString().trim().isEmpty()) {
                            new AlertDialog.Builder(MainActivity.this).setMessage("请输入单元名称").setTitle("错误").setNegativeButton("确定", null).setIcon(R.drawable.ic_warning_black_24dp).show();
                            return;
                        }
                        try {
                            DbManager.getHelper(this).getLearningUnitInfos().create(new LearningUnitInfo(et.getText().toString().trim(), getCurrentSubject()));
                            DbManager.releaseHelper();
                        } catch (SQLException e) {
                            Snackbar.make(_navigation, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
                            return;
                        }
                        refreshUnit();

                    })
                    .setPositiveButton("取消", null);

            ab.show();
        });

        _aboutBtn.setOnClickListener(v -> startActivityForResult(new Intent(this, AboutActivity.class), 0));


        //endregion


        //region init recycler
        _itemList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        _unitList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        refreshUnit();


        ArrayList<QuestionItemAdapter.QuestionItemInfo> strs = new ArrayList<>();
//        strs.add(new QuestionItemAdapter.QuestionItemInfo("hello world1", new Date().toString(), "test unit", null));
//        strs.add(new QuestionItemAdapter.QuestionItemInfo("hello world2", new Date().toString(), "test unit", null));
//        strs.add(new QuestionItemAdapter.QuestionItemInfo("hello world3", new Date().toString(), "test unit", null));
//        strs.add(new QuestionItemAdapter.QuestionItemInfo("hello world4", new Date().toString(), "test unit", null));
//        strs.add(new QuestionItemAdapter.QuestionItemInfo("hello world5", new Date().toString(), "test unit", null));

        QuestionItemAdapter adapter = new QuestionItemAdapter(strs);
        _itemList.setAdapter(adapter);

        //endregion

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
                //noinspection ConstantConditions
                Uri currentUri = data.getData();
                if (currentUri == null) return;
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
        if (_subjectSpinner == null) {
            return LearningSubject.CHINESE;
        }
        return LearningSubject.id2Obj(_subjectSpinner.getSelectedItemPosition());
    }

    //载入单元与统计列表
    private void refreshUnit() {
        ArrayList<UnitDisplayAdapter.UnitDisplayInfo> udi = new ArrayList<>();
        List<LearningUnitInfo> luis;
        try {
            luis = (DbManager.getHelper(this)).getLearningUnitInfos().queryForEq("subjectId", getCurrentSubject().getId());
            DbManager.releaseHelper();
        } catch (SQLException e) {
            e.printStackTrace();
            Snackbar.make(_navigation, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
            return;
        }
        if (luis.size() == 0) {
            _unitEmptyNotice.setVisibility(View.VISIBLE);
        } else {
            _unitEmptyNotice.setVisibility(View.INVISIBLE);
        }
        for (LearningUnitInfo item : luis) {
            UnitDisplayAdapter.UnitDisplayInfo udiItem = new UnitDisplayAdapter.UnitDisplayInfo(item.getName(), item.getExerciseLogCount(), item.computeCorrectRatio(), item.getExerciseLogCount(), 50, item.getIfNeedMoreQuiz());
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
        ad.setPositiveButton(R.string.cancel, null).setNegativeButton(R.string.confirm, (dialog, which) -> {
            try {
                DbManager.getHelper(this).getLearningUnitInfos().delete(info);
                DbManager.releaseHelper();
            } catch (SQLException e) {
                Snackbar.make(_navigation, R.string.sqlExp, Snackbar.LENGTH_LONG).show();
            }
            refreshUnit();
        });
        ad.show();
    }

    private void notifyResetUnit(View v, UnitDisplayAdapter.UnitDisplayInfo udi, LearningUnitInfo info) {
        AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
        ad.setTitle(R.string.confirmLog_title).setIcon(R.drawable.ic_warning_black_24dp).setMessage(String.format(getString(R.string.confirmLog_msg), udi.Title));
        ad.setPositiveButton(R.string.cancel, null).setNegativeButton(R.string.confirm, (dialog, which) -> {

        });
        ad.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_options, menu);
        _subjectSpinner = (Spinner) menu.findItem(R.id.subjectSpinner).getActionView();
        _filterMenu = menu.findItem(R.id.filter);
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

        //set spinner adapter
        ArrayAdapter<CharSequence> subjectDropdown = ArrayAdapter.createFromResource(this, R.array.learning_subjects, R.layout.layout_spinner_item);
        subjectDropdown.setDropDownViewResource(R.layout.layout_spinner_dropdown);
        _subjectSpinner.setAdapter(subjectDropdown);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter:
                FilterDialog fd = new FilterDialog();
                fd.show(getSupportFragmentManager(), "filter");
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        DbManager.releaseHelper();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
