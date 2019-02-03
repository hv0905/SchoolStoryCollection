package net.sakuratrak.schoolstorycollection;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import net.sakuratrak.schoolstorycollection.R.array;
import net.sakuratrak.schoolstorycollection.R.drawable;
import net.sakuratrak.schoolstorycollection.R.id;
import net.sakuratrak.schoolstorycollection.R.layout;
import net.sakuratrak.schoolstorycollection.R.string;
import net.sakuratrak.schoolstorycollection.core.AppSettingsMaster;
import net.sakuratrak.schoolstorycollection.core.DbManager;
import net.sakuratrak.schoolstorycollection.core.LearningSubject;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

public class MainActivity extends AppCompatActivity {

    private static final int[] SUBJECT_MENU_IDS = new int[]{
            id.nav_menu_chinese,
            id.nav_menu_math,
            id.nav_menu_english,
            id.nav_menu_physics,
            id.nav_menu_chemistry,
            id.nav_menu_biologic,
            id.nav_menu_politics,
            id.nav_menu_history,
            id.nav_menu_geo
    };
    private final String TAG = "MainActivity";
    public QuestionFilterDialog _questionFilterDialog;
    public UnitFilterDialog _unitFilterDialog;
    private MainActivityPagerAdapter _pageContext;
    private LearningSubject _currentSubject = LearningSubject.CHINESE;
    //region ui_control
    private DrawerLayout _drawer;
    private BottomNavigationView _navigation;
    private Toolbar _toolbar;
    private ViewPager _pager;
    private MenuItem _filterMenu;
    //endregion
    private MenuItem _displayModeToggle;
    private NavigationView _navigationView;
    //region fields
    private boolean _isSecondDisplayMode = false;
    //endregion
    private File _cameraCurrentFile;
    private ActionBarDrawerToggle _drawerToggle;
    private ArrayList<RequireRefreshEventHandler> _requireRefreshEvent;
    private ArrayList<ChangeDisplayModeEventHandler> _changeDisplayModeEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //region loadUI
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        //endregion

        //region get UI elements
        _pager = findViewById(id.pager);
        _navigation = findViewById(id.bottomNav);
        _toolbar = findViewById(id.toolbar);
        _drawer = findViewById(id.drawer);
        _navigationView = findViewById(id.navigationView);
        //endregion

