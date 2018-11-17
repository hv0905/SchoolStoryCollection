package sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.io.File;

import me.kareluo.imaging.IMGEditActivity;
import sakuratrak.schoolstorycollection.core.DbManager;
import sakuratrak.schoolstorycollection.core.LearningSubject;

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
    private final String TAG = "MainActivity";
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

        //open database
        DbManager.getDefaultHelper(this);

        //region Events

        _pager.setOffscreenPageLimit(MainActivityPagerAdapter.PAGES_COUNT);//全部加载
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
                }
                return false;
        });

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
            case R.id.settingBtn:
                startActivity(new Intent(this,SettingActivity.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        System.out.println("MainActivity Destroyed!!!");
        DbManager.releaseCurrentHelper();
        super.onDestroy();
    }

}
