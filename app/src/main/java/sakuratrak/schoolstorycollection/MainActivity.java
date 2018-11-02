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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import me.kareluo.imaging.IMGEditActivity;
import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;
import sakuratrak.schoolstorycollection.core.LearningUnitInfo;

public class MainActivity extends AppCompatActivity {

    private MainActivityPagerAdapter _pageContext;

    //region ui_control
    private Spinner _subjectSpinner;
    private BottomNavigationView _navigation;
    private Toolbar _toolbar;
    private ViewPager _pager;
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
        _pager = findViewById(R.id.pager);
        _navigation = findViewById(R.id.bottomNav);
        _toolbar = findViewById(R.id.toolbar);
        //endregion

        //设置工具栏
        setSupportActionBar(_toolbar);

        //region Events

        _pageContext = new MainActivityPagerAdapter(getSupportFragmentManager());
        _pageContext.unit._backupParent = this;
        _pager.setAdapter(_pageContext);

        _pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        _navigation.setSelectedItemId(R.id.navigation_home);
                        break;
                    case 1:
                        _navigation.setSelectedItemId(R.id.navigation_dashboard);
                        break;
                    case 2:
                        _navigation.setSelectedItemId(R.id.navigation_unit);
                        break;
                    case 3:
                        _navigation.setSelectedItemId(R.id.navigation_settings);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        _navigation.setOnNavigationItemSelectedListener(item -> {
                _subjectSpinner.setVisibility(View.VISIBLE);
                _filterMenu.setVisible(false);
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                            _pager.setCurrentItem(0);
                        _filterMenu.setVisible(true);
                        return true;
                    case R.id.navigation_dashboard:
                            _pager.setCurrentItem(1);
                        return true;
                    case R.id.navigation_unit:
                            _pager.setCurrentItem(2);
                        return true;
                    case R.id.navigation_settings:
                            _pager.setCurrentItem(3);
                        _subjectSpinner.setVisibility(View.INVISIBLE);
                        return true;
                }
                return false;
        });

//        _tempBtn.setOnClickListener(v -> CommonAlerts.AskPhoto(this, (dialog, which) -> {
//            switch (which) {
//                case 0: {
//                    if (!PermissionAdmin.get(this, Manifest.permission.CAMERA,1)) {
//                        Snackbar.make(v, R.string.cameraPermissionError, Snackbar.LENGTH_LONG).show();
//                        return;
//                    }
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    try {
//                        File privateFile = AndroidHelper.createLocalImageFile(this);
//                        Uri fileUri = FileProvider.getUriForFile(MainActivity.this, MainActivity.this.getApplicationContext().getPackageName() + ".files", privateFile);
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//                        _cameraCurrentFile = privateFile;
//                        startActivityForResult(intent, IntentResults.REQUEST_IMAGE_CAMERA);
//                    } catch (IOException e) {
//                        Snackbar.make(v, R.string.cameraIoExpt, Snackbar.LENGTH_LONG).show();
//                    }
//                    break;
//                }
//                case 1: {
//                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                    intent.setType("image/*");
//                    intent.addCategory(Intent.CATEGORY_OPENABLE);
//                    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
//                    if (intent.resolveActivity(getPackageManager()) != null) {
//                        startActivityForResult(intent, IntentResults.REQUEST_IMAGE_GET);
//                    }
//                    break;
//                }
//            }
//        }, null));
        
        //endregion


        //region init recycler


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
    @NonNull
    public LearningSubject getCurrentSubject() {
        if (_subjectSpinner == null) {
            return LearningSubject.CHINESE;
        }
        return LearningSubject.id2Obj(_subjectSpinner.getSelectedItemPosition());
    }


    private void notifyUnitSaveError(View v) {
        Snackbar.make(v, R.string.failSaveUnitError, Snackbar.LENGTH_LONG).show();
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
                _pageContext.unit.refreshUnit();
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
        System.out.println("MainActivity Destroyed!!!");
        DbManager.releaseHelper();
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