        //设置工具栏
        setSupportActionBar(_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(drawable.ic_menu_white_24dp);
        int startId = AppSettingsMaster.getStartupSubjectId(this);
        refreshSubject(LearningSubject.id2Obj(startId));
        _navigationView.setCheckedItem(SUBJECT_MENU_IDS[startId]);

        //设置Drawer
        _drawerToggle = new ActionBarDrawerToggle(this, _drawer, _toolbar, string.subject, string.subject) {

        };

        _drawer.addDrawerListener(_drawerToggle);

        _navigationView.setNavigationItemSelectedListener(menuItem -> {
            _drawer.closeDrawer(GravityCompat.START);
            if (menuItem.getItemId() == id.nav_menu_settings) {
                //settings...
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            } else {
                for (int i = 0; i < _navigationView.getMenu().size(); i++) {
                    _navigationView.getMenu().getItem(i).setChecked(false);
                }
                menuItem.setChecked(true);
                for (int subject = 0; subject < 9; subject++) {
                    if (menuItem.getItemId() == SUBJECT_MENU_IDS[subject]) {
                        refreshSubject(LearningSubject.id2Obj(subject));
                        break;
                    }
                }
            }
            return false;
        });


        //open database
        DbManager.getDefaultHelper(this);

        //region Events

        //_pager.setOffscreenPageLimit(MainActivityPagerAdapter.PAGES_COUNT);//全部加载
        _pageContext = new MainActivityPagerAdapter(getSupportFragmentManager());
        _pager.setAdapter(_pageContext);

        _pager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                switch (i) {
                    case 0:
                        _navigation.setSelectedItemId(id.navigation_home);
                        break;
                    case 1:
                        _navigation.setSelectedItemId(id.navigation_dashboard);
                        break;
                    case 2:
                        _navigation.setSelectedItemId(id.navigation_unit);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        _navigation.setOnNavigationItemSelectedListener(item -> {
            setToolBtnVisible(false);
            switch (item.getItemId()) {
                case id.navigation_home:
                    _pager.setCurrentItem(0);
                    setToolBtnVisible(true);
                    return true;
                case id.navigation_dashboard:
                    _pager.setCurrentItem(1);
                    return true;
                case id.navigation_unit:
                    _pager.setCurrentItem(2);
                    if (_pageContext.stat._pager.getCurrentItem() == 1) {
                        //in unit
                        setToolBtnVisible(true);
                    }
                    return true;
            }
            return false;
        });

        //endregion


        //region init recycler
        //endregion

    }

    //从顶部组合框中获取目前选中的科目
    @NonNull
    public LearningSubject getCurrentSubject() {
        return _currentSubject;
    }

    private void notifyUnitSaveError(View v) {
        Snackbar.make(v, string.failSaveUnitError, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu.top_options, menu);
        _filterMenu = menu.findItem(id.filter);
        _displayModeToggle = menu.findItem(id.displayModeToggle);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (_drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case id.filter:
                if (_pager.getCurrentItem() == 0) {
                    showQuestionFilterDialog();
                } else {
                    showUnitFilterDialog();
                }
                return true;
            case id.displayModeToggle:
                _isSecondDisplayMode = !_isSecondDisplayMode;
                _displayModeToggle.setIcon(_isSecondDisplayMode ? drawable.ic_view_list_white_24dp : drawable.ic_dashboard_white_24dp);
                changeDisplayMode();
                return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        System.out.println("MainActivity Destroyed!!!");
        AppSettingsMaster.setStartupSubjectId(this, _currentSubject.getId());
        DbManager.releaseCurrentHelper();
        super.onDestroy();
    }

    public void refreshSubject(LearningSubject subject) {
        _currentSubject = subject;
        //set toolbar color
        int uiColor = UiHelper.getFlatUiColor(this, _currentSubject.getId());
        getWindow().setStatusBarColor(uiColor);
        _toolbar.setBackgroundColor(uiColor);
        getSupportActionBar().setTitle(getResources().getStringArray(array.learning_subjects)[subject.getId()]);

        //reset filter dialog
        if (_questionFilterDialog != null) {
            _questionFilterDialog.set_subject(_currentSubject);
            _questionFilterDialog.resetDialog();
        }
        if (_unitFilterDialog != null) {
            _unitFilterDialog.set_subject(_currentSubject);
            _unitFilterDialog.resetDialog();
        }

        requireRefresh();
    }

    public void addRequireRefreshEvent(RequireRefreshEventHandler handler) {
        if (_requireRefreshEvent == null) _requireRefreshEvent = new ArrayList<>();
        _requireRefreshEvent.add(handler);
    }

    public void removeRequireRefreshEvent(RequireRefreshEventHandler handler) {
        if (_requireRefreshEvent == null) return;
        _requireRefreshEvent.remove(handler);
    }

    public void requireRefresh() {
        if (_requireRefreshEvent == null) return;
        Log.d(TAG, "requireRefresh: Time to refresh~");
        for (RequireRefreshEventHandler item :
                _requireRefreshEvent) {
            item.update();
        }
    }

    public void addChangeDisplayModeEvent(ChangeDisplayModeEventHandler handler) {
        if (_changeDisplayModeEvent == null) _changeDisplayModeEvent = new ArrayList<>();
        _changeDisplayModeEvent.add(handler);
    }

    public void removeChangeDisplayModeEvent(ChangeDisplayModeEventHandler handler) {
        if (_changeDisplayModeEvent == null) return;
        _changeDisplayModeEvent.remove(handler);
    }

    public void changeDisplayMode() {
        if (_changeDisplayModeEvent == null) return;
        for (ChangeDisplayModeEventHandler item :
                _changeDisplayModeEvent) {
            item.change(_isSecondDisplayMode);
        }
    }


    public boolean is_isSecondDisplayMode() {
        return _isSecondDisplayMode;
    }

    public void set_isSecondDisplayMode(boolean _isSecondDisplayMode) {
        this._isSecondDisplayMode = _isSecondDisplayMode;
    }

    void showQuestionFilterDialog() {

        if (_questionFilterDialog == null) {
            _questionFilterDialog = new QuestionFilterDialog(getCurrentSubject());
            _questionFilterDialog.set_onUpdate(this::onWorkbookFilterDialogUpdate);
        }
        _questionFilterDialog.showDialog(this);
    }

    void showUnitFilterDialog() {
        if (_unitFilterDialog == null) {
            _unitFilterDialog = new UnitFilterDialog(getCurrentSubject());
            _unitFilterDialog.set_onUpdate(this::onUnitFilterDialogUpdate);
        }
        _unitFilterDialog.showDialog(this);
    }

    void onWorkbookFilterDialogUpdate() {
        _pageContext.workBook.refreshList();
    }

    void onUnitFilterDialogUpdate() {
        _pageContext.stat._adapter.unit.refresh();
    }

    void setToolBtnVisible(boolean visible) {
        _filterMenu.setVisible(visible);
        _displayModeToggle.setVisible(visible);
    }

    @FunctionalInterface
    public interface RequireRefreshEventHandler {
        void update();
    }

    @FunctionalInterface
    public interface ChangeDisplayModeEventHandler {
        void change(boolean isSecondMode);
    }

}
